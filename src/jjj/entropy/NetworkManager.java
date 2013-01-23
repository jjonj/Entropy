package jjj.entropy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


import jjj.entropy.classes.EntUtilities;
import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.classes.Enums.Life;
import jjj.entropy.classes.Enums.Zone;
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
	
	private boolean searchingGame = false;
	
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
	
	}
	

	public void Disconnect() {

	}
	
	public void Login(String username, String password)
	{
		Game.GetInstance().SetGameState(GameState.MAIN_MENU);
	}
	
	public void JoinGame()
	{

	}
	
	public void SendAction(int cardID, int mode, int modeNumber) {

	}
	
	public void SendTextMessage(String text)
	{

	}
	
	public void QueueCardTemplateLoad(CardTemplate template)
	{

	}
	
	public NetworkState GetNetworkState()
	{
		return networkState;
	}

	
	@Override
	public void connected (Connection connection) {

	}
	
	@Override
	public void disconnected (Connection connection) {
		
	}
	
	@Override
	public void received (Connection connection, Object object) {
	
	}


	
}
