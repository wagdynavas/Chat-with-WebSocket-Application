package com.wagdynavas.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat/{username}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatEndpoint {
	private Logger log = Logger.getLogger(getClass().getName());

	private String username;
	private Session session;
	private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
		log.info(session.getId() + " connected!");

		this.username = username;
		this.session = session;
		chatEndpoints.add(this);
		users.put(session.getId(), username);

		Message message = new Message();
		message.setSender(username);
		message.setContent("Connected");

		broadcast(message);
	}

	@OnMessage
	public void onMessage(Session session, Message message) throws IOException, EncodeException {
		log.info(message.toString());

		message.setSender(users.get(session.getId()));
		sendMessageToOneUser(message);
	}
	
	
    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        log.info(session.getId() + " disconnected!");

        chatEndpoints.remove(this);
        Message message = new Message();
        message.setSender(users.get(session.getId()));
        message.setContent("disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.warning(throwable.toString());
    }
	

	public void sendMessageToOneUser(Message message) throws IOException, EncodeException {
		for (ChatEndpoint endpoint : chatEndpoints) {
			synchronized (endpoint) {
				if (endpoint.session.getId().equals(getSessionId(message.getReceiver()))) {
					endpoint.session.getBasicRemote().sendObject(message);
				}
			}
		}
	}

	private static String getSessionId(String receiver) {
		if (users.containsValue(receiver)) {
			for (String sessionID : users.keySet()) {
				if (users.get(sessionID).equals(receiver)) {
					return sessionID;
				}
			}
		}
		return null;
	}

	public void broadcast(Message message) throws IOException, EncodeException {
		for (ChatEndpoint endpoint : chatEndpoints) {
			synchronized (endpoint) {
				endpoint.session.getBasicRemote().sendObject(message);
			}
		}
	}

}
