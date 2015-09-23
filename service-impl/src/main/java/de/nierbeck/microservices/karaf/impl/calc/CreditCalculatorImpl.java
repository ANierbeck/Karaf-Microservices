package de.nierbeck.microservices.karaf.impl.calc;

import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.ops4j.pax.url.mvn.MavenResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nierbeck.microservices.karaf.calculator.CreditCalculator;
import de.nierbeck.microservices.karaf.calculator.values.Credit;

/**
 *
 */
@Component(configurationPid="de.nierbeck.microservices.karaf.calculator", 
		   property= {"institute=default", "fee=500"}, 
		   configurationPolicy=ConfigurationPolicy.REQUIRE, 
		   immediate=true)
public class CreditCalculatorImpl implements CreditCalculator {
	
	private static final Logger LOG = LoggerFactory.getLogger(CreditCalculatorImpl.class);
	
	private String institute;
	private double fee = 500.0; //Fixed Fee of 500

	private MavenResolver resolver;
	
	
	@Activate
	void activate(Map<String,?> properties) { 
		LOG.info("Activating "+getClass().getName());
		String institute = (String) properties.get("institute");
		if (institute != null)
			this.institute = institute;
		String fee = (String) properties.get("fee");
		if (fee != null)
			this.fee = Double.valueOf(fee);
	}
	
	@Deactivate
	void close() {
		LOG.info("Deactivating "+getClass().getName());
	}
	
	/* (non-Javadoc)
	 * @see de.nierbeck.microservices.karaf.impl.calc.CreditCalculator#calculateRate(de.nierbeck.microservices.karaf.impl.calc.values.Credit)
	 */
	@Override
	public void calculateRate(Credit creditValues) {
		Validate.notNull(creditValues.getCredit(), "Credit needs to be set to calculate the rate");
		Validate.notNull(creditValues.getInterest(), "Interest needs to be set to calculate the rate");
		Validate.notNull(creditValues.getRetention(), "Retention needs to be set to calculate the rate");
		
		Double credit = creditValues.getCredit();
		Double interest = creditValues.getInterest() / 100;
		Integer retention = creditValues.getRetention();
		
		creditValues.setFee(fee);
		
		Double q = ( 1+ interest);
		Double factor = (Math.pow(q, retention) * interest) / (Math.pow(q, retention) - 1);
		Double rate = (credit + fee) * factor;
		rate = (double) ((Math.round(rate * 100) )/ 100.0);
		creditValues.setRate(rate);
	}
	
	@Override
	public String getInstitute() {
		return institute;
	}
	
}
