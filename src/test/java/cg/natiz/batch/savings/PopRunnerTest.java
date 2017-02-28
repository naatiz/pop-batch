package cg.natiz.batch.savings;

import static org.junit.Assert.assertTrue;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopRunnerTest {
	private static final Logger logger = LoggerFactory.getLogger(PopRunnerTest.class);

	@Test
	public void execute() throws Exception {
		// Revival = relance ou reprise
		logger.debug("Running options : -R=Reporting -M=Monitoring -V=Revival");
		StartMain.main(new String[] { "-RMV" });
		assertTrue(true);
	}
}