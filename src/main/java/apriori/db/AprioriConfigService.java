package apriori.db;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * service class for task configurations
 */
@Service
public class AprioriConfigService {

	@Autowired
	private AprioriConfigRepository repo;

	/**
	 * for retrieving all task configurations
	 * 
	 * @return list task configurations
	 */
	public List<AprioriConfig> getAllRows() {
		List<AprioriConfig> list = (List<AprioriConfig>) repo.findAll();
		return list;
	}

	/**
	 * for deleting task configuration by configuration id
	 * 
	 * @param uuid_ac task configuration id
	 */
	public void deleteByUuid_ac(UUID uuid_ac) {
		repo.deleteConfig(uuid_ac);
	}

	/**
	 * for deleting task configuration by data set id
	 * 
	 * @param uuid_ht data set id
	 */
	public void deleteConfigByDataset(UUID uuid_ht) {
		repo.deleteConfigByDataset(uuid_ht);
	}

	/**
	 * for saving task configuration (no object)
	 * 
	 * @param uuid_ac         task configuration id
	 * @param uuid_ht         data set id
	 * @param minsupport      minimum support for the apriori algorithm
	 * @param minconfidence   minimum confidence for rules
	 * @param numelementsrule number of items to derive a rule
	 * @param numrulesasked   number of rules queried
	 * @param comment         name of the task configuration
	 */
	public void saveRow(UUID uuid_ac, UUID uuid_ht, int minsupport, int minconfidence, int numelementsrule,
			int numrulesasked, String comment) {
		AprioriConfig newConfig = new AprioriConfig();
		newConfig.setUuid_ac(uuid_ac);
		newConfig.setUuid_ht(uuid_ht);
		newConfig.setMinsupport(minsupport);
		newConfig.setNumrulesasked(numrulesasked);
		newConfig.setComment(comment);
		newConfig.setMinconfidence(minconfidence);

		newConfig.setNumelementsrule(numelementsrule);

		this.repo.save(newConfig);
	}

	/**
	 * for saving task configuration (object)
	 * 
	 * @param ht data set id
	 */
	public void saveRow(AprioriConfig ht) {
		this.repo.save(ht);
	}

	/**
	 * for checking if a configuration id already exists
	 * 
	 * @param uuid task configuration id to be checked
	 * @return true (exists), false (nonexistent)
	 */
	public boolean checkConfigExists(UUID uuid) {
		List<AprioriConfig> list = getAprioriConfig(uuid);

		if (list.size() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * for retrieving task configuration by configuration id
	 * 
	 * @param uuid task configuration id to be retrieved
	 * @return list task configuration
	 */
	public List<AprioriConfig> findConfigByUuid(UUID uuid) {

		List<AprioriConfig> list = repo.findAprioriConfigByUuid_ht(uuid);
		return list;
	}

	/**
	 * for retrieving task configuration by data set id
	 * 
	 * @param uuid data set id
	 * @return list task configuration
	 */
	public List<AprioriConfig> findConfigByUuid_ht(UUID uuid) {

		List<AprioriConfig> list = repo.findAprioriConfigByUuid_ht(uuid);
		return list;
	}

	/**
	 * for retrieving task configuration by configuration id
	 * 
	 * @param uuid task configuration id
	 * @return list task configurations id's
	 */
	public List<AprioriConfig> getAprioriConfig(UUID uuid) {
		List<AprioriConfig> list = repo.findAprioriConfigByUuid_ac(uuid);
		return list;
	}
}
