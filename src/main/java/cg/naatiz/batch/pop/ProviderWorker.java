package cg.naatiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.naatiz.batch.pop.util.Controller;
import cg.naatiz.batch.pop.util.ControllerType;

/**
 * Deliver containers in the incoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 */
final class ProviderWorker<T extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(ProviderWorker.class);
	private Puller<T> provider;
	private Repository<T> incoming;

	private long currentContainerId = 0L;

	/**
	 * @param provider
	 *            a data provider
	 * @param incoming
	 *            an incoming repository
	 * @return this worker object
	 */
	public ProviderWorker<T> init(@Controller(ControllerType.PROVIDER) Puller<T> provider, Repository<T> incoming) {
		this.provider = provider;
		this.incoming = incoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		final Reporting reporting = Reporting.newIncomingRepository();
		Optional<Container<T>> container = Optional.empty();
		int waiting = 1;
		do {
			logger.debug("Provider waiting for {} ms", waiting);
			Thread.sleep(waiting);
			container = Optional.ofNullable(provider.pull()).orElseGet(Optional::empty);
			container.ifPresent(c -> {
				try {
					if (incoming.push(Optional.of(c))) {
						c.setReference(++currentContainerId).setSendDate(new Date());
						reporting.incrementContainersNumber().incrementItemsNumber(c.getItems().size());
						logger.debug("Pushed to incoming {} \nIncoming stock = {}", c, incoming.size());
					} else {
						c.setReference(++currentContainerId).setSendDate(new Date());
						String message = String.format("Pushing to incoming fails %s \nIncoming stock = %d", c,
								incoming.size());
						throw new Exception(message);
					}
				} catch (Exception e) {
					reporting.addReport(e.getMessage());
					logger.warn("Cannot push container to incoming repository: " + c, e);
				}
			});
			incoming.isPushable(container.isPresent());
		} while (container.isPresent());
		logger.info("Worker ends successfully. {} ", reporting.stop());
		currentContainerId = 0L; // Initializing this ID
		return reporting;
	}
}
