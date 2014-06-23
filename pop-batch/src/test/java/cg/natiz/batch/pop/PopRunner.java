package cg.natiz.batch.pop;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import cg.natiz.batch.pop.util.Cfg;
import cg.natiz.batch.pop.util.PopConfiguration;


@PopConfiguration("/pop.cfg") 
public class PopRunner {

	@Inject
	private Logger logger;

	@Inject 
	private Cfg cfg;

	@Inject
	@Named("pop")
	private Pop<String, Long> pop;

	/**
	 * 
	 * @param event
	 * @param parameters
	 * @throws Exception
	 */
	public void execute(@Observes ContainerInitialized event,
			@Parameters List<String> parameters) throws Exception {
		if (parameters.isEmpty() || !parameters.get(0).contains("-R")) {
			logger.warn("-R option parameter is missing!");
			return;
		}

		logger.info("Batch starting ... ");
		pop.setCfg(cfg).build().execute();
		logger.info("Batch execution ends successfully!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//StartMain.main(args); 
		
		// Revival = relance ou reprise
		//logger.debug("Running options : -R=Reportiong -M=Monitoring -V=Revival ");
		StartMain.main(new String[] {"-R", "-M", "-V"});
	}
}