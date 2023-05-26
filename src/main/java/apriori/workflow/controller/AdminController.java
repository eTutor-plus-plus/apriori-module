package apriori.workflow.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apriori.db.AprioriConfig;
import apriori.db.AprioriConfigService;
import apriori.db.HorizontalTransaction;
import apriori.db.HorizontalTransactionService;
import apriori.logic.utility.DbUtility;
import apriori.workflow.service.AdminService;
import apriori.workflow.user.management.AdminParticipantService;

//!!disabled feature!!
//import apriori.workflow.export.ExportMapRepository;

/**
 * controller for administration processes
 */
@Controller
class AdminController {

	@Autowired
	AdminParticipantService repo;

	@Autowired
	HorizontalTransactionService horizontalService;

	@Autowired
	AprioriConfigService configService;

//!!disabled feature!!
//	@Autowired
//	ExportMapRepository repoExport;

	/**
	 * for starting storage
	 * 
	 * @param model     required by framework to render data inside a view
	 * @param initalVar starting condition for administration (generated by the
	 *                  eTutor++)
	 * @return reference view
	 */
	@GetMapping("/initialTables")
	public String initialTables(Model model, @RequestParam(value = "initalVar", required = false) String initalVar) {

		Map<String, String> map = DbUtility.decryptInitialVariableCreate(initalVar);

		if (map == null) {
			return "redirect:/sorry...?message=(Decryption failure)";
		}

		boolean check = AdminService.checkInitialLinkCreate(map);

		if (check == false) {
			return "redirect:/sorry...?message=(Decryption failure)";
		}

		repo.cleanParticipants();

		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		UUID adminProcessNo = repo.initialTable(map.get("user"));
		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		return "redirect:/tables?adminProcessNo=" + adminService.getAdminProcessNo().toString();
	}

	/**
	 * for restarting storage
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @return reference view
	 */
	@GetMapping("/restartTables")
	public String restartTables(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo) {

		repo.cleanParticipants();
		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		String user = adminService.getAdminId();

		repo.removeAdmin(UUID.fromString(adminProcessNo));

		adminProcessNo = repo.initialTable(user).toString();

		adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		return "redirect:/tables?adminProcessNo=" + adminService.getAdminProcessNo().toString();
	}

	/**
	 * for viewing data sets
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @return reference view
	 */
	@GetMapping("/tables")
	public String tables(Model model, @RequestParam(value = "adminProcessNo", required = true) String adminProcessNo) {

		repo.cleanParticipants();
		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		List<HorizontalTransaction> datasetList = horizontalService.listAllDatasets();
		model.addAttribute("datasetList", datasetList);

		model.addAttribute("adminService", adminService);

		return "adminStored";
	}

	/**
	 * for viewing task configurations
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param idDataset      id data set (uuid)
	 * @return reference view
	 */
	@GetMapping("/taskConfigs")
	public String taskConfigs(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "idDataset", required = true) String idDataset) {
		repo.cleanParticipants();
		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		List<AprioriConfig> cl = configService.findConfigByUuid_ht(UUID.fromString(idDataset));

		model.addAttribute("cl", cl);
		model.addAttribute("idDataset", idDataset);
		model.addAttribute("adminService", adminService);

		return "adminStoredConfig";
	}

	/**
	 * for deleting data set
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param idDataset      id data set (uuid)
	 * @return reference view
	 */
	@GetMapping("/deleteDataset")
	public String deleteDataset(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "idDataset", required = true) String idDataset) {
		repo.cleanParticipants();
		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		configService.deleteConfigByDataset(UUID.fromString(idDataset));

		horizontalService.deleteDataset(UUID.fromString(idDataset));

		return "redirect:/tables?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for deleting task configurations
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param idDataset      id data set (uuid)
	 * @param idConfig       id task configuration (uuid)
	 * @return reference view
	 */
	@GetMapping("/deleteConfig")
	public String deleteConfig(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "idDataset", required = true) String idDataset,
			@RequestParam(value = "idConfig", required = true) String idConfig) {

		repo.cleanParticipants();

		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		configService.deleteByUuid_ac(UUID.fromString(idConfig));

		model.addAttribute("adminService", adminService);

		return "redirect:/taskConfigs?adminProcessNo=" + adminProcessNo + "&idDataset=" + idDataset;
	}

	/**
	 * for loading stored task configurations
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param uuid_ds        id data set (uuid)
	 * @param uuid_ac        id task configuration (uuid)
	 * @return reference view
	 */
	@GetMapping("/loadStoredConfig")
	public String loadStoredConfig(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "uuid_ds", required = true) String uuid_ds,
			@RequestParam(value = "uuid_ac", required = true) String uuid_ac) {

		repo.cleanParticipants();

		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		if (horizontalService.checkDatasetExists(UUID.fromString(uuid_ds.toString())) == false
				|| configService.checkConfigExists(UUID.fromString(uuid_ac)) == false) {
			return "redirect:/sorry...?message=(data record not found)";
		}

		List<HorizontalTransaction> ht = horizontalService.findDatasetByUuid(UUID.fromString(uuid_ds));

		List<AprioriConfig> ac = configService.getAprioriConfig(UUID.fromString(uuid_ac));

		adminService.calculateTaskStoredConfig(ht, uuid_ds, ht.get(0).getComment(), uuid_ac, ac.get(0).getMinsupport(),
				ac.get(0).getMinconfidence(), ac.get(0).getNumelementsrule(), ac.get(0).getNumrulesasked(),
				ac.get(0).getComment());

		model.addAttribute("adminService", adminService);

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for loading stored data set
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param uuid_ds        id data set (uuid)
	 * @return reference view
	 */
	@GetMapping("/loadStoredDataset")
	public String loadStoredDataset(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "uuid_ds", required = true) String uuid_ds) {

		repo.cleanParticipants();
		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		List<HorizontalTransaction> ht = horizontalService.findDatasetByUuid(UUID.fromString(uuid_ds.toString()));

		adminService.calculateTaskStoredDS(ht, uuid_ds, ht.get(0).getComment());

		model.addAttribute("adminService", adminService);

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for starting task creation
	 * 
	 * @param model     required by framework to render data inside a view
	 * @param initalVar starting condition for administration (generated by the
	 *                  eTutor++)
	 * @return reference view
	 */
	@GetMapping("/initialTasks")
	public String initialTasks(Model model, @RequestParam(value = "initalVar", required = false) String initalVar) {

		Map<String, String> map = DbUtility.decryptInitialVariableCreate(initalVar);

		if (map == null) {
			return "redirect:/sorry...?message=(Decryption failure)";
		}

		boolean check = AdminService.checkInitialLinkCreate(map);

		if (check == false) {
			return "redirect:/sorry...?message=(Decryption failure)";
		}

		repo.cleanParticipants();

		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}
		UUID adminProcessNo = repo.initialTasks(map);

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		adminService.calculateTasksInital(adminService.getNoTransactionsLevel(), adminService.getAvItemsLevel(),
				adminService.getMaxItemsLevel(), adminService.getMinItemsLevel(), adminService.getTypeDatasetLevel(),
				adminService.getNoRulesLevel());

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for generating new data set
	 * 
	 * @param model               required by framework to render data inside a view
	 * @param adminProcessNo      id of the administration process (uuid)
	 * @param noTransactionsLevel number of transactions in data set
	 * @param avItemsLevel        number of available items to generate row from
	 * @param maxItemsLevel       maximum number of items in a row
	 * @param minItemsLevel       minimum number of items in a row
	 * @param types               type of data set
	 * @param difficultyLevel     difficulty level of tasks
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminDataset")
	public String newSetupAdminDataset(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "noTransactionsLevel", required = true) int noTransactionsLevel,
			@RequestParam(value = "avItemsLevel", required = true) int avItemsLevel,
			@RequestParam(value = "maxItemsLevel", required = true) int maxItemsLevel,
			@RequestParam(value = "minItemsLevel", required = true) int minItemsLevel,
			@RequestParam(value = "types", required = true) String types,
			@RequestParam(value = "difficultyLevel", required = true) String difficultyLevel

	) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		adminService.calculateTasksInital(noTransactionsLevel, avItemsLevel, maxItemsLevel, minItemsLevel, types, 2);

		adminService.setParams(noTransactionsLevel, avItemsLevel, maxItemsLevel, minItemsLevel,
				adminService.getMinSupportLevel(), adminService.getNoRulesLevel(), adminService.getAskRulesLevel(),
				adminService.getMinConfidenceLevel(), types);

		model.addAttribute("adminService", adminService);

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for restarting task creation
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @return reference view
	 */
	@GetMapping("/restartCreate")
	public String restartCreate(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		String user = adminService.getAdminId();
		String difLevel = adminService.getDifficultyLevel();

		Map<String, String> map = new HashMap<>();
		map.put("difLevel", difLevel);
		map.put("user", user);

		repo.removeAdmin(UUID.fromString(adminProcessNo));

		adminProcessNo = repo.initialTasks(map).toString();

		adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		adminService.calculateTasksInital(adminService.getNoTransactionsLevel(), adminService.getAvItemsLevel(),
				adminService.getMaxItemsLevel(), adminService.getMinItemsLevel(), adminService.getTypeDatasetLevel(),
				adminService.getNoRulesLevel());

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for viewing data set
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminDatasetPreview")
	public String newSetupAdminDatasetPreview(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		List<UUID> existingSets = adminService.existingSets(horizontalService.listAllDatasets());

		List<UUID> existingConfigs = adminService.existingConfigs(configService.getAllRows());

		model.addAttribute("adminProcessNo", adminProcessNo);
		model.addAttribute("level", adminService.getDifficultyLevel());
		model.addAttribute("scalingFactor", adminService.getScalingFactor());
		model.addAttribute("adminId", adminService.getAdminId());
		model.addAttribute("ads", adminService.getHorizontalTable().getList());
		model.addAttribute("minSupportLevel", adminService.getMinSupportLevel());
		model.addAttribute("minConfidenceLevel", adminService.getMinConfidenceLevel());
		model.addAttribute("frequentPatternForRules", adminService.getFrequentPatternForRules());
		model.addAttribute("solutionList", adminService.getSolutionList());
		model.addAttribute("frequentSolutionList", adminService.getFrequentTableSolution());
		model.addAttribute("ruleList", adminService.getRuleList());
		model.addAttribute("askRulesLevel", adminService.getAskRulesLevel());
		model.addAttribute("totalNumberOfRules", adminService.getTotalNumberOfRules());

		model.addAttribute("adminService", adminService);
		model.addAttribute("existingSets", existingSets);
		model.addAttribute("existingConfigs", existingConfigs);

		return "newAdminDataset";
	}

	/**
	 * for setting new task config
	 * 
	 * @param model              required by framework to render data inside a view
	 * @param adminProcessNo     id of the administration process (uuid)
	 * @param minSupportLevel    minimum support for the apriori algorithm
	 * @param askRulesLevel      number of rules queried
	 * @param minConfidenceLevel minimum confidence for rules
	 * @param noRulesLevel       number of items to derive a rule
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminNewTaskConfig")
	public String newSetupAdminNewTaskConfig(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "minSupportLevel", required = false) Integer minSupportLevel,
			@RequestParam(value = "askRulesLevel", required = false) Integer askRulesLevel,
			@RequestParam(value = "minConfidenceLevel", required = false) Integer minConfidenceLevel,
			@RequestParam(value = "noRulesLevel", required = false) Integer noRulesLevel) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		adminService.calculateNewTaskConfig(minSupportLevel, noRulesLevel, askRulesLevel, minConfidenceLevel);

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for saving data set
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param name           name of the data set
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminDatasetSave")
	public String newSetupAdminDatasetSave(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "name", required = true) String name) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		adminService.setNameDataset(name);

		if (horizontalService.checkDatasetExists(UUID.fromString(adminService.getIdDataset().toString())) != true) {
			horizontalService.saveDataset(adminService.getAds(), adminService.getIdDataset(),
					adminService.getNameDataset());
		} else {

			horizontalService.deleteDataset(UUID.fromString(adminService.getIdDataset().toString()));
			horizontalService.saveDataset(adminService.getAds(), adminService.getIdDataset(),
					adminService.getNameDataset());
		}

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for saving task configuration
	 * 
	 * @param model           required by framework to render data inside a view
	 * @param adminProcessNo  id of the administration process (uuid)
	 * @param nameDatasetI    name of the data set
	 * @param nameTaskConfigI name of the task configuration
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminConfigSave")
	public String newSetupAdminConfigSave(Model model,
			@RequestParam(value = "taskConfigId", required = true) String taskConfigId,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "nameDatasetI", required = false) String nameDatasetI,
			@RequestParam(value = "nameTaskConfigI", required = false) String nameTaskConfigI) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}
		adminService.setNameDataset(nameDatasetI);
		adminService.setNameConfig(nameTaskConfigI);

		if (horizontalService.checkDatasetExists(UUID.fromString(adminService.getIdDataset().toString())) != true) {
			horizontalService.saveDataset(adminService.getAds(), adminService.getIdDataset(), nameDatasetI);
		}

		if (configService.checkConfigExists(adminService.getIdTaskConfig()) != true) {
			configService.saveRow(UUID.fromString(taskConfigId), adminService.getIdDataset(),
					adminService.getMinSupportLevel(), adminService.getMinConfidenceLevel(),
					adminService.getNoRulesLevel(), adminService.getAskRulesLevel(), nameTaskConfigI);

		} else {
			if (configService.getAprioriConfig(adminService.getIdTaskConfig()).get(0).getComment()
					.equals(adminService.getNameConfig())) {
				configService.deleteByUuid_ac(adminService.getIdTaskConfig());

				configService.saveRow(adminService.getIdTaskConfig(), adminService.getIdDataset(),
						adminService.getMinSupportLevel(), adminService.getMinConfidenceLevel(),
						adminService.getNoRulesLevel(), adminService.getAskRulesLevel(), nameTaskConfigI);
			} else {
				UUID replaceUUID = UUID.randomUUID();

				configService.saveRow(replaceUUID, adminService.getIdDataset(), adminService.getMinSupportLevel(),
						adminService.getMinConfidenceLevel(), adminService.getNoRulesLevel(),
						adminService.getAskRulesLevel(), nameTaskConfigI);

				adminService.setIdTaskConfig(replaceUUID);
			}
		}

		return "redirect:/newSetupAdminDatasetPreview?adminProcessNo=" + adminProcessNo;
	}

	/**
	 * for setting up new data set
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminDatasetModify")
	public String newSetupAdminDatasetModify(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		if (adminService.getDifficultyLevel() == null) {
			adminService.setDifficultyLevel("easy");
			adminService.setDifficultyLevelFromConstants("easy");
		} else {
			adminService.setDifficultyLevel(AdminService.convertDifficulty(adminService.getDifficultyLevel()));
		}

		model.addAttribute("adminService", adminService);

		return "newAdminDatasetSetup";
	}

	/**
	 * for setting up difficulty level
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @param level          difficulty level of tasks
	 * @return reference view
	 */
	@GetMapping("/newSetupAdminDatasetSetLevel")
	public String newSetupAdminDatasetSetLevel(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
			@RequestParam(value = "level", required = false) String level

	) {

		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		adminService.setDifficultyLevel(level);
		adminService.setDifficultyLevelFromConstants(level);

		model.addAttribute("adminService", adminService);

		return "newAdminDatasetSetup";
	}

	/**
	 * for ending admin process
	 * 
	 * @param model          required by framework to render data inside a view
	 * @param adminProcessNo id of the administration process (uuid)
	 * @return reference view
	 */
	@GetMapping("/endAdminExtension")
	public String endExerciseExtension(Model model,
			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo) {

		repo.cleanParticipants();

		if (!repo.capacityCheck()) {
			return "capacityOverload";
		}

		AdminService adminService = repo.findAdmin((UUID.fromString(adminProcessNo)));

		if (adminService == null) {
			return "redirect:/sorry...?message=(Administration process not found)";
		}

		repo.removeAdmin(UUID.fromString(adminProcessNo));

		return "redirect:/sorry...?message=(Administration process closed)";
	}

	// !!disabled feature!!
//	/**
//	 * for exporting task configuration
//	 * 
//	 * @param model	required by framework to render data inside a view
//	 * @param adminProcessNo	id of the administration process (uuid)
//	 * @param idDataset	id data set (uuid)
//	 * @param configId	id task configuration (uuid)
//	 * @return	reference view
//	 */
//	@GetMapping("/exportToMap")
//	public String exportToMap(Model model,
//			@RequestParam(value = "adminProcessNo", required = true) String adminProcessNo,
//			@RequestParam(value = "idDataset", required = true) String idDataset,
//			@RequestParam(value = "configId", required = true) String configId) {
//
//		AdminService adminService = repo.findAdmin(UUID.fromString(adminProcessNo.toString()));
//		if (adminService == null) {
//			return "redirect:/sorry...?message=(Administration process not found)";
//		}
//
//		repoExport.setConfigId(adminService.getAdminId(), UUID.fromString(configId));
//
//		return "redirect:/taskConfigs?adminProcessNo=" + adminProcessNo + "&idDataset=" + idDataset;
//
//	}
}
