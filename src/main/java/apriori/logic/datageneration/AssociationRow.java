package apriori.logic.datageneration;

import apriori.logic.algorithm.ItemSet;
import apriori.logic.algorithm.Row;

/**
 * class for row in transaction data set
 *
 */
public class AssociationRow extends Row<String> {

	/**
	 * constructor
	 * 
	 * @param transactionID ID Transaction
	 * @param itemSet       Item set
	 */
	public AssociationRow(String transactionID, ItemSet itemSet) {
		this.itemset = itemSet;
		this.indicator = transactionID;
	}

}
