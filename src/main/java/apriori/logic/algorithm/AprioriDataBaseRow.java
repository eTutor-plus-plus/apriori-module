package apriori.logic.algorithm;

/**
 * class for row in apriori data set
 *
 */
public class AprioriDataBaseRow extends Row<Integer> {

	/**
	 * constructor
	 * 
	 * @param itemset      item set
	 * @param supportCount support count for items
	 */
	public AprioriDataBaseRow(ItemSet itemset, int supportCount) {
		this.itemset = itemset;
		this.indicator = supportCount;
	}

	/**
	 * constructor
	 * 
	 * @param itemset item set
	 */
	AprioriDataBaseRow(ItemSet itemset) {
		this.itemset = itemset;
	}

	/**
	 * constructor
	 * 
	 * @param row apriori database row
	 */
	AprioriDataBaseRow(AprioriDataBaseRow row) {
		this.itemset = new ItemSet(row.getItemset());
		this.indicator = row.getIndicator();
	}

	/**
	 * for creating a database row
	 * 
	 * @param itemset item set
	 * @return apriori data base row (items only)
	 */
	public static AprioriDataBaseRow createDataBaseRow(ItemSet itemset) {
		return new AprioriDataBaseRow(itemset);
	}

	/**
	 * for creating a database row
	 * 
	 * @param itemset      item set
	 * @param supportCount support count for items
	 * @return apriori data base row
	 */
	public static AprioriDataBaseRow createDataBaseRow(ItemSet itemset, int supportCount) {
		return new AprioriDataBaseRow(itemset, supportCount);
	}

	@Override
	public String toString() {
		return "DataBaseRow [supportCount=" + getIndicator() + ", itemset=" + itemset + "]";
	}

}
