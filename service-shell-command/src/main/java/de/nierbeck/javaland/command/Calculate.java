
package de.nierbeck.javaland.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import de.nierbeck.javaland.calculator.CreditCalculator;
import de.nierbeck.javaland.calculator.values.Credit;

@Command(scope = "javaland", name = "Calculate", description = "Calculates a Credit based on the service")
public class Calculate extends OsgiCommandSupport {

	private CreditServiceContainer creditServiceContainer;

    @Argument(name = "argument", description = "Argument to the command", required = false, multiValued = false)
    private String argument;

    protected Object doExecute() throws Exception {
    	CreditCalculator service = null;
    	
    	for (CreditCalculator creditCalculator : creditServiceContainer.getCreditCalculatorServices()) {
			if (creditCalculator.getInstitute().equalsIgnoreCase(argument)) {
				service = creditCalculator;
				break;
			}
		}
    	
    	if (service == null) {
    		return "No service found with institue name: "+argument;
    	}
    	
    	Credit creditValues = new Credit();
    	creditValues.setCredit(10000.0);
    	creditValues.setInterest(4.5);
    	creditValues.setRetention(5);
    	
		service.calculateRate(creditValues);
    	
		return creditValues.getRate();
    }

    public void setCreditServiceContainer(CreditServiceContainer creditServiceContainer) {
		this.creditServiceContainer = creditServiceContainer;
	}
    
    public CreditServiceContainer getCreditServiceContainer() {
		return creditServiceContainer;
	}
}
