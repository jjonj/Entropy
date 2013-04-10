package jjj.entropy;

import jjj.entropy.ui.UIComponent;

public interface GameState 
{
	
	//Method to be called right when gamestate is switched to this one
	public void Activate(Game game);	

	public void Draw();

	public UIComponent GetDefaultFocusedUIElement();
	
}
