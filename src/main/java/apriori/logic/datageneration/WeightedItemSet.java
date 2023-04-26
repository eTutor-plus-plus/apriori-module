package apriori.logic.datageneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * class for set of weighted items
 *
 */
public class WeightedItemSet {

	WeightedItem[] weightedItems;

	/**
	 * constructor
	 */
	public WeightedItemSet() {

	}

	/**
	 * constructor
	 * 
	 * @param weightedItems array of weighted items
	 */
	public WeightedItemSet(WeightedItem[] weightedItems) {
		this.weightedItems = weightedItems;
	}

	/**
	 * for accumulating weight of items
	 * 
	 * @return accumulatedWeightedItems array of weighted items accumulated
	 */
	WeightedItem[] weightsAccum() {
		WeightedItem[] accumulatedWeightedItems = deepCopy();
		double sumAccum = 0;
		for (WeightedItem wi : accumulatedWeightedItems) {
			sumAccum = sumAccum + wi.getWeight();
			wi.setWeight(sumAccum * 1000);
		}
		return accumulatedWeightedItems;
	}

	/**
	 * for getting random weighted item
	 * 
	 * @return number of random weighted item
	 */
	int getRandomWeightedItemInt() {
		WeightedItem[] weightsAccum = weightsAccum();
		WeightedItemSet weightsAccumSet = new WeightedItemSet(weightsAccum);
		int randomIndex = (int) (Math.random() * 1000 + 1);
		for (int i = 0; i < weightsAccumSet.getWeightedItems().length; i++) {
			if (randomIndex <= (int) weightsAccumSet.getWeightedItems()[i].getWeight()) {
				return i;
			}
		}
		return weightsAccumSet.getWeightedItems().length - 1;
	}

	/**
	 * for making deep copy of weighted items array
	 * 
	 * @return newWeightedItems copy of weighted items array
	 */
	public WeightedItem[] deepCopy() {
		WeightedItem[] newWeightedItems = new WeightedItem[weightedItems.length];
		for (int i = 0; i < weightedItems.length; i++) {
			newWeightedItems[i] = new WeightedItem(weightedItems[i].getItemName(), weightedItems[i].getWeight());
		}
		return newWeightedItems;
	}

	/**
	 * for generating weighted items set with letters in alphabetic order
	 * 
	 * @param numberOfLetters number of letters
	 * @return WeightedItemSet weighted items set
	 */
	public static WeightedItemSet generateAlphabetic(int numberOfLetters) {
		WeightedItem[] collector = new WeightedItem[numberOfLetters];
		double[] weights = new double[numberOfLetters];
		List<Integer> letters = new ArrayList<>();

		for (int i = 0; i < numberOfLetters; i++) {
			int random = (int) (Math.random() * (90 - 65) + 65);
			while (letters.contains(random)) {
				random = (int) (Math.random() * (90 - 65) + 65);
			}
			letters.add(random);
		}

		for (int i = 0; i < numberOfLetters; i++) {// generating letters
			char c = (char) letters.get(i).intValue();
			String letter = Character.toString(c);
			WeightedItem collect = new WeightedItem(letter, weights[i]);
			collector[i] = collect;
		}
		return new WeightedItemSet(collector);
	}

	/**
	 * for generating weighted items set with randomly chosen letters
	 * 
	 * @param numberOfLetters number of letters
	 * @return WeightedItemSet weighted items set
	 */
	public static WeightedItemSet generateAlphabeticABC(int numberOfLetters) {
		final int AAA = 65;
		WeightedItem[] collector = new WeightedItem[numberOfLetters];
		double[] weights = new double[numberOfLetters];
		double random = Math.random();
		double sumRandom = random;
		weights[0] = random;
		for (int i = 1; i < numberOfLetters; i++) {// generating random weights
			double min = 0;
			double max = 1 - sumRandom;
			random = Math.random() * (max - min) + min;
			weights[i] = random;
			sumRandom = sumRandom + random;
		}

		for (int i = 0; i < numberOfLetters; i++) {// generating letters
			int aa = AAA + i;
			char c = (char) aa;
			String letter = Character.toString(c);
			WeightedItem collect = new WeightedItem(letter, weights[i]);
			collector[i] = collect;
		}
		return new WeightedItemSet(collector);
	}

	/**
	 * for generating weighted items set from a csv file
	 * 
	 * @param dataFile String name csv file for import
	 * @return WeightedItemSet weighted items set of read in data
	 * @throws IOException if file name is wrong
	 */
	public static WeightedItemSet readData(String dataFile) throws IOException {
		File file = new File(dataFile);
		List<String[]> content = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				content.add(line.split(";"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		WeightedItem[] weightedItems = new WeightedItem[content.size()];
		for (int i = 0; i < content.size(); i++) {

			double dd = 0;
			try {
				dd = Double.parseDouble(content.get(i)[0]);
			} catch (NumberFormatException ex) {
				double min = 0.1;
				double max = Math.random() * (10 - min + 1) + min;
				Random random = new Random();
				dd = min + random.nextDouble() * (max - min);
//				System.out.println("Given String is not parsable to double");
			}
			WeightedItem wi = new WeightedItem(content.get(i)[1], (dd / 100));
			weightedItems[i] = wi;
		}

		WeightedItemSet contentReturned = new WeightedItemSet(weightedItems);
		return contentReturned;
	}

	public WeightedItem[] getWeightedItems() {
		return weightedItems;
	}

	void setWeightedItems(WeightedItem[] weightedItems) {
		this.weightedItems = weightedItems;
	}

	@Override
	public String toString() {
		return "WeightedItemSet [weightedItems=" + Arrays.toString(weightedItems) + "]";
	}

}
