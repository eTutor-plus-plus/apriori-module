package apriori.logic.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import apriori.logic.datageneration.AssociationDataSet;

/**
 * class for calculating apriori algorithm
 *
 */
@Component
public class Apriori {

	AssociationDataSet associationDataSet;
	int minSupport;
	FrequentPattern frequentPatterns;// for storing final result
	ArrayList<InterimResults> interimResults;// for calculating interim results
	ArrayList<InterimResults> interimResultsStorage;// for storing interim results
	private List<InterimResults> interimResultsPrunedOnly = new ArrayList<>();// start for studentGeneratedDataset

	/**
	 * constructor
	 */
	public Apriori() {

	}

	/**
	 * constructor
	 * 
	 * @param associationDataSet  association data set (AssociationDataSet)
	 * @param minimumSupportCount minimum support for apriori algorithm
	 */
	public Apriori(AssociationDataSet associationDataSet, int minimumSupportCount) {
		this.associationDataSet = associationDataSet;
		this.minSupport = minimumSupportCount;
		frequentPatterns = new FrequentPattern();
		interimResults = new ArrayList<InterimResults>();
		interimResultsStorage = new ArrayList<InterimResults>();
	}

	public List<InterimResults> getInterimResultsPrunedOnly() {
		return interimResultsPrunedOnly;
	}

	private void makeListPrunedOnly() {
		for (InterimResults ir : interimResultsStorage) {
			if (ir.getId().indexOf("pruned") != -1) {
				ir.displayTable();
				interimResultsPrunedOnly.add(ir);
			}
		}
	}

	/**
	 * for getting unique elements from data set with transactions (L1 without
	 * support counts)
	 * 
	 * @return InterimResults for (L1-item set)
	 */
	private InterimResults LOne() {
		AprioriDataBase ap = associationDataSet.getLOne();
		ap.setMin_support(minSupport);
		InterimResults l1 = new InterimResults("L1raw", associationDataSet.getLOne());
		return l1;
	}

	/**
	 * for adding support counts in L1-item set
	 * 
	 * @param InterimResults L1-item set
	 * @return InterimResults L1-item set
	 */
	private InterimResults LOneCount(InterimResults interimResult) {
		int indexInterimResults = interimResults.indexOf(interimResult);
		AprioriDataBase l1count = interimResults.get(indexInterimResults).getAdb();
		l1count.setMin_support(minSupport);
		l1count.addSupportCount(associationDataSet);
		InterimResults l1b = new InterimResults("L1counted", l1count);
		return l1b;
	}

	/**
	 * for marking rows with too low support count
	 * 
	 * @param InterimResults Lx-item set
	 * @return InterimResults Lx-item set
	 */
	private InterimResults markingTooLowSupport(InterimResults interimResult) {
		int indexInterimResults = interimResults.indexOf(interimResult);
		AprioriDataBase pruneMarker = interimResults.get(indexInterimResults).getAdb();
		pruneMarker.pruningAddMarker();
		String idName = "dummy";
		if (pruneMarker.getDb().length == 0) {
			idName = "L" + interimResult.getId().replaceAll("[^0-9]", "") + "lowSupportMarker";
		} else {
			idName = "L" + pruneMarker.getDb()[0].getItemset().getItems().length + "lowSupportMarker";
		}
		InterimResults lxPruneMark = new InterimResults(idName, pruneMarker);
		return lxPruneMark;
	}

	/**
	 * for pruning elements marked as 'too low support count'
	 * 
	 * @param InterimResults Lx-item set
	 * @return InterimResults Lx-item set
	 */
	private InterimResults pruning(InterimResults interimResult) {
		int indexInterimResults = interimResults.indexOf(interimResult);
		AprioriDataBase toPrune = interimResults.get(indexInterimResults).getAdb();
		toPrune.setMin_support(minSupport);
		AprioriDataBase pruned = new AprioriDataBase(toPrune.pruningClean());
		pruned.setMin_support(minSupport);
		String idName = "dummy";
		if (pruned.getDb().length == 0) {
			idName = "L" + interimResult.getId().replaceAll("[^0-9]", "") + "pruned";
		} else {
			idName = "L" + pruned.getDb()[0].getItemset().getItems().length + "pruned";
		}
		InterimResults prunedClean = new InterimResults(idName, pruned);
		return prunedClean;
	}

	/**
	 * for joining L1's (without support count)
	 * 
	 * @param InterimResults L1-item set
	 * @return InterimResults L2-item set (raw)
	 */
	private InterimResults LTwo(InterimResults interimResult) {
		int indexInterimResults = interimResults.indexOf(interimResult);
		InterimResults l2raw = new InterimResults("L2raw",
				new AprioriDataBase(interimResults.get(indexInterimResults).getAdb().joinLOnes()));
		return l2raw;
	}

	/**
	 * for adding support counts (k>1)
	 * 
	 * @param InterimResults Lx-item set
	 * @return InterimResults Lx-item set
	 */
	private InterimResults LxCount(InterimResults interimResult) {
		int indexInterimResults = interimResults.indexOf(interimResult);
		AprioriDataBase lxcount = interimResults.get(indexInterimResults).getAdb();
		lxcount.setMin_support(minSupport);
		lxcount.addSupportCount(associationDataSet);
		String idName = "dummy";
		if (lxcount.getDb().length == 0) {
			idName = "L" + interimResult.getId().replaceAll("[^0-9]", "") + "counted";
		} else {
			idName = "L" + lxcount.getDb()[0].getItemset().getItems().length + "counted";
		}
		InterimResults counted = new InterimResults(idName, lxcount);
		return counted;
	}

	/**
	 * for joining Lk with its self (k>1)
	 * 
	 * @param InterimResults Lx-item set
	 * @return InterimResults Lx-item set
	 */
	private InterimResults possibleCombinations(InterimResults interimResult) {
		int indexInterimResults = interimResults.indexOf(interimResult);
		AprioriDataBase lX = interimResults.get(indexInterimResults).getAdb();
		lX.setMin_support(minSupport);
		int l = lX.getDb()[0].getItemset().getItems().length + 1;
		String idName = "L" + l + "raw";
		InterimResults lXRaw = new InterimResults(idName, new AprioriDataBase(lX.joiner(l)));
		return lXRaw;
	}

	/**
	 * for adding frequent patterns found into result list
	 * 
	 * @param InterimResults Lx-item set
	 */
	private void addFrequentPattern(InterimResults interimResult) {
		AprioriDataBaseRow[] adb = interimResult.getAdb().getDb();
		frequentPatterns.addRows(adb);
	}

	/**
	 * for performing apriori process
	 */
	public void start() {
		InterimResults l1raw = LOne();
		interimResults.add(l1raw);
		interimResultsStorage.add(InterimResults.deepCopy(l1raw));
		InterimResults l1counted = LOneCount(l1raw);
		interimResults.add(l1counted);
		interimResultsStorage.add(InterimResults.deepCopy(l1counted));
		InterimResults l1marked = markingTooLowSupport(l1counted);
		interimResults.add(l1marked);
		interimResultsStorage.add(InterimResults.deepCopy(l1marked));
		InterimResults l1pruned = pruning(l1marked);
		interimResults.add(l1pruned);
		interimResultsStorage.add(InterimResults.deepCopy(l1pruned));
		InterimResults l2raw = LTwo(l1pruned);
		interimResults.add(l2raw);
		interimResultsStorage.add(InterimResults.deepCopy(l2raw));
		InterimResults l2counted = LxCount(l2raw);
		interimResults.add(l2counted);
		interimResultsStorage.add(InterimResults.deepCopy(l2counted));
		InterimResults l2marked = markingTooLowSupport(l2counted);
		interimResults.add(l2marked);
		interimResultsStorage.add(InterimResults.deepCopy(l2marked));
		InterimResults l2pruned = pruning(l2marked);
		interimResults.add(l2pruned);
		interimResultsStorage.add(InterimResults.deepCopy(l2pruned));
		addFrequentPattern(l2pruned);
		InterimResults lX = l2pruned;
		while (lX.getAdb().getDb().length > 1) {
			InterimResults lXRaw = possibleCombinations(lX);
			interimResults.add(lXRaw);
			interimResultsStorage.add(InterimResults.deepCopy(lXRaw));
			InterimResults lXCounted = LxCount(lXRaw);
			interimResults.add(lXCounted);
			interimResultsStorage.add(InterimResults.deepCopy(lXCounted));
			InterimResults lXMarked = markingTooLowSupport(lXCounted);
			interimResults.add(lXMarked);
			interimResultsStorage.add(InterimResults.deepCopy(lXMarked));
			InterimResults lXPruned = pruning(lXMarked);
			interimResults.add(lXPruned);
			interimResultsStorage.add(InterimResults.deepCopy(lXPruned));
			lX = lXPruned;
			addFrequentPattern(lXPruned);
		}
		makeListPrunedOnly();
	}

	/**
	 * for displaying results in tabular format (console; for development purposes
	 * only)
	 */
	public void displayResults() {
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println();
		associationDataSet.displayTable();
		System.out.println();
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println();
		System.out.println("Apriori: MinimalSupport Count: " + minSupport);
		System.out.println();
		for (int j = 0; j < interimResultsStorage.size(); j++) {
			interimResultsStorage.get(j).displayTable();
		}
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println();
		frequentPatterns.displayTable();
		System.out.println();
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	}

	/**
	 * for getting the available items
	 * 
	 * @return list of available items
	 */
	public List<String> getAvailableItems() {
		List<ItemSet> allItemsSets = getInterimResultsStorage().get(0).getAdb().getAllItemsets();
		List<String> allAvailableItems = new ArrayList<>();
		for (ItemSet it : allItemsSets) {
			for (String s : it.getItems()) {
				allAvailableItems.add(s);
			}
		}
		return allAvailableItems;
	}

	public FrequentPattern getFrequentPattern() {
		return frequentPatterns;
	}

	public ArrayList<InterimResults> getInterimResultsStorage() {
		return interimResultsStorage;
	}

	public AssociationDataSet getAssociationDataSet() {
		return associationDataSet;
	}

	public void setAssociationDataSet(AssociationDataSet associationDataSet) {
		this.associationDataSet = associationDataSet;
	}

	public FrequentPattern getFrequentPatterns() {
		return frequentPatterns;
	}

	public void setFrequentPatterns(FrequentPattern frequentPatterns) {
		this.frequentPatterns = frequentPatterns;
	}

	public ArrayList<InterimResults> getInterimResults() {
		return interimResults;
	}

	public void setInterimResults(ArrayList<InterimResults> interimResults) {
		this.interimResults = interimResults;
	}

	public int getMinSupport() {
		return minSupport;
	}

	public void setInterimResultsStorage(ArrayList<InterimResults> interimResultsStorage) {
		this.interimResultsStorage = interimResultsStorage;
	}
}
