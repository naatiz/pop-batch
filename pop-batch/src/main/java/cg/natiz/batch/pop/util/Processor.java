/**
 * 
 */
package cg.natiz.batch.pop.util;

import java.io.Serializable;

/**
 * @author natiz
 * 
 */
public interface Processor<T1, T2> extends Serializable {

	/**
	 * Process the received entity T1 and generate a new one T2
	 * 
	 * @param entity
	 *            entity to be processed
	 * @return an other processed entity
	 * @throws Exception if the operation fails
	 */
	public T2 doProcess(T1 entity) throws Exception;
}
