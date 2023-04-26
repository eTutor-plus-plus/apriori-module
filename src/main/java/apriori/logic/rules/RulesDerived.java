package apriori.logic.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import apriori.logic.algorithm.Apriori;
import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.datageneration.AssociationDataSet;

/**
 * class for storing derived rules
 *
 */
public class RulesDerived {

	private List<Rule> rules;

	/**
	 * constructor
	 * 
	 * @param ads             association data set (AssociationDataSet)
	 * @param apriori         apriori algorithm object
	 * @param frequentPattern list of frequent items
	 */
	public RulesDerived(AssociationDataSet ads, Apriori apriori, List<String> frequentPattern) {
		rules = new ArrayList<>();
		setRules(ads, apriori, frequentPattern);
	}

	/**
	 * for generating non empty subsets for rules
	 * 
	 * @param apriori         apriori algorithm object
	 * @param frequentPattern list of frequent items
	 * @return list of all non-empty subsets (possible combinations)
	 */
	private List<List<String>> nonEmptySubsets(Apriori apriori, List<String> frequentPattern) {
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
	 * for setting derived rules
	 * 
	 * @param ads             association data set (AssociationDataSet)
	 * @param apriori         apriori algorithm object
	 * @param frequentPattern list of frequent items
	 * @return list of rules
	 */
	private List<List<String>> setRules(AssociationDataSet ads, Apriori apriori, List<String> frequentPattern) {
		List<List<String>> subsets = nonEmptySubsets(apriori, frequentPattern);
		for (int i = 0; i < subsets.size(); i = i + 1) {
			Rule newRule = new Rule(ads, apriori, subsets.get(i));
			rules.add(newRule);
		}
		return subsets;
	}

	public List<Rule> getRules() {
		return rules;
	}

	@Override
	public String toString() {
		return "RulesDerived [rules=" + rules + "]";
	}

}
