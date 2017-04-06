/**
 * 
 */
package cg.natiz.batch.interests;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.Processor;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * Processing an amount in string type and compute its value plus 5-15 %
 * 
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.PROCESSOR)
@Interest
public class InterestProcessor implements Processor<String, Long> {

	@Inject
	private Logger logger;

	@Override
	public Optional<Long> process(Optional<String> entity) throws Exception {
		logger.debug("Processing String {} to Long value, plus 5-15 %", entity);
		Long value = Long.valueOf(entity.get());
		value = (long) Math.ceil(value * (1 + (ThreadLocalRandom.current().nextInt(5, 15) / 100)));
		return Optional.of(value);
	}
}
