/*******************************************************************************************************
 *
 * ITopLevelAgent.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.kernel.experiment;

import gama.core.common.interfaces.IScopedStepable;
import gama.core.common.util.RandomUtils;
import gama.core.kernel.simulation.SimulationClock;
import gama.core.metamodel.agent.IMacroAgent;
import gama.core.outputs.IOutputManager;
import gama.core.util.GamaColor;
import gama.gaml.statements.IExecutable;

/**
 * Class ITopLevelAgent Addition (Aug 2021): explicit inheritance of IScopedStepable
 *
 * @author drogoul
 * @since 27 janv. 2016
 *
 */
public interface ITopLevelAgent extends IMacroAgent, IScopedStepable {

	/**
	 * Gets the clock.
	 *
	 * @return the clock
	 */
	SimulationClock getClock();

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	GamaColor getColor();

	/**
	 * Gets the random generator.
	 *
	 * @return the random generator
	 */
	RandomUtils getRandomGenerator();

	/**
	 * Gets the output manager.
	 *
	 * @return the output manager
	 */
	IOutputManager getOutputManager();

	/**
	 * Post end action.
	 *
	 * @param executable
	 *            the executable
	 */
	void postEndAction(IExecutable executable);

	/**
	 * Post dispose action.
	 *
	 * @param executable
	 *            the executable
	 */
	void postDisposeAction(IExecutable executable);

	/**
	 * Post one shot action.
	 *
	 * @param executable
	 *            the executable
	 */
	void postOneShotAction(IExecutable executable);

	/**
	 * Execute action.
	 *
	 * @param executable
	 *            the executable
	 */
	void executeAction(IExecutable executable);

	/**
	 * Checks if is on user hold.
	 *
	 * @return true, if is on user hold
	 */
	boolean isOnUserHold();

	/**
	 * Sets the on user hold.
	 *
	 * @param state
	 *            the new on user hold
	 */
	void setOnUserHold(boolean state);

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	IExperimentAgent getExperiment();

	/**
	 * Gets the family name. Means either 'simulation', 'experiment' or 'platform'
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the family name
	 * @date 13 août 2023
	 */
	String getFamilyName();

	/**
	 * Checks if is platform.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is platform
	 * @date 3 sept. 2023
	 */
	default boolean isPlatform() { return false; }

	/**
	 * Checks if is experiment.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is experiment
	 * @date 3 sept. 2023
	 */
	default boolean isExperiment() { return false; }

	/**
	 * Checks if is simulation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is simulation
	 * @date 3 sept. 2023
	 */
	default boolean isSimulation() { return false; }

}
