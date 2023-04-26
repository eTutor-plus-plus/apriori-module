package apriori.workflow.user.management;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apriori.workflow.service.ExerciseService;

/**
 * service class for managing users of exercise processes
 *
 */
@Service
public class ExerciseParticipantService {

	@Autowired
	private ExerciseParticipantRepository repo;

	/**
	 * for creating new exercise process
	 * 
	 * @param userId	user id
	 * @param _taskNo	number of task 
	 * @param courseInstanceUUID	course instance id (uuid)
	 * @param exerciseSheetUUID	exercise sheet id (uuid)
	 * @param maxPoints	maximum reachable points in both tasks 
	 * @param taskConfigId	configuration id (uuid)
	 * @param difficultyLevel	difficulty level tasks
	 * @return id of the exercise process (uuid)
	 */
	public UUID setupExercise(String userId, int _taskNo, String courseInstanceUUID, String exerciseSheetUUID,
			int maxPoints, String taskConfigId, String difficultyLevel) {
		for (ExerciseService es : ExerciseParticipantRepository.getExerciseList().getExerciseProcessList()) {
			if (es.getUserId().equals(userId) && es.get_taskNo() == _taskNo
					&& es.getCourseInstanceUUID().toString().equals(courseInstanceUUID.toString())
					&& es.getExerciseSheetUUID().toString().equals(exerciseSheetUUID.toString())
					&& es.getTaskConfigId().toString().equals(taskConfigId.toString())) {
				UUID uuid = es.getExerciseProcessNo();
				return uuid;
			}
		}

		ExerciseService exerciseService = new ExerciseService(userId, _taskNo, courseInstanceUUID, exerciseSheetUUID,
				maxPoints, taskConfigId, difficultyLevel);
		UUID uuid = exerciseService.getExerciseProcessNo();
		repo.addExerciseProcess(exerciseService);
		return uuid;
	}

	/**
	 * for retrieving exercise process by id
	 * 
	 * @param uuid	id of the exercise process (uuid)
	 * @return ExerciseService
	 */
	public ExerciseService findExercise(UUID uuid) {
		ExerciseService es = repo.findExerciseProcess(uuid);
		return es;
	}

	/**
	 * for removing exercise process
	 * 
	 * @param uuid	id of the exercise process (uuid)
	 * @return true, false
	 */
	public boolean removeExercise(UUID uuid) {
		boolean removed = repo.removeExerciseProcess(uuid);
		return removed;
	}

	/**
	 * for retrieving list of exercise processes
	 * 
	 * @return list ExerciseService
	 */
	public List<ExerciseService> retrieveList() {
		return ExerciseParticipantRepository.getExerciseList().getExerciseProcessList();
	}

	/**
	 * for checking, if limit of exercise processes is reached
	 * 
	 * @return true, false
	 */
	public boolean capacityCheck() {
		return repo.capacityCheck();
	}

	/**
	 * for cleaning exercise processes list (time limit reached)
	 */
	public void cleanParticipants() {
		repo.cleanParticipants();
	}

	/**
	 * for displaying exercise process list (in console, for development purposes)
	 */
	public void display() {
		repo.display();
	}
}
