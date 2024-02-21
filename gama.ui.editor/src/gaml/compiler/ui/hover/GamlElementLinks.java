/*******************************************************************************************************
 *
 * GamlElementLinks.java, in gama.ui.shared.modeling, is part of the source code of the GAMA modeling and simulation
 * platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.compiler.ui.hover;

import java.net.URI;

import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.xtext.ui.editor.hover.html.XtextElementLinks;

/**
 * The class GamlElementLinks.
 *
 * @author drogoul
 * @since 29 août 2023
 *
 */
public class GamlElementLinks extends XtextElementLinks {

	@Override
	public LocationListener createLocationListener(final ILinkHandler handler) {
		return new XtextLinkAdapter(handler) {

			@Override
			protected URI initURI(final LocationEvent event) {
				String loc = event.location;
				if (loc != null && loc.startsWith("data:")) {
					handler.handleTextSet();
					return null;
				}
				return super.initURI(event);
			}

		};
	}

}
