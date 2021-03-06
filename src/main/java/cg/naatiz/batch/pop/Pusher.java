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
public interface Pusher<T extends Serializable> extends Serializable {

	/**
	 * 
	 * Deliver a container to any store
	 * 
	 * @param container
	 *            sent container
	 * @return true if the delivery is completed
	 * @throws Exception
	 *             if the push operation fails
	 */
	public boolean push(final Optional<Container<T>> container) throws Exception;

}
