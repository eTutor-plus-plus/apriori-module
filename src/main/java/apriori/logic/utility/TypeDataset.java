package apriori.logic.utility;

import java.util.EnumMap;
import java.util.Map;

/**
 * enumeration of available data sets
 */
public enum TypeDataset {

	/**
	 * data set with letters; available items in alphabetic sequence ('ABCD...')
	 */
	ABC("Alphabetic sequentially"), 
	/**
	 * data set with letters; available items in random alphabetic sequence ('SJBX...')
	 */
	RandomLetters("Alphabetic random order"), 
	/**
	 * shopping basket as data set (in german); data set defined by external file 'mikrowarenkorb.csv'
	 */
	BASKETA("Shopping Basket Statistic Austria"),
	
	
	/**
	 * shopping basket as data set (in english); data set defined by external file 'Groceries_english.csv'
	 */
	BASKETE("Shopping Basket")
	;

	private final String displayValue;
	
	private final static EnumMap<TypeDataset,String> map = new EnumMap<>(TypeDataset.class);
	
	static{
		map.put(BASKETA, "mikrowarenkorb.csv");	
		map.put(BASKETE, "Groceries_english.csv");
	}

	public static void printAll() {
	    for (Map.Entry<TypeDataset,String> entry : map.entrySet()) {
	        System.out.println(entry.getKey() + ":" + entry.getValue());
	    }
	}
	
	public static String retrieveFileName(String key) {
		return map.get(TypeDataset.BASKETA);
	}
	
	public static String retrieveFileNameDataset(String key) {
		String s="TypeDataset."+key;
		System.out.println(" tt22: "+s);
		
		String filename=null;
		
	    for (Map.Entry<TypeDataset,String> entry : map.entrySet()) {
	        if(   entry.getKey().toString().equals(key)) {
	        	filename=entry.getValue();        	
	        }
	    }

		return filename;
	}
	
	public static EnumMap<TypeDataset, String> getMap() {
		return map;
	}

	/**
	 * constructor
	 * 
	 * @param displayValue	text to be displayed
	 */
	private TypeDataset(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

}
