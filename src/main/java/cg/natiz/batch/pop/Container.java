/**
 * 
 */
package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

	private List<T> content = new ArrayList<T>(100);

	protected Container() {
	}

	public Container<T> add(T element) {
		this.content.add(element);
		return this;
	}

	public Container<T> addAll(Collection<T> collection) {
		this.content.addAll(collection);
		return this;
	}

	/**
	 * An set with the same content as the container
	 * 
	 * @return a set of the same content
	 */
	public List<T> getContent() {
		return new ArrayList<T>(this.content);
	}

	public int size() {
		return content.size();
	}

	public boolean isEmpty() {
		return content.isEmpty();
	}

	public void clear() {
		content.clear();
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
