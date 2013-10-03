package jjj.entropy.messages;


public class ShopDataMessage {
	public String[] shopItems;
	
	public ShopDataMessage()
	{
	}
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("ShopDataMessage - ");
	    result.append("items: ");
	    for ( String s : shopItems)
	    	result.append(s);
	    
	    return result.toString();
	}
}
