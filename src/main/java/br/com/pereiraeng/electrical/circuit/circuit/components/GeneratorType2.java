package br.com.pereiraeng.electrical.circuit.circuit.components;

/**
 * Condição imposta pelo gerador (tensão ou corrente)
 * 
 * @author Philipe PEREIRA
 *
 */
public enum GeneratorType2 {
	V, I;

	public String unit() {
		switch (this) {
		case V:
			return "V";
		case I:
			return "A";
		default:
			return null;
		}
	}
}
