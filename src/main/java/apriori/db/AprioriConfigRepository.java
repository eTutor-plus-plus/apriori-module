package apriori.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * jpa interface for task configurations
 */
public interface AprioriConfigRepository extends JpaRepository<AprioriConfig, Long> {

	/**
	 * for retrieving all task configurations
	 * 
	 * @return list of tasks configurations
	 */
	@Query("SELECT a FROM AprioriConfig a")
	List<AprioriConfig> findAllAprioriConfig();

	/**
	 * for retrieving task configuration by configuration id
	 * 
	 * @param uuid	id of tasks configuration
	 * @return list of tasks configurations
	 */
	@Query("SELECT h FROM AprioriConfig h WHERE h.uuid_ac = ?1")
	List<AprioriConfig> findAprioriConfigByUuid_ac(UUID uuid);

	/**
	 * for retrieving task configuration by data set id
	 * 
	 * @param uuid	id of data set
	 * @return list of tasks configurations
	 */
	@Query("SELECT h FROM AprioriConfig h WHERE h.uuid_ht = ?1")
	List<AprioriConfig> findAprioriConfigByUuid_ht(UUID uuid);

	/**
	 * for deleting task configuration by configuration id
	 * 
	 * @param uuid_ac	id of tasks configuration
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM AprioriConfig b WHERE b.uuid_ac= ?1")
	void deleteConfig(UUID uuid_ac);

	/**
	 * for deleting all task configurations for a specific data set id
	 * 
	 * @param uuid_ht	id data set
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM AprioriConfig b WHERE b.uuid_ht= ?1")
	void deleteConfigByDataset(UUID uuid_ht);
}
