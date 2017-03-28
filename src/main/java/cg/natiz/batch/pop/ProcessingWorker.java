package cg.natiz.batch.pop;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * 
 * Control delivered containers from incoming zone, filter, skip, process and
 * transform, then send them the outcoming zone
 * 
 * @author natiz
 * 
 * @param <T1>
 *            original managed data type
 * 
 * @param <T2>
 *            targeted managed data type
 */
public final class ProcessingWorker<T1 extends Serializable, T2 extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(ProcessingWorker.class);

	private Repository<T1> incoming;
	private Repository<T2> outcoming;
	private Processor<T1, T2> processor;

	/**
	 * 
	 * @param processor
	 *            a data processor
	 * @param incoming
	 *            incoming repository
	 * @param outcoming
	 *            outcoming repository
	 * @return this worker object
	 */
	public ProcessingWorker<T1, T2> init(@Controller(ControllerType.PROCESSOR) Processor<T1, T2> processor,
			Repository<T1> incoming, Repository<T2> outcoming) {
		this.processor = processor;
		this.incoming = incoming;
		this.outcoming = outcoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		final Reporting reporting = Reporting.PROCESSING;
		Optional<Container<T1>> container = Optional.empty();
		int waiting = 2;
		LocalDateTime beginDate = LocalDateTime.now();
		do {
			waiting = (int) Math.max(2, Math.cbrt(incoming.size()));
			logger.debug("Processor waiting for {} ms", waiting);
			Thread.sleep(waiting);
			container = incoming.pull();
			if (container == null)
				continue;
			container.ifPresent(c -> {
				c.setStartProcessDate(new Date());
				List<T2> items = c.getContent().stream().map(Optional::ofNullable).map(item -> {
					Optional<T2> empty = Optional.empty();
					try {
						return processor.process(item);
					} catch (Exception e) {
						reporting.setDescription(c.toString());
						logger.warn("Item processing fails: {}. Problem:  {} -> {}", item, e.getClass().getName(),
								e.getMessage());
						return empty;
					}

				}).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

				@SuppressWarnings("unchecked")
				Optional<Container<T2>> newContainer = Optional.of(Pop.newInstance(Container.class).addAll(items)
						.setReference(c.getReference()).setSendDate(c.getSendDate())
						.setStartProcessDate(c.getStartProcessDate()).setEndProcessDate(new Date()));
				try {
					if (outcoming.push(newContainer)) {
						newContainer.ifPresent(cc -> reporting.incrementContainersNumber()
								.incrementItemsNumber(cc.getContent().size()));
						logger.debug("Pushed to outcoming {}", newContainer);
					} else {
						newContainer.ifPresent(cc -> reporting.setDescription(cc.toString()));
						logger.warn("Pushing to outcoming fails {} \nOutcoming stock = {}", newContainer,
								outcoming.size());
					}
				} catch (Exception e) {
					newContainer.ifPresent(cc -> reporting.setDescription(cc.toString()));
					logger.warn("Pushing to outcoming fails: {}. Problem:  {} -> {}", newContainer,
							e.getClass().getName(), e.getMessage());
				}
			});
		} while (container.isPresent());

		Optional<Container<T2>> newContainer = Optional.empty();
		if (outcoming.push(newContainer)) {
			logger.debug("End signal to outcoming {}", newContainer);
		}
		reporting.setProcessingDuration(ChronoUnit.SECONDS.between(beginDate, LocalDateTime.now()));
		logger.info("Worker ends successfully in {}s, {} containers and {} items ", reporting.getProcessingDuration(),
				reporting.getContainersNumber(), reporting.getItemsNumber());

		return reporting;
	}
}
