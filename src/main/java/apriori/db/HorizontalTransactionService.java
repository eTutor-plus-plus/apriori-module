package apriori.db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apriori.logic.datageneration.AssociationDataSet;
import apriori.logic.utility.DbUtility;

/**
 * service class for the data sets (rows)
 */
@Service
public class HorizontalTransactionService {

	@Autowired
	private HorizontalTransactionRepository repo;

	/**
	 * for retrieving all rows of all data sets
	 * 
	 * @return list of rows
	 */
	public List<HorizontalTransaction> getAllRows() {

		return repo.findAll();

	}

	/**
	 * for checking if data set exists
	 * 
	 * @param uuid data set id
	 * @return true (exists), false (nonexistent)
	 */
	public boolean checkDatasetExists(UUID uuid) {
		List<HorizontalTransaction> list = findDatasetByUuid(uuid);
		return !list.isEmpty();
	}

	/**
	 * for deleting data set by its id
	 * 
	 * @param uuid data set id
	 * @return number of rows deleted
	 */
	public int deleteDataset(UUID uuid) {
		return repo.deleteDataset(uuid);
	}

	/**
	 * for finding data set by its id
	 * 
	 * @param uuid data set id
	 * @return list of rows
	 */
	public List<HorizontalTransaction> findDatasetByUuid(UUID uuid) {

		return repo.findDatasetByUuid(uuid);
	}

	/**
	 * for retrieving all data sets
	 * 
	 * @return list rows of all data sets
	 */
	public List<HorizontalTransaction> findAllSets() {
		return repo.findAll();
	}

	/**
	 * for list of all data sets
	 * 
	 * @return list with all data sets
	 */
	public List<HorizontalTransaction> listAllDatasets() {
		List<HorizontalTransaction> list = repo.findAll();

		List<HorizontalTransaction> listChecked = new ArrayList<>();
		for (HorizontalTransaction ht : list) {
			boolean check = false;
			for (HorizontalTransaction hts : listChecked) {
				if (ht.getUuid_ht().equals(hts.getUuid_ht())) {
					check = true;
					break;
				}
			}
			if (!check) {
				listChecked.add(ht);
			}
		}
		return listChecked;
	}

	/**
	 * for saving row
	 * 
	 * @param ht row of data set
	 */
	public void saveRow(HorizontalTransaction ht) {
		this.repo.save(ht);
	}

	/**
	 * for saving a data set
	 * 
	 * @param ads  association data set
	 * @param uuid id data set
	 * @param name name data set
	 */
	public void saveDataset(AssociationDataSet ads, UUID uuid, String name) {

		List<HorizontalTransaction> horizontalList = DbUtility.transformAssociationDataSet(ads, uuid, name);

		for (HorizontalTransaction ht : horizontalList) {
			saveRow(ht);
		}
	}

}
