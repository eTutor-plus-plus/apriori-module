package apriori.workflow.user.management;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import apriori.logic.utility.AprioriException;
import apriori.logic.utility.Property;
import apriori.workflow.service.ExerciseService;

/**
 * class for managing users of exercise processes
 */
public class ExerciseParticipant {

	private List<ExerciseService> exerciseProcessList;

	/**
	 * constructor
	 */
	public ExerciseParticipant() {
		this.exerciseProcessList = new ArrayList<>();
	}

	/**
	 * for adding an exercise process
	 * 
	 * @param es	ExerciseService
	 */
	public void addExerciseProcess(ExerciseService es) {
		exerciseProcessList.add(es);
	}

	/**
	 * for retrieving exercise process by id
	 * 
	 * @param exerciseProcessNo	id of the exercise process (uuid)
	 * @return ExerciseService	
	 * @throws AprioriException	
	 */
	public ExerciseService findExerciseProcess(UUID exerciseProcessNo) throws AprioriException {
		ExerciseService exerciseProcess = null;
		for (ExerciseService es : exerciseProcessList) {
			if (es.getExerciseProcessNo().equals(exerciseProcessNo)) {
				exerciseProcess = es;
			}
		}
		if (exerciseProcess != null) {
			return exerciseProcess;
		} else {
			throw new AprioriException("Exercise process not found, please restart");
		}
	}

	/**
	 * for removing exercise process
	 * 
	 * @param exerciseProcessNo	id of the exercise process (uuid)
	 * @return true, false
	 */
	public boolean removeExerciseProcess(UUID exerciseProcessNo) {
		for (ExerciseService es : exerciseProcessList) {
			if (es.getExerciseProcessNo().equals(exerciseProcessNo)) {
				exerciseProcessList.remove(exerciseProcessList.get(exerciseProcessList.indexOf(es)));
				return true;
			}
		}
		return false;
	}

	/**
	 * for checking, if limit of exercise processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		int capacity = Property.getIntProperty("max.exercises");
		int sizeList = exerciseProcessList.size();
		if (capacity >= sizeList) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * for cleaning exercise processes list (time limit reached)
	 */
	public void cleanParticipants() {
		if (exerciseProcessList.size() != 0) {
			List<UUID> limitReached = new ArrayList<>();
			for (ExerciseService es : exerciseProcessList) {
				if (es != null) {
					if (!timesUp(es.getStart())) {
						limitReached.add(es.getExerciseProcessNo());
					}
				}
			}
			for (UUID uuid : limitReached) {
				removeExerciseProcess(uuid);
			}
		}
	}

	/**
	 * for checking, if time limit for an exercise process is reached
	 * 
	 * @param start	starting time
	 * @return true, false
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

	public List<ExerciseService> getExerciseProcessList() {
		return exerciseProcessList;
	}

	public void setExerciseProcessList(List<ExerciseService> exerciseProcessList) {
		this.exerciseProcessList = exerciseProcessList;
	}

	@Override
	public String toString() {
		return "ExerciseParticipant [exerciseProcessList=" + exerciseProcessList + "]";
	}

	/**
	 * for displaying exercise process list (in console, for development purposes)
	 */
	public void display() {
		for (ExerciseService ts : exerciseProcessList) {
			System.out.println(ts.toString());
		}
	}

}
