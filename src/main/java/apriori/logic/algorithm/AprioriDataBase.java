package apriori.logic.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.datageneration.AssociationRow;

/**
 * class for storing the items sets and support counts during calculation
 *
 */
public class AprioriDataBase extends AbstractDataBase<String> {

	AprioriDataBaseRow[] db;
	int min_support;

	/**
	 * constructor
	 * 
	 * @param db      array of apriori database rows
	 * @param min_sup minimum support apriori algorithm
	 */
	AprioriDataBase(AprioriDataBaseRow[] db, int min_sup) {
		this.db = db;
		this.min_support = min_sup;
	}

	/**
	 * constructor
	 * 
	 * @param db array of apriori database rows
	 */
	public AprioriDataBase(AprioriDataBaseRow[] db) {
		this.db = db;
	}

	/**
	 * constructor
	 * 
	 * @param adb apriori database (AprioriDataBase)
	 */
	public AprioriDataBase(AprioriDataBase adb) {
		this.db = adb.getDb().clone();
		this.min_support = adb.getMin_support();
	}

	/**
	 * for joining L1 with its self
	 * 
	 * @return array of apriori database rows
	 */
	AprioriDataBaseRow[] joinLOnes() {
		int len = db.length;
		String[][] sets = new String[len][];
		for (int i = 0; i < db.length; i++) {
			sets[i] = db[i].getItemset().getItems();
		}
		ArrayList<String[]> res = new ArrayList<>();
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				res.add(new String[] { sets[i][0], sets[j][0] });
			}
		}
		AprioriDataBaseRow[] res2 = new AprioriDataBaseRow[res.size()];
		for (int i = 0; i < res.size(); i++) {
			String[] stringSets = new String[2];
			stringSets[0] = res.get(i)[0];
			stringSets[1] = res.get(i)[1];
			res2[i] = AprioriDataBaseRow.createDataBaseRow(new ItemSet(stringSets));
		}
		return res2;
	}

	/**
	 * for joining Lk (k>2) with its self
	 * 
	 * @param int number of items set (stage in apriori algorithm)
	 * @return array of apriori database rows
	 */
	AprioriDataBaseRow[] joiner(int k) {
		ArrayList<ItemSet> itemsets = getAllItemsets();
		String[][] data = new String[itemsets.size()][k - 1];
		for (int i = 0; i < itemsets.size(); i++) {
			data[i] = itemsets.get(i).getItems();
		}
		ArrayList<String[]> twoPartString = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {
			StringJoiner joiner = new StringJoiner(",");
			for (int j = 0; j < k - 1 - 1; j++) {
				joiner.add(data[i][j]);
			}
			String[] twoParts = new String[2];
			twoParts[0] = joiner.toString();
			twoParts[1] = data[i][k - 1 - 1];
			twoPartString.add(twoParts);
		}
		ArrayList<String[]> candidatesWithDublicates = new ArrayList<String[]>();
		for (String[] s : twoPartString) {
			for (String[] ss : twoPartString) {
				if (s[0].equals(ss[0])) {
					if (!s[1].equals(ss[1])) {
						StringJoiner joinerCandidate = new StringJoiner(",");
						joinerCandidate.add(s[0]);
						joinerCandidate.add(s[1]);
						joinerCandidate.add(ss[1]);
						String convertToString = joinerCandidate.toString();
						String[] candidate = convertToString.split(",");
						Arrays.sort(candidate);
						candidatesWithDublicates.add(candidate);
					}
				}
			}
		}

		ArrayList<String> candidatesChecking = new ArrayList<String>();
		ArrayList<String> candidates = new ArrayList<String>();
		for (String[] a : candidatesWithDublicates) {
			StringJoiner joinerCandidateSorted = new StringJoiner(",");
			for (String b : a) {
				joinerCandidateSorted.add(b);
			}
			candidatesChecking.add(joinerCandidateSorted.toString());
		}
		for (String s : candidatesChecking) {
			if (!candidates.contains(s)) {
				candidates.add(s);
			}
		}
		AprioriDataBaseRow[] res2 = new AprioriDataBaseRow[candidates.size()];
		for (int i = 0; i < candidates.size(); i++) {
			String[] candidate = candidates.get(i).split(",");
			res2[i] = AprioriDataBaseRow.createDataBaseRow(new ItemSet(candidate));
		}
		return res2;
	}

	/**
	 * for marking low support item sets
	 */
	void pruningAddMarker() {
		for (AprioriDataBaseRow adbr : db) {
			if (adbr.getIndicator() < min_support) {
				adbr.setIndicator(-1);
			}
		}
	}

	/**
	 * for pruning low support item sets
	 * 
	 * @return array of apriori database rows
	 */
	AprioriDataBaseRow[] pruningClean() {
		ArrayList<AprioriDataBaseRow> checkedRows = new ArrayList<>();
		for (AprioriDataBaseRow apdbr : db) {
			if (apdbr.getIndicator() >= min_support) {
				checkedRows.add(apdbr);
			}
		}
		AprioriDataBaseRow[] result = new AprioriDataBaseRow[checkedRows.size()];
		result = checkedRows.toArray(result);
		return result;
	}

	/**
	 * for adding support count
	 * 
	 * @param asd association data set (AssociationDataSet)
	 */
	void addSupportCount(AssociationDataSet asd) {
		for (AprioriDataBaseRow adr : db) {
			ItemSet items = adr.getItemset();
			adr.setIndicator(checkMembers(asd, items));
		}
	}

	/**
	 * for counting occurrences of item set
	 * 
	 * @param asd   association data set (AssociationDataSet)
	 * @param items item set
	 * @return occurrences of item set
	 */
	int checkMembers(AssociationDataSet asd, ItemSet items) {
		AssociationRow[] ar = asd.getAssociationRows();
		String check = "empty";
		int counter = 0;
		for (AssociationRow r : ar) {
			check = "empty";
			boolean check2;
			for (String s : items.getItems()) {
				check2 = false;
				String[] rr = r.getItemset().getItems();
				String tt = s;
				for (String rrrr : rr) {
					if (rrrr.equals(tt)) {
						check2 = true;
					}
				}
				if (check.equals("empty")) {
					check = String.valueOf(check2);
				} else if (check.equals(String.valueOf(true)) && check2 == true) {
					check = String.valueOf(true);
				} else if (check.equals(String.valueOf(false)) && check2 == true) {
					check = String.valueOf(false);
				} else {
					check = String.valueOf(false);
				}
			}
			if (check.equals(String.valueOf(true))) {
				counter = counter + 1;
			}
		}
		return counter;
	}

	/**
	 * for getting all items sets
	 * 
	 * @return list items sets
	 */
	public ArrayList<ItemSet> getAllItemsets() {
		ArrayList<ItemSet> itemsets = new ArrayList<>();
		for (AprioriDataBaseRow ap : db) {
			itemsets.add(ap.getItemset());
		}
		return itemsets;
	}

	public int getMin_support() {
		return min_support;
	}

	public void setMin_support(int min_support) {
		this.min_support = min_support;
	}

	public AprioriDataBaseRow[] getDb() {
		return db;
	}

	void setDb(AprioriDataBaseRow[] db) {
		this.db = db;
	}

	@Override
	public String toString() {
		return "AprioriDataBase [db=" + Arrays.toString(db) + "]";
	}

}
