package apriori.workflow.user.management;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apriori.workflow.service.TrainingService;

/**
 * service class for managing users of training processes
 *
 */
@Service
public class TrainingParticipantService {

	@Autowired
	private TrainingParticipantRepository repo;

	/**
	 * for setting up new training process
	 * 
	 * @return training process number (uuid)
	 */
	public UUID setupTraining() {
		TrainingService ts = new TrainingService();
		UUID uuid = ts.getTrainingProcessNo();
		repo.addTrainingProcess(ts);
		return uuid;
	}

	/**
	 * for retrieving training process by id
	 * 
	 * @param uuid	id of the training process (uuid)
	 * @return TrainingService
	 */
	public TrainingService findTraining(UUID uuid) {
		TrainingService ts = repo.findTrainingProcess(uuid);
		return ts;
	}

	/**
	 * for removing training process
	 * 
	 * @param uuid	id of the training process (uuid)
	 * @return true, false
	 */
	public boolean removeTraining(UUID uuid) {
		boolean removed = repo.removeTrainingProcess(uuid);
		return removed;
	}

	/**
	 * for retrieving list of training processes
	 * 
	 * @return list TrainingService
	 */
	public List<TrainingService> retrieveList() {
		return repo.getTrainingList().getTrainingProcessList();
	}

	/**
	 * for checking, if limit of training processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		return repo.capacityCheck();
	}

	/**
	 * for cleaning training processes list (time limit reached)
	 */
	public void cleanParticipants() {
		repo.cleanParticipants();
	}

	/**
	 * for displaying training process list (in console, for development purposes)
	 */
	public void display() {
		repo.display();
	}
}
