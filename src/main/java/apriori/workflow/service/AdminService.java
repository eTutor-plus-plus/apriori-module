package apriori.workflow.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apriori.logic.algorithm.AprioriDataBaseRow;
import apriori.logic.algorithm.InterimResults;
import apriori.db.AprioriConfig;
import apriori.db.HorizontalTransaction;
import apriori.db.HorizontalTransactionService;
import apriori.logic.algorithm.Apriori;
import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.datageneration.GenerateDataset;
import apriori.logic.rules.DerivingRules;
import apriori.logic.rules.Rule;
import apriori.logic.rules.RulesDerived;
import apriori.logic.utility.DbUtility;
import apriori.logic.utility.Property;
import apriori.workflow.elements.FrequentItemRow;
import apriori.workflow.elements.HorizontalTable;
import apriori.workflow.elements.TrainingRow;
import apriori.workflow.elements.TrainingTable;

//!!disabled feature!!
//import apriori.workflow.export.ExportMapRepository;

/**
 * service class for administration processes
 *
 */
@Service
public class AdminService {

	@Autowired
	HorizontalTransactionService horizontalService;

//	!!disabled feature!!
//	@Autowired
//	ExportMapRepository repo;

	private UUID adminProcessNo;//	identifier admin run

	private Instant start;//	starting time

	private String difficultyLevel;

	private String adminId;

	private int scalingFactor;

	private String nameDataset;
	private String nameConfig;

	private UUID idDataset;
	private UUID idTaskConfig;

//	config difficulty level
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

	private List<String> availableItems;
	private HorizontalTable horizontalTable;
	private List<FrequentItemRow> frequentTableSolution;
	private List<TrainingTable> solutionList;

//	rules
	private List<String> fpRules;
	private RulesDerived rulesDerived;
	private int rulesAsked = askRulesLevel;
	private int minConf = minConfidenceLevel;
	private List<String> frequentPatternForRules;
	private List<String> ruleList;
	private int totalNumberOfRules;

//	constants
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
	 * @param difficultyLevel	difficulty level tasks
	 * @param adminId	user id
	 */
	public AdminService(String difficultyLevel, String adminId) {
		super();
		this.difficultyLevel = difficultyLevel;
		this.adminId = adminId;
		this.adminProcessNo = UUID.randomUUID();
		this.start = Instant.now();
		this.difficultyLevel = convertDifficulty(difficultyLevel);
		setDifficultyLevelFromConstants(this.difficultyLevel);
		this.horizontalService = new HorizontalTransactionService();
		this.nameDataset = "na";
		this.nameConfig = "na";
	}

	/**
	 * Constructor
	 * @param adminId	user id
	 */
	public AdminService(String adminId) {
		this.adminProcessNo = UUID.randomUUID();
		this.start = Instant.now();
		this.adminId = adminId;
		this.horizontalService = new HorizontalTransactionService();
		this.nameDataset = "na";
		this.nameConfig = "na";
	}

	/**
	 * Constructor
	 */
	public AdminService() {
		this.horizontalService = new HorizontalTransactionService();
	}

	/**
	 * for converting difficulty level formats
	 * 
	 * @param level	difficulty level tasks
	 * @return difficulty level (extension format)
	 */
	public static String convertDifficulty(String level) {
		String result = "";

		if (level.contains("taskManagement.difficulties") == true) {

			if (level.equals(Property.getProperty("difficulty.level.et.ad.easy"))) {
				result = Property.getProperty("difficulty.level.easy");
			}
			if (level.equals(Property.getProperty("difficulty.level.et.ad.moderate"))) {
				result = Property.getProperty("difficulty.level.moderate");
			}
			if (level.equals(Property.getProperty("difficulty.level.et.ad.hard"))) {
				result = Property.getProperty("difficulty.level.hard");
			}
			if (level.equals(Property.getProperty("difficulty.level.et.ad.very_hard"))) {
				result = Property.getProperty("difficulty.level.very_hard");
			}
		} else {
			return level;
		}

		return result;
	}

	/**
	 * for retrieving all data sets
	 * 
	 * @return list transactions
	 */
	public List<HorizontalTransaction> getAllDatasets() {
		List<HorizontalTransaction> list = horizontalService.findAllSets();
		return list;
	}

//	!!disabled feature!!
//	/**
//	 * for exporting task config
//	 * 
//	 * @param configId	id task configuration (uuid)
//	 */
//	public void saveExport(UUID configId) {
//		repo.setConfigId(adminId, configId);
//	}

	/**
	 * for retrieving all datasets for transaction list
	 * 
	 * @param list	list of HorizontalTransaction
	 * @return	list of existing sets (uuid's)
	 */
	public List<UUID> existingSets(List<HorizontalTransaction> list) {
		List<UUID> sets = new ArrayList<>();
		for (HorizontalTransaction ht : list) {
			sets.add(ht.getUuid_ht());
		}
		return sets;
	}

	/**
	 * for retrieving all task configs from task config list
	 * 
	 * @param list	list of AprioriConfig
	 * @return	list of existing task configurations (uuid's)
	 */
	public List<UUID> existingConfigs(List<AprioriConfig> list) {
		List<UUID> configs = new ArrayList<>();
		for (AprioriConfig ac : list) {
			configs.add(ac.getUuid_ac());
		}
		return configs;
	}

	/**
	 * for setting difficulty levels from app constants
	 * 
	 * @param difficultyLevel	difficulty level tasks
	 */
	public void setDifficultyLevelFromConstants(String difficultyLevel) {
		String retrievalMarker = "difficulty.level." + difficultyLevel;
		difficultyLevel = Property.getProperty(retrievalMarker);
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

	/**
	 * for generating new data set
	 * 
	 * @param noTransactionsLevelMod	number of transactions in data set
	 * @param avItemsLevelMod	number of available items to generate row from
	 * @param maxItemsLevelMod	maximum number of items in a row
	 * @param minItemsLevelMod	minimum number of items in a row
	 * @param typeDatasetLevelMod	type of data set
	 */
	private void generateNewDataset(int noTransactionsLevelMod, int avItemsLevelMod, int maxItemsLevelMod,
			int minItemsLevelMod, String typeDatasetLevelMod) {
		ads = GenerateDataset.generateDataset(typeDatasetLevelMod, avItemsLevelMod, noTransactionsLevelMod,
				maxItemsLevelMod, minItemsLevelMod);
	}

	/**
	 * for calculating necessary tasks after retrieving stored task configuration
	 * 
	 * @param ht	list of HorizontalTransaction
	 * @param uuid_ds	id data set (uuid)
	 * @param nameDatasetI	name of data set
	 * @param uuid_ac	id task configuration (uuid)
	 * @param minSup	minimum support for the apriori algorithm
	 * @param minConf	minimum confidence for rules
	 * @param noElements	number of items to derive a rule 
	 * @param numAskedRules	number of rules queried 
	 * @param nameAC	name of the task configuration
	 */
	public void calculateTaskStoredConfig(List<HorizontalTransaction> ht, String uuid_ds, String nameDatasetI,
			String uuid_ac, int minSup, int minConf, int noElements, int numAskedRules, String nameAC) {

		AssociationDataSet adss = DbUtility.transformHorizontalTransaction(ht);
		ads = adss;
		nameDataset = nameDatasetI;
		nameConfig = nameAC;
		minSupportLevel = minSup;
		askRulesLevel = numAskedRules;
		minConfidenceLevel = minConf;
		noRulesLevel = noElements;

		HorizontalTable tempTable = new HorizontalTable();
		tempTable.setList(tempTable.makeList(ads));
		horizontalTable = tempTable;

		apriori = new Apriori(ads, minSup);

		apriori.start();
		setAvailableItems(apriori.getAvailableItems());
		setSolutionList();
		setFrequentTableSolution();

		idDataset = UUID.fromString(uuid_ds);
		idTaskConfig = UUID.fromString(uuid_ac);

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
		RulesDerived tempRulesDerived = new RulesDerived(ads, apriori, frequentPatternForRules);
		
		int viableRules = 0;
		for (Rule r : tempRulesDerived.getRules()) {
			if (r.getPercentage() >= minConfidenceLevel) {
				viableRules++;
			}
		}

		if (viableRules < askRulesLevel) {
			askRulesLevel = viableRules;
		}		

		rulesDerived = tempRulesDerived;
		totalNumberOfRules = tempRulesDerived.getRules().size();
		List<List<String>> rfp = DerivingRules.getRulesFromFP(ads, apriori, frequentPatternForRules);
		ruleList = DerivingRules.getRuleResultList(rfp);

	}

	/**
	 * for calculating necessary task after retrieving stored data set
	 * 
	 * @param ht	list of HorizontalTransaction
	 * @param uuid_ds	id data set (uuid)
	 * @param nameDatasetI	name of data set
	 */
	public void calculateTaskStoredDS(List<HorizontalTransaction> ht, String uuid_ds, String nameDatasetI) {

		AssociationDataSet adss = DbUtility.transformHorizontalTransaction(ht);

		ads = adss;
		nameDataset = nameDatasetI;
		minSupportLevel = 2;
		minConfidenceLevel = 60;
		askRulesLevel = 2;
		noRulesLevel = 2;

		HorizontalTable tempTable = new HorizontalTable();
		tempTable.setList(tempTable.makeList(ads));
		horizontalTable = tempTable;
		apriori = new Apriori(ads, minSupportLevel);
		apriori.start();
		setAvailableItems(apriori.getAvailableItems());
		setSolutionList();
		setFrequentTableSolution();

		idDataset = UUID.fromString(uuid_ds);

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
		RulesDerived tempRulesDerived = new RulesDerived(ads, apriori, frequentPatternForRules);

		int viableRules = 0;
		for (Rule r : tempRulesDerived.getRules()) {
			if (r.getPercentage() >= minConfidenceLevel) {
				viableRules++;
			}
		}

		if (viableRules < askRulesLevel) {
			askRulesLevel = viableRules;
		}		

		rulesDerived = tempRulesDerived;
		totalNumberOfRules = tempRulesDerived.getRules().size();
		List<List<String>> rfp = DerivingRules.getRulesFromFP(ads, apriori, frequentPatternForRules);
		ruleList = DerivingRules.getRuleResultList(rfp);
	}

	/**
	 * for initially calculating apriori and belonging rules
	 * 
	 * @param noTransactionsLevelMod	number of transactions in data set
	 * @param avItemsLevelMod	number of available items to generate row from
	 * @param maxItemsLevelMod	maximum number of items in a row
	 * @param minItemsLevelMod	minimum number of items in a row
	 * @param typeDatasetLevelMod	type of data set
	 * @param noRulesLevelMod	number of items to derive a rule (disabled)
	 */
	public void calculateTasksInital(int noTransactionsLevelMod, int avItemsLevelMod, int maxItemsLevelMod,
			int minItemsLevelMod, String typeDatasetLevelMod, int noRulesLevelMod) {

		noTransactionsLevel = noTransactionsLevelMod;
		avItemsLevel = avItemsLevelMod;
		maxItemsLevel = maxItemsLevelMod;
		minItemsLevel = minItemsLevelMod;
		typeDatasetLevel = typeDatasetLevelMod;
		noRulesLevel = 2;
		askRulesLevel = 1;
		minConfidenceLevel = 50;

		nameDataset = "na";
		nameConfig = "na";

		generateNewDataset(noTransactionsLevelMod, avItemsLevelMod, maxItemsLevelMod, minItemsLevelMod,
				typeDatasetLevelMod);
		HorizontalTable tempTable = new HorizontalTable();
		tempTable.setList(tempTable.makeList(ads));
		horizontalTable = tempTable;
		apriori = new Apriori(ads, minSupportLevel);
		apriori.start();
		setAvailableItems(apriori.getAvailableItems());
		setSolutionList();
		setFrequentTableSolution();

		idDataset = UUID.randomUUID();
		idTaskConfig = UUID.randomUUID();

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
		RulesDerived tempRulesDerived = new RulesDerived(ads, apriori, frequentPatternForRules);

		int viableRules = 0;
		for (Rule r : tempRulesDerived.getRules()) {
			if (r.getPercentage() >= minConfidenceLevel) {
				viableRules++;
			}
		}

		if (viableRules < askRulesLevel) {
			askRulesLevel = viableRules;
		}

		rulesDerived = tempRulesDerived;
		totalNumberOfRules = tempRulesDerived.getRules().size();
		List<List<String>> rfp = DerivingRules.getRulesFromFP(ads, apriori, frequentPatternForRules);
		ruleList = DerivingRules.getRuleResultList(rfp);
	}
	
	/**
	 * for checking, if rule viable (for config view)
	 * 
	 * @param row	rule (string)
	 * @param border	minimum confidence
	 * @return yes or empty
	 */
	public static String getWithinConstraint(String row, double border) {
		int index = row.lastIndexOf("=");
		String lastPart = row.substring(index + 1);
		index = lastPart.length() - 1;
		lastPart = lastPart.substring(0, index);
		double extract = Double.valueOf(lastPart);

		if (extract >= border) {
			return "yes";
		} else {
			return "";
		}
	}

	/**
	 * for calculating task necessary for new task config
	 * 
	 * @param minSupport	minimum support for the apriori algorithm
	 * @param rulesElements	number of items to derive a rule 
	 * @param rulesAsked	number of rules queried 
	 * @param minConfidence	minimum confidence for rules
	 */
	public void calculateNewTaskConfig(int minSupport, int rulesElements, int rulesAsked, int minConfidence) {

		minSupportLevel = minSupport;
		askRulesLevel = rulesAsked;
		minConfidenceLevel = minConfidence;
		noRulesLevel = rulesElements;
		idTaskConfig = UUID.randomUUID();

		apriori = new Apriori(ads, minSupportLevel);
		apriori.start();
		setAvailableItems(apriori.getAvailableItems());
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
			noRulesLevel = frequentTableSolution.get(indexMax).getItems().length;
		}
		setFpRules(frequentPatternForRules);
		RulesDerived tempRulesDerived = new RulesDerived(ads, apriori, frequentPatternForRules);

		int viableRules = 0;
		for (Rule r : tempRulesDerived.getRules()) {
			if (r.getPercentage() >= minConfidenceLevel) {
				viableRules++;
			}
		}

		if (viableRules < askRulesLevel) {
			askRulesLevel = viableRules;
		}

		rulesDerived = tempRulesDerived;
		totalNumberOfRules = tempRulesDerived.getRules().size();
		List<List<String>> rfp = DerivingRules.getRulesFromFP(ads, apriori, frequentPatternForRules);
		ruleList = DerivingRules.getRuleResultList(rfp);
	}

	/**
	 * for saving data set
	 * 
	 * @param name	name of the data set
	 */
	public void saveDataset(String name) {
		horizontalService.saveDataset(ads, idDataset, name);
	}

	/**
	 * for making frequent items solution table
	 */
	public void setFrequentTableSolution() {
		apriori.getFrequentPatterns().displayTable();
		frequentTableSolution = new ArrayList<>();
		for (AprioriDataBaseRow adbr : apriori.getFrequentPatterns().getFrequentPatterns()) {
			frequentTableSolution.add(new FrequentItemRow(adbr.getIndicator(), adbr.getItemset().getItems()));
		}
	}

	/**
	 * for making interim results solution table
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
	 * for verifying uuid's from initial variable
	 * 
	 * @param map	map of parameters from initial variable
	 * @return	true, false
	 */
	public static boolean checkInitialLinkCreate(Map<String, String> map) {

		if (!DbUtility.verifyUUID(map.get("idOne")) || !DbUtility.verifyUUID(map.get("idTwo"))
				|| !DbUtility.verifyUUID(map.get("idThree"))) {
			return false;
		}
		return true;
	}

	/**
	 * for setting new parameter for data set setup
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

	public UUID getAdminProcessNo() {
		return adminProcessNo;
	}

	public UUID getIdTaskConfig() {
		return idTaskConfig;
	}

	public void setIdTaskConfig(UUID idTaskConfig) {
		this.idTaskConfig = idTaskConfig;
	}

	public static int getAvitems() {
		return avItems;
	}

	public static int getNotransactions() {
		return noTransactions;
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

	public int getFeedbackLevel() {
		return feedbackLevel;
	}

	public void setFeedbackLevel(int feedbackLevel) {
		this.feedbackLevel = feedbackLevel;
	}

	public UUID getIdDataset() {
		return idDataset;
	}

	public void setIdDataset(UUID idDataset) {
		this.idDataset = idDataset;
	}

	public String getNameConfig() {
		return nameConfig;
	}

	public void setNameConfig(String nameConfig) {
		this.nameConfig = nameConfig;
	}

	public String getNameDataset() {
		return nameDataset;
	}

	public void setNameDataset(String nameDataset) {
		this.nameDataset = nameDataset;
	}

	public int getTotalNumberOfRules() {
		return totalNumberOfRules;
	}

	public int getRulesAsked() {
		return rulesAsked;
	}

	public int getAskRulesLevel() {
		return askRulesLevel;
	}

	public List<String> getFrequentPatternForRules() {
		return frequentPatternForRules;
	}

	public int getMinSupportLevel() {
		return minSupportLevel;
	}

	public int getNoRulesLevel() {
		return noRulesLevel;
	}

	public List<String> getRuleList() {
		return ruleList;
	}

	public List<String> getFpRules() {
		return fpRules;
	}

	public void setFpRules(List<String> fpRules) {
		this.fpRules = fpRules;
	}

	public List<FrequentItemRow> getFrequentTableSolution() {
		return frequentTableSolution;
	}

	public List<TrainingTable> getSolutionList() {
		return solutionList;
	}

	public int getMinConfidenceLevel() {
		return minConfidenceLevel;
	}

	public HorizontalTable getHorizontalTable() {
		return horizontalTable;
	}

	public AssociationDataSet getAds() {
		return ads;
	}

	public void setAds(AssociationDataSet ads) {
		this.ads = ads;
	}

	public String getTypeDatasetLevel() {
		return typeDatasetLevel;
	}

	public int getMinItemsLevel() {
		return minItemsLevel;
	}

	public int getMaxItemsLevel() {
		return maxItemsLevel;
	}

	public int getAvItemsLevel() {
		return avItemsLevel;
	}

	public int getNoTransactionsLevel() {
		return noTransactionsLevel;
	}

	public List<String> getAvailableItems() {
		return availableItems;
	}

	private void setAvailableItems(List<String> availableItems) {
		this.availableItems = availableItems;
	}

	public Instant getStart() {
		return start;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public int getScalingFactor() {
		return scalingFactor;
	}

	public void setAdminProcessNo(UUID adminProcessNo) {
		this.adminProcessNo = adminProcessNo;
	}

	public void setStart(Instant start) {
		this.start = start;
	}

	public void setScalingFactor(int scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	public void setDifficultyLevel(String difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public String getDifficultyLevel() {
		return difficultyLevel;
	}

	public static String getLinketutor() {
		return linkETutor;
	}

}
