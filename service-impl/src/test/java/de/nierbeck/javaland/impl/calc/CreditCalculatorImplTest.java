package de.nierbeck.javaland.impl.calc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nierbeck.javaland.calculator.CreditCalculator;
import de.nierbeck.javaland.calculator.values.Credit;
import de.nierbeck.javaland.impl.calc.CreditCalculatorImpl;

public class CreditCalculatorImplTest {

	private CreditCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new CreditCalculatorImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRateIncludingFee() throws Exception {
		Credit creditValues = new Credit();
		creditValues.setCredit(10000.0);
		creditValues.setInterest(2.5);
		creditValues.setRetention(2);

		calculator.calculateRate(creditValues);

		Double rate = creditValues.getRate();
		assertThat(rate, is(notNullValue()));
		assertThat(rate, is(5447.69));

	}
}
