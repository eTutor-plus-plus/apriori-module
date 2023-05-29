package apriori.logic.datageneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import apriori.logic.utility.Property;
import apriori.logic.utility.TypeDataset;

/**
 * class for generating data sets
 *
 */
public class GenerateDataset {

	/**
	 * for selecting type of data set
	 * 
	 * @param types          type data set
	 * @param maxElements    numbers of available items
	 * @param noTransactions number of transactions
	 * @param maxSize        maximum number of items in transactions
	 * @param minSize        minimum number of items in transactions
	 * @return ads association data set
	 */
	public static AssociationDataSet generateDataset(String types, int maxElements, int noTransactions, int maxSize,
			int minSize) {
		AssociationDataSet ads = null;
		if (types.equals("ABC")) {
			ads = GenerateDataset.generateAlphabeticAbc(maxElements, noTransactions, maxSize, minSize);
		} else if (types.equals("RandomLetters")) {
			ads = GenerateDataset.generateAlphabeticRandomLetters(maxElements, noTransactions, maxSize, minSize);
		}

		EnumMap<TypeDataset, String> map = TypeDataset.getMap();
		for (Map.Entry<TypeDataset, String> entry : map.entrySet()) {
			if (entry.getKey().toString().equals(types)) {
				ads = GenerateDataset.generateFromFileResource(entry.getValue().toString(), maxElements, noTransactions,
						maxSize, minSize);
			}
		}
		return ads;
	}

	/**
	 * for generating data set with randomly chosen letters
	 * 
	 * @param numberOfElements     numbers of available items
	 * @param numberOfTransactions number of transactions
	 * @param maxSize              maximum number of items in transactions
	 * @param minSize              minimum number of items in transactions
	 * @return ads association data set
	 */
	public static AssociationDataSet generateAlphabeticRandomLetters(int numberOfElements, int numberOfTransactions,
			int maxSize, int minSize) {
		AssociationDataSet ads;
		WeightedItemSet itemSet = WeightedItemSet.generateAlphabetic(numberOfElements);
		Sampl sample = new Sampl(itemSet);
		ads = sample.generateAssociationDataSet(numberOfTransactions, maxSize, minSize);
		return ads;
	}

	/**
	 * for generating data set with letters chosen in alphabetic order
	 * 
	 * @param numberOfElements     numbers of available items
	 * @param numberOfTransactions number of transactions
	 * @param maxSize              maximum number of items in transactions
	 * @param minSize              minimum number of items in transactions
	 * @return ads association data set
	 */
	public static AssociationDataSet generateAlphabeticAbc(int numberOfElements, int numberOfTransactions, int maxSize,
			int minSize) {
		AssociationDataSet ads;
		WeightedItemSet itemSet = WeightedItemSet.generateAlphabeticABC(numberOfElements);
		Sampl sample = new Sampl(itemSet);
		ads = sample.generateAssociationDataSet(numberOfTransactions, maxSize, minSize);
		return ads;
	}

	/**
	 * for generating data set from csv file, file not loaded as a resource, will
	 * not work in JAR files (in first column is the weight of an item, which is in
	 * the second column)
	 * 
	 * @param fileName             name of the file
	 * @param numberOfElements     numbers of available items
	 * @param numberOfTransactions number of transactions
	 * @param maxSize              maximum number of items in transactions
	 * @param minSize              minimum number of items in transactions
	 * @return ads association data set
	 */
	public static AssociationDataSet generateFromFile(String fileName, int numberOfElements, int numberOfTransactions,
			int maxSize, int minSize) {
		AssociationDataSet ads;
		WeightedItemSet itemSet = WeightedItemSet.generateAlphabeticABC(numberOfElements);
		Sampl sample = new Sampl(itemSet);
		ads = sample.generateAssociationDataSet(numberOfTransactions, maxSize, minSize);

		File file = new File(fileName);
		if (file.exists() == true) {
			try {
				WeightedItemSet wis = WeightedItemSet.readData(fileName);
				Sampl sam2 = new Sampl(wis);
				ads = sam2.generateAssociationDataSet(numberOfTransactions, maxSize, minSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("File not found; falling back to default. ");

		}
		return ads;
	}

	/**
	 * for generating data set from csv file, if the saved file is in the resource
	 * folder (in first column is the weight of an item, which is in the second
	 * column)
	 * 
	 * @param fileName             name of the file
	 * @param numberOfElements     numbers of available items
	 * @param numberOfTransactions number of transactions
	 * @param maxSize              maximum number of items in transactions
	 * @param minSize              minimum number of items in transactions
	 * @return ads association data set
	 */
	public static AssociationDataSet generateFromFileResource(String fileName, int numberOfElements,
			int numberOfTransactions, int maxSize, int minSize) {

		AssociationDataSet ads;
		WeightedItemSet itemSet = WeightedItemSet.generateAlphabeticABC(numberOfElements);

		Sampl sample = new Sampl(itemSet);
		ads = sample.generateAssociationDataSet(numberOfTransactions, maxSize, minSize);

		fileName = "/" + fileName;
		InputStream id = null;

		try (var rs = Property.class.getResourceAsStream(fileName)) {
			id = rs;
			List<String[]> content = new ArrayList<>();

			try (BufferedReader br = new BufferedReader(new InputStreamReader(rs))) {
				String line = "";
				while ((line = br.readLine()) != null) {
					content.add(line.split(";"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<String[]> contentMod = new ArrayList<>();
			for (String[] row : content) {
				String[] rowMod = new String[2];

				for (int i = 0; i < 2; i++) {
					String elementMod = row[i].toUpperCase();
					rowMod[i] = elementMod;
				}
				contentMod.add(rowMod);
			}

			Integer[] randoms = new Integer[numberOfElements];

			Set<Integer> set = new Random().ints(0, contentMod.size() - 1).distinct().limit(numberOfElements).boxed()
					.collect(Collectors.toSet());

			randoms = set.toArray(randoms);

			WeightedItem[] weightedItems = new WeightedItem[numberOfElements];
			for (int i = 0; i < weightedItems.length; i++) {
				double dd = 0;
				try {
					dd = Double.parseDouble(contentMod.get(randoms[i])[0]);

				} catch (NumberFormatException ex) {
					double min = 0.1;
					double max = Math.random() * (10 - min + 1) + min;
					Random random = new Random();
					dd = min + random.nextDouble() * (max - min);

				}
				WeightedItem wi = new WeightedItem(contentMod.get(randoms[i])[1], (dd / 100));

				weightedItems[i] = wi;

			}
			WeightedItemSet wis = new WeightedItemSet(weightedItems);

			Sampl sam2 = new Sampl(wis);
			ads = sam2.generateAssociationDataSet(numberOfTransactions, maxSize, minSize);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ads;
	}
}
