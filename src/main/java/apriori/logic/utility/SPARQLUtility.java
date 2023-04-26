package apriori.logic.utility;

import java.util.Objects;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.update.UpdateRequest;

/**
 * class for sparql utility functions (sparql queries adapted from eTutor++)
 */
public class SPARQLUtility {

	private static String linkSparql = Property.getProperty("semantic.link");
	private static String queryEndpoint = Property.getProperty("semantic.query.endpoint");
	private static String updateEndpoint = Property.getProperty("semantic.update.endpoint");

	/**
	 * for checking if sheet is closed
	 * 
	 * @param user    user id
	 * @param exSheet exercise sheet id (uuid)
	 * @param coInst  course instance id (uuid)
	 * @return true(=etutor:isClosed false), false(=etutor:isClosed true; or does
	 *         not exist)
	 */
	public static boolean checkIfIsClosed(String exSheet, String coInst) {

		Objects.requireNonNull(coInst);
		Objects.requireNonNull(exSheet);

		RDFConnection connectionAsk = RDFConnectionRemote.newBuilder().destination(linkSparql)
				.queryEndpoint(queryEndpoint).build();

		final String askQuery = """
				PREFIX etutor: <http://www.dke.uni-linz.ac.at/etutorpp/>

				ASK {
					  ?courseInstance etutor:hasExerciseSheetAssignment ?sheetA.
					  ?sheetA 	etutor:hasExerciseSheet ?sheet;
					           	etutor:isExerciseSheetClosed false.
				}
				""";

		String courseInstanceId = ETutorVocabulary.createCourseInstanceURLString(coInst);
		String sheetId = ETutorVocabulary.createExerciseSheetURLString(exSheet);
		ParameterizedSparqlString askQ = new ParameterizedSparqlString(askQuery);

		askQ.setIri("?sheet", sheetId);
		askQ.setIri("?courseInstance", courseInstanceId);

		boolean result = true;
		try (RDFConnection connection = connectionAsk) {
			result = connection.queryAsk(askQ.asQuery());
		}
		return result;

	}

	/**
	 * for checking if task was already submitted
	 * 
	 * @param user    user id
	 * @param exSheet exercise sheet id (uuid)
	 * @param coInst  course instance id (uuid)
	 * @param taskNo  task number
	 * @return true (submitted), false (not submitted)
	 */
	public static boolean checkIfSubmitted(String user, String exSheet, String coInst, int taskNo) {

		Objects.requireNonNull(coInst);
		Objects.requireNonNull(exSheet);
		Objects.requireNonNull(user);
		assert taskNo > 0;

		RDFConnection connectionAsk = RDFConnectionRemote.newBuilder().destination(linkSparql)
				.queryEndpoint(queryEndpoint).build();

		final String askQuery = """
				PREFIX etutor: <http://www.dke.uni-linz.ac.at/etutorpp/>

				ASK {
				  ?student etutor:hasIndividualTaskAssignment ?individualAssignment.
				  ?individualAssignment etutor:fromExerciseSheet ?sheet;
				  	                    etutor:fromCourseInstance ?courseInstance;
				                        etutor:hasIndividualTask ?individualTask.
				  ?individualTask etutor:isSubmitted true;
				                  etutor:hasOrderNo ?orderNo.
				}
				""";

		String courseInstanceId = ETutorVocabulary.createCourseInstanceURLString(coInst);
		String sheetId = ETutorVocabulary.createExerciseSheetURLString(exSheet);
		String studentUrl = ETutorVocabulary.getStudentURLFromMatriculationNumber(user);

		ParameterizedSparqlString askQ = new ParameterizedSparqlString(askQuery);

		askQ.setIri("?student", studentUrl);
		askQ.setIri("?sheet", sheetId);
		askQ.setIri("?courseInstance", courseInstanceId);
		askQ.setLiteral("?orderNo", taskNo);

		boolean result = true;
		try (RDFConnection connection = connectionAsk) {
			result = connection.queryAsk(askQ.asQuery());
		}
		return result;
	}

	/**
	 * for setting the points achieved in exercise
	 * 
	 * @param user    user id
	 * @param exSheet exercise sheet id (uuid)
	 * @param coInst  course instance id (uuid)
	 * @param taskNo  task number
	 * @param points  achieved point in both tasks
	 */
	public static void setPoints(String user, String exSheet, String coInst, int taskNo, double points) {

		Objects.requireNonNull(coInst);
		Objects.requireNonNull(exSheet);
		Objects.requireNonNull(user);
		assert taskNo > 0;

		RDFConnection connectionUpdate = RDFConnectionRemote.newBuilder().destination(linkSparql)
				.updateEndpoint(updateEndpoint).build();

		final String updateQuery = """
				PREFIX etutor: <http://www.dke.uni-linz.ac.at/etutorpp/>

				   DELETE {
				     ?individualTask etutor:hasDispatcherPoints ?oldDispatcherPoints.
				   } INSERT {
				     ?individualTask etutor:hasDispatcherPoints ?newDispatcherPoints.
				   } WHERE {
				     ?instance a etutor:CourseInstance.
				     ?student etutor:hasIndividualTaskAssignment ?individualAssignment.
				     ?individualAssignment etutor:fromExerciseSheet ?sheet;
				                           etutor:fromCourseInstance ?instance;
				                           etutor:hasIndividualTask ?individualTask.
				     ?individualTask etutor:hasOrderNo ?orderNo.

				     OPTIONAL {
				       ?individualTask etutor:hasDispatcherPoints ?oldDispatcherPoints.
				     }
				   }
							""";

		String courseInstanceId = ETutorVocabulary.createCourseInstanceURLString(coInst);
		String exerciseSheetId = ETutorVocabulary.createExerciseSheetURLString(exSheet);
		String studentId = ETutorVocabulary.getStudentURLFromMatriculationNumber(user);

		ParameterizedSparqlString updateQ = new ParameterizedSparqlString(updateQuery);

		updateQ.setIri("?instance", courseInstanceId);
		updateQ.setIri("?sheet", exerciseSheetId);
		updateQ.setIri("?student", studentId);
		updateQ.setLiteral("?orderNo", taskNo);
		updateQ.setLiteral("?newDispatcherPoints", points);

		UpdateRequest updateR = updateQ.asUpdate();

		try (RDFConnection connect = connectionUpdate) {
			connect.update(updateR);
		}

	}

}
