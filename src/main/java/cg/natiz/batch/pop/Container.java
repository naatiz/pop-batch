/**
 * 
 */
package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
public class Container<T> implements Serializable {

	private long reference;
	private Date receiptDate;
	private Date sendDate;

	private Date startProcessDate;
	private Date endProcessDate;

	private List<T> items = Lists.newArrayList();

	protected Container() {
	}

	public Container<T> addItem(T element) {
		this.items.add(element);
		return this;
	}

	public Container<T> addAllItems(Collection<T> collection) {
		this.items.addAll(collection);
		return this;
	}

	/**
	 * An set with the same content as the container
	 * 
	 * @return a set of the items
	 */
	public List<T> getItems() {
		return new ArrayList<T>(this.items);
	}

	public int size() {
		return items.size();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public void clear() {
		items.clear();
	}

	public long getReference() {
		return reference;
	}

	public Container<T> setReference(long reference) {
		this.reference = reference;
		return this;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public Container<T> setSendDate(Date sendDate) {
		this.sendDate = sendDate;
		return this;
	}

	public Date getStartProcessDate() {
		return startProcessDate;
	}

	public Container<T> setStartProcessDate(Date startProcessDate) {
		this.startProcessDate = startProcessDate;
		return this;
	}

	protected Date getEndProcessDate() {
		return endProcessDate;
	}

	public Container<T> setEndProcessDate(Date endProcessDate) {
		this.endProcessDate = endProcessDate;
		return this;
	}

	protected Date getReceiptDate() {
		return receiptDate;
	}

	public Container<T> setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (reference ^ (reference >>> 32));
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Container other = (Container) obj;
		if (reference != other.reference)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer().append("Container [Reference=").append(getReference())
				.append(", SendDate=").append(getSendDate()).append(", StartProcessDate=").append(getStartProcessDate())
				.append(", EndProcessDate=").append(getEndProcessDate()).append(", ReceiptDate=")
				.append(getReceiptDate()).append(", size=").append(size()).append("]");
		return sb.toString();
	}
}
