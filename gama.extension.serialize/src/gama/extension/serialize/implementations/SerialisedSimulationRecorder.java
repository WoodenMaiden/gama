/*******************************************************************************************************
 *
 * SerialisedSimulationRecorder.java, in gama.extension.serialize, is part of the source code of the GAMA modeling and
 * simulation platform.
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extension.serialize.implementations;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gama.core.common.interfaces.ISerialisationConstants;
import gama.core.kernel.experiment.ISimulationRecorder;
import gama.core.kernel.simulation.SimulationAgent;
import gama.core.metamodel.agent.SerialisedAgent;
import gama.core.util.ByteArrayZipper;
import gama.dev.DEBUG;

/**
 * The Class SerialisedSimulationRecorder. Used to record, store, and retrieve simulation states
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedSimulationRecorder implements ISimulationRecorder, ISerialisationConstants {

	static {
		DEBUG.ON();
	}

	/**
	 * The Class HistoryNode.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 22 oct. 2023
	 */
	private static class HistoryNode {

		/** The bytes. */
		byte[] bytes;

		/** The cycle. */
		long cycle;

		/**
		 * Instantiates a new history node.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param state
		 *            the state
		 * @date 22 oct. 2023
		 */
		public HistoryNode(final byte[] state, final long cycle) {
			bytes = state;
			this.cycle = cycle;
		}

	}

	/** The executor. */
	final ExecutorService executor = Executors.newCachedThreadPool();

	/** The processor. */
	final FSTBinaryProcessor processor = new FSTBinaryProcessor();

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
			long startTime = System.nanoTime();
			byte[] state = processor.saveAgentToBytes(sim.getScope(), sim);
			LinkedList<HistoryNode> history = getSimulationHistory(sim);
			HistoryNode node = new HistoryNode(state, sim.getClock().getCycle());
			history.push(node);
			asyncZip(node, startTime);
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
	private LinkedList<HistoryNode> getSimulationHistory(final SimulationAgent sim) {
		LinkedList<HistoryNode> history = (LinkedList<HistoryNode>) sim.getAttribute(SerialisedAgent.HISTORY_KEY);
		if (history == null) {
			history = new LinkedList<>();
			sim.setAttribute(SerialisedAgent.HISTORY_KEY, history);
		}
		return history;
	}

	/**
	 * Async zip.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param node
	 *            the node
	 * @date 8 août 2023
	 */
	protected void asyncZip(final HistoryNode node, final long startTime) {
		executor.execute(() -> {
			node.bytes = ByteArrayZipper.zip(node.bytes);
			DEBUG.OUT("Serialised and compressed to " + node.bytes.length / 1000000d + "Mb in "
					+ TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");

		});
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
				LinkedList<HistoryNode> history = getSimulationHistory(sim);
				HistoryNode node = history.pop();
				if (node != null && node.cycle == sim.getClock().getCycle()) { node = history.pop(); }
				if (node != null) {
					long startTime = System.nanoTime();
					processor.restoreAgentFromBytes(sim, ByteArrayZipper.unzip(node.bytes));
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
