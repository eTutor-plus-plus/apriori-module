package apriori.workflow.elements;

import java.util.Arrays;

/**
 * class for dataset row data base format
 */
public class HorizontalRow {

	private String id;
	private String[] items;

	/**
	 * constructor
	 */
	public HorizontalRow() {
	}

	/**
	 * constructor
	 * @param id	id row
	 * @param items	items row
	 */
	public HorizontalRow(String id, String[] items) {
		this.id = id;
		this.items = items;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "HorizontalRow [id=" + id + ", items=" + Arrays.toString(items) + "]";
	}

}
