/*******************************************************************************************************
 *
 * IDocManager.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.common.interfaces;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import gama.core.util.ByteArrayZipper;
import gama.gaml.descriptions.ModelDescription;
import gama.gaml.interfaces.IGamlDescription;

/**
 * The Interface IDocManager.
 */
// Internal interface instantiated by XText
public interface IDocManager {

	/** The null. */
	IDocManager NULL = new NullImpl();

	/**
	 * The Class DocumentationNode.
	 */
	record DocumentationNode(String title, byte[] doc) implements IGamlDescription {

		/**
		 * Instantiates a new documentation node.
		 *
		 * @param desc
		 *            the desc
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public DocumentationNode(final IGamlDescription desc) {
			this(desc.getTitle(), ByteArrayZipper.zip(desc.getDocumentation().toString().getBytes()));
		}

		/**
		 * Gets the documentation.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the documentation
		 * @date 30 déc. 2023
		 */
		@Override
		public Doc getDocumentation() {
			return new ConstantDoc(new String(ByteArrayZipper.unzip(doc)));

		}

		/**
		 * Gets the title.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return the title
		 * @date 30 déc. 2023
		 */
		@Override
		public String getTitle() { return title; }

	}

	/**
	 * The Class NullImpl.
	 */
	public static class NullImpl implements IDocManager {

		/**
		 * Do document.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param description
		 *            the description
		 * @date 31 déc. 2023
		 */
		@Override
		public void doDocument(final URI uri, final ModelDescription description,
				final Map<EObject, IGamlDescription> additionalExpressions) {}

		@Override
		public IGamlDescription getGamlDocumentation(final EObject o) {
			return null;
		}

		/**
		 * Sets the gaml documentation.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param object
		 *            the object
		 * @param description
		 *            the description
		 * @param replace
		 *            the replace
		 * @param force
		 *            the force
		 * @date 29 déc. 2023
		 */
		@Override
		public void setGamlDocumentation(final URI openResource, final EObject object,
				final IGamlDescription description) {}

		@Override
		public void invalidate(final URI key) {}

	}

	/**
	 * Document. Should be called after validation. Validates both the statements (from ModelDescription) and the
	 * expressions (Map)
	 *
	 * @param description
	 *            the description
	 * @param additionalExpressions
	 */
	void doDocument(URI resource, ModelDescription description, Map<EObject, IGamlDescription> additionalExpressions);

	/**
	 * Gets the gaml documentation.
	 *
	 * @param o
	 *            the o
	 * @return the gaml documentation
	 */
	IGamlDescription getGamlDocumentation(EObject o);

	/**
	 * Sets the gaml documentation.
	 *
	 * @param object
	 *            the object
	 * @param description
	 *            the description
	 * @param replace
	 *            the replace
	 * @param force
	 *            the force
	 */
	void setGamlDocumentation(URI openResource, final EObject object, final IGamlDescription description);

	/**
	 * Invalidate.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param key
	 *            the key
	 * @date 29 déc. 2023
	 */
	void invalidate(URI key);

}