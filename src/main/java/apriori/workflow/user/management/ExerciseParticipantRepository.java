package apriori.workflow.user.management;

import java.util.UUID;

import org.springframework.stereotype.Service;

import apriori.logic.utility.AprioriException;
import apriori.workflow.service.ExerciseService;

/**
 * repository (sub-service) class for managing users of exercise processes
 *
 */
@Service
public class ExerciseParticipantRepository {

	private static ExerciseParticipant exerciseList;

	/**
	 * constructor
	 */
	static {
		exerciseList = new ExerciseParticipant();
	}

	/**
	 * for creating new ExerciseService
	 * 
	 * @param userId	user id
	 * @param maxPoints	maximum reachable points
	 * @param taskUUID	id of the task
	 * @return ExerciseService
	 */
	public ExerciseService newExercise(String userId, int maxPoints, String taskUUID) {
		ExerciseService es = new ExerciseService(userId, maxPoints, taskUUID);
		return es;
	}

	/**
	 * for adding an exercise process
	 * 
	 * @param es	ExerciseService
	 */
	public void addExerciseProcess(ExerciseService es) {
		exerciseList.addExerciseProcess(es);
	}

	/**
	 * for retrieving exercise process by id
	 * 
	 * @param exerciseProcessNo	id of the exercise process (uuid)
	 * @return ExerciseService
	 */
	public ExerciseService findExerciseProcess(UUID exerciseProcessNo) {
		try {
			return exerciseList.findExerciseProcess(exerciseProcessNo);
		} catch (AprioriException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * for removing exercise process
	 * 
	 * @param exerciseProcessNo	id of the exercise process (uuid)
	 * @return true, false
	 */
	public boolean removeExerciseProcess(UUID exerciseProcessNo) {
		return exerciseList.removeExerciseProcess(exerciseProcessNo);
	}

	/**
	 * for checking, if limit of exercise processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		return exerciseList.capacityCheck();
	}

	/**
	 * for cleaning exercise processes list (time limit reached)
	 */
	public void cleanParticipants() {
		exerciseList.cleanParticipants();
	}

	public static ExerciseParticipant getExerciseList() {
		return exerciseList;
	}

	/**
	 * for displaying exercise process list (in console, for development purposes)
	 */
	public void display() {
		exerciseList.display();
	}

	@Override
	public String toString() {
		return "ExerciseParticipantRepository []";
	}

}
