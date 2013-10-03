package jjj.entropy.messages;


public class ChatMessage {
	public String message;
	
	public ChatMessage()
	{
	}
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("ChatMessage - ");
	    result.append(message);
	    return result.toString();
	}
}
