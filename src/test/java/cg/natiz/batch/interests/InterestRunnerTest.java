package cg.natiz.batch.interests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.Pop;

public class InterestRunnerTest {
	private static final Logger logger = LoggerFactory.getLogger(InterestRunnerTest.class);

	@Test
	public void execute() throws Exception {
		// Revival = relance ou reprise
		logger.debug("Running options : -M=Monitoring -R=Revival");
		assertTrue(Pop.main(new String[] { "-MR" }));
	}
}