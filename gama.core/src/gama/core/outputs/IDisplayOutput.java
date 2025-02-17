/*******************************************************************************************************
 *
 * IDisplayOutput.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.outputs;

import gama.core.common.interfaces.IGamaView;

/**
 * Display outputs extend regular outputs to represent and serve as the logical model of views displayed on the user
 * interface.
 *
 * @author Alexis Drogoul, IRD
 * @revised in Dec. 2015 to include documentation and comments
 */
public interface IDisplayOutput extends IOutput {

	/**
	 * Retuns whether this output is synchronized with the simulation or not. Synchronized outputs wait for simulation
	 * cycles to finish before updating themselves -- but also make the simulation wait for them to finish updating.
	 *
	 * @return true if this output is synchronized, false otherwise
	 */
	// boolean isSynchronized();

	/**
	 * The output should synchronize its operations with the simulation thread if the parameter passed is true, and is
	 * free to perform its update when it can if it is false. Passing true (resp. false) if the output is already
	 * synchronized (resp. desynchronized) should not change its behavior.
	 *
	 * @param sync
	 *            true if the output should synchronize, false otherwise
	 */
	// void setSynchronized(final boolean sync);

	/**
	 * If only one output of this kind is allowed in the UI (i.e. there can only be one instance of the corresponding
	 * view), the output should return true
	 *
	 * @return true if only one view for this kind of output is possible, false otherwise
	 */
	boolean isUnique();

	/**
	 * Returns the identifier of the view to be opened in the UI. If this view should be unique, then this identifier
	 * will be used to retrieve it (or create it if it is not yet instantiated). Otherwise, the identifier and the name
	 * of the output are used in combination to create a new view.
	 *
	 * @return the identifier of the view that will be used as the concrete support for this output
	 */
	String getViewId();

	/**
	 * Returns whether the output is its phase of initialization or not. Might be used to turn off certain secondary
	 * aspects (like the synchronization)
	 *
	 * @return
	 */
	// boolean isInInitPhase();

	/**
	 * Sets whether the output is initializing or not.
	 *
	 * @param state
	 */
	// void setInInitPhase(boolean state);

	/**
	 * Returns whether the output has been described as 'virtual', i.e. not showable on screen and only used for display
	 * inheritance.
	 *
	 * @return
	 */
	boolean isVirtual();

	/**
	 * Checks if is auto save.
	 *
	 * @return true, if is auto save
	 */
	default boolean isAutoSave() { return false; }

	/**
	 * Returns the GamaView associated with this output
	 */

	IGamaView getView();

	/**
	 * Checks if is rendered.
	 *
	 * @return true, if is rendered
	 */
	boolean isRendered();

	/**
	 * Sets the rendered.
	 *
	 * @param b the new rendered
	 */
	void setRendered(boolean b);

}
