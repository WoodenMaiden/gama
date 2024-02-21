/*******************************************************************************************************
 *
 * JsonInt.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.util.file.json;

import java.io.IOException;

import gama.core.runtime.IScope;

/**
 * The Class JsonNumber.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
@SuppressWarnings ("serial") // use default serial UID
class JsonInt extends JsonValue {

	/** The string. */
	private final String string;

	/**
	 * Instantiates a new json number.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @date 29 oct. 2023
	 */
	JsonInt(final String string) {
		if (string == null) throw new NullPointerException("string is null");
		this.string = string;
	}

	/**
	 * To string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the string
	 * @date 29 oct. 2023
	 */
	@Override
	public String toString() {
		return string;
	}

	/**
	 * Write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 oct. 2023
	 */
	@Override
	void write(final JsonWriter writer) throws IOException {
		writer.writeNumber(string);
	}

	/**
	 * Checks if is number.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is number
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean isNumber() { return true; }

	/**
	 * As int.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 29 oct. 2023
	 */
	@Override
	public int asInt() {
		return Integer.parseInt(string, 10);
	}

	/**
	 * As long.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the long
	 * @date 29 oct. 2023
	 */
	@Override
	public long asLong() {
		return asInt();
	}

	/**
	 * As float.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the float
	 * @date 29 oct. 2023
	 */
	@Override
	public float asFloat() {
		return asInt();
	}

	/**
	 * As double.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the double
	 * @date 29 oct. 2023
	 */
	@Override
	public double asDouble() {
		return asInt();
	}

	/**
	 * Hash code.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 29 oct. 2023
	 */
	@Override
	public int hashCode() {
		return string.hashCode();
	}

	/**
	 * Equals.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return true, if successful
	 * @date 29 oct. 2023
	 */
	@Override
	public boolean equals(final Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		JsonInt other = (JsonInt) object;
		return string.equals(other.string);
	}

	/**
	 * To gaml value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the object
	 * @date 2 nov. 2023
	 */
	@Override
	public Number toGamlValue(final IScope scope) {
		return asInt();
	}

}
