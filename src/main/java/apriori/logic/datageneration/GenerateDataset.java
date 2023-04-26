package apriori.logic.datageneration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

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
		for (Map.Entry<TypeDataset,String> entry : map.entrySet()) {			
			  if(   entry.getKey().toString().equals(types)) {  
				  ads = GenerateDataset.generateFromFile(entry.getValue().toString(), maxElements, noTransactions, maxSize, minSize);
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
	 * for generating data set from csv file (in first column is the weight of an
	 * item, which is in the second column)
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
		if (file.exists()==true) {			
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
}
