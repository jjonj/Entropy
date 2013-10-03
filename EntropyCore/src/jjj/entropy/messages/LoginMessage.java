package jjj.entropy.messages;

public class LoginMessage {

	public String username,
	 			  password,
	 			  message;
	
	public boolean rejected = false;
	
	
	@Override 
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append("ActionMessage - ");
	    result.append("username: " + username);
	    result.append(", password: " + password);
	    result.append(", message: " + message);
	    return result.toString();
	}
}   
