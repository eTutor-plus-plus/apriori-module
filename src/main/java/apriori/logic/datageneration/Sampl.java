package apriori.logic.datageneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import apriori.logic.algorithm.ItemSet;

/**
 * class for generating data sets
 */
public class Sampl {

	WeightedItemSet itemSet;

	/**
	 * Constructor
	 * 
	 * @param itemSet items set with occurrence probabilities
	 */
	public Sampl(WeightedItemSet itemSet) {
		this.itemSet = itemSet;
	}

	/**
	 * for generating association data set
	 * 
	 * @param numberOfTransactions number of transactions
	 * @param maxSizeTransaction   maximum number of items in transactions
	 * @param minSizeTransaction   maximum number of items in transactions
	 * @return asnew association data set
	 */
	public AssociationDataSet generateAssociationDataSet(int numberOfTransactions, int maxSizeTransaction,
			int minSizeTransaction) {

		AssociationRow[] associationDB = new AssociationRow[numberOfTransactions];// data set for collecting results

		for (int i = 0; i < numberOfTransactions; i++) {
			int numberOfItemsInTransaction = (int) (Math.random() * (maxSizeTransaction - minSizeTransaction + 1)
					+ minSizeTransaction);// generating size of single transaction

			int[] alreadyChosen = new int[numberOfItemsInTransaction];// storing items, that are already chosen
			Arrays.fill(alreadyChosen, -1);// filling with dummies
			ArrayList<String> result = new ArrayList<>(); // list to collect items in a transaction
			WeightedItemSet newWeightedItemSet = new WeightedItemSet(itemSet.deepCopy());

			for (int j = 0; j < numberOfItemsInTransaction; j++) {
				double[][] markov = new double[itemSet.getWeightedItems().length][itemSet.getWeightedItems().length];
				if (j == 0) {
					int[] alreadyChosen2 = new int[numberOfItemsInTransaction];
					Arrays.fill(alreadyChosen2, -1);
					for (int k = 0; k < itemSet.getWeightedItems().length; k++) {
						alreadyChosen2[j] = k;
						markov[k] = getMarkovVektor(alreadyChosen2, newWeightedItemSet.getWeightedItems().length, j);
					}
					alreadyChosen[j] = newWeightedItemSet.getRandomWeightedItemInt();// generate random element (number)
				} else {
					alreadyChosen[j] = newWeightedItemSet.getRandomWeightedItemInt();// generate random element (number)
					for (int k = 0; k < itemSet.getWeightedItems().length; k++) {
						markov[k] = getMarkovVektor(alreadyChosen, newWeightedItemSet.getWeightedItems().length, j);
					}
				}
				result.add(newWeightedItemSet.getWeightedItems()[alreadyChosen[j]].getItemName());
				WeightedItem[] weightedItems = new WeightedItem[newWeightedItemSet.getWeightedItems().length];
				for (int k = 0; k < newWeightedItemSet.getWeightedItems().length; k++) {
					weightedItems[k] = new WeightedItem(newWeightedItemSet.getWeightedItems()[k].getItemName(),
							markov[alreadyChosen[j]][k]);
				}
				newWeightedItemSet = new WeightedItemSet(weightedItems);
			}
			Collections.sort(result);
			String[] conv = new String[result.size()];
			int a = 0;
			for (String s : result) {
				conv[a] = s;
				a++;
			}
			ItemSet newItemSet = new ItemSet(conv);
			AssociationRow ar = new AssociationRow("TID" + i, newItemSet);
			associationDB[i] = ar;
		}

		AssociationDataSet asnew = new AssociationDataSet(associationDB);
		return asnew;
	}

	/**
	 * for generating row in markov matrix
	 * 
	 * @param areadyChosen numbers of the items already chosen
	 * @param size         of line in matrix
	 * @param step         which iteration
	 * @return double[] array with random numbers for markov
	 */
	double[] getMarkovVektor(int[] areadyChosen, int size, int step) {
		// size of the weighted data set
		int size2 = itemSet.getWeightedItems().length;
		// calc. how many elements were already chosen
		int step2 = 0;
		for (int i = 0; i < areadyChosen.length; i++) {
			if (areadyChosen[i] == -1) {
				step2++;
			}
		}
		step2 = size2 - step2;
		size = size2;
		step = step2;

		double[] line = new double[size];
		double sumweights = 0;
		double[] randomNumbers;
		if ((size - step) > 0) {
			randomNumbers = new double[size - step];
		} else {

			return line;
		}
		for (int j = 0; j < randomNumbers.length; j++) {
			if (j == randomNumbers.length - 1) {
				randomNumbers[j] = 1 - sumweights;
			} else {
				double max = 1 - sumweights;
				double min = 0;
				double random = Math.random() * (max - min) + min;
				sumweights = sumweights + random;
				randomNumbers[j] = random;

			}
		}
		double summe = 0;
		for (double r : randomNumbers) {
			summe = summe + r;
		}

		int numberUsed = 0;
		for (int i = 0; i < line.length; i++) {
			if (numberUsed >= randomNumbers.length) {
				return line;
			}
			boolean check = false;
			for (int in : areadyChosen) {
				if (in == i) {
					check = true;
				}
			}
			if (check == true) {
				line[i] = 0;
			} else {

				line[i] = randomNumbers[numberUsed];
				numberUsed = numberUsed + 1;
			}
		}
		return line;
	}
}
