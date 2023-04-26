package apriori.workflow.elements;

import java.util.Arrays;

import org.springframework.stereotype.Component;

/**
 * class for row for frequent items table
 */
@Component
public class FrequentItemRow {

	private int count;
	private String[] items;

	/**
	 * constructor
	 */
	public FrequentItemRow() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param supportCount support count row
	 * @param items	items row
	 */
	public FrequentItemRow(int supportCount, String[] items) {
		super();
		this.count = supportCount;
		this.items = items;
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

	@Override
	public String toString() {
		return "FrequentItemRow [count=" + count + ", items=" + Arrays.toString(items) + "]";
	}

}
