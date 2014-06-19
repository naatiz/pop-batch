/**
 * 
 */
package cg.natiz.batch.pop.util;

import java.io.Serializable;

import cg.natiz.batch.pop.Container;


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
	public boolean push(Container<T> container) throws Exception;

}
