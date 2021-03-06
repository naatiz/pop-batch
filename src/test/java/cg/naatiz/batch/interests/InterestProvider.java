/**
 * 
 */
package cg.naatiz.batch.interests;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.naatiz.batch.pop.Container;
import cg.naatiz.batch.pop.Pop;
import cg.naatiz.batch.pop.Puller;
import cg.naatiz.batch.pop.util.Controller;
import cg.naatiz.batch.pop.util.ControllerType;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.PROVIDER)
@Interest
public class InterestProvider implements Puller<String> {

	/* maximum number of containers */
	private static final long MAX_CONTAINER_NUMBER = 1000;
	/* maximum number of elements inside a container */
	private static final int MAX_CONTAINER_SIZE = 1000;

	/* current container number */
	private static final AtomicLong CURRENT_CONTAINER_NUMBER = new AtomicLong(0);

	@Inject
	private Logger logger;

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Container<String>> pull() throws Exception {

		if (!hasMoreData()) {
			logger.info("No data anymore...");
			return Optional.empty();
		}
		Container<String> container = Pop.newInstance(Container.class);
		String amount = null;
		for (int i = 0; i < MAX_CONTAINER_SIZE; i++) {
			amount = "" + ThreadLocalRandom.current().nextInt(MAX_CONTAINER_SIZE);
			if (CURRENT_CONTAINER_NUMBER.get() == 5 && i == 5) {
				amount += "A";
				logger.warn("Adding wrong data {}, this {} will be rejected", amount, container);
			}
			container.addItem(amount);
		}
		CURRENT_CONTAINER_NUMBER.incrementAndGet();
		return Optional.of(container);
	}

	private boolean hasMoreData() {
		return CURRENT_CONTAINER_NUMBER.get() < MAX_CONTAINER_NUMBER;
	}
}
