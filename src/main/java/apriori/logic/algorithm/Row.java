package apriori.logic.algorithm;

/**
 * abstract class for table rows
 *
 * @param <T>	generic number variable
 */
public abstract class Row<T> {

	protected T indicator;
	protected ItemSet itemset;

	public T getIndicator() {
		return indicator;
	}

	void setIndicator(T indicator) {
		this.indicator = indicator;
	}

	public ItemSet getItemset() {
		return itemset;
	}

	void setItemset(ItemSet itemset) {
		this.itemset = itemset;
	}

	@Override
	public String toString() {
		return "Row [indicator=" + indicator + ", itemset=" + itemset + "]";
	}

}
