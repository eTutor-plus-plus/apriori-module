package apriori.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * jpa interface for data set rows (in data base format)
 */
public interface HorizontalTransactionRepository extends JpaRepository<HorizontalTransaction, Long> {

	/**
	 * for retrieving all horizontal transactions
	 * 
	 * @return list of horizontal transactions
	 */
	@Query("SELECT h FROM HorizontalTransaction h")
	List<HorizontalTransaction> findAllHorizontalTransaction();

	/**
	 * for retrieving data set by data set id
	 * 
	 * @param uuid data set id
	 * @return list of horizontal transactions
	 */
	@Query("SELECT h FROM HorizontalTransaction h WHERE h.uuid_ht = ?1")
	List<HorizontalTransaction> findDatasetByUuid(UUID uuid);

	/**
	 * for deleting data set by id
	 * 
	 * @param uuid_ht data set id
	 * @return number of deleted rows
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM HorizontalTransaction b WHERE b.uuid_ht= ?1")
	int deleteDataset(UUID uuid_ht);

}
