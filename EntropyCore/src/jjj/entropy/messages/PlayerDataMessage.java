package jjj.entropy.messages;


public class PlayerDataMessage {
	
	public boolean loginAccepted = false;
	
	public String name;
	public int playerID,
			   activeDeck,
			   battleTokens,
			   goldTokens;
	public int[] allCards;
	public int[] allCardCounts;
	
	public int[][] decks;
	public int[] deckDBIDs;
	public int[][] deckCounts;
	
	
	public PlayerDataMessage()
	{	
	}
	
	@Override 
	public String toString() 
	{	
	    StringBuilder result = new StringBuilder();
	    result.append("PlayerDataMessage - ");
	    result.append("name: " + name);
	    result.append(", loginAccepted: " + loginAccepted);
	    result.append(", playerID: " + playerID);
	    result.append(", Gold/Battle tokens: " + goldTokens + ", " + battleTokens);
	    result.append(", active deck: " + activeDeck);
	    result.append(", allCards: ");
	    if (allCards != null)
	    {
		    for ( int s : allCards)
		    	result.append(s);
	    }
	    result.append(", decks: ");
	    if (decks != null)
	    {
		    for ( int i = 0; i < decks.length; i++)
		    {
		    	result.append("Deck ID: " + deckDBIDs[i] + " cardIDs,Counts: ");
		    	for(int j = 0; j < decks[i].length; j++)
		    		result.append(decks[i][j]+","+deckCounts[i][j]+";");
		    }
	    }
	   
	    return result.toString();
	}
}
