package apriori.logic.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * class for retrieving constants from configuration file
 */
public class Property {

	/**
	 * for retrieving double value
	 * 
	 * @param key key constant in config file
	 * @return corresponding double value
	 */
	public static double getDoubleProperty(String key) {

		double number = -9999999.999999;
		Properties property = new Properties();
		String configFilePath = "src/main/resources/aprioriConfig.properties";
		try {
			FileInputStream propertyInput = new FileInputStream(configFilePath);

			property.load(propertyInput);

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
		String configFilePath = "src/main/resources/aprioriConfig.properties";
		try {
			FileInputStream propertyInput = new FileInputStream(configFilePath);

			property.load(propertyInput);

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
		}
		return number;
	}

	/**
	 * for retrieving string value
	 * 
	 * @param key key constant in config file
	 * @return corresponding string
	 */
	public static String getProperty(String key) {

		String loaded = "na";
		Properties property = new Properties();
		String configFilePath = "src/main/resources/aprioriConfig.properties";
		try {
			FileInputStream propertyInput = new FileInputStream(configFilePath);

			property.load(propertyInput);

			loaded = property.getProperty(key);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loaded;
	}
}
