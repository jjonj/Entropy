package jjj.entropy.messages;


public class ActionMessage {
	
	public boolean swapTurn;
	public int playerID,
			   cardID,
			   mode,
			   modeNumber;
	
	public ActionMessage()
	{
		
			
	}
	
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("ActionMessage - ");
	    result.append("swap turn: - " + swapTurn + " - ");
	    result.append("playerID: " + playerID);
	    result.append(", cardID: " + cardID);
	    result.append(", mode: " + mode);
	    result.append(", modeNumber: " + modeNumber);
	    return result.toString();
	  }
	
}
