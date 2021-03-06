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
public interface Puller<T extends Serializable> extends Serializable {
	/**
	 * Pull a container from a store
	 * 
	 * @return a pulled container
	 * @throws Exception
	 *             if the pull operation fails
	 */
	public Optional<Container<T>> pull() throws Exception;
}
