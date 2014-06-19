/**
 * 
 */
package cg.natiz.batch.pop.util;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import cg.natiz.batch.pop.Container;
import cg.natiz.batch.pop.util.Poller;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Named("sender")
public class Sender implements Poller<String> {
	private static final Random rand = new Random();
	private static final int maxCount = 1000;
	private static final int size = 1000;

	private static int count = 0;

	@Inject
	private Logger logger;

	@SuppressWarnings("unchecked")
	@Override
	public Container<String> poll() throws Exception {

		if (!hasMoreData()) {
			logger.info("Meaning no data anymore");
			return null;
		}
		Container<String> container = Container.newInstance(Container.class);
		String token = null;
		for (int i = 0; i < size; i++) {
			token = "" + rand.nextInt(size);
			if (count == 5 && i == 5) {
				logger.info("Adding wrong data, this item will be rejected");
				token += "A";
			}
			container.add(token);
		}
		count++;
		return container;
	}

	private boolean hasMoreData() {
		return count < maxCount;
	}

}
