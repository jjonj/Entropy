package jjj.entropy;

import jjj.entropy.ui.UIComponent;

public interface GameState 
{
	
	//Method to be called right when gamestate is switched to this one
	void Activate(Game game);

	public void Draw();

	public UIComponent GetDefaultFocusedUIElement();

	public GameState GetExitState();	//Returns the gamestate to go to when escaping. Null results in quitting the game

	
	
}
