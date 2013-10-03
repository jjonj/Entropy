package jjj.entropy.server;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jjj.entropy.messages.CardDataMessage;
import jjj.entropy.messages.PlayerDataMessage;
import jjj.entropy.messages.Purchase;
import jjj.entropy.messages.ShopDataMessage;

//Db actions are blocking! Make it threaded!

public class DatabaseManager 
{

	private static DatabaseManager instance = null ;
	
	public boolean Connected = false;
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet resultSet = null;
	Random rnd = null;
	List<String> commonCards,	//Should these really be loaded into memory?
				 uncommonCards,
				 rareCards,
				 legendaryCards;
	
	
	protected DatabaseManager()
	{
		rnd = new Random();
	}
	
	public static DatabaseManager GetInstance()
	{
		
		if (instance == null)
			instance = new DatabaseManager();
		return instance;
	}

	
	public void GetPlayerInfo(int playerID, PlayerDataMessage pdm, CardDataMessage cdm, boolean onlyActiveDeck)
	{
		try {
			
			//First get the player information
			stmt = conn.createStatement();			
			resultSet = stmt.executeQuery("select account_id, username, active_deck_id, battle_tokens, gold_tokens from accounts where account_id = '" + playerID + "'");
		
			String username;
			if (resultSet.next())
			{
				username = resultSet.getString("username");
			}
			else 
				return;	//This should never happen
		
			//Next we get all the cards owned by a user so they can be loaded by the client
			
			Statement stmt2  = conn.createStatement();
			ResultSet resultSet2 = stmt2.executeQuery("select distinct cards.* from cards "+			//Get all cards owned  by user
													  "join card_collection_relation "+
													  "on cards.card_id = card_collection_relation.card_id "+
													  "join card_collections "+
													  "on card_collection_relation.collection_id = card_collections.collection_id "+
													  "where account_id = " + playerID);
			
			List<String> results = new ArrayList<String>();
			
			
			while (resultSet2.next())	//Load all the cards and gather their string encodings
			{
				// Encoding: (OBSOLETE)  	ID,TITLE,RACE,TYPE,COSTR,COSTA,INCOME,DEF,DMGB,DMGD
				// Encoding: (NEW)  	ID,TITLE,RACE,TYPE,RARITY,COSTR,COSTA,STR,INT,VIT
				results.add(resultSet2.getInt("card_id")+","+resultSet2.getString("title")+","+resultSet2.getInt("race")+","+resultSet2.getInt("type")+","+resultSet2.getInt("rarity")+","+
						resultSet2.getInt("race_cost")+","+resultSet2.getInt("any_cost")+","+resultSet2.getInt("strength")+","+resultSet2.getInt("intelligence")+
						","+resultSet2.getInt("vitality"));
			}
			
			
			cdm.cardTemplates  = (String[]) results.toArray(new String[results.size()]);
			
			
			pdm.loginAccepted = false;	//Note related to login
			
			int DBActiveDeck = resultSet.getInt("active_deck_id");
			
			
			if (!onlyActiveDeck)	//If asked to get full information (which happens on login, partial information is requested when getting opponent info)
			{
				
				pdm.battleTokens = resultSet.getInt("battle_tokens");
				pdm.goldTokens = resultSet.getInt("gold_tokens");
				
				List<Integer> allCardList = new ArrayList<Integer>();
				List<Integer> allCardCounts = new ArrayList<Integer>();
				//Type 1 collections are allcards collections
				//Get cards in the all cards collection (type 1) for that player (SINCE ITS A 1 TO 1 RELATION BETWEEN ACC AND TYPE 1, CONSIDER DIRECT REFERENCE IN DB)
				stmt2  = conn.createStatement();
				resultSet2 = stmt2.executeQuery("select card_id, count from card_collection_relation "+
										"where collection_id IN (select collection_id from card_collections where account_id = "+ playerID +
										" AND collection_type = 1)");
				
				//For each card the player owns in his all collection
				while (resultSet2.next())	
				{
					allCardList.add(resultSet2.getInt("card_id"));
					allCardCounts.add(resultSet2.getInt("count"));
				}
				
				
				
				pdm.allCards = new int[allCardList.size()];	//Really ugly!
				pdm.allCardCounts = new int[allCardList.size()];
			    for (int i = 0; i < pdm.allCards.length; i++)
			    {
			 
			    	pdm.allCards[i] = allCardList.get(i).intValue();
			    	pdm.allCardCounts[i] = allCardCounts.get(i).intValue();
			    }			
						
			    
			    //Get all decks by that player
				stmt2  = conn.createStatement();
				resultSet2 = stmt2.executeQuery("select collection_id from card_collections where account_id = "+playerID+" AND collection_type = 2");
				
				
				List<Integer> deckIDs = new ArrayList<Integer>();
					
				
				//Foreach of the players decks
				while (resultSet2.next())	
				{
					int tempDeckID = resultSet2.getInt("collection_id");
					deckIDs.add(tempDeckID);
					if (tempDeckID == DBActiveDeck)	//If the current deck is the players active deck
						pdm.activeDeck = deckIDs.size()-1;	//Converted from DB active deck to index in the returned deck array
				}
				pdm.deckDBIDs = new int[deckIDs.size()];
				for (int i = 0; i < deckIDs.size(); i++)	//Save the Database IDs which the player will need for reference when updating decks
				{
					pdm.deckDBIDs[i] = deckIDs.get(i);	
				}
				pdm.decks = new int[deckIDs.size()][];	//Allocate space for each array
				pdm.deckCounts = new int[deckIDs.size()][];	
				
				
				
				List<Integer> tempDeck = new ArrayList<Integer>();
				List<Integer> tempDeckCounts = new ArrayList<Integer>();
				int deckCounter = 0;
				for (Integer did : deckIDs)	//Iterate each deck and get the id's from that deck
				{
					//Get the card id's and counts for the current deck
					stmt2  = conn.createStatement();
					resultSet2 = stmt2.executeQuery("select card_id, count from card_collection_relation where collection_id = "+did +" order by card_id");
					while (resultSet2.next())	
					{
						tempDeck.add(resultSet2.getInt("card_id"));
						tempDeckCounts.add(resultSet2.getInt("count"));
					}

					pdm.decks[deckCounter] = new int[tempDeck.size()];	//Really ugly!
					pdm.deckCounts[deckCounter] = new int[tempDeck.size()];
				    for (int i = 0; i < tempDeck.size(); i++)
				    {
				    	pdm.decks[deckCounter][i] = tempDeck.get(i).intValue();	//Assign card id for the current deck
				    	pdm.deckCounts[deckCounter][i] = tempDeckCounts.get(i).intValue();	//Assign card count 
				    }	
				    deckCounter++;
				    tempDeck.clear();
				}

			}
			else	//If only asked to get the active deck
			{
				List<Integer> activeDeckList = new ArrayList<Integer>();
				List<Integer> activeDeckCounts = new ArrayList<Integer>();

				stmt  = conn.createStatement();
				resultSet = stmt.executeQuery("select card_id, count from card_collection_relation where collection_id = "+DBActiveDeck+" order by card_id");	//Get card id's from active deck
				while (resultSet.next())	
				{
					activeDeckList.add(resultSet.getInt("card_id"));
					activeDeckCounts.add(resultSet.getInt("count"));
				}
				if (activeDeckList.size() == 0)
				{
					pdm.decks = null;	//Active deck have no cards for that player
					pdm.activeDeck = -1;
				}
				else
				{
					pdm.decks = new int[1][];	//Just 1 deck in the decks collection (the active deck)
					pdm.deckCounts = new int[1][];	//Just 1 deck in the decks collection (the active deck)
					pdm.deckDBIDs = new int[] { DBActiveDeck };
					pdm.decks[0] = new int[activeDeckList.size()];
					pdm.deckCounts[0] = new int[activeDeckList.size()];
					try {
					    for (int i = 0; i < activeDeckList.size(); i++)
					    {
					    	pdm.decks[0][i] = activeDeckList.get(i).intValue();	
					    	pdm.deckCounts[0][i] = activeDeckCounts.get(i).intValue();
					    }	
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				    pdm.activeDeck = 0;
				}
			}
			
			
			pdm.name = username;
			pdm.playerID = playerID;	
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public boolean Login(PlayerDataMessage pdm, CardDataMessage cdm, String username, String password)
	{
		try {
			stmt = conn.createStatement();
			
			resultSet = stmt.executeQuery("select * from accounts where username='" + username + "'");
		
			if (resultSet.next())	//If there was any result
			{
				
				if (resultSet.getString("password").equals(password))
				{
		
					GetPlayerInfo(resultSet.getInt(1), pdm, cdm, false);
					pdm.loginAccepted = true;
					
					return true;
				}
					
				return false;
			}
			else
				return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return true;
	}
	
	
	public void UpdateDeck(int playerID, int DBID, int[] deck, int[] deckCounts) 
	{
		
		//Create a hashmap for quick inspection of cards in the deck being updated and quick access to counts of those cards.
		HashMap<Integer, Integer> deckMap = new HashMap<Integer, Integer>(deck.length);
		//Add the deck to the hashmap
		for (int i = 0; i < deck.length; i++)
		{
			deckMap.put(deck[i], deckCounts[i]);
		}
		
		//StringBuilder updateQuery = new StringBuilder(deckMap.size()*10);	//Just using a scaling guess at the initial size, not based on much
		//StringBuilder deleteQuery = new StringBuilder(deckMap.size()*10);	
		
		Statement stmt2 = null;
		
		try {
			stmt2  = conn.createStatement();
			//Get the deck from the database for comparison
			stmt  = conn.createStatement();
			resultSet = stmt.executeQuery("select card_id, count from card_collection_relation where collection_id = "+DBID+" order by card_id");	//Get card id's from active deck
			
			while (resultSet.next())	
			{
				Integer cID = resultSet.getInt("card_id");
				if (deckMap.containsKey(cID))	
				{
					if (deckMap.get(cID) != resultSet.getInt("count"))
					{
						//If the current card is also in the updated deck but the count is different, do a DB update to reflect the new count

						stmt2.addBatch("update card_collection_relation set count="+deckMap.get(cID)+" where card_id="+cID+" and collection_id="+DBID);
						//updateQuery.append("update card_collection_relation set count="+deckMap.get(cID)+" where card_id="+cID+" and collection_id="+DBID+";");
					}
					deckMap.remove(cID);
				}
				else
				{
					//If the card is not in the updated deck, it needs to be deleted from the DB
					stmt2.addBatch("delete from card_collection_relation where card_id="+cID+" and collection_id="+DBID+";");
					//deleteQuery.append("delete from card_collection_relation where card_id="+cID+" and collection_id="+DBID+";");
					deckMap.remove(cID);
				}
			}
		} catch (SQLException e) {
			
			
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		    e.printStackTrace();
		 //   System.exit(1);
		}
		
		
		try {
			
			stmt2.executeBatch();
			/*
			if (updateQuery.length() > 0)
			{
				
				stmt  = conn.createStatement();
				stmt.execute(updateQuery.toString());		//Update the already existing cards to their new values
			}
			if (deleteQuery.length() > 0)
			{
				stmt  = conn.createStatement();
				stmt.execute(deleteQuery.toString());		//Delete the obsolete cards
			}*/
		} catch (SQLException e) {
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		    e.printStackTrace();
		 //   System.exit(1);
		}
		
		
		//Any needed removals and updates have already been performed and removed from the HashMap so any remaining cards in the updated deck need to be added to the DB
		
		
		if (deckMap.size() > 0)
		{
			StringBuilder insertQuery = new StringBuilder(85 + deckMap.size() * 20);	
			insertQuery.append("insert into card_collection_relation (card_id, count, collection_id) values ");
			for (Integer cardID : deckMap.keySet())
			{
				insertQuery.append("(" + cardID + ","+deckMap.get(cardID)+","+DBID+"),");	//Append values for each card+count to be added
			}
			insertQuery.deleteCharAt(insertQuery.length()-1);	//Delete the last comma
			try {
				stmt  = conn.createStatement();
				stmt.execute(insertQuery.toString());	//Insert the missing cards
			} catch (SQLException e) {
			    System.out.println("SQLException: " + e.getMessage());
			    System.out.println("SQLState: " + e.getSQLState());
			    System.out.println("VendorError: " + e.getErrorCode());
			    e.printStackTrace();
			 //   System.exit(1);
			}
		}
	}
	
	public void GetShopData(ShopDataMessage shopData) 
	{
		List<String> shopItems = new ArrayList<String>();
		try {
			stmt  = conn.createStatement();
			resultSet = stmt.executeQuery("select item_id,item_type,item_name,item_description,item_cost_bt,item_cost_gt from shop_items order by item_id");	//Get card id's from active deck
			
			while (resultSet.next())	
			{
				// Encoding: itemID;itemType;itemName;itemDescription;costBT;costGT
				shopItems.add(""+resultSet.getInt("item_id")+";"+resultSet.getInt("item_type")+";"+resultSet.getString("item_name")+";"+resultSet.getString("item_description")+";"+resultSet.getInt("item_cost_bt")+";"+resultSet.getInt("item_cost_gt"));
			}
			
		} catch (SQLException e) {
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		    e.printStackTrace();
		}
		
		
		shopData.shopItems = shopItems.toArray(new String[shopItems.size()]);

		
	}
	
	
	
	public boolean Connect()
	{
		   
		try {
		    String url = "jdbc:mysql://localhost:3306/";
		    String dbName = "entropy";
		    String userName = "root"; 
		    String password = "agentaa3";
		    conn = DriverManager.getConnection(url+dbName,userName,password);
		    Connected = true;
		    
		    
		    commonCards = new ArrayList<String>();
		    uncommonCards = new ArrayList<String>();
		    rareCards = new ArrayList<String>();
		    legendaryCards = new ArrayList<String>();
		    
		    
		    // Encoding: (NEW)  	ID,TITLE,RACE,TYPE,RARITY,COSTR,COSTA,STR,INT,VIT
			stmt  = conn.createStatement();
			resultSet = stmt.executeQuery("select card_id,title,race,type,rarity,race_cost,any_cost,strength,intelligence,vitality from cards");
			
			
			List<String> cardCategory = null;
			while (resultSet.next())	
			{
				switch (resultSet.getInt("rarity"))
				{
				case 0:
					cardCategory = commonCards;
					break;
				case 1:
					cardCategory = uncommonCards;
					break;
				case 2:
					cardCategory = rareCards;
					break;
				case 3:
					cardCategory = legendaryCards;
					
					break;
				default:
						break;
				}
				cardCategory.add(resultSet.getInt("card_id")+","+resultSet.getString("title")+","+resultSet.getInt("race")+","+resultSet.getInt("type")+","+resultSet.getInt("rarity")+","+
						resultSet.getInt("race_cost")+","+resultSet.getInt("any_cost")+","+resultSet.getInt("strength")+","+resultSet.getInt("intelligence")+
						","+resultSet.getInt("vitality"));
			}
		    
			if (!(commonCards.size() != 0 && uncommonCards.size() != 0 && rareCards.size() != 0 && legendaryCards.size() != 0))
			{
				System.out.println("Constraint assertion failed! Database must contain atleast one card of each rarity category.");
				return false;
			}
			
			resultSet = stmt.executeQuery("select item_id, item_type, item_name, item_description, item_cost_bt, item_cost_gt from shop_items");	//Get common cards
			
			
			//Create methods for obtaining the data of each purchase. the item_id of the iteam in the database determines which of these methods to call
			HashMap<Integer, DataMethod> methodMap = new HashMap<Integer, DataMethod>(20);
			
			methodMap.put(1,  new DataMethod(){@Override 
				public String[] GetData(){
				 	return ShopManager.GenerateStartersPack();
				}
			});
			methodMap.put(2,  new DataMethod(){@Override 
				public String[] GetData(){
				 	return ShopManager.GenerateCollectorsPack();
				}
			});
			methodMap.put(3,  new DataMethod(){@Override 
				public String[] GetData(){
				 	return ShopManager.GenerateGoldPack();
				}
			});
			
			
			
			while (resultSet.next())	
			{
				
				ShopItem nSI = new ShopItem(resultSet.getString("item_name"), resultSet.getString("item_description"), 
						   resultSet.getInt("item_id"),resultSet.getInt("item_type"), resultSet.getInt("item_cost_bt"), resultSet.getInt("item_cost_gt"),
						   methodMap.get(resultSet.getInt("item_id")));
				ShopItem.shopItemMap.put(resultSet.getInt("item_id"), nSI);
			}
			return true;
			
			
		} catch (SQLException e) {
			// handle any errors
		    System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		 //   System.exit(1);
		    return false;
		}
		
		
		
	}
	
	public void Disconnect()
	{
		try {
			conn.close();
		} catch (SQLException e) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				System.out.println("FATAL ERROR: Cannot close DB Connection or sleep & retry. Close the connection manually!");
				e1.printStackTrace();
				System.exit(1);
			}
	//		Disconnect();
			   System.out.println("SQLException: " + e.getMessage());
			    System.out.println("SQLState: " + e.getSQLState());
			    System.out.println("VendorError: " + e.getErrorCode());
		}
		conn = null;
	}
	
   protected void finalize() throws SQLException
   {
   		if (conn != null)
   		{
   			conn.close();
   		}
   }

   
   
   //THESE METHODS REQUIRE THAT ATLEAST 1 CARD OF EACH RARITY EXIST
   
	public String GetRandomProbability(float commonChc, float uncommonChc, float rareChc) 
	{
		List<String> cardTable;
		
		if (rnd.nextFloat() < commonChc)
			cardTable = commonCards;
		else if (rnd.nextFloat() < uncommonChc)
			cardTable = uncommonCards;
		else if (rnd.nextFloat() < rareChc)
			cardTable = rareCards;
		else
			cardTable = legendaryCards;
		
		return cardTable.get(rnd.nextInt(cardTable.size()));
	}
	public String GetRandomCommon() 
	{
		return commonCards.get(rnd.nextInt(commonCards.size()));
	}
	public String GetRandomUncommon() 
	{
		return uncommonCards.get(rnd.nextInt(uncommonCards.size()));
	}
	
	public String GetRandomRare() 
	{
		return rareCards.get(rnd.nextInt(rareCards.size()));
	}
	
	public String GetRandomLegendary() 
	{
		return legendaryCards.get(rnd.nextInt(legendaryCards.size()));
	}

	public boolean FinalizePurchase(Purchase purchase) 
	{
		try {
			
			ShopItem item = ShopItem.shopItemMap.get(purchase.itemID);
			
			int itemPrice = purchase.ironPrice ? item.costBT : item.costGT;
			String currency = purchase.ironPrice ? "battle_tokens" : "gold_tokens";
			
			
			conn.setAutoCommit(false);	//Following works as a transaction
			
			stmt  = conn.createStatement();
			resultSet = stmt.executeQuery("select "+ currency +" as balance from accounts where account_id="+purchase.playerID);
			resultSet.next();
				
			
			//If player can afford it (note: this is also checked on client, but not trustworthy)
			if (resultSet.getInt("balance") >= itemPrice)
			{
				
				if (item.type == 1)
				{
					stmt  = conn.createStatement();
					resultSet = stmt.executeQuery("select collection_id from card_collections where account_id="+purchase.playerID+" and collection_type=1");
				
					resultSet.next();
					
					
					StringBuilder insertQuery = new StringBuilder(120 + purchase.data.length * 20);	
					insertQuery.append("insert into card_collection_relation (card_id, count, collection_id) values ");
					
		
					
					for (String card : purchase.data)
					{
						insertQuery.append("(" + Integer.parseInt(card.substring(0,  card.indexOf(','))) + ",1,"+resultSet.getInt("collection_id")+"),");	//Append values for each card+count to be added
					}
					insertQuery.deleteCharAt(insertQuery.length()-1);	//Delete the last comma
				
					insertQuery.append(" ON DUPLICATE KEY UPDATE count = VALUES(count) + card_collection_relation.count");
					
					
					stmt  = conn.createStatement();
					stmt.execute(insertQuery.toString());	//Insert the new cards
			
					stmt  = conn.createStatement();
					stmt.execute("update accounts set "+ currency +"="+ currency +"-"+itemPrice+" where account_id="+purchase.playerID);
					
					conn.commit();
					return true;
				}
				else
				{
					conn.commit();
					return false;
				}
				
				
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				System.out.println("SQLException: " + e1.getMessage());
				System.out.println("SQLState: " + e1.getSQLState());
				System.out.println("VendorError: " + e1.getErrorCode());
				e1.printStackTrace();
			}
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			e.printStackTrace();
		}
		return false;
	}



	
	
}
