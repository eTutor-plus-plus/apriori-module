package apriori.workflow.elements;

import java.util.List;

/**
 * class for apriori evaluation table (interim results)
 */
public class CorrectionTable {

	private int idNumber;
	private String id;// adapted id for displaying table
	private List<CorrectionRow> row;

	private String correct;

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}

	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}

	public String getId() {
		return "L" + idNumber;
	}

	public void setId() {
		this.id = "L" + id + "pruned";
	}

	public List<CorrectionRow> getRow() {
		return row;
	}

	public void setRow(List<CorrectionRow> row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return "CorrectionTable [idNumber=" + idNumber + ", id=" + id + ", row=" + row + ", correct=" + correct + "]";
	}

}
