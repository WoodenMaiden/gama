/*******************************************************************************************************
 *
 * JsonReferenceObject.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.util.file.json;

import gama.core.metamodel.agent.AgentReference;
import gama.core.metamodel.agent.IAgent;
import gama.core.runtime.IScope;

/**
 * The Class JsonReferenceObject.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 5 nov. 2023
 */
public class JsonReferenceObject extends JsonAbstractObject {

	/** The ref. */
	final String ref;

	/**
	 * Instantiates a new json reference object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 5 nov. 2023
	 */
	public JsonReferenceObject(final String ref, final Json json) {
		super(json);
		this.ref = ref;
	}

	@Override
	public IAgent toGamlValue(final IScope scope) {
		return AgentReference.of(ref).getReferencedAgent(scope);
	}

}
