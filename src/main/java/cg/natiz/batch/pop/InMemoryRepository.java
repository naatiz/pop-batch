package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 * 
 */
@SuppressWarnings("serial")
public class InMemoryRepository<T extends Serializable> implements Repository<T> {

	private BlockingQueue<Optional<Container<T>>> store = new LinkedBlockingQueue<Optional<Container<T>>>(50);

	public int size() {
		return this.store.size();
	}

	@Override
	public Optional<Container<T>> pull() throws InterruptedException {
		return this.store.poll(DEFAULT_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean push(Optional<Container<T>> container) throws InterruptedException {
		return this.store.offer(container, DEFAULT_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS);
	}
}
