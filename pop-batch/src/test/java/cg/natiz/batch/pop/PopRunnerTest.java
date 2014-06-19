package cg.natiz.batch.pop;

import static org.junit.Assert.assertTrue;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Test;

public class PopRunnerTest {
	@Test
	public void execute() {
		StartMain.main(new String[] {"PopRunner", "param2=2", "param3=3", "param4=4"});
		assertTrue(true);
	}
}