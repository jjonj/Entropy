package jjj.entropy;

import java.io.IOException;
import java.net.SocketException;


import jjj.entropy.messages.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class NetworkManager {

	private static Client client;
	
	public static void Connect(String IP, int port)
	{
		client = new Client();
		
		
		client.addListener(new Listener() {
			   public void received (Connection connection, Object object) {
			      if (object instanceof ChatMessage) {
			    	  ChatMessage response = (ChatMessage)object;
			         System.out.println(response.message);
			      }
			   }
			});

		  
		  Kryo kryo = client.getKryo();
		  kryo.register(ChatMessage.class);
		  kryo.register(GameMessage.class);
		//  kryo.register(AuthenticationMessage.class);
		  kryo.register(ActionMessage.class);
		  
		  client.start();
			try {
				client.connect(5000, IP, port);
			} catch (IOException e) {
				System.out.println("FAILED TO CONNECT TO SERVER: " + IP + " ON PORT "+port );
				e.printStackTrace();
				System.exit(1);
			}
	}
	
	public static void JoinGame()
	{
		GameMessage joinRequest = new GameMessage();
	//	ChatMessage joinRequest = new ChatMessage();
	//	joinRequest.message = "HELLO BITCHES!!";
		joinRequest.playerID = 3;
		client.sendTCP(joinRequest);
	}
	
	public static void SendTextMessage(String text)
	{
		ChatMessage request = new ChatMessage();
		client.sendTCP(request);
	}


	public static void Disconnect() {
		client.close();
	}
	
	
}
