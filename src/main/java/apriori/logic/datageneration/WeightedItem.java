package apriori.logic.datageneration;

/**
 * class for item and an attached occurrence probability
 *
 */
public class WeightedItem {

	private String itemName;
	private double weight;// occurrence probability

	/**
	 * constructor
	 * 
	 * @param itemName name of the item
	 * @param weight   weight of the item
	 */
	public WeightedItem(String itemName, double weight) {
		this.itemName = itemName;
		this.weight = weight;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "WeightedItem [itemName=" + itemName + ", probability=" + weight + "]";
	}

}
