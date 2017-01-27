/**
 * 
 */
package cg.natiz.batch.pop.util;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.util.Pusher;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.CONSUMER)
@Savings
public class Recipient implements Pusher<Long> {

	@Inject
	private Logger logger;
	
	@Override
	public boolean push(Container<Long> container) throws Exception {
		if (container.getReference() == 5) {
			logger.warn("Rejecting : {}", container);
		}
		logger.debug(container.toString());
		return true;
	}
}