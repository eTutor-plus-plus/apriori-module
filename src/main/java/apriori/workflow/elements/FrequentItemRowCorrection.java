package apriori.workflow.elements;

import java.util.Arrays;
import java.util.Objects;

/**
 * class for row for apriori evaluation table (frequent items)
 */
public class FrequentItemRowCorrection {

	private boolean counted;

	private int count;
	private String[] items;

	private String countOK;// convention: yes, no
	private String itemsOK;// convention: yes, no

	/**
	 * constructor
	 */
	public FrequentItemRowCorrection() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param count	support count row
	 * @param items	items row
	 */
	public FrequentItemRowCorrection(int count, String[] items) {
		this.count = count;
		this.items = items;
		this.counted = false;
	}

	public boolean getCounted() {
		return counted;
	}

	public void setCounted(boolean counted) {
		this.counted = counted;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	public String getCountOK() {
		return countOK;
	}

	public void setCountOK(String countOK) {
		this.countOK = countOK;
	}

	public String getItemsOK() {
		return itemsOK;
	}

	public void setItemsOK(String itemsOK) {
		this.itemsOK = itemsOK;
	}

	@Override
	public String toString() {
		return "FrequentItemRowCorrection [counted=" + counted + ", count=" + count + ", items="
				+ Arrays.toString(items) + ", countOK=" + countOK + ", itemsOK=" + itemsOK + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(items);
		result = prime * result + Objects.hash(count, countOK, itemsOK);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FrequentItemRowCorrection other = (FrequentItemRowCorrection) obj;
		return count == other.count && Objects.equals(countOK, other.countOK) && Arrays.equals(items, other.items)
				&& Objects.equals(itemsOK, other.itemsOK);
	}

	/**
	 * adapted equals function for apriori evaluation
	 * 
	 * @param obj	object
	 * @return true, false
	 */
	public boolean equalsForDublets(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FrequentItemRowCorrection other = (FrequentItemRowCorrection) obj;
		return Arrays.equals(items, other.items) && Objects.equals(itemsOK, other.itemsOK);
	}

	/**
	 * for deep copy row
	 * 
	 * @param firc	FrequentItemRowCorrection
	 * @return FrequentItemRowCorrection
	 */
	public FrequentItemRowCorrection deepCopy(FrequentItemRowCorrection firc) {
		FrequentItemRowCorrection copy = new FrequentItemRowCorrection();
		copy.setItems(items);
		copy.setCount(count);
		copy.setItemsOK(itemsOK);
		copy.setCountOK(countOK);
		copy.setCounted(counted);
		return copy;
	}
}
