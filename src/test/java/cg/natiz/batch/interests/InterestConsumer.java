/**
 * 
 */
package cg.natiz.batch.interests;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.Container;
import cg.natiz.batch.pop.Pusher;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.CONSUMER)
@Interest
public class InterestConsumer implements Pusher<Long> {

	@Inject
	private Logger logger;

	@Override
	public boolean push(Optional<Container<Long>> container) throws Exception {
		if (container.isPresent() && container.get().getReference() == 5) {
			logger.warn("Rejecting : {}", container);
		}
		logger.debug(container.toString());
		return true;
	}
}
