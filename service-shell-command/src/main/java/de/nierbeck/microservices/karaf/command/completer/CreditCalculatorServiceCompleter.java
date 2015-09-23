package de.nierbeck.microservices.karaf.command.completer;

import java.util.List;

import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.ArgumentCommandLine;
import org.apache.karaf.shell.support.completers.StringsCompleter;

import de.nierbeck.microservices.karaf.calculator.CreditCalculator;

@Service
public class CreditCalculatorServiceCompleter implements Completer {

	@Reference
	private List<CreditCalculator> creditCalculatorServices;

	StringsCompleter delegate = new StringsCompleter(false);

	public int complete(Session session, CommandLine commandLine, List<String> candidates) {
		if (session != null) {

			if (commandLine instanceof ArgumentCommandLine) {
				delegate.getStrings().add(commandLine.getCursorArgument());

			} else {

				for (CreditCalculator creditCalculator : creditCalculatorServices) {
					delegate.getStrings().add(creditCalculator.getInstitute());
				}

			}
		}
		return delegate.complete(session, commandLine, candidates);
	}

}
