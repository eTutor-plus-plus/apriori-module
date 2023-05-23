package apriori.logic.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * class for retrieving constants from configuration file
 */
public class Property {

	private final static String configFile = "/application.properties";
	
	/**
	 * for retrieving string value
	 * 
	 * @param key key constant in config file
	 * @return corresponding string
	 */
	public static String getProperty(String key) {

		String loaded = "na";
		Properties property = new Properties();
		try (
				var rs = Property.class.getResourceAsStream(configFile)
				)
		{
			property.load(rs);

			loaded = property.getProperty(key);
			if(loaded.contains("$")){ // the value is in fact a environment variable that has to be replaced
				loaded = System.getenv(loaded.substring(2, loaded.length() - 1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return loaded;
	}
	
	
	/**
	 * for retrieving double value
	 * 
	 * @param key key constant in config file
	 * @return corresponding double value
	 */
	public static double getDoubleProperty(String key) {

		double number = -9999999.999999;
		Properties property = new Properties();
		try (
				var rs = Property.class.getResourceAsStream(configFile)
				)
		{
			property.load(rs);

			String loaded = property.getProperty(key);

			try {

				number = Double.parseDouble(loaded);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}

	/**
	 * for retrieving integer value
	 * 
	 * @param key key constant in config file
	 * @return corresponding integer
	 */
	public static int getIntProperty(String key) {

		int number = -9999999;
		Properties property = new Properties();
		try (
				var rs = Property.class.getResourceAsStream(configFile)
				)
		{
			property.load(rs);

			String loaded = property.getProperty(key);

			try {
				number = Integer.parseInt(loaded);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}
}
