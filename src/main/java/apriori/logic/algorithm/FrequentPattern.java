package apriori.logic.algorithm;

import java.util.ArrayList;

/**
 * class for storing frequent patterns result
 *
 */
public class FrequentPattern {

	ArrayList<AprioriDataBaseRow> frequentPatterns;

	/**
	 * Constructor
	 */
	public FrequentPattern() {
		frequentPatterns = new ArrayList<AprioriDataBaseRow>();
	}

	/**
	 * for adding single row
	 * 
	 * @param row apriori database row
	 */
	public void addRow(AprioriDataBaseRow row) {
		frequentPatterns.add(row);
	}

	/**
	 * for adding multiple rows
	 * 
	 * @param array of apriori database rows
	 */
	void addRows(AprioriDataBaseRow[] adb) {
		for (AprioriDataBaseRow row : adb) {
			frequentPatterns.add(row);
		}
	}

	public ArrayList<AprioriDataBaseRow> getFrequentPatterns() {
		return frequentPatterns;
	}

	public void setFrequentPatterns(ArrayList<AprioriDataBaseRow> frequentPatterns) {
		this.frequentPatterns = frequentPatterns;
	}

	/**
	 * for displaying interim results in tabular format (for development purposes
	 * only)
	 */
	public void displayTable() {
		System.out.println("Table: FrequentPatterns");
		System.out.println("_____________________________________________________________________________");
		System.out.printf("%30s %30s", "ItemSet", "Support Count");
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------");
		for (AprioriDataBaseRow r : frequentPatterns) {
			System.out.format("%30s %30s", r.getItemset(), r.getIndicator());
			System.out.println();
		}
		System.out.println("_____________________________________________________________________________");
	}

}
