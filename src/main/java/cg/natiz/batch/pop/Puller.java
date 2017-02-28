/**
 * 
 */
package cg.natiz.batch.pop;

import java.io.Serializable;

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
