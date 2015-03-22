package de.nierbeck.javaland.command.completer;

import java.util.List;

import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;

import de.nierbeck.javaland.calculator.CreditCalculator;

public class CreditCalculatorServiceCompleter implements Completer {

	private List<CreditCalculator> creditCalculatorServices;

	public int complete(String buffer, int cursor, List<String> candidates) {
		StringsCompleter delegate = new StringsCompleter();
		
		for (CreditCalculator creditCalculator : creditCalculatorServices) {
			delegate.getStrings().add(creditCalculator.getInstitute());
		}
		
		return delegate.complete(buffer, cursor, candidates);
	}
	
	
	public void setCreditCalculatorServices(List<CreditCalculator> creditCalculatorServices) {
		this.creditCalculatorServices = creditCalculatorServices;
	}
	
	public List<CreditCalculator> getCreditCalculatorServices() {
		return creditCalculatorServices;
	}
}
