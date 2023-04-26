package apriori.workflow.elements;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import apriori.logic.algorithm.ItemSet;
import apriori.logic.algorithm.Row;
import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.datageneration.AssociationRow;

/**
 * class for data set table (data base format)
 */
@Component
public class HorizontalTable {

	private List<HorizontalRow> list;

	/**
	 * constructor
	 */
	public HorizontalTable() {

		list = new ArrayList<>();
	}

	/**
	 * for transforming apriori logic data set format to database format
	 * 
	 * @param ads AssociationDataSet
	 * @return list of rows
	 */
	public List<HorizontalRow> makeList(AssociationDataSet ads) {
		list = new ArrayList<>();
		for (Row<String> ar : ads.getAssociationRows()) {
			list.add(new HorizontalRow(ar.getIndicator(), ar.getItemset().getItems()));
		}
		return list;
	}

	/**
	 * for transforming database format to apriori logic format
	 * 
	 * @return AssociationRow[]
	 */
	public AssociationRow[] makeASD() {
		List<AssociationRow> asd = new ArrayList<>();
		for (HorizontalRow hr : list) {
			asd.add(new AssociationRow(hr.getId(), new ItemSet(hr.getItems())));
		}
		AssociationRow[] ar = new AssociationRow[asd.size()];
		asd.toArray(ar);
		return ar;
	}

	public List<HorizontalRow> getList() {
		return list;
	}

	public void setList(List<HorizontalRow> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "HorizontalTable [list=" + list + "]";
	}

}
