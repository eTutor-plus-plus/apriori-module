package apriori.workflow.user.management;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import apriori.logic.utility.AprioriException;
import apriori.logic.utility.Property;
import apriori.workflow.service.AdminService;

/**
 * class for managing users of administration processes
 *
 */
public class AdminParticipant {

	private List<AdminService> adminProcessList;

	/**
	 * constructor
	 */
	public AdminParticipant() {
		this.adminProcessList = new ArrayList<>();
	}

	/**
	 * for retrieving administration process by id
	 * 
	 * @param adminProcessNo	id of the administration process (uuid)
	 * @return AdminService	AdminService
	 * @throws AprioriException	if training process not found
	 */
	public AdminService findAdminProcess(UUID adminProcessNo) throws AprioriException {
		AdminService adminProcess = null;
		for (AdminService adminService : adminProcessList) {
			if (adminService.getAdminProcessNo().equals(adminProcessNo)) {
				adminProcess = adminService;
			}
		}
		if (adminProcess != null) {
			return adminProcess;
		} else {
			throw new AprioriException("training process not found, please restart");
		}
	}

	/**
	 * for removing administration process
	 * 
	 * @param adminProcessNo	id of the administration process (uuid)
	 * @return true, false
	 */
	public boolean removeAdminProcess(UUID adminProcessNo) {
		for (AdminService adminService : adminProcessList) {
			if (adminService.getAdminProcessNo().equals(adminProcessNo)) {
				adminProcessList.remove(adminProcessList.get(adminProcessList.indexOf(adminService)));
				return true;
			}
		}
		return false;
	}

	/**
	 * for retrieving administration process number by user id
	 * 
	 * @param adminId	user id
	 * @return id of the administration process (uuid)
	 */
	public UUID getAdminProcessNoForAdminId(String adminId) {
		UUID adminProcessNo = null;
		for (AdminService adminService : adminProcessList) {
			if (adminService.getAdminId().equals(adminId)) {
				adminProcessNo = adminService.getAdminProcessNo();
			}
		}
		return adminProcessNo;
	}

	/**
	 * for checking, if limit of administration processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		int capacity = Property.getIntProperty("max.admins");
		int sizeList = adminProcessList.size();

		if (capacity >= sizeList) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * for cleaning administration processes list (time limit reached)
	 */
	public void cleanParticipants() {
		if (adminProcessList.size() != 0) {
			List<UUID> limitReached = new ArrayList<>();
			for (AdminService adminService : adminProcessList) {
				if (adminService != null) {
					if (!timesUp(adminService.getStart())) {
						limitReached.add(adminService.getAdminProcessNo());
					}
				}
			}
			for (UUID uuid : limitReached) {
				removeAdminProcess(uuid);
			}
		}
	}

	/**
	 * for checking, if time limit for an administration process is reached
	 * 
	 * @param start	starting time
	 * @return true, false
	 */
	public boolean timesUp(Instant start) {
		Instant now = Instant.now();
		Duration timePassed = Duration.between(start, now);
		long timeLimit = Property.getIntProperty("minutes.admin");
		if (timePassed.toMinutes() >= timeLimit) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * for adding an administration process
	 * 
	 * @param adminService	AdminService
	 */
	public void addAdminProcess(AdminService adminService) {
		adminProcessList.add(adminService);
	}

	public List<AdminService> getAdminProcessList() {
		return adminProcessList;
	}

	public void setAdminProcessList(List<AdminService> adminProcessList) {
		this.adminProcessList = adminProcessList;
	}

	@Override
	public String toString() {
		return "AdminParticipant [adminProcessList=" + adminProcessList + "]";
	}

	/**
	 * for displaying administration process list (in console, for development
	 * purposes)
	 */
	public void display() {
		for (AdminService adminService : adminProcessList) {
			System.out.println("adminProcessList: " + adminService.toString());
		}
	}
}
