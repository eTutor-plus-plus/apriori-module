package apriori.workflow.user.management;

import java.util.UUID;

import org.springframework.stereotype.Service;

import apriori.logic.utility.AprioriException;
import apriori.workflow.service.AdminService;

/**
 * repository (sub-service) class for managing users of administration processes
 *
 */
@Service
public class AdminParticipantRepository {

	private static AdminParticipant adminList;

	/**
	 * constructor
	 */
	static {
		adminList = new AdminParticipant();
	}

	/**
	 * for adding an administration process
	 * 
	 * @param adminService	AdminService
	 */
	public void addAdminProcess(AdminService adminService) {
		adminList.addAdminProcess(adminService);
	}

	/**
	 * for retrieving administration process by id
	 * 
	 * @param adminProcessNo	id of the administration process (uuid)
	 * @return	AdminService
	 */
	public AdminService findAdminProcess(UUID adminProcessNo) {
		try {
			return adminList.findAdminProcess(adminProcessNo);
		} catch (AprioriException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * for retrieving administration process number by user id
	 * 
	 * @param adminId user id
	 * @return	id of the administration process (uuid)
	 */
	public UUID getAdminProcessNoForAdminId(String adminId) {
		return adminList.getAdminProcessNoForAdminId(adminId);
	}

	/**
	 * for removing administration process
	 * 
	 * @param adminProcessNo	id of the administration process (uuid)
	 * @return	true, false
	 */
	public boolean removeAdminProcess(UUID adminProcessNo) {
		return adminList.removeAdminProcess(adminProcessNo);
	}

	/**
	 * for checking, if limit of administration processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		return adminList.capacityCheck();
	}

	/**
	 * for cleaning administration processes list (time limit reached)
	 */
	public void cleanParticipants() {
		adminList.cleanParticipants();
	}

	public AdminParticipant getAdminList() {
		return adminList;
	}

	@Override
	public String toString() {
		return "AdminParticipantRepository []" + adminList.toString();
	}

	/**
	 * for displaying administration process list (in console, for development
	 * purposes)
	 */
	public void display() {
		adminList.display();
	}

}
