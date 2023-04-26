package apriori.logic.utility;

/**
 * class for apriori exceptions
 *
 */
public class AprioriException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * 
	 * @param errorMessage message to user
	 */
	public AprioriException(String errorMessage) {
		super(errorMessage);
	}

}
