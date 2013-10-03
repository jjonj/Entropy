package jjj.entropy.messages;


public class CardDataMessage {
	public String[] cardTemplates;	//Each string consists of an encoded card template
	
	public CardDataMessage()
	{
	}
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("CardDataMessage - ");
	    result.append("cards: ");
	    for ( String s : cardTemplates)
	    	result.append(s);
	    
	    return result.toString();
	}
}
