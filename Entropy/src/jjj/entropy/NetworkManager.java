package jjj.entropy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activity.InvalidActivityException;


import jjj.entropy.classes.EntUtilities;
import jjj.entropy.classes.Enums.GameLocation;
import jjj.entropy.messages.*;
import jjj.entropy.ui.UIManager;

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
	
	private Player cachedPlayer;
	
	
	
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
				   @Override
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
			  kryo.register(ShopDataMessage.class);
			  kryo.register(Purchase.class);
			  kryo.register(String[].class);
			  kryo.register(int[].class);
			  kryo.register(int[][].class);
				
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
		searchingGame = false;
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
		if (networkState == NetworkState.LOGGED_IN && searchingGame == false)
		{
			searchingGame = true;
			GameMessage joinRequest = new GameMessage();
			joinRequest.playerID = Game.GetInstance().GetPlayer().GetID();
			client.sendTCP(joinRequest);
		}
	}
	
	public void SendAction(int cardID, int mode, int modeNumber, boolean swapTurn) {
		if (networkState == NetworkState.IN_GAME)
		{
			ActionMessage amsg = new ActionMessage();
			amsg.cardID = cardID;
			amsg.mode = mode;
			amsg.modeNumber = modeNumber;
			amsg.swapTurn = swapTurn;
			amsg.playerID = Game.GetInstance().GetPlayer().GetID();
			client.sendTCP(amsg);
		}
	}
	
	
	public void SendDeckUpdate(Deck deck)
	{
		PlayerDataMessage pdm = new PlayerDataMessage();
		pdm.playerID = Game.GetInstance().GetPlayer().GetID();
		
		pdm.deckDBIDs = new int[]{deck.GetDBID()};	//Include the ID of the deck being updated as the first entry in the array
		int[][] IDsAndCounts = deck.ToIDCountArray();	//Returns two int arrays. One of the IDs one of the counts
		
		pdm.decks = new int[1][];		//Initialize the two dimensional arrays to only have 1 dimension/element
		pdm.decks[0] = IDsAndCounts[0];
		pdm.deckCounts = new int[1][];	
		pdm.deckCounts[0] = IDsAndCounts[1];
		client.sendTCP(pdm);
		
	}
	
	public void SendTextMessage(String text)
	{
		ChatMessage cmsg = new ChatMessage();
		cmsg.message = text;
		client.sendTCP(cmsg);
	}
	
	public void SendPurchase(int itemID, int purchaseID, boolean ironPrice)
	{
		Purchase pch = new Purchase();
		pch.itemID = itemID;
		pch.ironPrice = ironPrice;
		pch.purchaseID = purchaseID;
		pch.playerID = Game.GetInstance().GetPlayer().GetID();
		client.sendTCP(pch);
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
			
			}
		}
		else if (object instanceof PlayerDataMessage)	//When logging in, a cardDataMessage should always be recieved first to load the players deck.
		{
			PlayerDataMessage pdm = (PlayerDataMessage)object;
			System.out.println(pdm);
			if (pdm.loginAccepted)
			{
				Player p = new Player(pdm.playerID, pdm.name, pdm.battleTokens, pdm.goldTokens, pdm.activeDeck, pdm.allCards, pdm.allCardCounts, pdm.decks, pdm.deckCounts, pdm.deckDBIDs);
				Game.GetInstance().SetPlayer(p);
				
				UIManager.GetInstance().SetBattleTokensLabel(pdm.battleTokens);
				UIManager.GetInstance().SetGoldTokensLabel(pdm.goldTokens);
				UIManager.GetInstance().GetCardTable().SetDataSource(p.GetAllCards());

				//Game.GetInstance().GetDeckDropdown().SetDataSource(decks);
				
				
				networkState = NetworkState.LOGGED_IN;
				Game.GetInstance().SetGameState(Game.GetInstance().GetMainMenu());
			 	
		     	
			}
			else //Recieved data is for opponent
			{				
				cachedPlayer = new Player(pdm.playerID, pdm.name, pdm.decks[0], pdm.deckCounts[0]);
			}
		}
		else if (object instanceof CardDataMessage)
		{
			System.out.println((object));
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
				if (cachedPlayer != null)	//If data has been recieved for the opponent
				{
					EntUtilities.SetSeed(gmsg.seed1, gmsg.seed2);
					Game.GetInstance().StartGame(gmsg.gameID, cachedPlayer, gmsg.firstTurn);
					cachedPlayer = null;
					networkState = NetworkState.IN_GAME;
				}
				else
				{
					//TODO: Handle error, send to server a rejection
					Game.GetInstance().Quit();
				}
			}
		}
		else if(object instanceof ActionMessage)	//Recived action from opponent
		{
			ActionMessage amsg = (ActionMessage)object;
			System.out.println(amsg);
			Player opponent = Game.GetInstance().GetActiveMatch().GetOpponent();
			Card card = opponent.GetActiveDeck().GameGetCard(amsg.cardID);
		

			if (!card.IsShown())	//Returns the cards index in the render list and if it's -1 it was not already in the list
			{
				card.MoveToDeck(2);
				Game.GetInstance().ShowCard(card);
			}
			
			boolean movingOutOfZone = false;
			GameLocation cardOldZone = card.GetZone();
			float oldCardX = 0;
			if (cardOldZone != null)	//If the card is in a combat zone
			{
				oldCardX = card.GetTargetX();
				movingOutOfZone = true;	//assume the card is moving out of the combat zone (checked below)
				card.SetZone(null);		//
			}
			
			
			switch(amsg.mode)
			{
			case 1:
				switch(amsg.modeNumber)
				{
				case 1:
					card.PlayToLife(GameLocation.LIFE1, true);
					break;
				case 2:
					card.PlayToLife(GameLocation.LIFE2, true);
					break;
				case 3:
					card.PlayToLife(GameLocation.LIFE3, true);
					break;
				case 4:
					card.PlayToLife(GameLocation.LIFE4, true);
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
					if (cardOldZone != GameLocation.ZONE4)
					{
						card.PlayToZone(GameLocation.ZONE4, true);
						card.SetZone(GameLocation.ZONE4);
					}
					else
						movingOutOfZone = false;
					break;
				case 2:
					if (cardOldZone != GameLocation.ZONE3)
					{
						card.PlayToZone(GameLocation.ZONE3, true);
						card.SetZone(GameLocation.ZONE3);
					}
					else
						movingOutOfZone = false;
					break;
				case 3:
					if (cardOldZone != GameLocation.ZONE2)
					{
						card.PlayToZone(GameLocation.ZONE2, true);
						card.SetZone(GameLocation.ZONE2);
					}
					else
						movingOutOfZone = false;
					break;
				case 4:
					if (cardOldZone != GameLocation.ZONE1)
					{
						card.PlayToZone(GameLocation.ZONE1, true);
						card.SetZone(GameLocation.ZONE1);
					}
					else
						movingOutOfZone = false;
					break;
				}
				break;
			case 4:
				card.PlayToHand(amsg.modeNumber, true);
				break;
			}
			
			if (movingOutOfZone)
			{
				GameLocation.ReturnAvailCardX(cardOldZone, oldCardX, true);
			}
			
			if (amsg.swapTurn)
				Game.GetInstance().GetActiveMatch().SwapTurn();
			
		}
		else if(object instanceof Purchase)
		{
			Game.GetInstance().FinalizePurchase((Purchase) object);
		}
		else if(object instanceof ShopDataMessage)
		{
			Game.GetInstance().LoadShopData((ShopDataMessage)object);
		}
	}


	
}
