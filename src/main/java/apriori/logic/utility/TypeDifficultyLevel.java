package apriori.logic.utility;

/**
 * enumeration of possible difficulty levels
 */
public enum TypeDifficultyLevel {

	/**
	 * lowest difficulty level
	 */
	easy("Easy Configuration"), 
	/**
	 * second lowest difficulty level
	 */
	moderate("Moderate Configuration"), 
	/**
	 * second highest difficulty level
	 */
	hard("Hard Configuration"),
	/**
	 * highest difficulty level
	 */
	very_hard("Expert Configuration");

	private final String displayValue;

	/**
	 * constructor
	 * 
	 * @param displayValue text to be displayed
	 */
	private TypeDifficultyLevel(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	/**
	 * for generating retrieval marker for app constant
	 * 
	 * @param level difficulty level
	 * @return retrieval marker
	 */
	public String getConfigRetrievalMarker(String level) {
		return "difficulty.level." + level;
	}

	/**
	 * for getting value for view
	 * 
	 * @param level difficulty level
	 * @return value view
	 */
	public static String getDisplayValueL(TypeDifficultyLevel level) {
		return level.getDisplayValue();
	}

}
