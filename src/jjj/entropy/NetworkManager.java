package jjj.entropy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


import jjj.entropy.classes.Enums.GameState;
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
	
	private static NetworkManager instance = new NetworkManager();
	
	
	public static NetworkManager GetInstance()
	{
		return instance;
	}
	
	protected NetworkManager(){}
	
	private List<CardTemplate> cardsToLoad = new ArrayList<CardTemplate>();
	
	
	private Client client;
	
	private NetworkState networkState = NetworkState.OFFLINE;
	
	public void Connect(String IP, int port)
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
			  kryo.register(ActionMessage.class);
			  kryo.register(CardDataMessage.class);
			  kryo.register(LoginMessage.class);
			  kryo.register(PlayerDataMessage.class);
			  kryo.register(String[].class);
			  kryo.register(int[].class);
			  
				
			  client.addListener(this);
				
			  
			  
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
	

	public void Disconnect() {
		client.close();
		networkState = NetworkState.OFFLINE;
	}
	
	public void Login(String username, String password)
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
	
	public void JoinGame()
	{
		GameMessage joinRequest = new GameMessage();
		joinRequest.playerID = 3;
		client.sendTCP(joinRequest);
	}
	public void SendTextMessage(String text)
	{
		ChatMessage request = new ChatMessage();
		client.sendTCP(request);
	}
	
	public void QueueCardTemplateLoad(CardTemplate template)
	{
		cardsToLoad.add(template);
	}
	
	public NetworkState GetNetworkState()
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
		System.out.println("Message recieved!");
		if(object instanceof LoginMessage) {	//Assert: Only recieved when login was denied
			if (networkState == NetworkState.LOGGING_IN && ((LoginMessage)object).rejected)
			{
				// MISSING: Notify user
				networkState = NetworkState.CONNECTED;
			}
			
		}
		else if (object instanceof PlayerDataMessage)	//When logging in, a cardDataMessage should always be recieved first to load the players deck.
		{
			PlayerDataMessage pdm = (PlayerDataMessage)object;
			if (pdm.loginAccepted)
			{
				Game.GetInstance().SetPlayer(1, new Player(pdm.playerID, pdm.name, pdm.deck));
				networkState = NetworkState.LOGGED_IN;
				Game.GetInstance().SetGameState(GameState.MAIN_MENU);
			}
		}
		else if (object instanceof CardDataMessage)
		{
			System.out.println("CardMessage recieved!!! :)");
			for (String s : ((CardDataMessage) object).cardTemplates) //Load each cardtype/template 
			{
				CardTemplate.LoadCardTemplate(s);
			}
		}
	}
	
}
