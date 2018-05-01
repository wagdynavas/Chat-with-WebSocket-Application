package com.wagdynavas.websocket;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message decode(String s) throws DecodeException {
		
		Gson gson = new Gson();
		Message message = gson.fromJson(s, Message.class);
		return message;
	}

	@Override
	public boolean willDecode(String s) {
		// TODO Auto-generated method stub
		return (s != null);
	}

}
