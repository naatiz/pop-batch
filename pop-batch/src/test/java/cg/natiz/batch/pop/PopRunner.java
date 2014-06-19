package cg.natiz.batch.pop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import cg.natiz.batch.pop.IncomingWorker;
import cg.natiz.batch.pop.OutcomingWorker;
import cg.natiz.batch.pop.ProcessorWorker;
import cg.natiz.batch.pop.Repository;
import cg.natiz.batch.pop.util.Poller;
import cg.natiz.batch.pop.util.Processor;
import cg.natiz.batch.pop.util.Pusher;

import javax.inject.Named;

public class PopRunner {

	private static final int DEFAULT_SLEEP_TIME = 5;// milliseconds

	@Inject
	private Logger logger;

	@Inject 
	@Named("sender")
	private Poller<String> sender;
	@Inject
	private Processor<String, Long> processor;	
	@Inject
	@Named("recipient")
	private Pusher<Long> recipient;

	@Inject
	@Named("incoming")
	Repository<String> incoming;
	@Inject
	@Named("outcoming")
	Repository<Long> outcoming;

	@Inject
	@Named("incomingWorker")
	private IncomingWorker<String> incomingWorker;
	@Inject
	@Named("processorWorker")
	private ProcessorWorker<String, Long> processorWorker;
	@Inject
	@Named("outcomingWorker")
	private OutcomingWorker<Long> outcomingWorker;

	public void execute(@Observes ContainerInitialized event,
			@Parameters List<String> parameters) throws InterruptedException,
			ExecutionException {
		if (!parameters.contains("PopRunner")) {
			logger.warn("PopRunner is missing!");
			return;
		}
		logger.info("Batch starting ... ");
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		Set<Callable<String>> callables = new HashSet<Callable<String>>(3);
		callables.add(incomingWorker.addSender(sender).addRepository(
				incoming.setThreadSleep(DEFAULT_SLEEP_TIME)));
		callables.add(processorWorker
				.addProcessor(processor)
				.addIncomingRepository(incoming)
				.addOutcomingRepository(
						outcoming.setThreadSleep(DEFAULT_SLEEP_TIME)));
		callables.add(outcomingWorker.addRecipient(recipient)
				.addRepository(
						outcoming.setThreadSleep(DEFAULT_SLEEP_TIME)));

		List<Future<String>> futures = executorService.invokeAll(callables);
		for (Future<String> future : futures) {
			logger.info("future.get = " + future.get());
		}
		executorService.shutdown();
		logger.info("End of the batch !");
	}
}