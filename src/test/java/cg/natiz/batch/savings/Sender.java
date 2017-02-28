/**
 * 
 */
package cg.natiz.batch.savings;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.Container;
import cg.natiz.batch.pop.Pop;
import cg.natiz.batch.pop.Puller;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.PROVIDER)
@Savings
public class Sender implements Puller<String> {

	/* maximum number of containers */
	private final long MAX_CONTAINER_NUMBER = 1000;
	/* maximum number of elements inside a container */
	private final int MAX_CONTAINER_SIZE = 100;

	/* current container number */
	private final AtomicLong CURRENT_CONTAINER_NUMBER = new AtomicLong(0);

	@Inject
	private Logger logger;

	@SuppressWarnings("unchecked")
	@Override
	public Container<String> pull() throws Exception {

		if (!hasMoreData()) {
			logger.info("No data anymore ...");
			return null;
		}
		Container<String> container = Pop.newInstance(Container.class);
		container.setReference(CURRENT_CONTAINER_NUMBER.get());
		String token = null;
		for (int i = 0; i < MAX_CONTAINER_SIZE; i++) {
			token = "" + ThreadLocalRandom.current().nextInt(MAX_CONTAINER_SIZE);
			if (CURRENT_CONTAINER_NUMBER.get() == 5 && i == 5) {
				token += "A";
				logger.warn("Adding wrong data {}, this {} will be rejected", token, container);
			}
			container.add(token);
		}
		CURRENT_CONTAINER_NUMBER.incrementAndGet();
		return container;
	}

	private boolean hasMoreData() {
		return CURRENT_CONTAINER_NUMBER.get() < MAX_CONTAINER_NUMBER;
	}

}
