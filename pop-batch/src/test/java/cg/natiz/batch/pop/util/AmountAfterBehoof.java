/**
 * 
 */
package cg.natiz.batch.pop.util;

import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.util.Processor;

/**
 * Processing an amount in string type and compute its value plus 5-15 %
 * 
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.PROCESSOR)
@Savings
public class AmountAfterBehoof implements Processor<String, Long> {

	@Inject
	private Logger logger;

	@Override
	public Long process(String entity) throws Exception {
		logger.debug("Processing String {} to Long value, plus 5-15 %", entity);
		Long value = Long.valueOf(entity);
		value = (long) Math.ceil(value
				* (1 + (ThreadLocalRandom.current().nextInt(5, 15) / 100)));
		return value;
	}
}
