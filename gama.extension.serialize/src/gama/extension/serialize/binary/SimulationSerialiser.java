/*******************************************************************************************************
 *
 * SimulationSerialiser.java, in gama.extension.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extension.serialize.binary;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import gama.core.common.interfaces.ISerialisationConstants;
import gama.core.kernel.experiment.ISimulationRecorder;
import gama.core.kernel.simulation.SimulationAgent;
import gama.core.metamodel.agent.SerialisedAgent;
import gama.dev.DEBUG;
import gama.extension.serialize.binary.SimulationHistory.SimulationHistoryNode;

/**
 * The Class SimulationSerialiser. Used to record, store, and retrieve simulation states
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SimulationSerialiser implements ISimulationRecorder, ISerialisationConstants {

	static {
		DEBUG.ON();
	}

	/** The processor. */
	final BinarySerialiser processor = new BinarySerialiser();

	/**
	 * Record.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	@Override
	public void record(final SimulationAgent sim) {
		try {
			// long startTime = System.nanoTime();
			byte[] state = processor.saveAgentToBytes(sim.getScope(), sim);
			SimulationHistory history = getSimulationHistory(sim);
			SimulationHistoryNode node = new SimulationHistoryNode(state, sim.getClock().getCycle());
			history.push(node);
			// asyncZip(node, startTime);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the simulation history.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return the simulation history
	 * @date 22 oct. 2023
	 */
	@SuppressWarnings ("unchecked")
	private SimulationHistory getSimulationHistory(final SimulationAgent sim) {
		SimulationHistory history = (SimulationHistory) sim.getAttribute(SerialisedAgent.HISTORY_KEY);
		if (history == null) {
			history = new SimulationHistory();
			sim.setAttribute(SerialisedAgent.HISTORY_KEY, history);
		}
		return history;
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	@Override
	public void restore(final SimulationAgent sim) {
		try {
			synchronized (sim) {
				LinkedList<SimulationHistoryNode> history = getSimulationHistory(sim);
				SimulationHistoryNode node = history.pop();
				if (node != null && node.cycle == sim.getClock().getCycle()) { node = history.pop(); }
				if (node != null) {
					long startTime = System.nanoTime();
					processor.restoreAgentFromBytes(sim, node.bytes);

					DEBUG.OUT("Deserialised in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Can step back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 * @return true, if successful
	 * @date 9 août 2023
	 */
	@Override
	public boolean canStepBack(final SimulationAgent sim) {
		return getSimulationHistory(sim).size() > 0;
	}

}
