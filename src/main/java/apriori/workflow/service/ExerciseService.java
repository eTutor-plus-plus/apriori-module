package apriori.workflow.service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import apriori.db.AprioriConfig;
import apriori.db.HorizontalTransaction;
import apriori.logic.algorithm.Apriori;
import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.algorithm.InterimResults;
import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.rules.Rule;
import apriori.logic.rules.RulesDerived;
import apriori.logic.utility.DbUtility;
import apriori.logic.utility.Property;
import apriori.logic.utility.SPARQLUtility;
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
 * service class for exercise processes
 *
 */
@Service
public class ExerciseService {

	private UUID exerciseProcessNo;// identifier exercise run

	private Instant start;// starting time
	private String userId;
	private int maxPoints;
	private UUID _taskUUID;

	private UUID taskConfigId;// id task configuration
	private int _taskNo;
	private UUID courseInstanceUUID;
	private UUID exerciseSheetUUID;
	private int feedbackLevel;
	private String difficultyLevel;
	private int minSupportLevel;
	private int minConfidenceLevel;
	private int askRulesLevel;
	private int noRulesLevel;

//	algorithm
	private Apriori apriori;
	private AssociationDataSet ads;

//	workflow apriori
	private List<TrainingTable> exerciseList;
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

	// evaluation results
	ResultApriori resultApriori;
	ResultRules resultRules;

//	rules
	private List<String> fpRules;
	private RulesDerived rulesDerived;
	private int minConf = minConfidenceLevel;
	private List<String> frequentPatternForRules;

//	workflow rules
	private List<RuleExercise> listRulesStudent;

//	workflow rules correction
	private List<RuleExerciseCorrection> listRulesCorrection;

	private final static String linkETutor = Property.getProperty("etutorplusplus.link");
	
	/**
	 * constructor
	 */
	public ExerciseService() {
	}

	/**
	 * constructor
	 * 
	 * @param userId	user id
	 * @param maxPoints	maximum reachable points in both tasks
	 * @param taskUUID task id (uuid)
	 */
	public ExerciseService(String userId, int maxPoints, String taskUUID) {
		this.userId = userId;
		this.exerciseProcessNo = UUID.randomUUID();
		this.start = Instant.now();
		this.maxPoints = maxPoints;
		this._taskUUID = UUID.fromString(taskUUID);
	}

	/**
	 * constructor
	 * 
	 * @param userId	user id
	 * @param _taskNo	number of task 
	 * @param courseInstanceUUID	course instance id (uuid)
	 * @param exerciseSheetUUID		exercise sheet id (uuid)
	 * @param maxPoints		maximum reachable points in both tasks 
	 * @param taskConfigId	configuration id (uuid)
	 * @param difficultyLevel	difficulty level tasks
	 */
	public ExerciseService(String userId, int _taskNo, String courseInstanceUUID, String exerciseSheetUUID,
			int maxPoints, String taskConfigId, String difficultyLevel) {
		this.userId = userId;
		this.exerciseProcessNo = UUID.randomUUID();
		this.start = Instant.now();
		this.maxPoints = maxPoints;
		this.taskConfigId = UUID.fromString(taskConfigId);
		this.difficultyLevel = difficultyLevel;
		this._taskNo = _taskNo;
		this.courseInstanceUUID = UUID.fromString(courseInstanceUUID);
		this.exerciseSheetUUID = UUID.fromString(exerciseSheetUUID);

		if (difficultyLevel.equals(Property.getProperty("difficulty.level.et.easy"))) {
			this.difficultyLevel = Property.getProperty("difficulty.level.easy");
		}
		if (difficultyLevel.equals(Property.getProperty("difficulty.level.et.moderate"))) {
			this.difficultyLevel = Property.getProperty("difficulty.level.moderate");
		}
		if (difficultyLevel.equals(Property.getProperty("difficulty.level.et.hard"))) {
			this.difficultyLevel = Property.getProperty("difficulty.level.hard");
		}
		if (difficultyLevel.equals(Property.getProperty("difficulty.level.et.very_hard"))) {
			this.difficultyLevel = Property.getProperty("difficulty.level.very_hard");
		}

		String retrievalMarker = "difficulty.level." + this.difficultyLevel;
		this.feedbackLevel = Property.getIntProperty(retrievalMarker + ".feedbackLevel");
		exerciseList = new ArrayList<>();
		frequentTable = new ArrayList<>();
		listRulesStudent = new ArrayList<>();
	}

	/**
	 * for retrieving result for both tasks
	 * 
	 * @return overall result
	 */
	public double calcResult() {
		double result = resultApriori.getSum() + resultRules.getSum();
		return result;
	}

	/**
	 * for counting duplicates in correction (interim results)
	 * 
	 * @param interimTable
	 * @return number duplicates
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
	 * for counting duplicates frequent items correction
	 * 
	 * @return	number of duplicates row from frequent items
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
	 * for calculating point for apriori task
	 * 
	 * @return apriori result object
	 */
	public ResultApriori calcPoints() {
		int countViableItemsFreq = 0;// for counting right items (frequent items)
		int countViableSupFreq = 0;// for counting right support count (frequent items)
		int countMalRowFreq = 0;// for counting wrong rows (row wrong, if items wrong) (frequent items)
		int countForgottenFreq = 0;// for counting forgotten row (row forgotten, if relevant items not present)
									// (frequent items)
		int frequentTableMal = 0; // for counting wrong support count (frequent items)

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

		int countViableItemsInter = 0;// for counting right items (interim results)
		int countViableSupInter = 0;// for counting right support count (interim results)
		int countMalRowInter = 0;// for counting wrong rows (row wrong, if items wrong) (interim results)
		int countForgottenInter = 0;// for counting forgotten row (row forgotten, if relevant items not present)
									// (interim results)
		int numberOfRowInSolutionI = 0;// for counting number of rows solution

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
						interimTableMal++;
						countForgottenInter = countForgottenInter + numberOfRowsInInterimSolutionI
								- rightRowsInCorrectionI;
					}

					dublicatesI = dublicatesI(ct);// row is a doublet, if items and support are equal (and row right) or
													// if items are equal (and row wrong)
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
	 * for getting max points (not scaled)
	 * 
	 * @return	maximum achievable point in both tasks (not scaled)
	 */
	private double getMaxNumberOfPointsUnscaled() {
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
	 * for scaling points
	 * 
	 * @param points (not scaled)
	 * @return scaled points
	 */
	public double scale(double points) {
		double scaledValue = (points / getMaxNumberOfPointsUnscaled()) * maxPoints;
		return scaledValue;
	}

	/**
	 * for preparing tables for calculating tasks for exercise run
	 * 
	 * @param config	task configuration
	 * @param dataset	data set in list format
	 */
	public void prepareExercise(AprioriConfig config, List<HorizontalTransaction> dataset) {
		this.minSupportLevel = config.getMinsupport();
		this.minConfidenceLevel = config.getMinconfidence();
		this.askRulesLevel = config.getNumrulesasked();
		this.noRulesLevel = config.getNumelementsrule();

		ads = DbUtility.transformHorizontalTransaction(dataset);
		apriori = new Apriori(ads, minSupportLevel);
		apriori.start();

		setAvailableItems(apriori.getAvailableItems());
		HorizontalTable hTable = new HorizontalTable();
		hTable.setList(hTable.makeList(ads));
		horizontalTable = hTable;
		resetList();
		setStepNumber(1);
		setSolutionList();
		setFrequentTableSolution();

		frequentPatternForRules = new ArrayList<>();

		for (FrequentItemRow row : frequentTableSolution) {
			if (row.getItems().length == noRulesLevel) {
				frequentPatternForRules.addAll(Arrays.asList(row.getItems()));
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

		if (rd.getRules().size() < askRulesLevel) {
			askRulesLevel = viableRules;
		}
		rulesDerived = rd;
	}

	/**
	 * for setting up solution table for frequent items
	 */
	public void setFrequentTableSolution() {
		frequentTableSolution = new ArrayList<>();
		for (AprioriDataBaseRow adbr : apriori.getFrequentPatterns().getFrequentPatterns()) {
			frequentTableSolution.add(new FrequentItemRow(adbr.getIndicator(), adbr.getItemset().getItems()));
		}
	}

	/**
	 * for setting up solution table interim results
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
	 * for resetting variables for exercise restart
	 */
	public void resetList() {
		if (exerciseList != null) {
			exerciseList.clear();
		}
		if (frequentTable != null) {
			frequentTable.clear();
		}
		if (listRulesStudent != null) {
			listRulesStudent.clear();
		}
	}

	/**
	 * for adapting string format
	 * 
	 * @param string
	 * @return string array
	 */
	private String[] adaptString(String string) {
		String input2 = string.toUpperCase();
		String[] inp2 = input2.split(",");
		for (String s : inp2) {
			System.out.println(s);
		}
		Arrays.sort(inp2);
		return inp2;
	}

	/**
	 * for checking for empty interim result table
	 * 
	 * @return true, false
	 */
	public boolean emptyInterim() {
		if (exerciseList.size() < stepNumber) {
			return true;
		}
		return false;
	}

	/**
	 * for increasing interim result table (stage in apriori algorithm)
	 */
	public void nextStep() {
		if (exerciseList.size() < stepNumber) {
			return;
		}
		stepNumber = stepNumber + 1;
	}

	/**
	 * for decreasing interim result table (stage in apriori algorithm)
	 */
	public void lastStep() {
		if (stepNumber > 1) {
			stepNumber = stepNumber - 1;
		}
	}

	/**
	 * for adding new row interim result
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items
	 */
	public void setNewInterimRow(String items, int supportCount) {
		String[] rowSorted = adaptString(items.trim());
		Arrays.sort(rowSorted);
		TrainingRow newTrainingRow = new TrainingRow(rowSorted, supportCount);
		boolean checkTableId = false;
		for (TrainingTable exerciseTab : exerciseList) {
			if (exerciseTab.getIdNumber() == stepNumber) {
				checkTableId = true;
			}
		}
		if (checkTableId == false) {
			TrainingTable exerciseTable = new TrainingTable();
			exerciseTable.setIdNumber(stepNumber);
			exerciseTable.getRow().add(newTrainingRow);
			exerciseList.add(exerciseTable);
		} else {
			for (TrainingTable exerciseTable : exerciseList) {
				if (exerciseTable.getIdNumber() == stepNumber) {
					exerciseTable.getRow().add(newTrainingRow);
				}
			}
		}
	}

	/**
	 * for adding new row frequent items
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items
	 */
	public void setNewFrequentRow(String items, int supportCount) {
		FrequentItemRow newFrequentItemRow = new FrequentItemRow(supportCount, adaptString(items));
		frequentTable.add(newFrequentItemRow);
	}

	/**
	 * for deleting row interim result
	 * 
	 * @param items	items in row
	 * @param supportCount	support count for items
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
		for (int i = 0; i < exerciseList.get(step - 1).getRow().size(); i++) {
			if (Arrays.equals(itemsArray, exerciseList.get(step - 1).getRow().get(i).getItems())
					& supportCount == exerciseList.get(step - 1).getRow().get(i).getSupportCount()) {
				index = i;
				break;
			}
		}
		List<Integer> forDeletion = new ArrayList<>();
		for (int i = 0; i < exerciseList.size(); i++) {
			if (exerciseList.get(i).getIdNumber() > step) {
				forDeletion.add(i);
			}
		}
		Collections.reverse(forDeletion);
		for (int i : forDeletion) {
			exerciseList.remove(i);
		}
		if (index != -1) {
			exerciseList.get(step - 1).getRow().remove(index);
		}
		setStepNumber(step);
		if (exerciseList.get(step - 1).getRow().size() == 0) {
			exerciseList.remove(step - 1);
			setStepNumber(step);
		}
	}

	/**
	 * for deleting row frequent items
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
	 * for preparing table for evaluation (interim results)
	 */
	public void prepareCorretionList() {
		correctionList = new ArrayList<>();
		for (TrainingTable exerciseTable : exerciseList) {
			CorrectionTable correctionTable = new CorrectionTable();
			correctionTable.setIdNumber(exerciseTable.getIdNumber());

			List<CorrectionRow> crList = new ArrayList<>();
			for (TrainingRow tr : exerciseTable.getRow()) {

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
	 * for evaluating row (interim result)
	 * 
	 * @param idTableTraining	id of interim result table
	 * @param items	items in row
	 * @param supportCount	support count for items
	 * @return -1=wrong; 1=items ok; 2=items+support count ok
	 */
	private int checkRow(int idTableTraining, String[] items, int supportCount) {
		int check = -1;
		for (TrainingTable exerciseTable : solutionList) {
			int idTableS = exerciseTable.getIdNumber();
			check = -1;
			for (TrainingRow trainingRow : exerciseTable.getRow()) {

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
	 * for preparing frequent items table for evaluation
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
	 * for evaluating, if frequent items ok (whole table)
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
	 * for evaluating, if whole task is ok (both parts)
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
	 * for evaluating, if row ok (frequent items)
	 * 
	 * @param itemsF
	 * @param supportF
	 * @return 3=wrong; 1=items ok; 2=items+support count ok
	 */
	private int checkRowFrequent(String[] itemsF, int supportF) {
		int check = 3;// convention: 1=items ok; 2=items+supportCount ok;
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
	 * for adding rule
	 * 
	 * @param rule rule
	 */
	private void addRule(RuleExercise rule) {
		listRulesStudent.add(rule);
	}

	/**
	 * for adding new rule
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
		rule = new RuleExercise(adaptString(lhs), adaptString(rhs), numerator, denominator, 0, percent,
				adaptString(upper), adaptString(lower));
		addRule(rule);
	}

	/**
	 * for deleting rule
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
	 * @param strings	string array to be trimmed
	 * @return trimmed strings in array
	 */
	private String[] trimArray(String[] strings) {
		String[] trimmedStrings = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			trimmedStrings[i] = strings[i].trim();
		}
		return trimmedStrings;
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
	 * for evaluating, if all rules are correct
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
	 * for calculating point for rules task
	 * 
	 * @return rules result object
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
	 * for setting up table for rules evaluation
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
	 * for evaluating rule
	 * 
	 * @param ruleStudent	rule (correction format)
	 * @return yes, no
	 */
	public String ruleCheck(RuleExerciseCorrection ruleStudent) {
		String result = "no";
		for (Rule r : rulesDerived.getRules()) {
			if (r.getConfidence() >= (minConfidenceLevel / 100)) {
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
	 * for evaluating formula rule
	 * 
	 * @param ruleStudent	rule (correction format)
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
	 * for evaluating confidence rule
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
							DecimalFormat d2Format = new DecimalFormat("0");
							if (Double.parseDouble(d2Format.format(ruleStudent.getPercentage())) == Double
									.parseDouble(d2Format.format(r.getPercentage()))) {
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
	 * for checking, if exercise was already submitted
	 * 
	 * @return true, false
	 */
	public boolean checkSubmitted() {
		return SPARQLUtility.checkIfSubmitted(userId, exerciseSheetUUID.toString(), courseInstanceUUID.toString(),
				_taskNo);
	}

	/**
	 * for checking, if sheet is closed
	 * 
	 * @return true, false
	 */
	public boolean checkClosed() {
		return SPARQLUtility.checkIfIsClosed(exerciseSheetUUID.toString(), courseInstanceUUID.toString());
	}
	
	/**
	 * for saving evaluation result
	 */
	public void saveResult() {
		if (checkSubmitted() == false) {	
			if (checkClosed() == true) {		
				SPARQLUtility.setPoints(userId, exerciseSheetUUID.toString(), courseInstanceUUID.toString(), _taskNo,
					calcResult());
			}
		}
	}

	/**
	 * for preparing for redoing rules task
	 */
	public void cleanRulesLists() {
		listRulesStudent.removeAll(listRulesStudent);
		listRulesCorrection.removeAll(listRulesCorrection);
	}

	/**
	 * for preparing for redoing apriori task
	 */
	public void cleanAprioriLists() {
		resetList();
		setStepNumber(1);
	}

	/**
	 * for verifying initial variable
	 * 
	 * @param map	parameter submitted by initial variable (starting conditions exercise)
	 * @return true, false
	 */
	public static boolean checkInitialLink(Map<String, String> map) {
		if (!DbUtility.verifyUUID(map.get("courseInstanceUUID")) || !DbUtility.verifyUUID(map.get("exerciseSheetUUID"))
				|| !DbUtility.verifyUUID(map.get("taskConfigId"))) {
			return false;
		}
		return true;
	}

	public List<String> getFpRules() {
		return fpRules;
	}

	public String getFrequentTableCorrect() {
		return frequentTableCorrect;
	}

	public void setFrequentTableCorrect(String frequentTableCorrect) {
		this.frequentTableCorrect = frequentTableCorrect;
	}

	public UUID get_taskUUID() {
		return _taskUUID;
	}

	public void set_taskUUID(UUID _taskUUID) {
		this._taskUUID = _taskUUID;
	}

	public int get_taskNo() {
		return _taskNo;
	}

	public void set_taskNo(int _taskNo) {
		this._taskNo = _taskNo;
	}

	public UUID getCourseInstanceUUID() {
		return courseInstanceUUID;
	}

	public void setCourseInstanceUUID(UUID courseInstanceUUID) {
		this.courseInstanceUUID = courseInstanceUUID;
	}

	public UUID getExerciseSheetUUID() {
		return exerciseSheetUUID;
	}

	public void setExerciseSheetUUID(UUID exerciseSheetUUID) {
		this.exerciseSheetUUID = exerciseSheetUUID;
	}

	public ResultRules getResultRules() {
		return resultRules;
	}

	public ResultApriori getResultApriori() {
		return resultApriori;
	}

	public List<RuleExerciseCorrection> getListRulesCorrection() {
		return listRulesCorrection;
	}

	public List<RuleExercise> getListRulesStudent() {
		return listRulesStudent;
	}

	public List<String> getFrequentPatternForRules() {
		return frequentPatternForRules;
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

	public void setFpRules(List<String> fpRules) {
		this.fpRules = fpRules;
	}

	public int getNoRulesLevel() {
		return noRulesLevel;
	}

	public void setNoRulesLevel(int noRulesLevel) {
		this.noRulesLevel = noRulesLevel;
	}

	public List<TrainingTable> getExerciseList() {
		return exerciseList;
	}

	public void setExerciseList(List<TrainingTable> exerciseList) {
		this.exerciseList = exerciseList;
	}

	public HorizontalTable getHorizontalTable() {
		return horizontalTable;
	}

	public void setHorizontalTable(HorizontalTable horizontalTable) {
		this.horizontalTable = horizontalTable;
	}

	public List<FrequentItemRow> getFrequentTable() {
		return frequentTable;
	}

	public void setFrequentTable(List<FrequentItemRow> frequentTable) {
		this.frequentTable = frequentTable;
	}

	public List<String> getAvailableItems() {
		return availableItems;
	}

	public void setAvailableItems(List<String> availableItems) {
		this.availableItems = availableItems;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public int getFeedbackLevel() {
		return feedbackLevel;
	}

	public void setFeedbackLevel(int feedbackLevel) {
		this.feedbackLevel = feedbackLevel;
	}

	public String getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(String difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public int getMinSupportLevel() {
		return minSupportLevel;
	}

	public void setMinSupportLevel(int minSupportLevel) {
		this.minSupportLevel = minSupportLevel;
	}

	public int getMinConfidenceLevel() {
		return minConfidenceLevel;
	}

	public void setMinConfidenceLevel(int minConfidenceLevel) {
		this.minConfidenceLevel = minConfidenceLevel;
	}

	public int getAskRulesLevel() {
		return askRulesLevel;
	}

	public void setAskRulesLevel(int askRulesLevel) {
		this.askRulesLevel = askRulesLevel;
	}

	public void setExerciseProcessNo(UUID exerciseProcessNo) {
		this.exerciseProcessNo = exerciseProcessNo;
	}

	public UUID getTaskConfigId() {
		return taskConfigId;
	}

	public void setTaskConfigId(UUID taskConfigId) {
		this.taskConfigId = taskConfigId;
	}

	public UUID getTaskUUID() {
		return _taskUUID;
	}

	public void setTaskUUID(UUID taskUUID) {
		this._taskUUID = taskUUID;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public UUID getExerciseProcessNo() {
		return exerciseProcessNo;
	}

	public Instant getStart() {
		return start;
	}

	public String getUserId() {
		return userId;
	}

	public static String getLinketutor() {
		return linkETutor;
	}

	@Override
	public String toString() {
		return "ExerciseService [exerciseProcessNo=" + exerciseProcessNo + ", start=" + start + ", userId=" + userId
				+ ", maxPoints=" + maxPoints + ", _taskUUID=" + _taskUUID + ", taskConfigId=" + taskConfigId
				+ ", _taskNo=" + _taskNo + ", courseInstanceUUID=" + courseInstanceUUID + ", exerciseSheetUUID="
				+ exerciseSheetUUID + ", feedbackLevel=" + feedbackLevel + ", difficultyLevel=" + difficultyLevel
				+ ", minSupportLevel=" + minSupportLevel + ", minConfidenceLevel=" + minConfidenceLevel
				+ ", askRulesLevel=" + askRulesLevel + ", noRulesLevel=" + noRulesLevel + ", apriori=" + apriori
				+ ", ads=" + ads + ", exerciseList=" + exerciseList + ", horizontalTable=" + horizontalTable
				+ ", frequentTable=" + frequentTable + ", availableItems=" + availableItems + ", stepNumber="
				+ stepNumber + ", solutionList=" + solutionList + ", correctionList=" + correctionList
				+ ", frequentTableSolution=" + frequentTableSolution + ", frequentTableCorrect=" + frequentTableCorrect
				+ ", frequentTableCorretion=" + frequentTableCorretion + ", resultApriori=" + resultApriori
				+ ", resultRules=" + resultRules + ", fpRules=" + fpRules + ", rulesDerived=" + rulesDerived
				+ ", minConf=" + minConf + ", frequentPatternForRules=" + frequentPatternForRules
				+ ", listRulesStudent=" + listRulesStudent + ", listRulesCorrection=" + listRulesCorrection + "]";
	}

}
