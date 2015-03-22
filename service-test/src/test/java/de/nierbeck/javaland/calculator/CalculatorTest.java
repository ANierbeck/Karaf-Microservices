package de.nierbeck.javaland.calculator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nierbeck.javaland.calculator.CreditCalculator;
import de.nierbeck.javaland.calculator.values.Credit;


@RunWith(PaxExam.class)
public class CalculatorTest extends TestBase {
	
	private static final Logger LOG = LoggerFactory.getLogger(CalculatorTest.class);
	
	@Inject
	private ConfigurationAdmin configAdminService;

	@Configuration
	public Option[] config() {
		return options(
					combine(
							configBase(), 
							mavenBundle()
								.groupId("de.nierbeck.javaland")
								.artifactId("service-api")
								.versionAsInProject(),
							mavenBundle()
								.groupId("de.nierbeck.javaland")
								.artifactId("service-impl")
								.versionAsInProject()
					)
				);
	}
	
	
	@Test
	public void testCalculatorService() throws Exception {
		LOG.info("starting test");
		org.osgi.service.cm.Configuration configuration = configAdminService.createFactoryConfiguration("de.nierbeck.javaland.calculator");
		
		Dictionary<String,Object> dictionary = configuration.getProperties();
		if (dictionary == null) {
			dictionary = new Hashtable<String, Object>();
		}
		
		dictionary.put("institute", "JavaBank");
		dictionary.put("fee", "1500");
		LOG.info("updating configuration");
		
		configuration.setBundleLocation(null);
		configuration.update(dictionary);
		
		
		LOG.info("retrieving service");
		CreditCalculator osgiService = getOsgiService(CreditCalculator.class);
		
		assertThat(osgiService, is(notNullValue()));
		
		Credit creditValues = new Credit();
		creditValues.setCredit(10000.0);
		creditValues.setInterest(2.5);
		creditValues.setRetention(2);
		
		osgiService.calculateRate(creditValues);
		
		Double rate = creditValues.getRate();
		assertThat(rate, is(notNullValue()));
		assertThat(rate, is(5966.51));
	}

	
	@Test
	public void testRequiredFeatures() throws Exception {
		assertThat(featuresService.isInstalled(featuresService.getFeature("scr")), is(true));
	}
}