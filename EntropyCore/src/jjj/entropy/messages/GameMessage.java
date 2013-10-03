package jjj.entropy.messages;


public class GameMessage 
{
	public String message;
	public boolean accepted = false,
					disconnecting = false;
	public int gameID = -1;
	public int playerID; 
	public long seed1, seed2;
	public boolean firstTurn;
	
	public GameMessage()
	{
	}
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("GameMessage - ");
	    result.append("first turn: " + firstTurn + " ");
	    result.append("accepted: " + accepted);
	    result.append(", disconnecting: " + disconnecting);
	    result.append(", gameID: " + gameID);
	    result.append(", playerID: " + playerID);
	    return result.toString();
	}
}
