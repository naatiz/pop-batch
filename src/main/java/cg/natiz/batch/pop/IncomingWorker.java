package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * Deliver containers in the incoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 */
public class IncomingWorker<T extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(IncomingWorker.class);
	private Puller<T> provider;
	protected Repository<T> incoming;

	/**
	 * @param provider
	 *            a data provider
	 * @param incoming
	 *            an in memory repository
	 * @return this worker object
	 */
	public IncomingWorker<T> init(@Controller(ControllerType.PROVIDER) Puller<T> provider, Repository<T> incoming) {
		this.provider = provider;
		this.incoming = incoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		Container<T> current = null;
		Container<T> container = null;
		int waiting = 1;
		do {
			logger.debug("Provider waiting for {} ms", waiting);
			container = provider.pull();
			if (container != null && !container.isEmpty()
					&& incoming.push(container.setReference(incoming.getReference()).setSendDate(new Date()))) {
				current = container;
				logger.debug("Pushed to incoming {} \nIncoming stock = {}", current, incoming.size());
			}
			Thread.sleep(waiting);
		} while (container != null);
		incoming.close();

		StringBuilder sb = new StringBuilder().append(this.getClass().getSimpleName()).append(" : ")
				.append(current.toString());
		Reporting reporting = Pop.newInstance(Reporting.class).setDescription(sb.toString());
		return reporting;
	}
}
