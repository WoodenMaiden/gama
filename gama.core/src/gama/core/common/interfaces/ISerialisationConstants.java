/*******************************************************************************************************
 *
 * ISerialisationConstants.java, in gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.common.interfaces;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * The Interface SerialisedAgentConstants.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 21 août 2023
 */
public interface ISerialisationConstants {

	/** The serialisation string. */
	String SERIALISATION_STRING = "serialisation_string";

	/** The Constant CLASS_PREFIX. */
	String CLASS_PREFIX = "";

	/** The json format. */
	String JSON_FORMAT = "json";

	/** The binary format. */
	String BINARY_FORMAT = "binary";

	/** The agent format. */
	String AGENT_FILE = IKeyword.AGENT;

	/** The simulation formation. */
	String SIMULATION_FILE = IKeyword.SIMULATION;

	/** The file formats. */
	Set<String> FILE_FORMATS = Set.of(JSON_FORMAT, BINARY_FORMAT);

	/** The file types. */
	Set<String> FILE_TYPES = Set.of(AGENT_FILE, SIMULATION_FILE);

	/** The Constant NULL. */
	byte[] NULL = {};

	/** The Constant COMPRESSED. */
	byte COMPRESSED = 1;

	/** The Constant UNCOMPRESSED. */
	byte UNCOMPRESSED = 0;

	/** The Constant GAMA_IDENTIFIER. */
	byte GAMA_AGENT_IDENTIFIER = 42;

	/** The gama object identifier. */
	byte GAMA_OBJECT_IDENTIFIER = 43;

	/** The Constant STRING_BYTE_ARRAY_CHARSET. The Charset to use to save byte arrays in strings and reversely */
	Charset STRING_BYTE_ARRAY_CHARSET = StandardCharsets.ISO_8859_1;

}
