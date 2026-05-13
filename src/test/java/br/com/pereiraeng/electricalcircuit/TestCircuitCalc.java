package br.com.pereiraeng.electricalcircuit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.electricalcircuit.solving.CircuitCalc;
import br.com.pereiraeng.math.Complex;

public class TestCircuitCalc {

	@Test
	public void parallelAssociationTest() {
		float result = CircuitCalc.parallel(5f, 3f, 2f);

		assertEquals(0.967741935483871f, result);

		double result2 = CircuitCalc.parallel(Math.E, ExtendedMath.LN_2, Math.PI);

		assertEquals(0.4697295987121897, result2);

		Complex result3 = CircuitCalc.parallel(new Complex(1.5, 7.9), new Complex(-.5, -.45), new Complex(3.7, .7));
		assertEquals(new Complex(-0.6075814382413847, -0.6091277519175713), result3);
	}
}
