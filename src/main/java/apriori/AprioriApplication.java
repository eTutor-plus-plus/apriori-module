package apriori;

import apriori.logic.utility.Property;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * class for starting eTutor++ apriori extension
 */
@SpringBootApplication
public class AprioriApplication {


	/**
	 * starting function
	 * 
	 * @param args	application starting parameters
	 */
	public static void main(String[] args) {
		System.out.println(Property.getProperty("----------------------CONFIGURATION--------------------"));
		System.out.println(Property.getProperty("etutorplusplus.link"));
		System.out.println(Property.getProperty("etutorplusplus.extension.key"));
		System.out.println(Property.getProperty("semantic.link"));
		System.out.println(Property.getProperty("semantic.query.endpoint"));
		System.out.println(Property.getProperty("semantic.update.endpoint"));
		System.out.println(Property.getProperty("spring.datasource.url"));
		System.out.println(Property.getProperty("spring.datasource.username"));
		System.out.println(Property.getProperty("spring.datasource.password"));
		System.out.println(Property.getProperty("----------------------CONFIGURATION END--------------------"));
		SpringApplication.run(AprioriApplication.class, args);
	}

}
