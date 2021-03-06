/**
 * 
 */
package cg.naatiz.batch.pop;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author natiz
 * 
 */
public interface Processor<T1 extends Serializable, T2 extends Serializable> extends Serializable {

	/**
	 * Process the received entity T1 and generate a new one T2
	 * 
	 * @param entity
	 *            entity to be processed
	 * @return an other processed entity
	 * @throws Exception
	 *             if the operation fails
	 */
	public Optional<T2> process(final Optional<T1> entity) throws Exception;
}
