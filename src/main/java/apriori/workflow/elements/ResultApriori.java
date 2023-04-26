package apriori.workflow.elements;

import java.text.DecimalFormat;

/**
 * class for result of the apriori evaluation
 */
public class ResultApriori {

	private static final DecimalFormat d2Format = new DecimalFormat("0.00");

	double countViableItemsFreq;
	double countViableSupFreq;
	double countMalRowFreq;
	double countForgottenFreq;

	double countViableItemsInter;
	double countViableSupInter;
	double countMalRowInter;
	double countForgottenInter;

	double sumInterim;
	double sumFrequent;
	double sum;

	double countViableItemsFreqTwoDecimals;
	double countViableSupFreqTwoDecimals;
	double countMalRowFreqTwoDecimals;
	double countForgottenFreqTwoDecimals;

	double countViableItemsInterTwoDecimals;
	double countViableSupInterTwoDecimals;
	double countMalRowInterTwoDecimals;
	double countForgottenInterTwoDecimals;

	double sumInterimTwoDecimals;
	double sumFrequentTwoDecimals;
	double sumTwoDecimals;

	/**
	 * constructor
	 */
	public ResultApriori() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param countViableItemsFreq	points for right rows (frequent items)
	 * @param countViableSupFreq	points for right support in rows (frequent items)
	 * @param countMalRowFreq	points for wrong rows (frequent items)
	 * @param countForgottenFreq	points for forgotten rows (frequent items)
	 * @param countViableItemsInter	points for right rows in interim results
	 * @param countViableSupInter	points for right support in rows (interim results)
	 * @param countMalRowInter	points for wrong rows (interim results)
	 * @param countForgottenInter	points for forgotten rows (interim results)
	 */
	public ResultApriori(double countViableItemsFreq, double countViableSupFreq, double countMalRowFreq,
			double countForgottenFreq, double countViableItemsInter, double countViableSupInter,
			double countMalRowInter, double countForgottenInter) {

		this.countViableItemsFreq = countViableItemsFreq;
		this.countViableSupFreq = countViableSupFreq;
		this.countMalRowFreq = countMalRowFreq;
		this.countForgottenFreq = countForgottenFreq;
		this.countViableItemsInter = countViableItemsInter;
		this.countViableSupInter = countViableSupInter;
		this.countMalRowInter = countMalRowInter;
		this.countForgottenInter = countForgottenInter;

		double calcSum = countViableItemsFreq + countViableSupFreq + countMalRowFreq + countForgottenFreq
				+ countViableItemsInter + countViableSupInter + countMalRowInter + countForgottenInter;

		if (calcSum < 0) {
			this.sum = 0;
		} else {
			this.sum = calcSum;
		}

		double calcInterim = countViableItemsInter + countViableSupInter + countMalRowInter + countForgottenInter;

		if (calcInterim < 0) {
			this.sumInterim = 0;
		} else {
			this.sumInterim = calcInterim;
		}

		double calcFrequent = countViableItemsFreq + countViableSupFreq + countMalRowFreq + countForgottenFreq;

		if (calcFrequent < 0) {
			this.sumFrequent = 0;
		} else {
			this.sumFrequent = calcFrequent;
		}

		this.countViableItemsFreqTwoDecimals = Double.parseDouble(d2Format.format(countViableItemsFreq));
		this.countViableSupFreqTwoDecimals = Double.parseDouble(d2Format.format(countViableSupFreq));
		this.countMalRowFreqTwoDecimals = Double.parseDouble(d2Format.format(countMalRowFreq));
		this.countForgottenFreqTwoDecimals = Double.parseDouble(d2Format.format(countForgottenFreq));
		this.countViableItemsInterTwoDecimals = Double.parseDouble(d2Format.format(countViableItemsInter));
		this.countViableSupInterTwoDecimals = Double.parseDouble(d2Format.format(countViableSupInter));
		this.countMalRowInterTwoDecimals = Double.parseDouble(d2Format.format(countMalRowInter));
		this.countForgottenInterTwoDecimals = Double.parseDouble(d2Format.format(countForgottenInter));

		sumTwoDecimals = Double.parseDouble(d2Format.format(sum));

		sumInterimTwoDecimals = Double.parseDouble(d2Format.format(sumInterim));

		sumFrequentTwoDecimals = Double.parseDouble(d2Format.format(sumFrequent));

	}

	public double getSumInterim() {
		return sumInterim;
	}

	public double getSumFrequent() {
		return sumFrequent;
	}

	public double getCountViableItemsFreq() {
		return countViableItemsFreq;
	}

	public void setCountViableItemsFreq(double countViableItemsFreq) {
		this.countViableItemsFreq = countViableItemsFreq;
	}

	public double getCountViableSupFreq() {
		return countViableSupFreq;
	}

	public void setCountViableSupFreq(double countViableSupFreq) {
		this.countViableSupFreq = countViableSupFreq;
	}

	public double getCountMalRowFreq() {
		return countMalRowFreq;
	}

	public void setCountMalRowFreq(double countMalRowFreq) {
		this.countMalRowFreq = countMalRowFreq;
	}

	public double getCountForgottenFreq() {
		return countForgottenFreq;
	}

	public void setCountForgottenFreq(double countForgottenFreq) {
		this.countForgottenFreq = countForgottenFreq;
	}

	public double getCountViableItemsInter() {
		return countViableItemsInter;
	}

	public void setCountViableItemsInter(double countViableItemsInter) {
		this.countViableItemsInter = countViableItemsInter;
	}

	public double getCountViableSupInter() {
		return countViableSupInter;
	}

	public void setCountViableSupInter(double countViableSupInter) {
		this.countViableSupInter = countViableSupInter;
	}

	public double getCountMalRowInter() {
		return countMalRowInter;
	}

	public void setCountMalRowInter(double countMalRowInter) {
		this.countMalRowInter = countMalRowInter;
	}

	public double getCountForgottenInter() {
		return countForgottenInter;
	}

	public void setCountForgottenInter(double countForgottenInter) {
		this.countForgottenInter = countForgottenInter;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getCountViableItemsFreqTwoDecimals() {
		return countViableItemsFreqTwoDecimals;
	}

	public void setCountViableItemsFreqTwoDecimals(double countViableItemsFreqTwoDecimals) {
		this.countViableItemsFreqTwoDecimals = countViableItemsFreqTwoDecimals;
	}

	public double getCountViableSupFreqTwoDecimals() {
		return countViableSupFreqTwoDecimals;
	}

	public void setCountViableSupFreqTwoDecimals(double countViableSupFreqTwoDecimals) {
		this.countViableSupFreqTwoDecimals = countViableSupFreqTwoDecimals;
	}

	public double getCountMalRowFreqTwoDecimals() {
		return countMalRowFreqTwoDecimals;
	}

	public void setCountMalRowFreqTwoDecimals(double countMalRowFreqTwoDecimals) {
		this.countMalRowFreqTwoDecimals = countMalRowFreqTwoDecimals;
	}

	public double getCountForgottenFreqTwoDecimals() {
		return countForgottenFreqTwoDecimals;
	}

	public void setCountForgottenFreqTwoDecimals(double countForgottenFreqTwoDecimals) {
		this.countForgottenFreqTwoDecimals = countForgottenFreqTwoDecimals;
	}

	public double getCountViableItemsInterTwoDecimals() {
		return countViableItemsInterTwoDecimals;
	}

	public void setCountViableItemsInterTwoDecimals(double countViableItemsInterTwoDecimals) {
		this.countViableItemsInterTwoDecimals = countViableItemsInterTwoDecimals;
	}

	public double getCountViableSupInterTwoDecimals() {
		return countViableSupInterTwoDecimals;
	}

	public void setCountViableSupInterTwoDecimals(double countViableSupInterTwoDecimals) {
		this.countViableSupInterTwoDecimals = countViableSupInterTwoDecimals;
	}

	public double getCountMalRowInterTwoDecimals() {
		return countMalRowInterTwoDecimals;
	}

	public void setCountMalRowInterTwoDecimals(double countMalRowInterTwoDecimals) {
		this.countMalRowInterTwoDecimals = countMalRowInterTwoDecimals;
	}

	public double getCountForgottenInterTwoDecimals() {
		return countForgottenInterTwoDecimals;
	}

	public void setCountForgottenInterTwoDecimals(double countForgottenInterTwoDecimals) {
		this.countForgottenInterTwoDecimals = countForgottenInterTwoDecimals;
	}

	public double getSumInterimTwoDecimals() {
		return sumInterimTwoDecimals;
	}

	public void setSumInterimTwoDecimals(double sumInterimTwoDecimals) {
		this.sumInterimTwoDecimals = sumInterimTwoDecimals;
	}

	public double getSumFrequentTwoDecimals() {
		return sumFrequentTwoDecimals;
	}

	public void setSumFrequentTwoDecimals(double sumFrequentTwoDecimals) {
		this.sumFrequentTwoDecimals = sumFrequentTwoDecimals;
	}

	public double getSumTwoDecimals() {
		return sumTwoDecimals;
	}

	public void setSumTwoDecimals(double sumTwoDecimals) {
		this.sumTwoDecimals = sumTwoDecimals;
	}

	public static DecimalFormat getD2format() {
		return d2Format;
	}

	public void setSumInterim(double sumInterim) {
		this.sumInterim = sumInterim;
	}

	public void setSumFrequent(double sumFrequent) {
		this.sumFrequent = sumFrequent;
	}

	@Override
	public String toString() {
		return "ResultApriori [countViableItemsFreq=" + countViableItemsFreq + ", countViableSupFreq="
				+ countViableSupFreq + ", countMalRowFreq=" + countMalRowFreq + ", countForgottenFreq="
				+ countForgottenFreq + ", countViableItemsInter=" + countViableItemsInter + ", countViableSupInter="
				+ countViableSupInter + ", countMalRowInter=" + countMalRowInter + ", countForgottenInter="
				+ countForgottenInter + ", sumInterim=" + sumInterim + ", sumFrequent=" + sumFrequent + ", sum=" + sum
				+ ", countViableItemsFreqTwoDecimals=" + countViableItemsFreqTwoDecimals
				+ ", countViableSupFreqTwoDecimals=" + countViableSupFreqTwoDecimals + ", countMalRowFreqTwoDecimals="
				+ countMalRowFreqTwoDecimals + ", countForgottenFreqTwoDecimals=" + countForgottenFreqTwoDecimals
				+ ", countViableItemsInterTwoDecimals=" + countViableItemsInterTwoDecimals
				+ ", countViableSupInterTwoDecimals=" + countViableSupInterTwoDecimals
				+ ", countMalRowInterTwoDecimals=" + countMalRowInterTwoDecimals + ", countForgottenInterTwoDecimals="
				+ countForgottenInterTwoDecimals + ", sumInterimTwoDecimals=" + sumInterimTwoDecimals
				+ ", sumFrequentTwoDecimals=" + sumFrequentTwoDecimals + ", sumTwoDecimals=" + sumTwoDecimals + "]";
	}

}
