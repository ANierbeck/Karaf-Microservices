package de.nierbeck.microservices.karaf.calculator.values;

import javax.xml.bind.annotation.XmlRootElement;

import de.nierbeck.microservices.karaf.calculator.CreditCalculator;

/**
 * The credit object containing all values. Those values are optional and it's decided on the method in the {@link CreditCalculator} which value needs to be calculated. 
 */
@XmlRootElement
public class Credit {

	private Double credit;
	
	private Double interest;
	
	private Double rate;
	
	private Integer retention;
	
	private Double residualDebt;
	
	private Double fee;
	
//	private Long feePercentage;

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Integer getRetention() {
		return retention;
	}

	public void setRetention(Integer retention) {
		this.retention = retention;
	}

	public Double getResidualDebt() {
		return residualDebt;
	}

	public void setResidualDebt(Double residualDebt) {
		this.residualDebt = residualDebt;
	}
	
	public void setFee(Double fee) {
		this.fee = fee;
	}
	
	public Double getFee() {
		return fee;
	}

//	public Long getFeePercentage() {
//		return feePercentage;
//	}
//
//	public void setFeePercentage(Long feePercentage) {
//		this.feePercentage = feePercentage;
//	}
}
