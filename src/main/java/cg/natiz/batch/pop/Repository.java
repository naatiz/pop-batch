package cg.natiz.batch.pop;

import java.io.Serializable;

/**
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 * 
 */
public interface Repository<T extends Serializable> extends Serializable, Puller<T>, Pusher<T> {

	public static final int DEFAULT_QUEUE_TIMEOUT = 200; // millisecond

	/**
	 * Size Repository 
	 * @return current size repository
	 */
	public int size();
	
	/**
	 * Is the repository open
	 * @return true if open
	 */
	public boolean isOpen();
	
	/**
	 * To allow or not the repository is pushable
	 * @param pushable true if the repository can be pushed or false
	 */
	public  void setPushable(boolean pushable);
}
