package apriori.logic.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * abstract class for association data set and apriori data base
 *
 */
public abstract class AbstractDataBase<T> {

	protected Row<T>[] rows;
	protected int min_support;

	/**
	 * for getting unique elements
	 * 
	 * @return list items
	 */
	public List<String> getUniqueElementsFromRows() {
		ArrayList<String> uniqueElements = getAllItems();
		List<String> distinctList = uniqueElements.stream().distinct().sorted().collect(Collectors.toList());
		return distinctList;
	}

	/**
	 * for counting unique elements
	 * 
	 * @return list with item counts
	 */
	public List<Integer> getCountUniqueElements() {
		List<String> distinctList = getUniqueElementsFromRows();
		ArrayList<String> uniqueElements = getAllItems();
		ArrayList<Integer> occurances = new ArrayList<Integer>();
		for (String element : distinctList) {
			occurances.add(Collections.frequency(uniqueElements, element));
		}
		return occurances;
	}

	/**
	 * for getting all items
	 * 
	 * @return list with items
	 */
	private ArrayList<String> getAllItems() {
		ArrayList<String> uniqueElements = new ArrayList<String>();
		for (Row<T> r : rows) {
			uniqueElements.addAll(Arrays.asList(r.getItemset().getItems()));
		}
		return uniqueElements;
	}

	public int getMin_support() {
		return min_support;
	}

	public void setMin_support(int min_support) {
		this.min_support = min_support;
	}

	public Row<T>[] getRows() {
		return rows;
	}

	void setRows(Row<T>[] rows) {
		this.rows = rows;
	}

}
