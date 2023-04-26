package apriori.workflow.user.management;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import apriori.logic.utility.AprioriException;
import apriori.logic.utility.Property;
import apriori.workflow.service.TrainingService;

/**
 * class for managing users of training processes
 *
 */
public class TrainingParticipant {

	private List<TrainingService> trainingProcessList;

	/**
	 * constructor
	 */
	public TrainingParticipant() {
		this.trainingProcessList = new ArrayList<>();
	}

	/**
	 * for adding a training process
	 * 
	 * @param ts	TrainingService
	 */
	public void addTrainingProcess(TrainingService ts) {
		trainingProcessList.add(ts);
	}

	/**
	 * for retrieving training process by id
	 * 
	 * @param trainingProcessNo	id of the training process (uuid)
	 * @return TrainingService
	 * @throws AprioriException
	 */
	public TrainingService findTrainingProcess(UUID trainingProcessNo) throws AprioriException {
		TrainingService trainingProcess = null;
		for (TrainingService ts : trainingProcessList) {
			if (ts.getTrainingProcessNo().equals(trainingProcessNo)) {
				trainingProcess = ts;
			}
		}
		if (trainingProcess != null) {
			return trainingProcess;
		} else {
			throw new AprioriException("training process not found, please restart");
		}
	}

	/**
	 * for removing training process
	 * 
	 * @param trainingProcessNo	id of the training process (uuid)
	 * @return true, false
	 */
	public boolean removeTrainingProcess(UUID trainingProcessNo) {
		for (TrainingService ts : trainingProcessList) {
			if (ts.getTrainingProcessNo().equals(trainingProcessNo)) {
				trainingProcessList.remove(trainingProcessList.get(trainingProcessList.indexOf(ts)));
				return true;
			}
		}
		return false;
	}

	/**
	 * for checking, if limit of training processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		int capacity = Property.getIntProperty("max.trainee");
		int sizeList = trainingProcessList.size();
		if (capacity >= sizeList) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * for cleaning training processes list (time limit reached)
	 */
	public void cleanParticipants() {
		if (trainingProcessList.size() != 0) {
			List<UUID> limitReached = new ArrayList<>();
			for (TrainingService ts : trainingProcessList) {
				if (ts != null) {
					System.out.println("ts.getStart(): " + ts.getStart());
					if (!timesUp(ts.getStart())) {
						limitReached.add(ts.getTrainingProcessNo());
					}
				}
			}
			for (UUID uuid : limitReached) {
				removeTrainingProcess(uuid);
			}
		}
	}

	/**
	 * for checking, if time limit for an training process is reached
	 * 
	 * @param start
	 * @return
	 */
	public boolean timesUp(Instant start) {
		Instant now = Instant.now();
		Duration timePassed = Duration.between(start, now);
		long min = timePassed.toMinutes();
		long timeLimit = Property.getIntProperty("minutes.training");
		if (timePassed.toMinutes() >= timeLimit) {
			return false;
		} else {
			return true;
		}
	}

	public List<TrainingService> getTrainingProcessList() {
		return trainingProcessList;
	}

	public void setTrainingProcessList(List<TrainingService> trainingProcessList) {
		this.trainingProcessList = trainingProcessList;
	}

	@Override
	public String toString() {
		return "TrainingCollection [getTrainingProcessList()=" + getTrainingProcessList() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	/**
	 * for displaying training process list (in console, for development purposes)
	 */
	public void display() {
		for (TrainingService ts : trainingProcessList) {
			System.out.println(ts.toString());
		}
	}
}
