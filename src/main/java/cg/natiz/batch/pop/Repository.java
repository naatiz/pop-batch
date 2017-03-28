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

	public int size();
}
