package cg.natiz.batch.savings;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import cg.natiz.batch.pop.util.PopProperties;
import cg.natiz.batch.pop.Pop;
import cg.natiz.batch.pop.Processor;
import cg.natiz.batch.pop.Puller;
import cg.natiz.batch.pop.Pusher;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

public class PopRunner {

	@Inject
	private Logger logger;

	@Inject
	// @PopConfig({"/pop.cfg"})
	private PopProperties config;

	private Pop<String, Long> savings;

	@Inject
	@SuppressWarnings("unchecked")
	public void initSavings(@Savings @Controller(ControllerType.PROVIDER) final Puller<String> sender,
			@Savings @Controller(ControllerType.PROCESSOR) final Processor<String, Long> processor,
			@Savings @Controller(ControllerType.CONSUMER) final Pusher<Long> recipient) {
		this.savings = Pop.newInstance(Pop.class).setProvider(sender).setProcessor(processor).setConsumer(recipient);
	}

	/**
	 * 
	 * @param event
	 * @param parameters
	 * @throws Exception
	 */
	public void execute(@Observes ContainerInitialized event, @Parameters List<String> parameters) throws Exception {
		if (parameters.isEmpty() || !parameters.get(0).contains("-RMV")) {
			throw new IllegalStateException("R, M or V option parameter is missing!");
		}

		logger.info("Batch starting ... ");
		savings.execute(config);
		logger.info("Batch execution ends successfully!");
	}
}