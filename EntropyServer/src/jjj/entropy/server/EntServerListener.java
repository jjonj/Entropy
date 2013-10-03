package jjj.entropy.server;

import java.util.HashMap;
import java.util.Random;


import jjj.entropy.classes.Const;
import jjj.entropy.classes.EntUtilities;
import jjj.entropy.messages.ActionMessage;
import jjj.entropy.messages.CardDataMessage;
import jjj.entropy.messages.ChatMessage;
import jjj.entropy.messages.GameMessage;
import jjj.entropy.messages.LoginMessage;
import jjj.entropy.messages.PlayerDataMessage;
import jjj.entropy.messages.Purchase;
import jjj.entropy.messages.ShopDataMessage;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class EntServerListener extends Listener {

	private Random rnd = new Random();
	
	private int connectedPlayerCount;
	//Is actually max players logged in (connect and login will happen at the same time later FIX)
	public HashMap<Integer, Connection> playerConnectionMap = new HashMap<Integer, Connection>(Const.MAX_PLAYERS_CONNECTED);
	//Hashmap for quick lookup in the above connection array, contains the playerID for connections
	public HashMap<Connection, Integer> connectionPlayerMap = new HashMap<Connection, Integer>(Const.MAX_PLAYERS_CONNECTED);	
	
	@Override
	public void connected (Connection connection) 
	{
		
		System.out.println(connection.getRemoteAddressTCP().toString() + " CONNECTING!");
		
	}
	
	@Override
	public void disconnected (Connection connection) 
	{
		System.out.println(" DISCONNECTING!");
		if(connectionPlayerMap.containsKey(connection))
			Logout(connection);
		
		
		
	}
	
	public void Logout(Connection connection)
	{
		playerConnectionMap.remove(connectionPlayerMap.get(connection));
		--connectedPlayerCount;
		
		int connectedPlayerID = connectionPlayerMap.get(connection);

		GameInstance.PlayerLeave(connectedPlayerID);
		
	}
	
	
	@Override
	public void received (Connection connection, Object object) {
		
		if (object instanceof ChatMessage) {
			System.out.println("ChatMessage recieved");
			ChatMessage request = (ChatMessage)object;
			System.out.println(request.message);
			
			ChatMessage response = new ChatMessage();
			connection.sendTCP(response);
		}
		else if(object instanceof GameMessage) 
		{
			System.out.println("Gamemessage recieved");
			
			if (((GameMessage) object).disconnecting == false)
			{
				
				
				int outOtherPlayerID[] = { -1 };	// Output parameter, using array as a hack, could also use a wrapper instead
				int instanceID = GameInstance.AssignToSlot(((GameMessage)object).playerID, outOtherPlayerID);
				int firstPlayerID = outOtherPlayerID[0];	
				int secondPlayerID = ((GameMessage) object).playerID;
				
				
				if (instanceID != -1)
				{
		
					if (firstPlayerID != -1)	//If both players are now present
					{
						
						
						CardDataMessage firstPlayerDeck = new CardDataMessage();
						CardDataMessage secondPlayerDeck = new CardDataMessage();
						
						PlayerDataMessage firstPlayer = new PlayerDataMessage();
						PlayerDataMessage secondPlayer = new PlayerDataMessage();
						
						DatabaseManager.GetInstance().GetPlayerInfo(firstPlayerID, firstPlayer, firstPlayerDeck, true);	//Get basic info for each player
						DatabaseManager.GetInstance().GetPlayerInfo(secondPlayerID, secondPlayer, secondPlayerDeck, true);
						


						
						playerConnectionMap.get(firstPlayerID).sendTCP(secondPlayerDeck);	//first send the other players deck so the cards can be loaded
						connection.sendTCP(firstPlayerDeck);
						
						playerConnectionMap.get(firstPlayerID).sendTCP(secondPlayer);		//Then send the other players data
						connection.sendTCP(firstPlayer);
						
						GameMessage responseTo1 = new GameMessage();
						GameMessage responseTo2 = new GameMessage();
						boolean firstPlayerFirstTurn = rnd.nextBoolean();
						responseTo1.firstTurn = firstPlayerFirstTurn;
						responseTo2.firstTurn = !firstPlayerFirstTurn;
						responseTo1.accepted = true;
						responseTo2.accepted = true;
						responseTo1.gameID = instanceID;
						responseTo2.gameID = instanceID;
						responseTo1.seed1 = (long)EntUtilities.GetRandom(0, 10000, 1);	//Is this proper?
						responseTo1.seed2 = (long)EntUtilities.GetRandom(0, 10000, 1);
						responseTo2.seed1 = responseTo1.seed1;
						responseTo2.seed2 = responseTo1.seed2;
						playerConnectionMap.get(firstPlayerID).sendTCP(responseTo1);		//Finally send the response that the game have been accepted
						connection.sendTCP(responseTo2);
						System.out.println("Player " + ((GameMessage)object).playerID + " has joined game instance " + instanceID);
						System.out.println("Player " + firstPlayerID + " has joined game instance " + instanceID);
					}
				}
				else
				{
					GameMessage response = new GameMessage();
					response.accepted = false;
					connection.sendTCP(response);
					System.out.println("Player " + ((GameMessage)object).playerID + " could not connect to any game.");
				}
			}
			else
			{
				GameInstance.PlayerLeave(((GameMessage)object).playerID);
			}
	    }
		else if (object instanceof LoginMessage)
		{
			System.out.println("LoginMesage recieved!!");
			if (connectedPlayerCount < Const.MAX_PLAYERS_CONNECTED)
			{
				
				
				LoginMessage lgmsg = (LoginMessage)object;
				
				PlayerDataMessage player = new PlayerDataMessage();
				CardDataMessage playerDeck = new CardDataMessage();
				ShopDataMessage shopData = new ShopDataMessage();
				if (DatabaseManager.GetInstance().Login(player, playerDeck, lgmsg.username, lgmsg.password))
				{
					DatabaseManager.GetInstance().GetShopData(shopData);
					connectedPlayerCount++;
					
					playerConnectionMap.put(player.playerID, connection);	//Remember connection for this player
	
					connectionPlayerMap.put(connection, player.playerID);
					connection.sendTCP(playerDeck);
					connection.sendTCP(player);
					connection.sendTCP(shopData);
					System.out.println(lgmsg.username + " has logged in!");
					
				}
				else
				{
					LoginMessage reject = new LoginMessage();
					reject.rejected = true;
					connection.sendTCP(reject);
				}
				
			}
			else
			{
				System.out.println("It was rejected, since server has reached maximum capacity: " + Const.MAX_PLAYERS_CONNECTED);
				LoginMessage reject = new LoginMessage();
				reject.rejected = true;
				connection.sendTCP(reject);
			}
		}
		else if (object instanceof PlayerDataMessage) //Occours on player deck updates
		{
			PlayerDataMessage pdm = (PlayerDataMessage)object;
			DatabaseManager.GetInstance().UpdateDeck(pdm.playerID, pdm.deckDBIDs[0], pdm.decks[0], pdm.deckCounts[0]);
		}
		else if (object instanceof ActionMessage)	//Simply redirect the message to the other player.
		{
			int playerID = ((ActionMessage) object).playerID;
			Connection con = playerConnectionMap.get(GameInstance.GetOtherPlayerID(playerID));
			if (con != null)
				con.sendTCP(object);	// TODO: can this still cause exception?
		}
		else if(object instanceof Purchase)
		{
			if (ShopManager.ValidatePurchase((Purchase) object))
			{
				connection.sendTCP((Purchase) object);
			}
			else
				; //TODO: Handle error
		}
	}
	
}
