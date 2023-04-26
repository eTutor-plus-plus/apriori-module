package apriori.workflow.user.management;

import java.util.UUID;

import org.springframework.stereotype.Service;

import apriori.logic.utility.AprioriException;
import apriori.workflow.service.TrainingService;

/**
 * repository (sub-service) class for managing users of training processes
 *
 */
@Service
public class TrainingParticipantRepository {

	private static TrainingParticipant trainingList;

	/**
	 * constructor (static)
	 */
	static {
		trainingList = new TrainingParticipant();
	}

	/**
	 * for creating new TrainingService
	 * 
	 * @return TrainingService
	 */
	public TrainingService newTraining() {
		TrainingService ts = new TrainingService();
		return ts;
	}

	/**
	 * for adding an training process
	 * 
	 * @param ts	TrainingService
	 */
	public void addTrainingProcess(TrainingService ts) {
		trainingList.addTrainingProcess(ts);
	}

	/**
	 * for retrieving training process by id
	 * 
	 * @param trainingProcessNo	id of the training process (uuid)
	 * @return TrainingService
	 */
	public TrainingService findTrainingProcess(UUID trainingProcessNo) {
		try {
			return trainingList.findTrainingProcess(trainingProcessNo);
		} catch (AprioriException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * for removing training process
	 * 
	 * @param trainingProcessNo	id of the training process (uuid)
	 * @return true, false
	 */
	public boolean removeTrainingProcess(UUID trainingProcessNo) {
		return trainingList.removeTrainingProcess(trainingProcessNo);
	}

	/**
	 * for checking, if limit of training processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		return trainingList.capacityCheck();
	}

	/**
	 * for cleaning training processes list (time limit reached)
	 */
	public void cleanParticipants() {
		trainingList.cleanParticipants();
	}

	public TrainingParticipant getTrainingList() {
		return trainingList;
	}

	@Override
	public String toString() {
		return "TrainingCollectionRepository [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	/**
	 * for displaying training process list (in console, for development purposes)
	 */
	public void display() {
		trainingList.display();
	}

}
