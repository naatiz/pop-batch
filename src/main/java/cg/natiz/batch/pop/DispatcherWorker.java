package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

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
public class DispatcherWorker<T1 extends Serializable, T2 extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(DispatcherWorker.class);

	protected Repository<T1> incoming;
	protected Repository<T2> outcoming;
	protected Processor<T1, T2> processor;

	/**
	 * 
	 * @param processor
	 *            a data processor
	 * @param incoming
	 *            in memory incoming repository
	 * @param outcoming
	 *            in memory outcoming repository
	 * @return this worker object
	 */
	public DispatcherWorker<T1, T2> init(@Controller(ControllerType.PROCESSOR) Processor<T1, T2> processor,
			Repository<T1> incoming, Repository<T2> outcoming) {
		this.processor = processor;
		this.incoming = incoming;
		this.outcoming = outcoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		Container<T1> current = null;
		Container<T1> container = null;
		int waiting = 2;
		while (incoming.isOpen()) {
			logger.debug("Processor waiting for {} ms", waiting);
			Thread.sleep(waiting);
			waiting = (int) Math.max(2, Math.cbrt(incoming.size()));
			while ((container = incoming.pull()) != null) {
				container.setStartProcessDate(new Date());
				List<T1> content = container.getContent();
				List<T2> processedContent = new ArrayList<T2>(content.size());
				for (T1 data : content) {
					try {
						T2 value = processor.process(data);
						processedContent.add(value);
					} catch (Exception e) {
						logger.warn("Container data processing fails: {}  \nRelated : {}", data, container);
						logger.warn("Container item processing fails : {}", e.getMessage());
					}
				}
				if (!processedContent.isEmpty()) {
					@SuppressWarnings("unchecked")
					Container<T2> newContainer = Pop.newInstance(Container.class);
					if (outcoming.push(newContainer.setReference(outcoming.getReference())
							.setSendDate(container.getSendDate()).setStartProcessDate(container.getStartProcessDate())
							.setEndProcessDate(new Date()).addAll(processedContent))) {
						logger.debug("Pushed to outcoming {}", newContainer);
					}
				}
				current = container.setEndProcessDate(new Date());
			}
		}
		outcoming.close();
		StringBuilder sb = new StringBuilder().append(this.getClass().getSimpleName()).append(" : ")
				.append(current.toString());
		Reporting reporting = Pop.newInstance(Reporting.class).setDescription(sb.toString());
		return reporting;
	}
}
