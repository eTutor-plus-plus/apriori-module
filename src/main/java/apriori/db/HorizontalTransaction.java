package apriori.db;

import java.util.Arrays;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.array.StringArrayType;

/**
 * class for row of data set (data base format)
 */
@TypeDefs({

		@TypeDef(

				name = "string-array",

				typeClass = StringArrayType.class

		) })

@Entity
public class HorizontalTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private UUID uuid_ht;// id data set

	private String tid;// transaction row id (not relevant for algorithm)

	@Type(type = "string-array")
	private String[] items;// items in transaction

	private String comment;// name of the dataset

	public UUID getUuid_ht() {
		return uuid_ht;
	}

	public void setUuid_ht(UUID uuid_ht) {
		this.uuid_ht = uuid_ht;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "HorizontalTransaction [id=" + id + ", uuid_ht=" + uuid_ht + ", tid=" + tid + ", items="
				+ Arrays.toString(items) + ", comment=" + comment + "]";
	}

}
