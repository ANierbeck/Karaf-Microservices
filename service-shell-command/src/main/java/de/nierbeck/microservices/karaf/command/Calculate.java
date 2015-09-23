
package de.nierbeck.microservices.karaf.command;

import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.nierbeck.microservices.karaf.calculator.CreditCalculator;
import de.nierbeck.microservices.karaf.calculator.values.Credit;
import de.nierbeck.microservices.karaf.command.completer.CreditCalculatorServiceCompleter;

@Command(scope = "javaland", name = "Calculate", description = "Calculates a Credit based on the service")
@Service
public class Calculate implements Action {

	@Reference
	private List<CreditCalculator> creditServices;

    @Argument(name = "argument", description = "Argument to the command", required = false, multiValued = false)
    @Completion(CreditCalculatorServiceCompleter.class)
    private String argument;

    public Object execute() throws Exception {
    	CreditCalculator service = null;
    	
    	for (CreditCalculator creditCalculator : creditServices) {
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

}
