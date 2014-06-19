/**
 * 
 */
package cg.natiz.batch.pop.util;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.util.Processor;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
public class Converter implements Processor<String, Long> {

	@Inject
	private Logger logger;
	
	@Override
	public Long doProcess(String entity)
			throws Exception {
		//logger.debug("Converting String {} to Long ", entity);
		Long value = Long.valueOf(entity);
		return value;
	}
}
