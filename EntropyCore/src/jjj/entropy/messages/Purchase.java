package jjj.entropy.messages;

public class Purchase 
{
	public boolean accepted,
				   ironPrice; //Pay with battle tokens? if false, then pay the gold price
	public int purchaseID,
			   itemID,
			   playerID;
	
	public String[] data;
	
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("Purchase - ");
	    result.append("accepted: " + accepted);
	    result.append(", iron price: " + ironPrice);
	    result.append(", playerID: " + playerID);
	    result.append(", data: " + data);
	    result.append(", purchaseID: " + purchaseID);
	    result.append(", itemID: " + itemID);
	    return result.toString();
	}
	
}
