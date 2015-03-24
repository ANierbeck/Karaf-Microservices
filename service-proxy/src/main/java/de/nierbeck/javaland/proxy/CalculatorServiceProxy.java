package de.nierbeck.javaland.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.nierbeck.javaland.calculator.CreditCalculator;
import de.nierbeck.javaland.calculator.values.Credit;

@Produces(MediaType.APPLICATION_JSON)
public class CalculatorServiceProxy {

	private List<CreditCalculator> creditCalculatorServices;
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOverview() {
		
		List<String> serviceNames = new ArrayList<String>();
		
		for (CreditCalculator creditCalculator : creditCalculatorServices) {
			serviceNames.add(creditCalculator.getInstitute());
		}
		
		
		InstitueListing institueListing = new InstitueListing();
		institueListing.institutes = serviceNames;
		
		return Response.ok(institueListing).build();
	}
	
	@POST
	@Path("{institute}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Credit calculateCredit(@PathParam("institute") String institute,  Credit credit) {
		CreditCalculator service = null;
		
		for (CreditCalculator serviceReference : creditCalculatorServices) {
			if (institute.equalsIgnoreCase(serviceReference.getInstitute())) {
				service = serviceReference;
			}
		}
		
		if (service == null) {
			throw new WebApplicationException("Corresponding Institute wasn't found as service", Response.Status.NOT_FOUND);
		}
		
		service.calculateRate(credit);
		
		return credit;
	}
	
	
	@GET
	@Path("find")
	public Credit find() {
		Credit credit = new Credit();
		credit.setCredit(10000.0);
		credit.setInterest(2.5);
		credit.setRetention(10);
		
		return credit;
	}
	
	
	public void setCreditCalculatorServices(List<CreditCalculator> creditCalculatorServices) {
		this.creditCalculatorServices = creditCalculatorServices;
	}
	
	public List<CreditCalculator> getCreditCalculatorServices() {
		return creditCalculatorServices;
	}
}
