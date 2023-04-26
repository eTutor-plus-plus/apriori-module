package apriori.workflow.elements;

import org.springframework.stereotype.Component;

/**
 * class for row for interim results tables
 */
@Component
public class TrainingRow {

	private String[] items;
	private int supportCount;

	/**
	 * constructor
	 */
	public TrainingRow() {

	}

	/**
	 * constructor
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items in row
	 */
	public TrainingRow(String[] items, int supportCount) {
		this.items = items;
		this.supportCount = supportCount;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	public int getSupportCount() {
		return supportCount;
	}

	public void setSupportCount(int supportCount) {
		this.supportCount = supportCount;
	}

	@Override
	public String toString() {
		return "ResultRow [items=" + items + ", supportCount=" + supportCount + "]";
	}
}
