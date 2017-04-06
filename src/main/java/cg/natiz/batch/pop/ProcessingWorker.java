package cg.natiz.batch.pop;

import java.io.Serializable;
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
		final Reporting reporting = Reporting.newProcessingRepository();
		Optional<Container<T1>> container = Optional.empty();
		int waiting = 1;
		do {
			//waiting = (int) Math.max(2, Math.cbrt(incoming.size()));
			logger.debug("Processor waiting for {} ms", waiting);
			Thread.sleep(waiting);
			container = Optional.ofNullable(incoming.pull()).orElseGet(Optional::empty);
			container.ifPresent(c -> {
				c.setStartProcessDate(new Date());
				List<T2> items = c.getItems().stream().map(Optional::ofNullable).map(item -> {
					Optional<T2> empty = Optional.empty();
					try {
						return processor.process(item);
					} catch (Exception e) {
						String message = String.format("Item processing fails: %s. Problem:  %s -> %S", item,
								e.getClass().getName(), e.getMessage());
						reporting.addRapport(message);
						logger.warn(message);
						return empty;
					}

				}).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

				@SuppressWarnings("unchecked")
				Optional<Container<T2>> newContainer = Optional.of(Pop.newInstance(Container.class).addAllItems(items)
						.setReference(c.getReference()).setSendDate(c.getSendDate())
						.setStartProcessDate(c.getStartProcessDate()).setEndProcessDate(new Date()));
				try {
					if (outcoming.push(newContainer)) {
						newContainer.ifPresent(
								cc -> reporting.incrementContainersNumber().incrementItemsNumber(cc.getItems().size()));
						logger.debug("Pushed to outcoming {}", newContainer);
					} else {
						String message = String.format("Pushing to outcoming fails %s \nOutcoming stock = %d", newContainer,
								outcoming.size());
						reporting.addRapport(message);
						throw new Exception(message);
					}
				} catch (Exception e) {
					reporting.addRapport(e.getMessage());
					logger.warn("Cannot push container to outcoming repository: " + newContainer, e);
				}
			});
			outcoming.setPushable(incoming.isOpen());
		} while (incoming.isOpen());
		logger.info("Worker ends successfully. {} ", reporting.stop());
		return reporting;
	}
}
