package apriori.workflow.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import apriori.logic.algorithm.Apriori;
import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.algorithm.InterimResults;
import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.datageneration.GenerateDataset;
import apriori.logic.rules.Rule;
import apriori.logic.rules.RulesDerived;
import apriori.logic.utility.AprioriException;
import apriori.logic.utility.Property;
import apriori.workflow.elements.CorrectionRow;
import apriori.workflow.elements.CorrectionTable;
import apriori.workflow.elements.FrequentItemRow;
import apriori.workflow.elements.FrequentItemRowCorrection;
import apriori.workflow.elements.HorizontalTable;
import apriori.workflow.elements.ResultApriori;
import apriori.workflow.elements.ResultRules;
import apriori.workflow.elements.RuleExercise;
import apriori.workflow.elements.RuleExerciseCorrection;
import apriori.workflow.elements.TrainingRow;
import apriori.workflow.elements.TrainingTable;

/**
 * service class for training processes
 *
 */
@Service
public class TrainingService {

	private UUID trainingProcessNo;// identifier training run
	private Instant start;// starting time
	private String difficultyLevel;
	private int maxPoints;

//	config dif level
	int noTransactionsLevel;
	int avItemsLevel;
	int maxItemsLevel;
	int minItemsLevel;
	int minSupportLevel;
	int noRulesLevel;
	int askRulesLevel;
	int minConfidenceLevel;
	int feedbackLevel;
	String typeDatasetLevel;

//	algorithm
	private Apriori apriori;
	private AssociationDataSet ads;

//	workflow apriori
	private List<TrainingTable> trainingList;
	private HorizontalTable horizontalTable;
	private List<FrequentItemRow> frequentTable;
	private List<String> availableItems;
	private int stepNumber;

//	workflow apriori correction
	private List<TrainingTable> solutionList;
	private List<CorrectionTable> correctionList;
	private List<FrequentItemRow> frequentTableSolution;
	private String frequentTableCorrect;
	private List<FrequentItemRowCorrection> frequentTableCorretion;

//	rules
	private List<String> fpRules;
	private RulesDerived rulesDerived;
	private int rulesAsked = askRulesLevel;
	private int minConf = minConfidenceLevel;
	private List<String> frequentPatternForRules;

//	workflow rules
	private List<RuleExercise> listRulesStudent;

//	workflow rules correction
	private List<RuleExerciseCorrection> listRulesCorrection;

	ResultApriori resultApriori;
	ResultRules resultRules;

	private final static int scalingFactorTraining = Property.getIntProperty("exercise.eval.training.scalingFactor");
	private final static double interimItemsCorrect = Property.getDoubleProperty("eval.interimresult.items");
	private final static double interimSupportCorrect = Property.getDoubleProperty("eval.interimresult.support");
	private final static double interimRowIncorrect = Property
			.getDoubleProperty("eval.penalty.interimresult.row.incorrect");
	private final static double interimRowForgotten = Property
			.getDoubleProperty("eval.penalty.interimresult.row.forgotten");
	private final static double fpItemsCorrect = Property.getDoubleProperty("eval.frequentpattern.items");
	private final static double fpSupportCorrect = Property.getDoubleProperty("eval.frequentpattern.support");
	private final static double fpRowIncorrect = Property
			.getDoubleProperty("eval.penalty.frequentpattern.row.incorrect");
	private final static double fpRowForgotten = Property
			.getDoubleProperty("eval.penalty.frequentpattern.row.forgotten");
	private final static double ruleCorrect = Property.getDoubleProperty("eval.rule");
	private final static double ruleConfidenceCorrect = Property.getDoubleProperty("eval.confidence");
	private final static double ruleIncorrect = Property.getDoubleProperty("eval.penalty.rule.incorrect");
	private final static double ruleForgotten = Property.getDoubleProperty("eval.penalty.rule.forgotten");
	private final static int noTransactions = Property.getIntProperty("exercise.numberOfTransactions");
	private final static int avItems = Property.getIntProperty("exercise.numberOfAvailableItems");
	private final static int maxItems = Property.getIntProperty("exercise.maxItems");
	private final static int minItems = Property.getIntProperty("exercise.minItems");
	private final static int minSupport = Property.getIntProperty("exercise.minSupport");
	private final static int noRules = Property.getIntProperty("exercise.noRules");
	private final static int askRules = Property.getIntProperty("exercise.askedRules");
	private final static int minConfidence = Property.getIntProperty("exercise.minConfidence");

	private final static String linkETutor = Property.getProperty("etutorplusplus.link");
	
	/**
	 * Constructor
	 */
	public TrainingService() {
		this.trainingProcessNo = UUID.randomUUID();
		this.start = Instant.now();
		try {
			this.setDifficultyLevel(Property.getProperty("difficulty.level.easy"));
		} catch (AprioriException e) {
			e.printStackTrace();
		}
		trainingList = new ArrayList<>();
		frequentTable = new ArrayList<>();
		listRulesStudent = new ArrayList<>();
		maxPoints = Property.getIntProperty("exercise.eval.training.scalingFactor");
	}

	/**
	 * for calculating overall result
	 * 
	 * @return overall result
	 */
	public double calcResult() {
		double result = resultApriori.getSum() + resultRules.getSum();
		return result;
	}

	/**
	 * for counting duplicates interim results
	 * 
	 * @param interimTable single interim result (correction format)
	 * @return
	 */
	private int dublicatesI(CorrectionTable interimTable) {
		int duplicate = 0;
		for (CorrectionRow cr : interimTable.getRow()) {
			cr.setCounted(true);
			if (cr.getItemsCorrect().equals("yes")) {
				for (CorrectionRow crI : interimTable.getRow()) {
					if (crI.getCounted() != true && crI.equalsForDublets(cr)) {
						duplicate++;
						crI.setCounted(true);
					}
				}
			}
		}
		return duplicate;
	}

	/**
	 * for counting duplicates frequent items
	 * 
	 * @return	number of duplicates rows in frequent items
	 */
	private int dublicatesF() {
		int duplicate = 0;
		for (FrequentItemRowCorrection firc : frequentTableCorretion) {
			firc.setCounted(true);
			if (firc.getItemsOK().equals("yes")) {
				for (FrequentItemRowCorrection fircI : frequentTableCorretion) {
					if (fircI.getCounted() != true && fircI.equalsForDublets(firc)) {
						duplicate++;
						fircI.setCounted(true);
					}

				}
			}
		}
		return duplicate;
	}

	/**
	 * for calculating result apriori task
	 * 
	 * @return points apriori task
	 */
	public ResultApriori calcPoints() {
		int countViableItemsFreq = 0;
		int countViableSupFreq = 0;
		int countMalRowFreq = 0;// row wrong, if items wrong
		int countForgottenFreq = 0;
		int frequentTableMal = 0;

		for (FrequentItemRowCorrection fts : frequentTableCorretion) {
			if (fts.getItemsOK().equals("yes")) {
				countViableItemsFreq++;
			}
			if (fts.getCountOK().equals("yes")) {
				countViableSupFreq++;
			} else {
				frequentTableMal++;
			}
			if (fts.getItemsOK().equals("no")) {
				countMalRowFreq++;
			}
		}

		int numberOfRowInSolutionF = frequentTableSolution.size();
		int rightRowsInCorrectionF = frequentTableCorretion.size() - countMalRowFreq;

		if ((numberOfRowInSolutionF - rightRowsInCorrectionF) <= 0) {
			countForgottenFreq = 0;
		} else {
			countForgottenFreq = numberOfRowInSolutionF - rightRowsInCorrectionF;
		}

		int dublicatesF = dublicatesF();// row is a doublet, if items and support are equal (and row right) or if items
										// are equal (and row wrong)
		countMalRowFreq = countMalRowFreq + dublicatesF;

		if (frequentTableSolution.size() != (frequentTableCorretion.size())) {
			setFrequentTableCorrect("no");
		} else {
			if (dublicatesF != 0 || countMalRowFreq != 0 || countForgottenFreq != 0 || frequentTableMal != 0) {
				setFrequentTableCorrect("no");
			} else {
				setFrequentTableCorrect("yes");
			}
		}

		int countViableItemsInter = 0;
		int countViableSupInter = 0;
		int countMalRowInter = 0;
		int countForgottenInter = 0;
		int numberOfRowInSolutionI = 0;

		for (TrainingTable tt : solutionList) {
			numberOfRowInSolutionI = numberOfRowInSolutionI + tt.getRow().size();
		}
		if (correctionList.size() == 0) {
			countForgottenInter = numberOfRowInSolutionI;
		} else {

			for (CorrectionTable ct : correctionList) {
				int countMalRowInterLoop = 0;
				int interimTableMal = 0;

				for (CorrectionRow cr : ct.getRow()) {
					if (cr.getItemsCorrect().equals("yes")) {
						countViableItemsInter++;
					}
					if (cr.getSupportCorrect().equals("yes")) {
						countViableSupInter++;
					} else {
						interimTableMal++;
					}
					if (cr.getItemsCorrect().equals("no")) {
						countMalRowInter++;
						countMalRowInterLoop++;
						interimTableMal++;
					}
				}

				int indexInterim = correctionList.indexOf(ct);
				int dublicatesI = 0;
				if (solutionList.size() - 1 >= indexInterim) {
					int numberOfRowsInInterimSolutionI = solutionList.get(indexInterim).getRow().size();
					int numberOfRowInInterimCorrectionI = ct.getRow().size();
					int rightRowsInCorrectionI = numberOfRowInInterimCorrectionI - countMalRowInterLoop;
					if (rightRowsInCorrectionI <= 0) {
						rightRowsInCorrectionI = 0;
					}
					if ((numberOfRowsInInterimSolutionI - rightRowsInCorrectionI) <= 0) {
						
					} else {
						countForgottenInter = countForgottenInter + numberOfRowsInInterimSolutionI
								- rightRowsInCorrectionI;
					}
					dublicatesI = dublicatesI(ct);// row is a doublet, if items and support are equal (and row right) or
													// if items are equal (and row wrong); meaning if only items are
													// right
					countMalRowInter = countMalRowInter + dublicatesI;
				}

				int indexCorrection = correctionList.indexOf(ct);
				if (solutionList.size() - 1 < indexCorrection) {
					ct.setCorrect("no");
				} else {
					if (solutionList.get(indexCorrection).getRow().size() != (ct.getRow().size())) {
						ct.setCorrect("no");
					} else {
						if (dublicatesI != 0 || interimTableMal != 0) {
							ct.setCorrect("no");
						} else {
							ct.setCorrect("yes");
						}
					}
				}
			}

			if (correctionList.size() < solutionList.size()) {
				int forgottenInterimTables = 0;
				for (int i = (solutionList.size() - correctionList.size()); i < solutionList.size(); i++) {
					forgottenInterimTables = forgottenInterimTables + solutionList.get(i).getRow().size();
				}
				countForgottenInter = countForgottenInter + forgottenInterimTables;
			}
		}

		double evalInterimresultItems = Property.getDoubleProperty("eval.interimresult.items");
		double evalinterimresultsupport = Property.getDoubleProperty("eval.interimresult.support");
		double evalpenaltyinterimresultrowincorrect = Property
				.getDoubleProperty("eval.penalty.interimresult.row.incorrect");
		double evalpenaltyinterimresultrowforgotten = Property
				.getDoubleProperty("eval.penalty.interimresult.row.forgotten");
		double evalfrequentpatternitems = Property.getDoubleProperty("eval.frequentpattern.items");
		double evalfrequentpatternsupport = Property.getDoubleProperty("eval.frequentpattern.support");
		double evalpenaltyfrequentpatternrowincorrect = Property
				.getDoubleProperty("eval.penalty.frequentpattern.row.incorrect");
		double evalpenaltyfrequentpatternrowforgotten = Property
				.getDoubleProperty("eval.penalty.frequentpattern.row.forgotten");

		evalfrequentpatternitems = countViableItemsFreq * (scale(evalfrequentpatternitems));
		evalfrequentpatternsupport = countViableSupFreq * (scale(evalfrequentpatternsupport));
		evalpenaltyfrequentpatternrowincorrect = countMalRowFreq * (scale(evalpenaltyfrequentpatternrowincorrect));
		evalpenaltyfrequentpatternrowforgotten = countForgottenFreq * (scale(evalpenaltyfrequentpatternrowforgotten));
		evalInterimresultItems = countViableItemsInter * (scale(evalInterimresultItems));
		evalinterimresultsupport = countViableSupInter * (scale(evalinterimresultsupport));
		evalpenaltyinterimresultrowincorrect = countMalRowInter * (scale(evalpenaltyinterimresultrowincorrect));
		evalpenaltyinterimresultrowforgotten = countForgottenInter * (scale(evalpenaltyinterimresultrowforgotten));

		ResultApriori result = new ResultApriori(evalfrequentpatternitems, evalfrequentpatternsupport,
				evalpenaltyfrequentpatternrowincorrect, evalpenaltyfrequentpatternrowforgotten, evalInterimresultItems,
				evalinterimresultsupport, evalpenaltyinterimresultrowincorrect, evalpenaltyinterimresultrowforgotten);

		resultApriori = result;
		return result;
	}

	/**
	 * for calculating max points (not scaled)
	 * 
	 * @return maximum points (not scaled)
	 */
	public double getMaxNumberOfPointsUnscaled() {
		double maxPointsBoth = 0;
		int counter = 0;
		for (TrainingTable trainingTable : solutionList) {
			counter = counter + trainingTable.getRow().size();
		}
		double pointsInterim = counter * (Property.getDoubleProperty("eval.interimresult.items")
				+ Property.getDoubleProperty("eval.interimresult.support"));
		double pointsFrequent = frequentTableSolution.size() * (Property.getDoubleProperty("eval.frequentpattern.items")
				+ Property.getDoubleProperty("eval.frequentpattern.support"));
		maxPointsBoth = pointsInterim + pointsFrequent;
		double pointsRules = askRulesLevel * (Property.getDoubleProperty("eval.rule")
				+ Property.getDoubleProperty("eval.confidence") + Property.getDoubleProperty("eval.formula"));
		double maxPointsAll = maxPointsBoth + pointsRules;
		return maxPointsAll;
	}

	/**
	 * for scaling points to given maximum points
	 * 
	 * @param points (not scaled)
	 * @return scaled points
	 */
	public double scale(double points) {
		double scaledValue = (points / getMaxNumberOfPointsUnscaled()) * maxPoints;
		return scaledValue;
	}

	/**
	 * for checking, if all rules are right
	 * 
	 * @return yes, no
	 */
	public String allCorrectRules() {
		String tableOK = "yes";
		if (listRulesCorrection.size() == 0 && (askRulesLevel == 0)) {
			tableOK = "yes";
			return tableOK;
		}
		if (listRulesCorrection.size() == 0 && (askRulesLevel > 0)) {
			tableOK = "no";
			return tableOK;
		}
		if (listRulesCorrection.size() != 0 && (askRulesLevel == 0)) {
			tableOK = "yes";
			return tableOK;
		}
		for (RuleExerciseCorrection rec : listRulesCorrection) {
			if (rec.getRuleOk().equals("no") || rec.getConfOk().equals("no") || rec.getFormulaConfOk().equals("no")) {
				tableOK = "no";
				break;
			}
		}
		return tableOK;
	}

	/**
	 * for preparing rules tables for calculating result
	 */
	public void prepareListRulesCorrection() {
		listRulesCorrection = new ArrayList<>();
		for (RuleExercise re : listRulesStudent) {
			RuleExerciseCorrection rec = new RuleExerciseCorrection(re.getLhs(), re.getRhs(), re.getUpperConf(),
					re.getLowerConf(), re.getConfidence(), re.getPercentage(), re.getUpperSupp(), re.getLowerSupp());
			String ruleOK = ruleCheck(rec);
			rec.setRuleOk(ruleOK);

			if (ruleOK.equals("yes")) {
				String formulaOK = formulaCheck(rec);
				rec.setFormulaConfOk(formulaOK);

				if (formulaOK.equals("yes")) {
					String confOK = confCheck(rec);
					rec.setConfOk(confOK);
				} else {
					rec.setConfOk("no");
				}

			} else {
				rec.setFormulaConfOk("no");
				rec.setConfOk("no");
			}

			rec.setHolisticOk();
			listRulesCorrection.add(rec);
		}
	}

	/**
	 * for checking, if formula in rule is right
	 * 
	 * @param ruleStudent rule (correction format)
	 * @return yes, no
	 */
	private String formulaCheck(RuleExerciseCorrection ruleStudent) {
		String result = "no";
		for (Rule r : rulesDerived.getRules()) {
			if (r.getConfidence() >= (minConf / 100)) {

				String[] adaptedL = r.getLhs().toArray(new String[r.getLhs().size()]);
				String[] adaptedR = r.getRhs().toArray(new String[r.getRhs().size()]);

				if (Arrays.equals(ruleStudent.getLhs(), adaptedL) && Arrays.equals(ruleStudent.getRhs(), adaptedR)) {
					StringBuilder sbU = new StringBuilder();
					for (String s : ruleStudent.getUpperSupp()) {
						sbU.append(s.toString());
					}
					StringBuilder sbL = new StringBuilder();
					for (String s : ruleStudent.getLowerSupp()) {
						sbL.append(s.toString());
					}

					if (sbU.toString().equals(r.getUpperSupp()) && sbL.toString().equals(r.getLowerSupp())) {
						result = "yes";
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * for counting duplicates rules
	 * 
	 * @return number of duplicates rules
	 */
	public int duplicatesRules() {
		int duplicate = 0;
		for (RuleExerciseCorrection rec : listRulesCorrection) {
			rec.setCounted(true);
			if (rec.getRuleOk().equals("yes")) {
				for (RuleExerciseCorrection recR : listRulesCorrection) {
					if (recR.getCounted() != true && recR.equalsForDuplets(rec)) {
						duplicate++;
						rec.setCounted(true);
					}
				}
			}
		}
		return duplicate;
	}

	/**
	 * for calculating result rules task
	 * 
	 * @return points in rules task
	 */
	public ResultRules calcPointsRules() {
		ResultRules result;
		int countViableRules = 0;
		int countViableFormula = 0;
		int countViableConfidence = 0;
		int countMalRow = 0;
		int countForgottenRow = 0;

		for (RuleExerciseCorrection rec : listRulesCorrection) {

			if (rec.getRuleOk().equals("yes")) {
				countViableRules++;
				if (rec.getFormulaConfOk().equals("yes")) {
					countViableFormula++;
				}
				if (rec.getConfOk().equals("yes")) {
					countViableConfidence++;
				}
			} else {
				countMalRow++;
			}
		}

		int duplicates = duplicatesRules();
		countMalRow = countMalRow + duplicates;

		int numberOfRightRulesStudent = listRulesCorrection.size() - countMalRow;
		countForgottenRow = askRulesLevel - numberOfRightRulesStudent;
		double evalrule = Property.getDoubleProperty("eval.rule");
		double evalconfidence = Property.getDoubleProperty("eval.confidence");
		double evalformula = Property.getDoubleProperty("eval.formula");
		double evalpenaltyruleincorrect = Property.getDoubleProperty("eval.penalty.rule.incorrect");
		double evalpenaltyruleforgotten = Property.getDoubleProperty("eval.penalty.rule.incorrect");

		evalrule = countViableRules * (scale(evalrule));
		evalconfidence = countViableConfidence * (scale(evalconfidence));
		evalformula = countViableFormula * (scale(evalformula));
		evalpenaltyruleincorrect = countMalRow * (scale(evalpenaltyruleincorrect));
		evalpenaltyruleforgotten = countForgottenRow * (scale(evalpenaltyruleforgotten));
		result = new ResultRules(evalrule, evalconfidence, evalformula, evalpenaltyruleincorrect,
				evalpenaltyruleforgotten);

		resultRules = result;
		return result;
	}

	/**
	 * for checking, if whole frequent items table is right
	 * 
	 * @return yes, no
	 */
	public String frequentItemsTableCorrect() {
		String tableOK = "yes";
		if (frequentTableCorretion.size() == 0 && frequentTableSolution.size() == 0) {
			tableOK = "yes";
			return tableOK;
		}
		if (frequentTableCorretion.size() == 0 && frequentTableSolution.size() != 0) {
			tableOK = "no";
		}
		for (FrequentItemRowCorrection firc : frequentTableCorretion) {
			if (firc.getItemsOK().equals("no") || firc.getCountOK().equals("no")) {
				tableOK = "no";
			}
		}
		return tableOK;
	}

	/**
	 * for checking, if whole task is right
	 * 
	 * @return yes, no
	 */
	public String allCorrect() {
		String allOk = "yes";

		if (frequentItemsTableCorrect().equals("no") ) {
			allOk = "no";
			return allOk;
		}	
		if (frequentTableCorretion.size()!= frequentTableSolution.size() ) {
			allOk = "no";
			return allOk;
		}
		if (correctionList.size() == 0) {
			allOk = "no";
			return allOk;
		}		
		
		for (CorrectionTable ct : correctionList) {
			if (ct.getCorrect().equals("no")) {
				allOk = "no";
				return allOk;
			}
		}
		if (resultApriori.getCountForgottenFreq() != 0 || resultApriori.getCountMalRowFreq() != 0) {		
			allOk = "no";
			return allOk;
		}
		if (resultApriori.getCountForgottenInter() != 0 || resultApriori.getCountMalRowInter() != 0) {
			allOk = "no";
			return allOk;
		}
		return allOk;
	}

	/**
	 * for checking, if confidence is right
	 * 
	 * @param ruleStudent	rule (correction format)
	 * @return yes, no
	 */
	public String confCheck(RuleExerciseCorrection ruleStudent) {
		String result = "no";
		for (Rule r : rulesDerived.getRules()) {
			if (r.getConfidence() >= (minConf / 100)) {
				String[] adaptedL = r.getLhs().toArray(new String[r.getLhs().size()]);
				String[] adaptedR = r.getRhs().toArray(new String[r.getRhs().size()]);
				if (Arrays.equals(ruleStudent.getLhs(), adaptedL) && Arrays.equals(ruleStudent.getRhs(), adaptedR)) {

					StringBuilder sbU = new StringBuilder();
					for (String s : ruleStudent.getUpperSupp()) {
						sbU.append(s.toString());
					}
					StringBuilder sbL = new StringBuilder();
					for (String s : ruleStudent.getLowerSupp()) {
						sbL.append(s.toString());
					}

					if (sbU.toString().equals(r.getUpperSupp()) && sbL.toString().equals(r.getLowerSupp())) {
						if (ruleStudent.getUpperConf() == r.getUpperConf()
								&& ruleStudent.getLowerConf() == r.getLowerConf()) {
							if (ruleStudent.getPercentage() == r.getPercentage()) {
								result = "yes";
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * for checking, if rule is right
	 * 
	 * @param ruleStudent rule (correction format)
	 * @return yes, no
	 */
	public String ruleCheck(RuleExerciseCorrection ruleStudent) {
		String result = "no";
		for (Rule r : rulesDerived.getRules()) {
			if (r.getConfidence() >= (minConf / 100)) {
				String[] adaptedL = r.getLhs().toArray(new String[r.getLhs().size()]);
				String[] adaptedR = r.getRhs().toArray(new String[r.getRhs().size()]);
				if (Arrays.equals(ruleStudent.getLhs(), adaptedL) && Arrays.equals(ruleStudent.getRhs(), adaptedR)) {
					result = "yes";
					break;
				}
			}
		}
		return result;
	}

	/**
	 * for adding rule to solution
	 * 
	 * @param rule	rule
	 */
	private void addRule(RuleExercise rule) {
		listRulesStudent.add(rule);
	}

	/**
	 * for adding a new rule to solution
	 * 
	 * @param lhs	left hand side rule
	 * @param rhs	right hand side rule
	 * @param upper	upper part formula
	 * @param lower	lower part formula
	 * @param numerator	numerator fraction
	 * @param denominator	denominator fraction
	 * @param percent	confidence in percent
	 */
	public void addNewRule(String lhs, String rhs, String upper, String lower, int numerator, int denominator,
			int percent) {
		RuleExercise rule;
		rule = new RuleExercise(adaptString(lhs), adaptString(rhs), numerator, denominator, percent / 100, percent,
				adaptString(upper), adaptString(lower));
		addRule(rule);
	}

	/**
	 * for deleting rule from solutions list
	 * 
	 * @param lhs	left hand side rule
	 * @param rhs	right hand side rule
	 * @param upper	upper part formula
	 * @param lower	lower part formula
	 * @param numerator	numerator fraction
	 * @param denominator	denominator fraction
	 * @param percent	confidence in percent
	 */
	public void deleteRule(String lhs, String rhs, String upper, String lower, int numerator, int denominator,
			int percent) {
		RuleExercise newRule = new RuleExercise(trimArray(adaptString(lhs)), trimArray(adaptString(rhs)), numerator,
				denominator, 0, percent, trimArray(adaptString(upper)), trimArray(adaptString(lower)));
		for (int i = 0; i < listRulesStudent.size(); i++) {
			if (listRulesStudent.get(i).equals(newRule)) {
				listRulesStudent.remove(i);
				break;
			}
		}
	}

	/**
	 * for trimming strings in array
	 * 
	 * @param strings array of strings to be trimmed
	 * @return	array of trimmed strings
	 */
	private String[] trimArray(String[] strings) {
		String[] trimmedStrings = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			trimmedStrings[i] = strings[i].trim();
		}
		return trimmedStrings;
	}

	/**
	 * for checking row in frequent items, if it is right
	 * 
	 * @param itemsF	items in row
	 * @param supportF	support count for items
	 * @return 1=items right; 2=items+support count right
	 */
	private int checkRowFrequent(String[] itemsF, int supportF) {
		int check = 3;
		for (FrequentItemRow fir : frequentTableSolution) {
			String[] itemsS = fir.getItems();
			if (Arrays.equals(itemsS, itemsF)) {
				check = 1;
				if (fir.getCount() == supportF) {
					check = 2;
				}
				return check;
			}
		}
		return check;
	}

	/**
	 * for preparing frequent table correction table
	 */
	public void prepareFrequentTableCorretion() {
		frequentTableCorretion = new ArrayList<>();

		for (FrequentItemRow fir : frequentTable) {
			frequentTableCorretion.add(new FrequentItemRowCorrection(fir.getCount(), fir.getItems()));
		}
		for (FrequentItemRowCorrection firc : frequentTableCorretion) {
			int count = firc.getCount();
			String[] items = firc.getItems();
			int check = checkRowFrequent(items, count);
			if (check == 1) {
				firc.setItemsOK("yes");
			}
			if (check == 2) {
				firc.setItemsOK("yes");
				firc.setCountOK("yes");
			}
		}
		for (FrequentItemRowCorrection firc : frequentTableCorretion) {
			if (firc.getItemsOK() != "yes") {
				firc.setItemsOK("no");
			}
			if (firc.getCountOK() != "yes") {
				firc.setCountOK("no");
			}
		}
	}

	/**
	 * for checking, if row in interim result is right
	 * 
	 * @param idTableTraining	id single interim result
	 * @param items	items in row
	 * @param supportCount	support count for items
	 * @return 1=items right; 2=items+support count right
	 */
	private int checkRow(int idTableTraining, String[] items, int supportCount) {
		int check = -1;
		for (TrainingTable trainingTable : solutionList) {
			int idTableS = trainingTable.getIdNumber();
			check = -1;
			for (TrainingRow trainingRow : trainingTable.getRow()) {

				if (idTableS == idTableTraining) {

					String[] trainingRowItemsTrimmed = Arrays.stream(trainingRow.getItems()).map(String::trim)
							.toArray(String[]::new);
					trainingRowItemsTrimmed = Stream.of(trainingRowItemsTrimmed).sorted().toArray(String[]::new);

					String[] inpItemsTrimmed = Arrays.stream(items).map(String::trim).toArray(String[]::new);
					inpItemsTrimmed = Stream.of(inpItemsTrimmed).sorted().toArray(String[]::new);

					if (Arrays.equals(trainingRowItemsTrimmed, inpItemsTrimmed)) {
						check = 1;
						if (supportCount == trainingRow.getSupportCount()) {
							check = 2;
						}
						return check;
					}
				}
			}
		}
		return check;
	}

	/**
	 * for preparing interim results for evaluation
	 */
	public void prepareCorretionList() {
		correctionList = new ArrayList<>();
		for (TrainingTable trainingTable : trainingList) {
			CorrectionTable correctionTable = new CorrectionTable();
			correctionTable.setIdNumber(trainingTable.getIdNumber());
			List<CorrectionRow> crList = new ArrayList<>();
			for (TrainingRow tr : trainingTable.getRow()) {
				crList.add(new CorrectionRow(tr.getItems(), tr.getSupportCount()));
			}
			correctionTable.setRow(crList);
			correctionList.add(correctionTable);
		}

		int indexTable = 0;
		for (CorrectionTable correctionTable : correctionList) {
			int idTable = correctionTable.getIdNumber();
			int indexRow = 0;
			for (CorrectionRow correctionRow : correctionTable.getRow()) {
				int check = checkRow(idTable, correctionRow.getItemsTraining(),
						correctionRow.getSupportCountTraining());
				if (check == 1) {
					correctionList.get(indexTable).getRow().get(indexRow).setItemsCorrect("yes");
				}
				if (check == 2) {
					correctionList.get(indexTable).getRow().get(indexRow).setItemsCorrect("yes");
					correctionList.get(indexTable).getRow().get(indexRow).setSupportCorrect("yes");
				}
				indexRow = indexRow + 1;
			}
			indexTable = indexTable + 1;
		}

		for (CorrectionTable correctionTable : correctionList) {
			for (CorrectionRow correctionRow : correctionTable.getRow()) {
				if (correctionRow.getItemsCorrect() != "yes") {
					correctionRow.setItemsCorrect("no");
				}
				if (correctionRow.getSupportCorrect() != "yes") {
					correctionRow.setSupportCorrect("no");
					;
				}
			}
		}

		for (CorrectionTable correctionTable : correctionList) {
			for (CorrectionRow correctionRow : correctionTable.getRow()) {
				correctionRow.setCalcHolisticCorrect();
			}
		}
	}

	/**
	 * for deleting row in interim result (including depending rows)
	 * 
	 * @param items	items to delete
	 * @param supportCount	support count (items) to delete
	 * @param step	stage in apriori algorithm
	 */
	public void deleteInterim(String items, int supportCount, int step) {
		String[] stringToArray = null;
		stringToArray = items.split(",");
		String[] itemsArray = new String[stringToArray.length];
		for (int i = 0; i < stringToArray.length; i++) {
			itemsArray[i] = stringToArray[i].trim();
		}
		int index = -1;
		for (int i = 0; i < trainingList.get(step - 1).getRow().size(); i++) {
			if (Arrays.equals(itemsArray, trainingList.get(step - 1).getRow().get(i).getItems())
					& supportCount == trainingList.get(step - 1).getRow().get(i).getSupportCount()) {
				index = i;
				break;
			}
		}
		List<Integer> forDeletion = new ArrayList<>();
		for (int i = 0; i < trainingList.size(); i++) {
			if (trainingList.get(i).getIdNumber() > step) {
				forDeletion.add(i);
			}
		}
		Collections.reverse(forDeletion);
		for (int i : forDeletion) {
			trainingList.remove(i);
		}
		if (index != -1) {
			trainingList.get(step - 1).getRow().remove(index);
		}
		setStepNumber(step);
		if (trainingList.get(step - 1).getRow().size() == 0) {
			trainingList.remove(step - 1);
			setStepNumber(step);
		}
	}

	/**
	 * for deleting row in frequent items
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items
	 */
	public void deleteFrequent(String items, int supportCount) {
		String[] stringToArray = null;
		stringToArray = items.split(",");
		String[] itemsArray = new String[stringToArray.length];
		for (int i = 0; i < stringToArray.length; i++) {
			itemsArray[i] = stringToArray[i].trim();
		}
		int index = -1;
		for (int i = 0; i < frequentTable.size(); i++) {
			if (Arrays.equals(itemsArray, frequentTable.get(i).getItems())
					& supportCount == frequentTable.get(i).getCount()) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			frequentTable.remove(index);
		}
	}

	/**
	 * for adding new interim results row
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items
	 */
	public void setNewInterimRow(String items, int supportCount) {
		String[] rowSorted = adaptString(items.trim());
		Arrays.sort(rowSorted);
		TrainingRow newTrainingRow = new TrainingRow(rowSorted, supportCount);
		boolean checkTableId = false;
		for (TrainingTable trainingTab : trainingList) {
			if (trainingTab.getIdNumber() == stepNumber) {
				checkTableId = true;
			}
		}
		if (checkTableId == false) {
			TrainingTable trainingTable = new TrainingTable();
			trainingTable.setIdNumber(stepNumber);
			trainingTable.getRow().add(newTrainingRow);
			trainingList.add(trainingTable);
		} else {
			for (TrainingTable trainingTable : trainingList) {
				if (trainingTable.getIdNumber() == stepNumber) {
					trainingTable.getRow().add(newTrainingRow);
				}
			}
		}
	}

	/**
	 * for adding new frequent items row
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items
	 */
	public void setNewFrequentRow(String items, int supportCount) {
		FrequentItemRow newFrequentItemRow = new FrequentItemRow(supportCount, adaptString(items));
		frequentTable.add(newFrequentItemRow);
	}

	/**
	 * for adapting a string to a string array
	 * 
	 * @param string to be adapted to string array
	 * @return	array with strings
	 */
	private String[] adaptString(String string) {
		String input2 = string.toUpperCase();
		String[] inp2 = input2.split(",");
		
		String [] trimmed = new String[inp2.length];
		for (int i=0; i<inp2.length; i=i+1) {
			trimmed[i]=inp2[i].trim();
		}		
		
		Arrays.sort(trimmed);
		return trimmed;
	}

	/**
	 * for checking, if an interim table is empty
	 * 
	 * @return true, false
	 */
	public boolean emptyInterim() {
		if (trainingList.size() < stepNumber) {
			return true;
		}
		return false;
	}

	/**
	 * for increasing step (stage in apriori algorithm)
	 */
	public void nextStep() {
		if (trainingList.size() < stepNumber) {
			return;
		}
		stepNumber = stepNumber + 1;
	}

	/**
	 * for decreasing step (stage in apriori algorithm)
	 */
	public void lastStep() {
		stepNumber = stepNumber - 1;
	}

	/**
	 * for generating a new data set
	 * 
	 * @param noTransactionsLevelMod : Number of transactions in data set
	 * @param avItemsLevelMod        : Number of available items
	 * @param maxItemsLevelMod       : Maximum number of items in transaction
	 * @param minItemsLevelMod       : Minimum number of items in transaction
	 * @param minSupportLevelMod     : Minimum Support for apriori algorithm
	 * @param noRulesLevelMod        : Number of Elements to generate derived rules
	 *                               from
	 * @param askRulesLevelMod       : Number of rules to query
	 * @param minConfidenceLevelMod  : Minimum confidence for deriving rules
	 *                               (percent)
	 * @param typeDatasetLevelMod    : Type of data set
	 */
	public void generateNewDataset(int noTransactionsLevelMod, int avItemsLevelMod, int maxItemsLevelMod,
			int minItemsLevelMod, int minSupportLevelMod,
			int noRulesLevelMod, int askRulesLevelMod, int minConfidenceLevelMod, String typeDatasetLevelMod) {
		ads = GenerateDataset.generateDataset(typeDatasetLevelMod, avItemsLevelMod, noTransactionsLevelMod,
				maxItemsLevelMod, minItemsLevelMod);

		apriori = new Apriori(ads, minSupportLevelMod);
		apriori.start();
	}

	/**
	 * for calculating apriori and belonging rules
	 */
	public void calculateTasks() {
		setAvailableItems(apriori.getAvailableItems());
		HorizontalTable hTable = new HorizontalTable();
		hTable.setList(hTable.makeList(ads));
		horizontalTable = hTable;
		resetList();
		setStepNumber(1);
		setSolutionList();
		setFrequentTableSolution();
		frequentPatternForRules = new ArrayList<>();

		for (FrequentItemRow fir : frequentTableSolution) {
			if (fir.getItems().length == noRulesLevel) {
				frequentPatternForRules.addAll(Arrays.asList(fir.getItems()));
				break;
			}
		}

		if (frequentPatternForRules.size() == 0 && frequentTableSolution.size() > 0) {
			int indexMax = -1;
			int sizeMax = 0;
			for (FrequentItemRow f : frequentTableSolution) {
				if (f.getItems().length > sizeMax) {
					sizeMax = f.getItems().length;
					indexMax = frequentTableSolution.indexOf(f);
				}
			}
			frequentPatternForRules.addAll(Arrays.asList(frequentTableSolution.get(indexMax).getItems()));
		}

		setFpRules(frequentPatternForRules);
		RulesDerived rd = new RulesDerived(ads, apriori, frequentPatternForRules);
		int viableRules = 0;
		for (Rule r : rd.getRules()) {
			if (r.getPercentage() >= minConfidenceLevel) {
				viableRules++;
			}
		}
		if (viableRules < askRulesLevel) {
			askRulesLevel = viableRules;
		}
		rulesDerived = rd;
	}

	/**
	 * for setting parameters for a configuration
	 * 
	 * @param noTransactionsLevel	number of transactions in data set
	 * @param avItemsLevel	number of available items to generate row from
	 * @param maxItemsLevel	maximum number of items in a row
	 * @param minItemsLevel	minimum number of items in a row
	 * @param minSupportLevel	minimum support for the apriori algorithm
	 * @param noRulesLevel	number of items to derive a rule 
	 * @param askRulesLevel	number of rules queried 
	 * @param minConfidenceLevel	minimum confidence for rules
	 * @param types	type of data set
	 */
	public void setParams(int noTransactionsLevel, int avItemsLevel, int maxItemsLevel, int minItemsLevel,
			int minSupportLevel, int noRulesLevel, int askRulesLevel, int minConfidenceLevel, String types) {
		this.noTransactionsLevel = noTransactionsLevel;
		this.avItemsLevel = avItemsLevel;
		this.maxItemsLevel = maxItemsLevel;
		this.minItemsLevel = minItemsLevel;
		this.minSupportLevel = minSupportLevel;
		this.minConfidenceLevel = minConfidenceLevel;
		this.noRulesLevel = noRulesLevel;
		this.askRulesLevel = askRulesLevel;
		this.typeDatasetLevel = types;
	}

	/**
	 * for generating solution frequent items table
	 */
	public void setFrequentTableSolution() {
		frequentTableSolution = new ArrayList<>();
		for (AprioriDataBaseRow adbr : apriori.getFrequentPatterns().getFrequentPatterns()) {
			frequentTableSolution.add(new FrequentItemRow(adbr.getIndicator(), adbr.getItemset().getItems()));
		}
	}

	/**
	 * for generating solution interim results
	 */
	public void setSolutionList() {
		List<InterimResults> interimList = apriori.getInterimResultsPrunedOnly();
		solutionList = new ArrayList<>();
		for (InterimResults interimResult : interimList) {
			List<TrainingRow> listTrainingRow = new ArrayList<>();
			for (AprioriDataBaseRow row : interimResult.getAdb().getDb()) {
				TrainingRow trainingRow = new TrainingRow(row.getItemset().getItems(), row.getIndicator());
				listTrainingRow.add(trainingRow);
			}
			TrainingTable trainingTable = new TrainingTable();
			int id = Integer.parseInt(interimResult.getId().replaceAll("[^0-9]", " ").replaceAll(" +", ""));
			trainingTable.setIdNumber(id);
			trainingTable.setRow(listTrainingRow);
			solutionList.add(trainingTable);
		}
	}

	/**
	 * for clearing interim results, frequent items and rules
	 */
	public void resetList() {
		if (trainingList != null) {
			trainingList.clear();
		}
		if (frequentTable != null) {
			frequentTable.clear();
		}
		if (listRulesStudent != null) {
			listRulesStudent.clear();
		}
	}

	/**
	 * for verifying difficulty level
	 * 
	 * @param difficultyLevel difficulty level
	 * @return	true,false
	 */
	public boolean checkDiff(String difficultyLevel) {
		if (difficultyLevel != null && (difficultyLevel.equals(Property.getProperty("difficulty.level.easy"))
				| difficultyLevel.equals(Property.getProperty("difficulty.level.moderate"))
				| difficultyLevel.equals(Property.getProperty("difficulty.level.hard"))
				| difficultyLevel.equals(Property.getProperty("difficulty.level.very_hard")))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * for setting difficulty level
	 * 
	 * @param difficultyLevel	difficulty level
	 * @throws AprioriException
	 */
	public void setDifficultyLevel(String difficultyLevel) throws AprioriException {
		String level;
		if (difficultyLevel.equals(Property.getProperty("difficulty.level.easy"))
				| difficultyLevel.equals(Property.getProperty("difficulty.level.moderate"))
				| difficultyLevel.equals(Property.getProperty("difficulty.level.hard"))
				| difficultyLevel.equals(Property.getProperty("difficulty.level.very_hard"))) {
			level = difficultyLevel;
		} else {
			throw new AprioriException("no viable difficulty level");
		}
		difficultyLevel = level;
		this.setDifficultyDetails(difficultyLevel);
	}

	/**
	 * for setting details of a difficulty level
	 * 
	 * @param difficultyLevel	difficulty level
	 */
	private void setDifficultyDetails(String difficultyLevel) {
		String retrievalMarker = "difficulty.level." + difficultyLevel;
		this.difficultyLevel = Property.getProperty(retrievalMarker);
		noTransactionsLevel = Property.getIntProperty(retrievalMarker + ".numberOfTransactions");
		avItemsLevel = Property.getIntProperty(retrievalMarker + ".numberOfAvailableItems");
		maxItemsLevel = Property.getIntProperty(retrievalMarker + ".maxItems");
		minItemsLevel = Property.getIntProperty(retrievalMarker + ".minItems");
		minSupportLevel = Property.getIntProperty(retrievalMarker + ".minSupport");
		noRulesLevel = Property.getIntProperty(retrievalMarker + ".noRules");
		askRulesLevel = Property.getIntProperty(retrievalMarker + ".askedRules");
		minConfidenceLevel = Property.getIntProperty(retrievalMarker + ".minConfidence");
		feedbackLevel = Property.getIntProperty(retrievalMarker + ".feedbackLevel");
		typeDatasetLevel = Property.getProperty(retrievalMarker + ".typeDataset");
	}

	public String getFrequentTableCorrect() {
		return frequentTableCorrect;
	}

	public void setFrequentTableCorrect(String frequentTableCorrect) {
		this.frequentTableCorrect = frequentTableCorrect;
	}

	public List<RuleExerciseCorrection> getListRulesCorrection() {
		return listRulesCorrection;
	}

	public List<RuleExercise> getListRulesStudent() {
		return listRulesStudent;
	}

	public Apriori getApriori() {
		return apriori;
	}

	public List<FrequentItemRowCorrection> getFrequentTableCorretion() {
		return frequentTableCorretion;
	}

	public List<CorrectionTable> getCorrectionList() {
		return correctionList;
	}

	public List<FrequentItemRow> getFrequentTable() {
		return frequentTable;
	}

	public List<TrainingTable> getTrainingList() {
		return trainingList;
	}

	public List<String> getAvailableItems() {
		return availableItems;
	}

	public void setStepNumber(int newStepNumber) {
		stepNumber = newStepNumber;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	private void setAvailableItems(List<String> avItems) {
		availableItems = avItems;
	}

	public HorizontalTable getHorizontalTable() {
		return horizontalTable;
	}

	public void setFpRules(List<String> fpRules) {
		this.fpRules = fpRules;
	}

	public RulesDerived getRulesDerived() {
		return rulesDerived;
	}

	public List<String> getFrequentPatternForRules() {
		return frequentPatternForRules;
	}

	public UUID getTrainingProcessNo() {
		return trainingProcessNo;
	}

	public Instant getStart() {
		return start;
	}

	public String getDifficultyLevel() {
		return difficultyLevel;
	}

	public int getMinConf() {
		return minConf;
	}

	public int getRulesAsked() {
		return rulesAsked;
	}

	public List<String> getFpRules() {
		return fpRules;
	}

	public List<FrequentItemRow> getFrequentTableSolution() {
		return frequentTableSolution;
	}

	public List<TrainingTable> getSolutionList() {
		return solutionList;
	}

	public String getTypeDatasetLevel() {
		return typeDatasetLevel;
	}

	public void setTypeDatasetLevel(String typeDatasetLevel) {
		this.typeDatasetLevel = typeDatasetLevel;
	}

	public int getNoTransactionsLevel() {
		return noTransactionsLevel;
	}

	public void setNoTransactionsLevel(int noTransactionsLevel) {
		this.noTransactionsLevel = noTransactionsLevel;
	}

	public int getAvItemsLevel() {
		return avItemsLevel;
	}

	public void setAvItemsLevel(int avItemsLevel) {
		this.avItemsLevel = avItemsLevel;
	}

	public int getMaxItemsLevel() {
		return maxItemsLevel;
	}

	public void setMaxItemsLevel(int maxItemsLevel) {
		this.maxItemsLevel = maxItemsLevel;
	}

	public int getMinItemsLevel() {
		return minItemsLevel;
	}

	public void setMinItemsLevel(int minItemsLevel) {
		this.minItemsLevel = minItemsLevel;
	}

	public int getMinSupportLevel() {
		return minSupportLevel;
	}

	public void setMinSupportLevel(int minSupportLevel) {
		this.minSupportLevel = minSupportLevel;
	}

	public int getNoRulesLevel() {
		return noRulesLevel;
	}

	public void setNoRulesLevel(int noRulesLevel) {
		this.noRulesLevel = noRulesLevel;
	}

	public int getAskRulesLevel() {
		return askRulesLevel;
	}

	public void setAskRulesLevel(int askRulesLevel) {
		this.askRulesLevel = askRulesLevel;
	}

	public int getMinConfidenceLevel() {
		return minConfidenceLevel;
	}

	public void setMinConfidenceLevel(int minConfidenceLevel) {
		this.minConfidenceLevel = minConfidenceLevel;
	}

	public int getFeedbackLevel() {
		return feedbackLevel;
	}

	public void setFeedbackLevel(int feedbackLevel) {
		this.feedbackLevel = feedbackLevel;
	}

	public static int getScalingfactortraining() {
		return scalingFactorTraining;
	}

	public static double getInterimitemscorrect() {
		return interimItemsCorrect;
	}

	public static double getInterimsupportcorrect() {
		return interimSupportCorrect;
	}

	public static double getInterimrowincorrect() {
		return interimRowIncorrect;
	}

	public static double getInterimrowforgotten() {
		return interimRowForgotten;
	}

	public static double getFpitemscorrect() {
		return fpItemsCorrect;
	}

	public static double getFpsupportcorrect() {
		return fpSupportCorrect;
	}

	public static double getFprowincorrect() {
		return fpRowIncorrect;
	}

	public static double getFprowforgotten() {
		return fpRowForgotten;
	}

	public static double getRulecorrect() {
		return ruleCorrect;
	}

	public static double getRuleconfidencecorrect() {
		return ruleConfidenceCorrect;
	}

	public static double getRuleincorrect() {
		return ruleIncorrect;
	}

	public static double getRuleforgotten() {
		return ruleForgotten;
	}

	public static int getNotransactions() {
		return noTransactions;
	}

	public static int getAvitems() {
		return avItems;
	}

	public static int getMaxitems() {
		return maxItems;
	}

	public static int getMinitems() {
		return minItems;
	}

	public static int getMinsupport() {
		return minSupport;
	}

	public static int getNorules() {
		return noRules;
	}

	public static int getAskrules() {
		return askRules;
	}

	public static int getMinconfidence() {
		return minConfidence;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public ResultApriori getResultApriori() {
		return resultApriori;
	}

	public ResultRules getResultRules() {
		return resultRules;
	}

	public static String getLinketutor() {
		return linkETutor;
	}

	@Override
	public String toString() {
		return "TrainingService [trainingProcessNo=" + trainingProcessNo + ", start=" + start + ", difficultyLevel="
				+ difficultyLevel + ", maxPoints=" + maxPoints + ", noTransactionsLevel=" + noTransactionsLevel
				+ ", avItemsLevel=" + avItemsLevel + ", maxItemsLevel=" + maxItemsLevel + ", minItemsLevel="
				+ minItemsLevel + ", minSupportLevel=" + minSupportLevel + ", noRulesLevel=" + noRulesLevel
				+ ", askRulesLevel=" + askRulesLevel + ", minConfidenceLevel=" + minConfidenceLevel + ", feedbackLevel="
				+ feedbackLevel + ", typeDatasetLevel=" + typeDatasetLevel + ", apriori=" + apriori + ", ads=" + ads
				+ ", trainingList=" + trainingList + ", horizontalTable=" + horizontalTable + ", frequentTable="
				+ frequentTable + ", availableItems=" + availableItems + ", stepNumber=" + stepNumber
				+ ", solutionList=" + solutionList + ", correctionList=" + correctionList + ", frequentTableSolution="
				+ frequentTableSolution + ", frequentTableCorrect=" + frequentTableCorrect + ", frequentTableCorretion="
				+ frequentTableCorretion + ", fpRules=" + fpRules + ", rulesDerived=" + rulesDerived + ", rulesAsked="
				+ rulesAsked + ", minConf=" + minConf + ", frequentPatternForRules=" + frequentPatternForRules
				+ ", listRulesStudent=" + listRulesStudent + ", listRulesCorrection=" + listRulesCorrection
				+ ", resultApriori=" + resultApriori + ", resultRules=" + resultRules + "]";
	}

}
