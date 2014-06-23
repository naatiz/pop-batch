package cg.natiz.batch.pop.util;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.slf4j.Logger;



/**
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 * 
 */
@SuppressWarnings("serial")
public class Repository<T extends Serializable> implements Serializable,
		Puller<T>, Pusher<T> {

	public static final int DEFAULT_QUEUE_TIMEOUT = 200; // millisecond

	@Inject
	private static Logger logger;

	private BlockingQueue<Container<T>> store = new LinkedBlockingQueue<Container<T>>(
			50);
	private AtomicBoolean closing = new AtomicBoolean(false);
	private AtomicLong reference = new AtomicLong(0);

	/**
	 * The repository is closed or not
	 * 
	 * @return true if the repository is closed
	 */
	public boolean isClosed() {
		return this.closing.get();
	}

	/**
	 * Close the repository activities
	 * 
	 * @return true if the repository is already closed, false if not
	 */
	public boolean close() {
		return this.closing.getAndSet(true);
	}

	/**
	 * The repository is open or not
	 * 
	 * @return true if the repository is open
	 */
	public boolean isOpen() {
		return !this.isClosed() || !this.store.isEmpty();
	}

	public Long getReference() {
		return reference.incrementAndGet();
	}

	public int size() {
		return this.store.size();
	}

	@Override
	public Container<T> pull() throws InterruptedException {
		return this.store.poll(DEFAULT_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean push(Container<T> container) throws InterruptedException {
		return this.store.offer(container, DEFAULT_QUEUE_TIMEOUT,
				TimeUnit.MILLISECONDS);
	}
}
