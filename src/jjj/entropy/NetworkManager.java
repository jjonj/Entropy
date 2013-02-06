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
import jjj.entropy.ui.EntDropdown;
import jjj.entropy.ui.IEntTableRow;

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
		if (networkState == NetworkState.OFFLINE)
		{
			 networkState = NetworkState.CONNECTED;
		}
	}
	

	public void Disconnect() {

		networkState = NetworkState.OFFLINE;
	}
	
	public void Login(String username, String password)
	{
		if (networkState == NetworkState.CONNECTED)
		{

			networkState = NetworkState.LOGGING_IN;
		}
	}
	
	public void JoinGame()
	{
		if (networkState == NetworkState.LOGGED_IN && searchingGame == false)
		{

		}
	}
	
	public void SendAction(int cardID, int mode, int modeNumber) {
		if (networkState == NetworkState.IN_GAME)
		{

		}
	}
	
	public void SendTextMessage(String text)
	{

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

	}
	
	@Override
	public void disconnected (Connection connection) {
		
	}

	@Override
	public void received (Connection connection, Object object) {


		
		else if (object instanceof PlayerDataMessage)	//When logging in, a cardDataMessage should always be recieved first to load the players deck.
		{
			PlayerDataMessage pdm = (PlayerDataMessage)object;
			System.out.println(pdm);
			if (pdm.loginAccepted)
			{
				Player p = new Player(pdm.playerID, pdm.name, pdm.activeDeck, pdm.allCards, pdm.decks);
				Game.GetInstance().SetPlayer(1, p);
				
				//Create a new two dimensional list for the card table
				List<IEntTableRow> playerCards = new ArrayList<IEntTableRow>();
				//Add the newly created players cards to it
				
				for (Card c : p.GetAllCards().GetList())
				{
					playerCards.add(c);
				}

				
				Game.GetInstance().GetCardTable().SetDataSource(playerCards);
				
				List<Object> decks = new ArrayList<Object>();
				for (int i = 0; i < pdm.decks.length; i++)
				{
					decks.add("Deck " + (i+1));
				}
				

				
				Game.GetInstance().AddPlayerDeckDropdown(new EntDropdown(-0.35f,0.39f,12,20, decks));
				

		     	
				
				//Game.GetInstance().GetDeckDropdown().SetDataSource(decks);
				
				
				networkState = NetworkState.LOGGED_IN;
				Game.GetInstance().SetGameState(GameState.MAIN_MENU);
			 	
		     	
			}
			else //Recieved data is for opponent
			{
				Game.GetInstance().SetPlayer(2, new Player(pdm.playerID, pdm.name, pdm.decks[0]));
			}
		}
		else if (object instanceof CardDataMessage)
		{
			System.out.println(((CardDataMessage) object));
			System.out.println("CardMessage recieved!!! :)");
			for (String s : ((CardDataMessage) object).cardTemplates) //Load each cardtype/template 
			{
				CardTemplate.LoadCardTemplate(s);
			}
		}
		else if (object instanceof GameMessage)
		{
			GameMessage gmsg = (GameMessage)object;
			System.out.println(gmsg);
			if (gmsg.accepted)
			{
				Game.GetInstance().SetGameID(gmsg.gameID);
				EntUtilities.SetSeed(gmsg.seed1, gmsg.seed2);
				Game.GetInstance().StartGame();
				networkState = NetworkState.IN_GAME;
			}
		}
		else if(object instanceof ActionMessage)	//Recived action from opponent
		{
			ActionMessage amsg = (ActionMessage)object;
			System.out.println(amsg);
			Player opponent = Game.GetInstance().GetPlayer(2);
			Card card = opponent.GetActiveDeck().GameGetCard(amsg.cardID);
		

			if (card.GetGLMIndex() == -1)	//Returns the cards index in the render list and if it's -1 it was not already in the list
			{
				card.MoveToDeck(2);
				Game.GetInstance().ShowCard(card);
			}
			switch(amsg.mode)
			{
			case 1:
				switch(amsg.modeNumber)
				{
				case 1:
					card.PlayToLife(Life.LIFE1, true);
					break;
				case 2:
					card.PlayToLife(Life.LIFE2, true);
					break;
				case 3:
					card.PlayToLife(Life.LIFE3, true);
					break;
				case 4:
					card.PlayToLife(Life.LIFE4, true);
					break;
				}
				break;
			case 2:
				card.PlayToLimbo(true);
				break;
			case 3:
				switch(amsg.modeNumber)	//Note that zone numbers are reversed for the other player so they get reversed here
				{
				case 1:
					card.PlayToZone(Zone.ZONE4, true);
					break;
				case 2:
					card.PlayToZone(Zone.ZONE3, true);
					break;
				case 3:
					card.PlayToZone(Zone.ZONE2, true);
					break;
				case 4:
					card.PlayToZone(Zone.ZONE1, true);
					break;
				}
				break;
			case 4:
				card.PlayToHand(amsg.modeNumber, true);
				break;
			}
		}
	}


	
}
