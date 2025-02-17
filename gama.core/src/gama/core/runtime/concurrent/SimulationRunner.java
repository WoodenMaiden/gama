/*******************************************************************************************************
 *
 * SimulationRunner.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.runtime.concurrent;

import static gama.core.runtime.concurrent.GamaExecutorService.EXCEPTION_HANDLER;
import static gama.core.runtime.concurrent.GamaExecutorService.THREADS_NUMBER;
import static gama.core.runtime.concurrent.GamaExecutorService.getParallelism;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import gama.core.common.interfaces.IScopedStepable;
import gama.core.kernel.experiment.IExperimentPlan;
import gama.core.kernel.simulation.SimulationPopulation;
import gama.core.runtime.concurrent.GamaExecutorService.Caller;

/**
 * The Class SimulationRunner.
 */
public class SimulationRunner {

	/** The executor. */
	public volatile ExecutorService executor;

	/** The runnables. */
	final Map<IScopedStepable, Callable<Boolean>> runnables;

	/** The concurrency. */
	final int concurrency;

	/** The active threads. */
	volatile int activeThreads;

	/**
	 * Of.
	 *
	 * @param pop
	 *            the pop
	 * @return the simulation runner
	 */
	public static SimulationRunner of(final SimulationPopulation pop) {
		int concurrency = 0;
		final IExperimentPlan plan = pop.getHost().getSpecies();
		if (plan.isHeadless() && !plan.isBatch()) {
			concurrency = 1;
		} else {
			concurrency = getParallelism(pop.getHost().getScope(), plan.getConcurrency(), Caller.SIMULATION);
		}
		return withConcurrency(concurrency);
	}

	/**
	 * With concurrency.
	 *
	 * @param concurrency
	 *            the concurrency
	 * @return the simulation runner
	 */
	public static SimulationRunner withConcurrency(final int concurrency) {
		return new SimulationRunner(concurrency < 0 ? 1 : concurrency);
	}

	/**
	 * Instantiates a new simulation runner.
	 *
	 * @param concurrency
	 *            the concurrency
	 */
	private SimulationRunner(final int concurrency) {
		this.concurrency = concurrency;
		runnables = new LinkedHashMap<>();
	}

	/**
	 * Removes the.
	 *
	 * @param agent
	 *            the agent
	 */
	public void remove(final IScopedStepable agent) {
		runnables.remove(agent);
	}

	/**
	 * Adds the.
	 *
	 * @param agent
	 *            the agent
	 */
	public void add(final IScopedStepable agent) {
		add(agent, () -> {
			activeThreads = computeNumberOfThreads();
			return agent.step();
		});
	}

	/**
	 * Adds the.
	 *
	 * @param agent
	 *            the agent
	 * @param callable
	 *            the callable
	 */
	public void add(final IScopedStepable agent, final Callable<Boolean> callable) {
		runnables.put(agent, callable);
	}

	/**
	 * Step.
	 */
	public void step() {
		try {
			getExecutor().invokeAll(runnables.values());
		} catch (final InterruptedException e) {

		}

	}

	/**
	 * Compute number of threads.
	 *
	 * @return the int
	 */
	private int computeNumberOfThreads() {
		return getExecutor() instanceof ThreadPoolExecutor tpe ? Math.min(concurrency, tpe.getActiveCount()) : 1;
	}

	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	protected ExecutorService getExecutor() {
		return executor == null
				? executor = concurrency == 0 ? newSingleThreadExecutor() : new Executor(THREADS_NUMBER.getValue())
				: executor;
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		runnables.clear();
		if (executor != null) { executor.shutdownNow(); }
	}

	/**
	 * Gets the active stepables.
	 *
	 * @return the active stepables
	 */
	public Set<IScopedStepable> getStepable() { return runnables.keySet(); }

	/**
	 * Gets the active threads.
	 *
	 * @return the active threads
	 */
	public int getActiveThreads() { return activeThreads; }

	/**
	 * Checks for simulations.
	 *
	 * @return true, if successful
	 */
	public boolean hasSimulations() {
		return runnables.size() > 0;
	}

	/**
	 * The Class Executor.
	 */
	static class Executor extends ThreadPoolExecutor {

		/**
		 * Instantiates a new executor.
		 *
		 * @param nb
		 *            the nb
		 */
		Executor(final int nb) {
			super(nb, nb, 0L, MILLISECONDS, new LinkedBlockingQueue<>());
		}

		@Override
		protected void afterExecute(final Runnable r, final Throwable exception) {
			Throwable t = exception;
			super.afterExecute(r, t);
			if (t == null && r instanceof Future<?>) {
				try {
					final Future<?> future = (Future<?>) r;
					if (future.isDone()) { future.get(); }
				} catch (final CancellationException ce) {
					t = ce;
				} catch (final ExecutionException ee) {
					t = ee.getCause();
				} catch (final InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}
			if (t != null) { EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), t); }
		}

	}

}
