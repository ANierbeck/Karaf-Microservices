package de.nierbeck.microservices.karaf.command;

import java.util.List;

import de.nierbeck.microservices.karaf.calculator.CreditCalculator;

public class CreditServiceContainer {
	
	private List<CreditCalculator> creditCalculatorServices;

	
	public void setCreditCalculatorServices(List<CreditCalculator> creditCalculatorServices) {
		this.creditCalculatorServices = creditCalculatorServices;
	}
	
	public List<CreditCalculator> getCreditCalculatorServices() {
		return creditCalculatorServices;
	}
	
}
