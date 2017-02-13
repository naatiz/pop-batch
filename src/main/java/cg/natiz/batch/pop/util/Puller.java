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
public interface Puller<T extends Serializable> extends Serializable {
	/**
	 * Pull a container from a store
	 * 
	 * @return a pulled container
	 * @throws Exception
	 *             if the pull operation fails
	 */
	public Container<T> pull() throws Exception;
}
