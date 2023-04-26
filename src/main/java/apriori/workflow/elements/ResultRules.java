package apriori.workflow.elements;

import java.text.DecimalFormat;

/**
 * class for the result of the association rules task
 */
public class ResultRules {

	private static final DecimalFormat d2Format = new DecimalFormat("0.00");

	private double evalrule;
	private double evalconfidence;
	private double evalformula;
	private double evalpenaltyruleincorrect;
	private double evalpenaltyruleforgotten;

	private double sum;
	private double percent;

	private double evalruleTwoDecimals;
	private double evalconfidenceTwoDecimals;
	private double evalformulaTwoDecimals;
	private double evalpenaltyruleincorrectTwoDecimals;
	private double evalpenaltyruleforgottenTwoDecimals;

	private double sumTwoDecimals;
	private double percentTwoDecimals;

	/**
	 * constructor
	 */
	public ResultRules() {

	}

	/**
	 * constructor
	 * 
	 * @param evalrule	points for right rule
	 * @param evalconfidence	points for right confidence
	 * @param evalformula	points for right formula and fraction
	 * @param evalpenaltyruleincorrect	points for wrong rules
	 * @param evalpenaltyruleforgotten	points for forgotten rules
	 */
	public ResultRules(double evalrule, double evalconfidence, double evalformula, double evalpenaltyruleincorrect,
			double evalpenaltyruleforgotten) {
		super();
		this.evalrule = evalrule;
		this.evalconfidence = evalconfidence;
		this.evalformula = evalformula;
		this.evalpenaltyruleincorrect = evalpenaltyruleincorrect;
		this.evalpenaltyruleforgotten = evalpenaltyruleforgotten;
		sum = evalrule + evalconfidence + evalformula + evalpenaltyruleincorrect + evalpenaltyruleforgotten;
		if (sum < 0) {
			this.sum = 0;
		}
		this.evalruleTwoDecimals = Double.parseDouble(d2Format.format(this.evalrule));
		this.evalconfidenceTwoDecimals = Double.parseDouble(d2Format.format(this.evalconfidence));
		this.evalformulaTwoDecimals = Double.parseDouble(d2Format.format(this.evalformula));
		this.evalpenaltyruleincorrectTwoDecimals = Double.parseDouble(d2Format.format(this.evalpenaltyruleincorrect));
		this.evalpenaltyruleforgottenTwoDecimals = Double.parseDouble(d2Format.format(this.evalpenaltyruleforgotten));
		sumTwoDecimals = Double.parseDouble(d2Format.format(this.sum));

	}

	public double getEvalrule() {
		return evalrule;
	}

	public void setEvalrule(double evalrule) {
		this.evalrule = evalrule;
	}

	public double getEvalconfidence() {
		return evalconfidence;
	}

	public void setEvalconfidence(double evalconfidence) {
		this.evalconfidence = evalconfidence;
	}

	public double getEvalformula() {
		return evalformula;
	}

	public void setEvalformula(double evalformula) {
		this.evalformula = evalformula;
	}

	public double getEvalpenaltyruleincorrect() {
		return evalpenaltyruleincorrect;
	}

	public void setEvalpenaltyruleincorrect(double evalpenaltyruleincorrect) {
		this.evalpenaltyruleincorrect = evalpenaltyruleincorrect;
	}

	public double getEvalpenaltyruleforgotten() {
		return evalpenaltyruleforgotten;
	}

	public void setEvalpenaltyruleforgotten(double evalpenaltyruleforgotten) {
		this.evalpenaltyruleforgotten = evalpenaltyruleforgotten;
	}

	public double getSum() {
		return sum;
	}

	public double getPercent() {
		return percent;
	}

	public double getEvalruleTwoDecimals() {
		return evalruleTwoDecimals;
	}

	public void setEvalruleTwoDecimals(double evalruleTwoDecimals) {
		this.evalruleTwoDecimals = evalruleTwoDecimals;
	}

	public double getEvalconfidenceTwoDecimals() {
		return evalconfidenceTwoDecimals;
	}

	public void setEvalconfidenceTwoDecimals(double evalconfidenceTwoDecimals) {
		this.evalconfidenceTwoDecimals = evalconfidenceTwoDecimals;
	}

	public double getEvalformulaTwoDecimals() {
		return evalformulaTwoDecimals;
	}

	public void setEvalformulaTwoDecimals(double evalformulaTwoDecimals) {
		this.evalformulaTwoDecimals = evalformulaTwoDecimals;
	}

	public double getEvalpenaltyruleincorrectTwoDecimals() {
		return evalpenaltyruleincorrectTwoDecimals;
	}

	public void setEvalpenaltyruleincorrectTwoDecimals(double evalpenaltyruleincorrectTwoDecimals) {
		this.evalpenaltyruleincorrectTwoDecimals = evalpenaltyruleincorrectTwoDecimals;
	}

	public double getEvalpenaltyruleforgottenTwoDecimals() {
		return evalpenaltyruleforgottenTwoDecimals;
	}

	public void setEvalpenaltyruleforgottenTwoDecimals(double evalpenaltyruleforgottenTwoDecimals) {
		this.evalpenaltyruleforgottenTwoDecimals = evalpenaltyruleforgottenTwoDecimals;
	}

	public double getSumTwoDecimals() {
		return sumTwoDecimals;
	}

	public void setSumTwoDecimals(double sumTwoDecimals) {
		this.sumTwoDecimals = sumTwoDecimals;
	}

	public double getPercentTwoDecimals() {
		return percentTwoDecimals;
	}

	public void setPercentTwoDecimals(double percentTwoDecimals) {
		this.percentTwoDecimals = percentTwoDecimals;
	}

	public static DecimalFormat getD2format() {
		return d2Format;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	@Override
	public String toString() {
		return "ResultRules [evalrule=" + evalrule + ", evalconfidence=" + evalconfidence + ", evalformula="
				+ evalformula + ", evalpenaltyruleincorrect=" + evalpenaltyruleincorrect + ", evalpenaltyruleforgotten="
				+ evalpenaltyruleforgotten + ", sum=" + sum + ", percent=" + percent + ", evalruleTwoDecimals="
				+ evalruleTwoDecimals + ", evalconfidenceTwoDecimals=" + evalconfidenceTwoDecimals
				+ ", evalformulaTwoDecimals=" + evalformulaTwoDecimals + ", evalpenaltyruleincorrectTwoDecimals="
				+ evalpenaltyruleincorrectTwoDecimals + ", evalpenaltyruleforgottenTwoDecimals="
				+ evalpenaltyruleforgottenTwoDecimals + ", sumTwoDecimals=" + sumTwoDecimals + ", percentTwoDecimals="
				+ percentTwoDecimals + "]";
	}

}
