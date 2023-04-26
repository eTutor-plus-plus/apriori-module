package apriori.workflow.elements;

import java.util.Arrays;
import java.util.Objects;

/**
 * class for row for rules evaluation
 */
public class RuleExerciseCorrection {

	private boolean counted;

	private String[] lhs;
	private String[] rhs;
	private int upperConf;
	private int lowerConf;
	private double confidence;
	private double percentage;
	private String[] upperSupp;
	private String[] lowerSupp;

	private String ruleOk;
	private String confOk;

	private String holisticOk;

	private String formulaConfOk;
	private String fractionOk;
	private String percentageOk;

	/**
	 * constructor
	 * 
	 * @param lhs	left hand side rule
	 * @param rhs	right hand side rule
	 * @param upperConf	numerator fraction
	 * @param lowerConf	denominator fraction
	 * @param confidence	confidence
	 * @param percentage	confidence percent
	 * @param upperSupp	upper part formula
	 * @param lowerSupp	lower part formula
	 */
	public RuleExerciseCorrection(String[] lhs, String[] rhs, int upperConf, int lowerConf, double confidence,
			double percentage, String[] upperSupp, String[] lowerSupp) {
		super();
		this.lhs = lhs;
		this.rhs = rhs;
		this.upperConf = upperConf;
		this.lowerConf = lowerConf;
		this.confidence = confidence;
		this.percentage = percentage;
		this.upperSupp = upperSupp;
		this.lowerSupp = lowerSupp;
	}

	/**
	 * for marking whole row correct or incorrect
	 */
	public void setHolisticOk() {
		String result;
		if (ruleOk == null || confOk == null || formulaConfOk == null || fractionOk == null || percentageOk == null) {
			result = "no";
		} else {
			if (ruleOk == "yes" || confOk == "yes" || formulaConfOk == "yes" || fractionOk == "yes"
					|| percentageOk == "yes") {
				result = "yes";
			} else {
				result = "no";
			}
		}
		this.holisticOk = result;
	}

	public String getHolisticOk() {
		return holisticOk;
	}

	public String getFormulaConfOk() {
		return formulaConfOk;
	}

	public void setFormulaConfOk(String formulaConfOk) {
		this.formulaConfOk = formulaConfOk;
	}

	public String getFractionOk() {
		return fractionOk;
	}

	public void setFractionOk(String fractionOk) {
		this.fractionOk = fractionOk;
	}

	public String getPercentageOk() {
		return percentageOk;
	}

	public void setPercentageOk(String percentageOk) {
		this.percentageOk = percentageOk;
	}

	public String[] getLhs() {
		return lhs;
	}

	public void setLhs(String[] lhs) {
		this.lhs = lhs;
	}

	public String[] getRhs() {
		return rhs;
	}

	public void setRhs(String[] rhs) {
		this.rhs = rhs;
	}

	public int getUpperConf() {
		return upperConf;
	}

	public void setUpperConf(int upperConf) {
		this.upperConf = upperConf;
	}

	public int getLowerConf() {
		return lowerConf;
	}

	public void setLowerConf(int lowerConf) {
		this.lowerConf = lowerConf;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String[] getUpperSupp() {
		return upperSupp;
	}

	public void setUpperSupp(String[] upperSupp) {
		this.upperSupp = upperSupp;
	}

	public String[] getLowerSupp() {
		return lowerSupp;
	}

	public void setLowerSupp(String[] lowerSupp) {
		this.lowerSupp = lowerSupp;
	}

	public String getRuleOk() {
		return ruleOk;
	}

	public void setRuleOk(String ruleOk) {
		this.ruleOk = ruleOk;
	}

	public String getConfOk() {
		return confOk;
	}

	public void setConfOk(String confOk) {
		this.confOk = confOk;
	}

	public boolean getCounted() {
		return counted;
	}

	public void setCounted(boolean counted) {
		this.counted = counted;
	}

	@Override
	public String toString() {
		return "RuleExerciseCorrection [counted=" + counted + ", lhs=" + Arrays.toString(lhs) + ", rhs="
				+ Arrays.toString(rhs) + ", upperConf=" + upperConf + ", lowerConf=" + lowerConf + ", confidence="
				+ confidence + ", percentage=" + percentage + ", upperSupp=" + Arrays.toString(upperSupp)
				+ ", lowerSupp=" + Arrays.toString(lowerSupp) + ", ruleOk=" + ruleOk + ", confOk=" + confOk
				+ ", holisticOk=" + holisticOk + ", formulaConfOk=" + formulaConfOk + ", fractionOk=" + fractionOk
				+ ", percentageOk=" + percentageOk + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(lhs);
		result = prime * result + Arrays.hashCode(lowerSupp);
		result = prime * result + Arrays.hashCode(rhs);
		result = prime * result + Arrays.hashCode(upperSupp);
		result = prime * result + Objects.hash(confOk, confidence, counted, formulaConfOk, fractionOk, holisticOk,
				lowerConf, percentage, percentageOk, ruleOk, upperConf);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleExerciseCorrection other = (RuleExerciseCorrection) obj;
		return Objects.equals(confOk, other.confOk)
				&& Double.doubleToLongBits(confidence) == Double.doubleToLongBits(other.confidence)
				&& counted == other.counted && Objects.equals(formulaConfOk, other.formulaConfOk)
				&& Objects.equals(fractionOk, other.fractionOk) && Objects.equals(holisticOk, other.holisticOk)
				&& Arrays.equals(lhs, other.lhs) && lowerConf == other.lowerConf
				&& Arrays.equals(lowerSupp, other.lowerSupp)
				&& Double.doubleToLongBits(percentage) == Double.doubleToLongBits(other.percentage)
				&& Objects.equals(percentageOk, other.percentageOk) && Arrays.equals(rhs, other.rhs)
				&& Objects.equals(ruleOk, other.ruleOk) && upperConf == other.upperConf
				&& Arrays.equals(upperSupp, other.upperSupp);
	}

	/**
	 * adapted equals function for rules evaluation
	 * 
	 * @param obj	object
	 * @return	true, false
	 */
	public boolean equalsForDuplets(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleExerciseCorrection other = (RuleExerciseCorrection) obj;
		return Arrays.equals(lhs, other.lhs) && Arrays.equals(rhs, other.rhs);
	}
}
