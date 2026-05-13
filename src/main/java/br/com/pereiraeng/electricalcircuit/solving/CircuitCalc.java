package br.com.pereiraeng.electricalcircuit.solving;

import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.math.Complex;

/**
 * Classe que reúne métodos de cálculo de circuitos elétricos
 * 
 * @author Philipe PEREIRA
 *
 */
public class CircuitCalc {

	// ================ ASSOCIAÇÃO SÉRIE E PARALELO ================

	/**
	 * Função que calcula a impedância equivalente em paralelo de um conjunto de
	 * impedância
	 * 
	 * @param z enumeração de impedâncias
	 * @return equivalente paralelo
	 */
	public static float parallel(float... z) {
		float zeq = z[0];
		for (int i = 1; i < z.length; i++)
			zeq = (zeq * z[i]) / (zeq + z[i]);
		return zeq;
	}

	/**
	 * Função que calcula a impedância equivalente em paralelo de um conjunto de
	 * impedância
	 * 
	 * @param z enumeração de impedâncias
	 * @return equivalente paralelo
	 */
	public static double parallel(double... z) {
		double zeq = z[0];
		for (int i = 1; i < z.length; i++)
			zeq = (zeq * z[i]) / (zeq + z[i]);
		return zeq;
	}

	/**
	 * Função que calcula a impedância equivalente em paralelo de um conjunto de
	 * impedância
	 * 
	 * @param z enumeração de impedâncias
	 * @return equivalente paralelo
	 */
	public static Complex parallel(Complex... z) {
		Complex zeq = z[0];
		for (int i = 1; i < z.length; i++) {
			if (zeq.isNull() || z[i].isNull())
				zeq = new Complex();
			else
				zeq = Complex.div(Complex.mult(zeq, z[i]), Complex.sum(zeq, z[i]));
		}
		return zeq;
	}

	/**
	 * Função que calcula a impedância equivalente em série de um conjunto de
	 * impedâncias
	 * 
	 * @param c enumeração de impedâncias
	 * @return equivalente série
	 */
	public static Complex serie(Complex... c) {
		return Complex.sum(c);
	}

	// ================ VALORES DE BASE ================

	/**
	 * Função que calcula a impedância de base
	 * 
	 * @param vb tensão de base, dada na seguinte unidade:
	 *           <ul>
	 *           <li>opção 1: em V;</i>
	 *           <li>opção 2: em kV.</i>
	 *           </ul>
	 * @param sb potência aparente de base, dada na seguinte unidade:
	 *           <ul>
	 *           <li>opção 1: em VA;</i>
	 *           <li>opção 2: em MVA.</i>
	 *           </ul>
	 * @return impedância de base, dada em Ohms
	 */
	public static double calcZb(double vb, double sb) {
		return Math.pow(vb, 2) / sb;
	}

	/**
	 * Função que calcula a corrente de base para sistemas trifásicos
	 * 
	 * @param vb tensão de base, em V
	 * @param sb potência aparente de base, em VA
	 * @return corrente de base, em A
	 */
	public static double calcIb(double vb, double sb) {
		return sb / vb / ExtendedMath.SQRT3;
	}

	// ================ CÁLCULO FASORIAL ================

	/**
	 * Função que calcula a potência aparente complexa
	 * 
	 * @param v fasor de tensão
	 * @param i fasor de corrente
	 * @return potência aparente complexa
	 */
	public static Complex getS(Complex v, Complex i) {
		return Complex.mult(v, Complex.conj(i));
	}

	/**
	 * 
	 * @param i
	 *          <ol start="0">
	 *          <li>IVM</i>
	 *          <li>IAZ</i>
	 *          <li>IBR</i>
	 *          </ol>
	 * @return corrente de neutro complexa (i.e., soma complexa das correntes)
	 */
	public static Complex getN(Complex[] i) {
		return Complex.sum(i[0], i[1], i[2]);
	}

	/**
	 * Função que retorna a potência aparente complexa a partir de uma trinca de
	 * pares de fasores de corrente e tensão
	 * 
	 * @param iv
	 *           <ol start="0">
	 *           <li>
	 *           <ol start="0">
	 *           <li>IVM</i>
	 *           <li>IAZ</i>
	 *           <li>IBR</i>
	 *           </ol>
	 *           </i>
	 *           <li>
	 *           <ol start="0">
	 *           <li>VVM</i>
	 *           <li>VAZ</i>
	 *           <li>VBR</i>
	 *           </ol>
	 *           </i>
	 *           </ol>
	 * @return potência aparente complexa
	 */
	public static Complex getS(Complex[][] iv) {
		Complex out = new Complex();
		for (int p = 0; p < 3; p++)
			out.sum(getS(iv[1][p], iv[0][p]));
		return out;
	}

	/**
	 * 
	 * @param iv
	 *           <ol start="0">
	 *           <li>
	 *           <ol start="0">
	 *           <li>IVM</i>
	 *           <li>IAZ</i>
	 *           <li>IBR</i>
	 *           </ol>
	 *           </i>
	 *           <li>
	 *           <ol start="0">
	 *           <li>VVM</i>
	 *           <li>VAZ</i>
	 *           <li>VBR</i>
	 *           </ol>
	 *           </i>
	 *           </ol>
	 * @return potência reativa
	 */
	public static double getQ(Complex[][] iv) {
		double out = 0.;
		for (int p = 0; p < 3; p++)
			out += iv[1][p].getMod() * iv[0][p].getMod() * Math.sin(iv[1][p].getArg() - iv[0][p].getArg());
		return out;
	}

	/**
	 * 
	 * @param iv
	 *           <ol start="0">
	 *           <li>
	 *           <ol start="0">
	 *           <li>IVM</i>
	 *           <li>IAZ</i>
	 *           <li>IBR</i>
	 *           </ol>
	 *           </i>
	 *           <li>
	 *           <ol start="0">
	 *           <li>VVM</i>
	 *           <li>VAZ</i>
	 *           <li>VBR</i>
	 *           </ol>
	 *           </i>
	 *           </ol>
	 * @return potência ativa
	 */
	public static double getP(Complex[][] iv) {
		double out = 0.;
		for (int p = 0; p < 3; p++)
			out += iv[1][p].getMod() * iv[0][p].getMod() * Math.cos(iv[1][p].getArg() - iv[0][p].getArg());
		return out;
	}

	// ================ IMPEDÂNCIAS E ADMITÂNCIAS ================

	/**
	 * Função que calcula a impedância complexa de uma resistência para uma dada
	 * frequência angular
	 * 
	 * @param w frequência angular, em radianos por segundo
	 * @param r resistência, em Ohms
	 * @return valor da impedância complexa, em Ohms
	 */
	public static Complex getZr(double w, double r) {
		return new Complex(r, 0.);
	}

	/**
	 * Função que calcula a impedância complexa de uma indutância para uma dada
	 * frequência angular
	 * 
	 * @param w frequência angular, em radianos por segundo
	 * @param l indutância, em Henries
	 * @return valor da impedância complexa, em Ohms
	 */
	public static Complex getZl(double w, double l) {
		return new Complex(0., w * l);
	}

	/**
	 * Função que calcula a impedância complexa de uma capacitância para uma dada
	 * frequência angular
	 * 
	 * @param w frequência angular, em radianos por segundo
	 * @param c capacitância, em Faradays
	 * @return valor da impedância complexa, em Ohms
	 */
	public static Complex getZc(double w, double c) {
		if (c == 0.)
			return new Complex(0, Double.NEGATIVE_INFINITY);
		else
			return new Complex(0., -1. / (w * c));
	}

	/**
	 * Função que calcula a admitância complexa de uma resistência para uma dada
	 * frequência angular
	 * 
	 * @param w frequência angular, em radianos por segundo
	 * @param r resistência, em Ohms
	 * @return valor da admitância complexa, em Mhos
	 */
	public static Complex getYr(double w, double r) {
		return new Complex(1 / r, 0.);
	}

	/**
	 * Função que calcula a admitância complexa de uma indutância para uma dada
	 * frequência angular
	 * 
	 * @param w frequência angular, em radianos por segundo
	 * @param l indutância, em Henries
	 * @return valor da admitância complexa, em Mhos
	 */
	public static Complex getYl(double w, double l) {
		return new Complex(0., -1. / (w * l));
	}

	/**
	 * Função que calcula a admitância complexa de uma capacitância para uma dada
	 * frequência angular
	 * 
	 * @param w frequência angular, em radianos por segundo
	 * @param c capacitância, em Faradays
	 * @return valor da admitância complexa, em Mhos
	 */
	public static Complex getYc(double w, double c) {
		return new Complex(0., w * c);
	}

	// ================ COEFICIENTE DE REFLEXÃO E TRANSMISSÃO ================

	/**
	 * Função que calcula o coeficiente de reflexão entre duas linhas de impedâncias
	 * dadas
	 * 
	 * @param z0 impedância da linha do lado incidente
	 * @param z1 impedância da linha do lado transmitido
	 * @return coeficiente de reflexão
	 */
	public static double reflection(double z0, double z1) {
		return (z1 - z0) / (z1 + z0);
	}

	/**
	 * Função que calcula o coeficiente de transmissão entre duas linhas de
	 * impedâncias dadas
	 * 
	 * @param z0 impedância da linha do lado incidente
	 * @param z1 impedância da linha do lado transmitido
	 * @return coeficiente de transmissão
	 */
	public static double transmission(double z0, double z1) {
		return 2 * z1 / (z0 + z1);
	}

	/**
	 * função que calcula o coeficiente de reflexão na carga
	 * 
	 * @param r resistência normalizada da carga
	 * @param x reatância normalizada da carga
	 * @return coeficiente de reflexão na carga, em coordenadas cartesianas
	 */
	public static double[] getReflection(double r, double x) {
		double n = Math.pow(r + 1, 2) + Math.pow(x, 2);
		return new double[] { (Math.pow(r, 2) + Math.pow(x, 2) - 1) / n, 2 * x / n };
	}

	/**
	 * função que calcula o coeficiente de reflexão generalizado em um dado ponto
	 * 
	 * @param md módulo do coeficiente de reflexão na carga
	 * @param ph fase do coeficiente de reflexão na carga
	 * @param x  distância da carga, expresso em unidades do comprimento de onda
	 * @return coeficiente de reflexão generalizado, em coordenadas cartesianas
	 */
	public static double[] getRfx(double md, double ph, double x) {
		double angle = Math.toRadians(ph + Math.toDegrees(-4 * Math.PI * x));
		return new double[] { md * Math.cos(angle), md * Math.sin(angle) };
	}

	/**
	 * função que calcula a impedância equivalente da linha vista de um dado ponto
	 * 
	 * @param rfx coeficiente de reflexão num dado ponto, expresso em coordenadas
	 *            cartesianas
	 * @return impedância vista do ponto, em coordenadas cartesianas
	 */
	public static double[] getZx(double[] rfx) {
		double d = Math.pow(1 - rfx[0], 2) + Math.pow(rfx[1], 2);

		return new double[] { (1 - rfx[0] * rfx[0] - rfx[1] * rfx[1]) / d, 2 * rfx[1] / d };
	}

}
