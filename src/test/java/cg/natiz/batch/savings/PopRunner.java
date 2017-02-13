package cg.natiz.batch.savings;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import cg.natiz.batch.pop.util.PopProperties;
import cg.natiz.batch.pop.Pop;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;
import cg.natiz.batch.pop.util.PopConfig;
import cg.natiz.batch.pop.util.Processor;
import cg.natiz.batch.pop.util.Puller;
import cg.natiz.batch.pop.util.Pusher;

public class PopRunner {

	@Inject
	private Logger logger;

	@Inject
	@PopConfig("pop.cfg")
	private PopProperties config;
	
	private Pop<String, Long> savings;

	@Inject
	@SuppressWarnings("unchecked")
	public void initSavings(
			@Savings @Controller(ControllerType.PROVIDER) final Puller<String> sender,
			@Savings @Controller(ControllerType.PROCESSOR) final Processor<String, Long> processor,
			@Savings @Controller(ControllerType.CONSUMER) final Pusher<Long> recipient) {
		this.savings = Pop.newInstance(Pop.class).setProvider(sender)
				.setProcessor(processor).setConsumer(recipient);
	}

	/**
	 * 
	 * @param event
	 * @param parameters
	 * @throws Exception
	 */
	public void execute(@Observes ContainerInitialized event,
			@Parameters List<String> parameters) throws Exception {
		if (parameters.isEmpty() || !parameters.get(0).contains("-RMV")) {
			logger.warn("R, M or V option parameter is missing!");
			return;
		}

		logger.info("Batch starting ... ");
		savings.execute(config);
		logger.info("Batch execution ends successfully!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// StartMain.main(args);
		// Revival = relance ou reprise
		// logger.debug("Running options : -R=Reporting -M=Monitoring -V=Revival ");
		StartMain.main(new String[] { "-RMV" });
	}
}