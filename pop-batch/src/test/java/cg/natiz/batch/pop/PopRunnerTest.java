package cg.natiz.batch.pop;

import static org.junit.Assert.assertTrue;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopRunnerTest {
	private static final Logger logger = LoggerFactory.getLogger(PopRunnerTest.class);
	
	@Test
	public void execute() {
		// Revival = relance ou reprise
		logger.debug("Running options : -R=Reportiong -M=Monitoring -V=Revival ");
		StartMain.main(new String[] {"-R", "-M", "-V"});
		assertTrue(true);
	}
}