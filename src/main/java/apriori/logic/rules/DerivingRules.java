package apriori.logic.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import apriori.logic.algorithm.Apriori;
import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.algorithm.FrequentPattern;
import apriori.logic.algorithm.ItemSet;
import apriori.logic.datageneration.AssociationDataSet;

/**
 * class for generating derived rules from subset
 *
 */
public class DerivingRules {

	/**
	 * for getting left hand side from subset
	 * 
	 * @param subset subset of possible combinations rule pattern
	 * @return list items
	 */
	public static List<String> getLHS(List<String> subset) {
		List<String> lhs = new ArrayList<>();
		int i = 0;
		while (subset.get(i).equals("=>") == false) {
			lhs.add(subset.get(i));
			i = i + 1;
		}
		return lhs;
	}

	/**
	 * for getting right hand side from subset
	 * 
	 * @param subset subset of possible combinations rule pattern
	 * @return list items
	 */
	public static List<String> getRHS(List<String> subset) {
		List<String> rhs = new ArrayList<>();
		int i = 0;
		while (subset.get(i).equals("=>") == false) {
			i = i + 1;
		}
		i = i + 1;
		while (i < subset.size()) {
			rhs.add(subset.get(i));
			i = i + 1;
		}
		return rhs;
	}

	/**
	 * for getting the support count for a rule
	 * 
	 * @param ads   association data set (AssociationDataSet)
	 * @param items items in row
	 * @param fps   frequent items table
	 * @return support count
	 */
	public static int getSupportCountRules(AssociationDataSet ads, List<String> items, FrequentPattern fps) {
		List<AprioriDataBaseRow> fp = new ArrayList<>();
		for (int i = 0; i < ads.getUniqueElementsFromRows().size(); i = i + 1) {
			fp.add(AprioriDataBaseRow.createDataBaseRow(
					ItemSet.createItemSet(new String[] { ads.getUniqueElementsFromRows().get(i) }),
					ads.getCountUniqueElements().get(i)));
		}
		List<AprioriDataBaseRow> row = new ArrayList<>();
		row.addAll(fp);
		row.addAll(fps.getFrequentPatterns());
		List<String> list;
		int supportCount = 0;
		for (AprioriDataBaseRow s : row) {
			list = Arrays.asList(s.getItemset().getItems());
			if (list.equals(items)) {
				supportCount = s.getIndicator();
			}
		}
		return supportCount;
	}

	/**
	 * for getting confidence for a rule
	 * 
	 * @param ads        association data set (AssociationDataSet)
	 * @param itemsLeft  left hand side rule
	 * @param itemsRight right hand side rule
	 * @param fps        frequent items table
	 * @return confidence confidence rule
	 */
	public static double getConfidence(AssociationDataSet ads, List<String> itemsLeft, List<String> itemsRight,
			FrequentPattern fps) {
		List<String> upper = new ArrayList<>();
		upper.addAll(itemsLeft);
		upper.addAll(itemsRight);
		Collections.sort(upper);
		int up = getSupportCountRules(ads, upper, fps);
		int down = getSupportCountRules(ads, itemsLeft, fps);
		double conf = (double) up / (double) down;
		return conf;
	}

	/**
	 * for getting text for a rule
	 * 
	 * @param rules list of rules
	 * @return list with rule text
	 */
	public static List<String> getFlatText(List<List<String>> rules) {
		List<String> rulesList = new ArrayList<>();
		for (List<String> ls : rules) {
			String delim = "";
			String res = String.join(delim, ls);
			rulesList.add(res);
		}
		return rulesList;
	}

	/**
	 * for getting text for confidence
	 * 
	 * @param ads        association data set (AssociationDataSet)
	 * @param itemsLeft  left hand side rule
	 * @param itemsRight right hand side rule
	 * @param fps        frequent items table
	 * @return list with confidence text
	 */
	public static List<String> getConfidenceText(AssociationDataSet ads, List<String> itemsLeft,
			List<String> itemsRight, FrequentPattern fps) {
		List<String> upper = new ArrayList<>();
		upper.addAll(itemsLeft);
		upper.addAll(itemsRight);
		Collections.sort(upper);
		int up = getSupportCountRules(ads, upper, fps);
		int down = getSupportCountRules(ads, itemsLeft, fps);
		double conf = (double) up / (double) down;
		List<String> resulttext = new ArrayList<>();
		resulttext.add("; Confidence = ");
		resulttext.add(String.valueOf(up));
		resulttext.add("/");
		resulttext.add(String.valueOf(down));
		resulttext.add(" = ");
		resulttext.add(String.valueOf(conf * 100));
		resulttext.add("%");
		return resulttext;
	}

	/**
	 * for generating patterns for rule generation
	 * 
	 * @param apriori         apriori algorithm object
	 * @param frequentPattern list of frequent items
	 * @return list with patterns
	 */
	public static List<List<String>> nonEmptySubsets(Apriori apriori, List<String> frequentPattern) {
		List<List<String>> solutions = new ArrayList<>();

		for (AprioriDataBaseRow adbr : apriori.getFrequentPattern().getFrequentPatterns()) {
			List<String> stringList = new ArrayList<>(Arrays.asList(adbr.getItemset().getItems()));
			solutions.add(stringList);
		}

		List<List<String>> subsets = Generator.subset(frequentPattern).simple().stream()
				.collect(Collectors.<List<String>>toList());

		List<List<String>> allSubsets = new ArrayList<>();

		for (List<String> ls : subsets) {
			List<String> tempSubset = new ArrayList<>();
			if (ls.isEmpty() == false) {
				if (frequentPattern.equals(ls) == false) {
					tempSubset.addAll(ls);
					tempSubset.add("=>");
					for (String s : frequentPattern) {
						if (ls.contains(s) == false) {
							tempSubset.add(s);
						}
					}
					allSubsets.add(tempSubset);
				}
			}
		}
		return allSubsets;
	}

	/**
	 * for getting rules from frequent pattern
	 * 
	 * @param ads             association data set (AssociationDataSet)
	 * @param apriori         apriori algorithm object
	 * @param frequentPattern list of frequent items
	 * @return list rules
	 */
	public static List<List<String>> getRulesFromFP(AssociationDataSet ads, Apriori apriori,
			List<String> frequentPattern) {
		List<List<String>> subsets = nonEmptySubsets(apriori, frequentPattern);
		for (int i = 0; i < subsets.size(); i = i + 1) {
			subsets.get(i).addAll(getConfidenceText(ads, DerivingRules.getLHS(subsets.get(i)),
					DerivingRules.getRHS(subsets.get(i)), apriori.getFrequentPattern()));
		}
		return subsets;
	}

	/**
	 * for generating list of rules (text)
	 * 
	 * @param rulesFP rules list
	 * @return list of rules (text)
	 */
	public static List<String> getRuleResultList(List<List<String>> rulesFP) {
		List<String> list = new ArrayList<>();
		for (List<String> sl : rulesFP) {
			String line = String.join(" ", sl);
			list.add(line);
		}
		return list;
	}

}
