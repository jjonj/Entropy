package jjj.entropy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


import jjj.entropy.messages.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class NetworkManager extends Listener 
{
	
	public enum NetworkState {
		OFFLINE, CONNECTED, LOGGING_IN, LOGGED_IN, IN_GAME
	}
	
	private static List<CardTemplate> cardsToLoad = new ArrayList<CardTemplate>();
	
	private NetworkManager(){}	//Shouldn't be initialized as it's a purely static class
	
	private static Client client;
	
	private static NetworkState networkState = NetworkState.OFFLINE;
	
	public static void Connect(String IP, int port)
	{
		if (networkState == NetworkState.OFFLINE)
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
				
			  networkState = NetworkState.CONNECTED;
		}
	}
	

	public static void Disconnect() {
		client.close();
		networkState = NetworkState.OFFLINE;
	}
	
	public static void Login(String username, String password)
	{
		if (networkState == NetworkState.CONNECTED)
		{
			LoginMessage logMsg = new LoginMessage();
			logMsg.username = username;
			logMsg.password = password;
			client.sendTCP(logMsg);
			networkState = NetworkState.LOGGING_IN;
		}
	}
	
	public static void JoinGame()
	{
		GameMessage joinRequest = new GameMessage();
		joinRequest.playerID = 3;
		client.sendTCP(joinRequest);
	}
	public static void SendTextMessage(String text)
	{
		ChatMessage request = new ChatMessage();
		client.sendTCP(request);
	}
	
	public static void QueueCardTemplateLoad(CardTemplate template)
	{
		cardsToLoad.add(template);
	}
	
	public static NetworkState GetNetworkState()
	{
		return networkState;
	}

	
	@Override
	public void connected (Connection connection) {
		
		System.out.println(connection.getRemoteAddressTCP().toString() + " CONNECTING!");
		
	}
	
	@Override
	public void disconnected (Connection connection) {
		System.out.println("SOMEONE DISCONNECTING!");
		
	}
	
	@Override
	public void received (Connection connection, Object object) {
		if(object instanceof LoginMessage) {	//Assert: Only recieved when login was denied
			if (networkState == NetworkState.LOGGING_IN && ((LoginMessage)object).rejected)
			{
				// MISSING: Notify user
				System.out.println(((LoginMessage)object).message);
				networkState = NetworkState.OFFLINE;
			}
			
		}
		else if (object instanceof PlayerDataMessage)
		{
			PlayerDataMessage pdm = (PlayerDataMessage)object;
			if (pdm.loginAccepted)
			{
				Game.GetInstance().SetPlayer(1, new Player(pdm.playerID, pdm.name, pdm.deck));
				networkState = NetworkState.LOGGED_IN;
			}
		}
		else if (object instanceof CardDataMessage)
		{
			for (String s : ((CardDataMessage) object).cardTemplates)
			{
				CardTemplate.LoadCardTemplate(s);
			}
		}
	}
	
}
