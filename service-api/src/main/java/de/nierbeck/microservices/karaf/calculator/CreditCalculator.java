package de.nierbeck.microservices.karaf.calculator;

import de.nierbeck.microservices.karaf.calculator.values.Credit;

/**
 * Public interface to be implemented by a calculating object. 
 * The different calculation Objects might be different regarding the fee. 
 */
public interface CreditCalculator {
	
	/**
	 * Calculate the rate based on the given values. 
	 * 
	 * @param creditValues
	 */
	void calculateRate(Credit creditValues);

	String getInstitute();
	
//	Long getInstitueFee(String institute);
	
}
