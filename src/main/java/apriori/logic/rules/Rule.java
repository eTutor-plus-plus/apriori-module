package apriori.logic.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import apriori.logic.algorithm.Apriori;
import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.algorithm.FrequentPattern;
import apriori.logic.algorithm.ItemSet;
import apriori.logic.datageneration.AssociationDataSet;

/**
 * class for rules (for association rules task)
 *
 */
public class Rule {

	private List<String> lhs;
	private List<String> rhs;
	private int upperConf;
	private int lowerConf;
	private double confidence;
	private double percentage;
	private String upperSupp;
	private String lowerSupp;

	/**
	 * constructor
	 * 
	 * @param ads     association data set (AssociationDataSet)
	 * @param apriori apriori algorithm object
	 * @param subset  subset of possible combinations rule pattern
	 */
	public Rule(AssociationDataSet ads, Apriori apriori, List<String> subset) {
		this.lhs = getLHS(subset);
		this.rhs = getRHS(subset);
		this.upperConf = upperConfidenceElement(ads, lhs, rhs, apriori.getFrequentPatterns());
		this.lowerConf = lowerConfidenceElement(ads, lhs, apriori.getFrequentPatterns());
		this.confidence = confidence(upperConf, lowerConf);
		this.percentage = percentage(confidence);
		List<String> itemsList = new ArrayList<>();
		itemsList.addAll(rhs);
		itemsList.addAll(lhs);
		Collections.sort(itemsList);
		this.upperSupp = itemsList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(""));
		this.lowerSupp = lhs.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(""));
	}

	/**
	 * for calculating numerator
	 * 
	 * @param ads             association data set (AssociationDataSet)
	 * @param lhs             left hand side rule
	 * @param rhs             right hand side rule
	 * @param frequentPattern frequent items table
	 * @return numerator for confidence calculation
	 */
	private int upperConfidenceElement(AssociationDataSet ads, List<String> lhs, List<String> rhs,
			FrequentPattern frequentPattern) {
		List<String> upper = new ArrayList<>();
		upper.addAll(lhs);
		upper.addAll(rhs);
		Collections.sort(upper);
		int up = getSupportCountRules(ads, upper, frequentPattern);
		return up;
	}

	/**
	 * for calculating denominator
	 * 
	 * @param ads             association data set (AssociationDataSet)
	 * @param lhs             left hand side rule
	 * @param frequentPattern frequent items table
	 * @return denominator for confidence calculation
	 */
	private int lowerConfidenceElement(AssociationDataSet ads, List<String> lhs, FrequentPattern frequentPattern) {
		int down = getSupportCountRules(ads, lhs, frequentPattern);
		return down;
	}

	/**
	 * for calculating confidence
	 * 
	 * @param upper numerator for confidence calculation
	 * @param lower denominator for confidence calculation
	 * @return confidence
	 */
	private double confidence(int upper, int lower) {
		double confidence = (double) upper / (double) lower;
		return confidence;
	}

	/**
	 * for calculating percentage
	 * 
	 * @param conf confidence (double format)
	 * @return percentage
	 */
	private double percentage(double conf) {
		double percent = conf * 100;
		return percent;
	}

	/**
	 * for calculating support count
	 * 
	 * @param ads   association data set (AssociationDataSet)
	 * @param items items in row
	 * @param fps   frequent items table
	 * @return support count
	 */
	private int getSupportCountRules(AssociationDataSet ads, List<String> items, FrequentPattern fps) {
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
	 * for retrieving left hand side rule
	 * 
	 * @param subset list of rules
	 * @return list items left hand side rule
	 */
	public List<String> getLHS(List<String> subset) {
		List<String> lhs = new ArrayList<>();
		int i = 0;
		while (subset.get(i).equals("=>") == false) {
			lhs.add(subset.get(i));
			i = i + 1;
		}
		return lhs;
	}

	/**
	 * for retrieving right hand side rule
	 * 
	 * @param subset list of rules
	 * @return list items right hand side rule
	 */
	public List<String> getRHS(List<String> subset) {
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

	public String getUpperSupp() {
		return upperSupp;
	}

	public String getLowerSupp() {
		return lowerSupp;
	}

	public List<String> getLhs() {
		return lhs;
	}

	public List<String> getRhs() {
		return rhs;
	}

	public int getUpperConf() {
		return upperConf;
	}

	public int getLowerConf() {
		return lowerConf;
	}

	public double getConfidence() {
		return confidence;
	}

	public double getPercentage() {
		return percentage;
	}

	@Override
	public String toString() {
		return "Rule [lhs=" + lhs + ", rhs=" + rhs + ", upperConf=" + upperConf + ", lowerConf=" + lowerConf
				+ ", confidence=" + confidence + ", percentage=" + percentage + ", upperSupp=" + upperSupp
				+ ", lowerSupp=" + lowerSupp + "]";
	}

}
