/*******************************************************************************************************
 *
 * GrayscaleMeshColorProvider.java, in gama.core, is part of the source code of the GAMA modeling and simulation
 * platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.gaml.statements.draw;

/**
 * The Class GrayscaleMeshColorProvider.
 */
public class GrayscaleMeshColorProvider implements IMeshColorProvider {

	@Override
	public double[] getColor(final int index, final double z, final double min, final double max, final double[] rgb) {
		double[] result = rgb;
		if (result == null) { result = newArray(); }
		result[0] = result[1] = result[2] = (z - min) / (max - min);
		result[3] = 1;
		return result;
	}

}
