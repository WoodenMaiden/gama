/*******************************************************************************************************
 *
 * StopCommand.java, in gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.headless.listener;

import org.java_websocket.WebSocket;

import gama.core.kernel.experiment.IExperimentPlan;
import gama.core.runtime.server.CommandResponse;
import gama.core.runtime.server.GamaServerMessage;
import gama.core.runtime.server.GamaWebSocketServer;
import gama.core.runtime.server.ISocketCommand;
import gama.core.util.IMap;

/**
 * The Class StopCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class StopCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		plan.getController().processPause(true);
		plan.getController().dispose();
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}
}
