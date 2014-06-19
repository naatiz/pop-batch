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
public interface Poller<T extends Serializable> extends Serializable {
	/**
	 * Receiver a container from a store
	 * 
	 * @return a delivered container
	 * @throws Exception
	 *             if the poll operation fails
	 */
	public Container<T> poll() throws Exception;
}
