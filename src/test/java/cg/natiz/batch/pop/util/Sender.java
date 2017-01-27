/**
 * 
 */
package cg.natiz.batch.pop.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.util.Puller;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.PROVIDER)
@Savings
public class Sender implements Puller<String> {
	
	private static final int maxCount = 1000;
	private static final int size = 100;

	private static AtomicInteger count = new AtomicInteger(0);

	@Inject
	private Logger logger;

	@SuppressWarnings("unchecked")
	@Override
	public Container<String> pull() throws Exception {

		if (!hasMoreData()) {
			logger.info("Meaning no data anymore");
			return null;
		}
		Container<String> container = Container.newInstance(Container.class);
		String token = null;
		for (int i = 0; i < size; i++) {
			token = "" + ThreadLocalRandom.current().nextInt(size);
			if (count.get() == 5 && i == 5) {
				logger.info("Adding wrong data, this item will be rejected");
				token += "A";
			}
			container.add(token);
		}
		count.incrementAndGet();
		return container;
	}

	private boolean hasMoreData() {
		return count.get() < maxCount;
	}

}
