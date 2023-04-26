package apriori.logic.datageneration;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import apriori.logic.algorithm.AbstractDataBase;
import apriori.logic.algorithm.AprioriDataBase;
import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.algorithm.ItemSet;
import apriori.logic.algorithm.Row;

/**
 * class for transaction data set (horizontal format)
 *
 */
@Component
public class AssociationDataSet extends AbstractDataBase<String> {

	/**
	 * constructor
	 */
	public AssociationDataSet() {

	}

	/**
	 * constructor
	 * 
	 * @param associationDB array association rows (transactions in database)
	 */
	public AssociationDataSet(AssociationRow[] associationDB) {
		this.rows = associationDB;
	}

	/**
	 * constructor
	 * 
	 * @param associationDB array association rows (transactions in database)
	 * @param min_sup       minimum support for apriori
	 */
	public AssociationDataSet(AssociationRow[] associationDB, int min_sup) {
		this.rows = associationDB;
		this.min_support = min_sup;
	}

	/**
	 * for displaying interim results in tabular format (for development purposes
	 * only)
	 */
	public void displayTable() {
		System.out.println("Table: Dataset for Association Analysis (horizontal format)");
		System.out.println("_____________________________________________________________________________");
		System.out.printf("%30s %30s", "TID", "Items");
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------");
		for (Row<String> ar : rows) {
			System.out.format("%30s %30s", ar.getIndicator(), ar.getItemset().toString());
			System.out.println();
		}
		System.out.println("_____________________________________________________________________________");
	}

	/**
	 * for calculating L1 in apriori algorithm
	 * 
	 * @return AprioriDataBase interim result in apriori algorithm (1-itemset)
	 */
	public AprioriDataBase getLOne() {
		List<String> distinctList = getUniqueElementsFromRows();
		AprioriDataBaseRow[] rows = new AprioriDataBaseRow[distinctList.size()];
		for (int i = 0; i < distinctList.size(); i++) {
			ItemSet set = ItemSet.createItemSet(new String[] { distinctList.get(i) });
			AprioriDataBaseRow adbr = AprioriDataBaseRow.createDataBaseRow(set);
			rows[i] = adbr;
		}
		AprioriDataBase adb = new AprioriDataBase(rows);
		return adb;
	}

	public AssociationRow[] getAssociationRows() {
		return (AssociationRow[]) rows;
	}

	@Override
	public String toString() {
		return "AssociationDataSet [associationDB=" + Arrays.toString(rows) + "]";
	}

}
