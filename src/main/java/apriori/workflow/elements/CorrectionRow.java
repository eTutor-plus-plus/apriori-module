package apriori.workflow.elements;

import java.util.Arrays;
import java.util.Objects;

/**
 * class for row for apriori evaluation table (interim results)
 */
public class CorrectionRow {

	private boolean counted;

	private String[] itemsTraining;
	private int supportCountTraining;

	private String itemsCorrect; // convention: yes, no
	private String supportCorrect; // convention: yes, no

	private String holisticCorrect;// convention: yes(both correct); no (one or both incorrect)

	/**
	 * constructor
	 */
	public CorrectionRow() {

	}

	/**
	 * constructor
	 * 
	 * @param itemsTraining	items row
	 * @param supportCountTraining	support count row
	 */
	public CorrectionRow(String[] itemsTraining, int supportCountTraining) {
		this.itemsTraining = itemsTraining;
		this.supportCountTraining = supportCountTraining;
	}

	/**
	 * for marking whole row correct or incorrect
	 */
	public void setCalcHolisticCorrect() {
		String result;
		if (itemsCorrect == null || supportCorrect == null) {
			result = "no";
		} else {
			if (itemsCorrect == "yes" && supportCorrect == "yes") {
				result = "yes";
			} else {
				result = "no";
			}
		}
		this.holisticCorrect = result;
	}

	public boolean getCounted() {
		return counted;
	}

	public void setCounted(boolean counted) {
		this.counted = counted;
	}

	public String getHolisticCorrect() {
		return holisticCorrect;
	}

	public String[] getItemsTraining() {
		return itemsTraining;
	}

	public void setItemsTraining(String[] itemsTraining) {
		this.itemsTraining = itemsTraining;
	}

	public int getSupportCountTraining() {
		return supportCountTraining;
	}

	public void setSupportCountTraining(int supportCountTraining) {
		this.supportCountTraining = supportCountTraining;
	}

	public String getItemsCorrect() {
		return itemsCorrect;
	}

	public void setItemsCorrect(String itemsCorrect) {
		this.itemsCorrect = itemsCorrect;
	}

	public String getSupportCorrect() {
		return supportCorrect;
	}

	public void setSupportCorrect(String supportCorrect) {
		this.supportCorrect = supportCorrect;
	}

	@Override
	public String toString() {
		return "CorrectionRow [counted=" + counted + ", itemsTraining=" + Arrays.toString(itemsTraining)
				+ ", supportCountTraining=" + supportCountTraining + ", itemsCorrect=" + itemsCorrect
				+ ", supportCorrect=" + supportCorrect + ", holisticCorrect=" + holisticCorrect + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(itemsTraining);
		result = prime * result
				+ Objects.hash(counted, holisticCorrect, itemsCorrect, supportCorrect, supportCountTraining);
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
		CorrectionRow other = (CorrectionRow) obj;
		return counted == other.counted && Objects.equals(holisticCorrect, other.holisticCorrect)
				&& Objects.equals(itemsCorrect, other.itemsCorrect) && Arrays.equals(itemsTraining, other.itemsTraining)
				&& Objects.equals(supportCorrect, other.supportCorrect)
				&& supportCountTraining == other.supportCountTraining;
	}

	/**
	 * adapted equals function for apriori evaluation
	 * 
	 * @param obj	object
	 * @return true, false
	 */
	public boolean equalsForDublets(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CorrectionRow other = (CorrectionRow) obj;
		return Objects.equals(itemsCorrect, other.itemsCorrect) && Arrays.equals(itemsTraining, other.itemsTraining);
	}

}
