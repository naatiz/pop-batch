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
import cg.natiz.batch.pop.Reporting;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;
import cg.natiz.batch.pop.util.ExecutionOption;

public class PopRunner {

	@Inject
	private Logger logger;

	@Inject
	// @PopConfig({"/pop.cfg"})
	private PopProperties popProperties;

	private Pop<String, Long> savings;

	@Inject
	@SuppressWarnings("unchecked")
	public void initSavings(@Savings @Controller(ControllerType.PROVIDER) final Puller<String> provider,
			@Savings @Controller(ControllerType.PROCESSOR) final Processor<String, Long> processor,
			@Savings @Controller(ControllerType.CONSUMER) final Pusher<Long> consumer) {

		this.savings = Pop.newInstance(Pop.class).addConfiguration(popProperties, ExecutionOption.NONE).build(provider,
				processor, consumer);
	}

	/**
	 * 
	 * @param event
	 * @param parameters
	 * @throws Exception
	 */
	public void execute(@Observes ContainerInitialized event, @Parameters List<String> parameters) throws Exception {

		// Revival = relance ou reprise
		logger.debug("Running options : -M=Monitoring -R=Revival");
		if (parameters.isEmpty() || !parameters.get(0).contains("-MR")) {
			throw new IllegalStateException("M or R option parameter is missing!");
		}

		logger.info("Batch savings starting ... ");
		List<Reporting> reportings = savings.start();
		logger.info("Reportings size: " + reportings.size());
		logger.info("Batch savings ends successfully!");
	}
}