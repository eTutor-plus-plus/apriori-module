package apriori.workflow.elements;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * class for interim result tables
 */
@Component
public class TrainingTable {

	private int idNumber;
	private String id;// adapted id for displaying table
	private List<TrainingRow> row;

	/**
	 * constructor
	 */
	public TrainingTable() {
		row = new ArrayList<>();
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

	public List<TrainingRow> getRow() {
		return row;
	}

	public void setRow(List<TrainingRow> row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return "ResultTable [idNumber=" + idNumber + ", id=" + id + ", row=" + row + "]";
	}
}
