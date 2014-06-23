/**
 * 
 */
package cg.natiz.batch.pop.util;

import java.io.Serializable;

import javax.inject.Inject;

import org.slf4j.Logger;

import cg.natiz.batch.pop.util.Processor;

/**
 * Default processor simply returning the entity argument without any processing
 * @author natiz
 * 
 * @param <T>
 *            original managed data type
 * 
 */
@SuppressWarnings("serial")
@Controller(ControllerType.PROCESSOR)
public class DefaultProcessor <T extends Serializable> implements Processor<T, T> {

	@Inject
	private Logger logger;

	@Override
	public T process(T entity) throws Exception {
		logger.debug("Return the entity argument without any processing");
		return entity;
	}
}
