package apriori.workflow.elements;

import java.util.Arrays;
import java.util.Objects;

/**
 * class for rules table
 */
public class RuleExercise {

	private String[] lhs;
	private String[] rhs;
	private int upperConf;
	private int lowerConf;
	private double confidence;
	private double percentage;
	private String[] upperSupp;
	private String[] lowerSupp;

	/**
	 * constructor
	 * 
	 * @param lhs	left hand side rule
	 * @param rhs	right hand side rule
	 * @param upperConf	numerator fraction
	 * @param lowerConf	denominator fraction
	 * @param confidence	confidence
	 * @param percentage	confidence in percent
	 * @param upperSupp	upper part formula
	 * @param lowerSupp	lower part formula
	 */
	public RuleExercise(String[] lhs, String[] rhs, int upperConf, int lowerConf, double confidence, double percentage,
			String[] upperSupp, String[] lowerSupp) {
		super();
		Arrays.sort(lhs);
		Arrays.sort(rhs);
		Arrays.sort(upperSupp);
		Arrays.sort(lowerSupp);

		this.lhs = lhs;
		this.rhs = rhs;
		this.upperConf = upperConf;
		this.lowerConf = lowerConf;
		this.confidence = confidence;
		this.percentage = percentage;
		this.upperSupp = upperSupp;
		this.lowerSupp = lowerSupp;
	}

	public String[] getLhs() {
		return lhs;
	}

	public void setLhs(String[] lhs) {
		Arrays.sort(lhs);
		this.lhs = lhs;
	}

	public String[] getRhs() {
		return rhs;
	}

	public void setRhs(String[] rhs) {
		Arrays.sort(rhs);
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
		Arrays.sort(upperSupp);
		this.upperSupp = upperSupp;
	}

	public String[] getLowerSupp() {
		return lowerSupp;
	}

	public void setLowerSupp(String[] lowerSupp) {
		Arrays.sort(lowerSupp);
		this.lowerSupp = lowerSupp;
	}

	@Override
	public String toString() {
		return "RuleExercise [lhs=" + Arrays.toString(lhs) + ", rhs=" + Arrays.toString(rhs) + ", upperConf="
				+ upperConf + ", lowerConf=" + lowerConf + ", confidence=" + confidence + ", percentage=" + percentage
				+ ", upperSupp=" + Arrays.toString(upperSupp) + ", lowerSupp=" + Arrays.toString(lowerSupp) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(lhs);
		result = prime * result + Arrays.hashCode(lowerSupp);
		result = prime * result + Arrays.hashCode(rhs);
		result = prime * result + Arrays.hashCode(upperSupp);
		result = prime * result + Objects.hash(confidence, lowerConf, percentage, upperConf);
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
		RuleExercise other = (RuleExercise) obj;
		return Double.doubleToLongBits(confidence) == Double.doubleToLongBits(other.confidence)
				&& Arrays.equals(lhs, other.lhs) && lowerConf == other.lowerConf
				&& Arrays.equals(lowerSupp, other.lowerSupp)
				&& Double.doubleToLongBits(percentage) == Double.doubleToLongBits(other.percentage)
				&& Arrays.equals(rhs, other.rhs) && upperConf == other.upperConf
				&& Arrays.equals(upperSupp, other.upperSupp);
	}

}
