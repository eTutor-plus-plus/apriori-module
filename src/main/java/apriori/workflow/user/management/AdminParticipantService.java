package apriori.workflow.user.management;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apriori.workflow.service.AdminService;

/**
 * service class for managing users of administration processes
 *
 */
@Service
public class AdminParticipantService {

	@Autowired
	private AdminParticipantRepository repo;

	/**
	 * for setting up new administration process
	 * 
	 * @param adminId	user id
	 * @param difficultyLevel	difficulty level
	 * @return id of the administration process (uuid)
	 */
	public UUID setupAdmin(String adminId, String difficultyLevel) {
		AdminService adminService = new AdminService(difficultyLevel, adminId);
		UUID uuid = adminService.getAdminProcessNo();
		repo.addAdminProcess(adminService);
		return uuid;
	}

	/**
	 * for setting up new administration process
	 * 
	 * @param adminId	user id
	 * @return id of the administration process (uuid)
	 */
	public UUID setupAdminTables(String adminId) {
		AdminService adminService = new AdminService(adminId);
		UUID uuid = adminService.getAdminProcessNo();
		repo.addAdminProcess(adminService);
		return uuid;
	}

	/**
	 * for deleting administration process
	 * 
	 * @param idAdminRemovable	user id
	 */
	private void deleteObsoleteAdminTasks(String idAdminRemovable) {
		UUID adminNo = repo.getAdminProcessNoForAdminId(idAdminRemovable);
		if (adminNo != null) {
			repo.removeAdminProcess(adminNo);
		}
		cleanParticipants();
	}

	/**
	 * for setting up initial variables for administration process
	 * 
	 * @param idAdmin user id
	 * @return id of the administration process (uuid)
	 */
	public UUID initialTable(String idAdmin) {
		UUID adminProcessNumberList = repo.getAdminProcessNoForAdminId(idAdmin);
		if (adminProcessNumberList != null) {
			deleteObsoleteAdminTasks(idAdmin);
		}
		cleanParticipants();
		UUID adminProcessNo = setupAdminTables(idAdmin);
		AdminService adminService = findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return null;
		}
		return adminService.getAdminProcessNo();
	}

	/**
	 * for performing initial task for administration process
	 * 
	 * @param map	parameter submitted by initial variable (administration starting conditions)
	 * @return id of the administration process (uuid)
	 */
	public UUID initialTasks(Map<String, String> map) {
		String idAdmin = map.get("user");
		String difLevel = map.get("difLevel");
		UUID adminProcessNumberList = repo.getAdminProcessNoForAdminId(idAdmin);
		if (adminProcessNumberList != null) {
			deleteObsoleteAdminTasks(idAdmin);
		}
		UUID adminProcessNo = setupAdmin(idAdmin, difLevel);
		AdminService adminService = findAdmin(UUID.fromString(adminProcessNo.toString()));
		if (adminService == null) {
			return null;
		}
		adminService.setDifficultyLevel(difLevel);
		return adminService.getAdminProcessNo();
	}

	/**
	 * for finding administration process by id
	 * 
	 * @param uuid	id of the administration process (uuid)
	 * @return AdminService
	 */
	public AdminService findAdmin(UUID uuid) {
		AdminService adminService = repo.findAdminProcess(uuid);
		return adminService;
	}

	/**
	 * for retrieving administration process number by user id
	 * 
	 * @param adminId	user id
	 * @return id of the administration process (uuid)
	 */
	public UUID getAdminProcessNoForAdminId(String adminId) {
		UUID adminProcessNo = repo.getAdminProcessNoForAdminId(adminId);
		return adminProcessNo;
	}

	/**
	 * for removing administration process
	 * 
	 * @param uuid	id of the administration process (uuid)
	 * @return true, false
	 */
	public boolean removeAdmin(UUID uuid) {
		boolean removed = repo.removeAdminProcess(uuid);
		return removed;
	}

	/**
	 * for retrieving all administration processes
	 * 
	 * @return list administration processes
	 */
	public List<AdminService> retrieveList() {
		return repo.getAdminList().getAdminProcessList();
	}

	/**
	 * for checking, if limit of administration processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		return repo.capacityCheck();
	}

	/**
	 * for cleaning administration processes list (time limit reached)
	 */
	public void cleanParticipants() {
		repo.cleanParticipants();
	}

	/**
	 * for displaying administration process list (in console, for development
	 * purposes)
	 */
	public void display() {
		repo.display();
	}
}
