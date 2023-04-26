package apriori.db;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * class for storing task configurations
 */
@Entity
public class AprioriConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private UUID uuid_ac;// id config
	private UUID uuid_ht;// id data set
	private int minsupport;// minimum support
	private int minconfidence;// minimum confidence

	private int numelementsrule;// numbers of elements to derive rule from

	private int numrulesasked;// number of rules queried
	private String comment;// name config

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getUuid_ac() {
		return uuid_ac;
	}

	public void setUuid_ac(UUID uuid_ac) {
		this.uuid_ac = uuid_ac;
	}

	public UUID getUuid_ht() {
		return uuid_ht;
	}

	public void setUuid_ht(UUID uuid_ht) {
		this.uuid_ht = uuid_ht;
	}

	public int getMinsupport() {
		return minsupport;
	}

	public void setMinsupport(int minsupport) {
		this.minsupport = minsupport;
	}

	public int getMinconfidence() {
		return minconfidence;
	}

	public void setMinconfidence(int minconfidence) {
		this.minconfidence = minconfidence;
	}

	public int getNumrulesasked() {
		return numrulesasked;
	}

	public void setNumrulesasked(int numrulesasked) {
		this.numrulesasked = numrulesasked;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getNumelementsrule() {
		return numelementsrule;
	}

	public void setNumelementsrule(int numelementsrule) {
		this.numelementsrule = numelementsrule;
	}

	@Override
	public String toString() {
		return "AprioriConfig [id=" + id + ", uuid_ac=" + uuid_ac + ", uuid_ht=" + uuid_ht + ", minsupport="
				+ minsupport + ", minconfidence=" + minconfidence + ", numelementsrule=" + numelementsrule
				+ ", numrulesasked=" + numrulesasked + ", comment=" + comment + "]";
	}

}
