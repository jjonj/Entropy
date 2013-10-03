package jjj.entropy.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jjj.entropy.classes.Const;

public class GameInstance {

	
	private static GameInstance[] games = new GameInstance[Const.MAX_GAME_COUNT];
	//private static int[] otherPlayers = new int[Const.MAX_PLAYERS_CONNECTED];	//Each index is the player by index id's opponents id
	private static HashMap<Integer, Integer> otherPlayers = new HashMap<Integer, Integer>(Const.MAX_PLAYERS_CONNECTED);	//Each index is the player by index id's opponents id
	
	private static HashMap<Integer, GameInstance> playerGameMap = new HashMap<Integer, GameInstance>(Const.MAX_PLAYERS_CONNECTED);	
	
	
	private static int gameCount;
	private static List<Integer> avaliableSlots = new ArrayList<Integer>();
	private static List<Integer> openGames = new ArrayList<Integer>();
	public int gameIndex;
	
	int  player1ID = -1,
		 player2ID = -1;
	

	
	//Assigns a player to a slot, returns the other players ID if there was already another player in the game, otherwise otherplayer is set to -1
	public static int AssignToSlot(int playerID, int[] outOtherPlayerID) {
		if (openGames.size() > 0)	//If there is an open game, assign to that
		{
			int openGameIndex = openGames.get(openGames.size()-1);
			openGames.remove(openGames.size()-1);
			games[openGameIndex].AddPlayer(playerID);
			int otherPlayerID = games[openGameIndex].player1ID;
			outOtherPlayerID[0] = otherPlayerID; //Assign otherplayer as the other players ID
			otherPlayers.put(playerID,  otherPlayerID);	//Assign those players to eachother for easy access
			otherPlayers.put(otherPlayerID,  playerID);
			
			System.out.println("Found open game: " + openGameIndex);
			
			return openGameIndex;
		}
		else
		{
			if (avaliableSlots.size() > 0)
			{
				GameInstance newGameI = new GameInstance();
				int slotIndex = avaliableSlots.get(avaliableSlots.size()-1);
				avaliableSlots.remove(avaliableSlots.size()-1);
				games[slotIndex] = newGameI;
				newGameI.AddPlayer(playerID);
				outOtherPlayerID[0] = -1;
				return slotIndex;
			}
			else
			{
				if (gameCount < 100)
				{
					GameInstance newGameI = new GameInstance();
					games[gameCount] = newGameI;
					newGameI.AddPlayer(playerID);
					gameCount++;
					outOtherPlayerID[0] = -1;
					return gameCount-1;
				}
				else 
					return -1;
			}
		}
	}

	private void AddPlayer(int playerID) 
	{
		playerGameMap.put(playerID, this);
		if (player1ID == -1)	//If this is the first player joining the game
		{
			player1ID = playerID;
			openGames.add(gameIndex);
		}
		else
		{
			player2ID = playerID;
		}

	}


	
	public static void PlayerLeave(int playerID) 
	{
		GameInstance game = playerGameMap.get(playerID);
		if (game != null)
		{
			System.out.println("Player " + playerID + " left game: " + game.gameIndex);
			playerGameMap.remove(playerID);
			
			//If both players now left
			if (otherPlayers.containsKey(otherPlayers.get(playerID)))
			{
				avaliableSlots.add(game.gameIndex);
				games[game.gameIndex] = null;
			}
			
			otherPlayers.remove(playerID);
		}
	}

	public static int GetOtherPlayerID(int playerID) {
		return otherPlayers.get(playerID);
	}

	
	
	
}
