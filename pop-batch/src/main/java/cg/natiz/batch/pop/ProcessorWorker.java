package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Processor;

/**
 * 
 * Control delivered containers from incoming zone, process and transform data
 * and send them the outcoming zone
 * 
 * @author natiz
 * 
 * @param <T1>
 *            original managed data type
 * 
 * @param <T2>
 *            processed managed data type
 */
public class ProcessorWorker<T1 extends Serializable, T2 extends Serializable>
		implements Callable<String> {

	private static final Logger logger = LoggerFactory
			.getLogger(ProcessorWorker.class);

	private Repository<T1> incoming;
	private Repository<T2> outcoming;
	private Processor<T1, T2> processor;

	/**
	 * @param incoming
	 *            an incoming repository
	 */
	public ProcessorWorker<T1, T2> addIncomingRepository(Repository<T1> incoming) {
		this.incoming = incoming;
		return this;
	}

	/**
	 * @param outcomingStore
	 *            an outcoming store
	 */
	public ProcessorWorker<T1, T2> addOutcomingRepository(
			Repository<T2> outcoming) {
		this.outcoming = outcoming;
		return this;
	}

	/**
	 * @param processor
	 *            a processor
	 */
	public ProcessorWorker<T1, T2> addProcessor(Processor<T1, T2> processor) {
		this.processor = processor;
		return this;
	}

	@Override
	public String call() throws Exception {
		Container<T1> current = null;
		Container<T1> container = null;
		while (incoming.isOpen()) {
			Thread.sleep(incoming.getThreadSleep());
			logger.debug("incoming stock = {}", incoming.size());
			container = incoming.poll();
			if (container != null) {
				container.setStartProcessDate(new Date());
				List<T1> content = container.getContent();
				List<T2> processedContent = new ArrayList<T2>(content.size());
				for (T1 item : content) {
					try {
						T2 value = processor.doProcess(item);
						processedContent.add(value);
					} catch (Exception e) {
						logger.warn("{} item processing fails. Related : {}",
								item, container);
						logger.warn("item processing fails", e);
					}
				}
				if (!processedContent.isEmpty()) {
					@SuppressWarnings("unchecked")
					Container<T2> newContainer = Container
							.newInstance(Container.class);
					outcoming.push(newContainer
							.setReference(outcoming.getReference())
							.setSendDate(container.getSendDate())
							.setStartProcessDate(
									container.getStartProcessDate())
							.setEndProcessDate(new Date())
							.addAll(processedContent));
					logger.debug("Push a container to outcoming. Stock = {}",
							outcoming.size());
				}
			}
			if (container != null)
				current = container.setEndProcessDate(new Date());
		}
		outcoming.close();
		return current.toString();
	}
}
