package br.com.pereiraeng.electricalcircuit.components;

/**
 * Forma de onda na saída:
 * 
 * <ol>
 * <li>{@link GeneratorType1#GENERAL f(t);}</i>
 * <li>{@link GeneratorType1#DC f(t)=K;}</i>
 * <li>{@link GeneratorType1#AC f(t)=K cos(wt+&phi;);}</i>
 * <li>{@link GeneratorType1#FOURIER f(t)=&Sigma;<sub>i</sub><sup>N</sup>
 * K<sub>i</sub> cos(w i t+&phi;<sub>i</sub>)};</i>
 * <li>{@link GeneratorType1#LAPLACE f(t)=K(t) &delta;(t-e^ja)};</i>
 * </ol>
 * 
 * @author Philipe PEREIRA
 *
 */
public enum GeneratorType1 {
	GENERAL, DC, AC, FOURIER, LAPLACE;
}
