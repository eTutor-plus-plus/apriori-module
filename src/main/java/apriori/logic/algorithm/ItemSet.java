package apriori.logic.algorithm;

import java.util.Arrays;

/**
 * class for storing set of elements of transactions
 *
 */
public class ItemSet {

	private String[] items;// for storing the elements of a transaction

	/**
	 * constructor
	 * 
	 * @param items array of items
	 */
	public ItemSet(String[] items) {
		Arrays.sort(items);
		this.items = items;
	}

	/**
	 * constructor
	 * 
	 * @param itemSet item set
	 */
	ItemSet(ItemSet itemSet) {
		this.items = itemSet.getItems().clone();
	}

	/**
	 * for getting length of items set
	 * 
	 * @return length
	 */
	int getLength() {
		return items.length;
	}

	/**
	 * for creating items set
	 * 
	 * @param itemSet array of items
	 * @return items set item set
	 */
	public static ItemSet createItemSet(String[] itemSet) {
		Arrays.sort(itemSet);
		return new ItemSet(itemSet);
	}

	@Override
	public String toString() {
		return Arrays.toString(items);
	}

	public String[] getItems() {
		return items;
	}

	void setItems(String[] items) {
		this.items = items;
	}

}
