/*******************************************************************************************************
 *
 * ServerService.java, in gama.network, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extension.network.websocket;

import java.io.IOException;
import java.net.UnknownHostException;

import gama.core.metamodel.agent.IAgent;
import gama.extension.network.common.IConnector;
import gama.extension.network.common.MessageFactory;
import gama.extension.network.common.MessageFactory.MessageType;
import gama.extension.network.tcp.ServerService;
import gama.extension.network.tcp.TCPConnector;

/**
 * The Class ServerService.
 */
public class WebSocketServerService extends ServerService {

	/** The server socket. */
	protected GamaServer gamaServer;

	public WebSocketServerService(IAgent agent, int port, IConnector conn) {
		super(agent, port, conn);
	}

	@Override
	public void startService() throws UnknownHostException, IOException {
		this.gamaServer = new GamaServer(port, this);
		this.isAlive = true;
		this.isOnline = true;
		this.start();
		this.gamaServer.start();
	}

	@Override
	public void run() {
		while (this.isAlive) {
			isOnline = true;

		}
		// DEBUG.OUT("closed ");
		try {
			myAgent.setAttribute(TCPConnector._TCP_SERVER + gamaServer.getPort(), null);
		} catch (final Exception e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(final String msg) throws IOException {
		String message = msg;
		if (gamaServer == null || !isOnline()) return;
		if (!connector.isRaw()) {
			message = message.replace("\n", "@n@");
			message = message.replace("\b\r", "@b@@r@");			
		}
		gamaServer.broadcast(message);
	}

	@Override
	public void receivedMessage(final String sender, final String message) {
		final MessageType mte = MessageFactory.identifyMessageType(message);
		if (mte.equals(MessageType.COMMAND_MESSAGE)) {
			((WebSocketConnector)connector).extractAndApplyCommand(sender, message);
		} else { 
			final String r = ((WebSocketConnector)connector).isRaw() ? message : MessageFactory.unpackReceiverName(message);
			((WebSocketConnector)connector).storeMessage(sender, r, message);
		}
	}
	@Override
	public void stopService() {
		isOnline = false;
		isAlive = false;

		if (sender != null) {
			sender.close();
		}
		if (gamaServer != null) {
			try {
				gamaServer.stop(1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		this.interrupt();
	}

}
