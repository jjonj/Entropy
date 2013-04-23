package jjj.entropy;

import javax.media.opengl.GL2;

import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class MainMenu implements GameState 
{

	private boolean loggedIn = false;
	private GameState exitState;

	public MainMenu(GameState exitState)
	{
		this.exitState = exitState;
	}
	
	@Override
	public void Activate(Game game) 
	{
		if (loggedIn == false)
		{
			loggedIn = true;
			//Add the players deck to the dropdown of decks
			UIManager.GetInstance().GetPlayerDeckDropdown().SetDataSource(game.GetPlayer().GetAllDecks());
		}
	}
	
	
	@Override
	public void Draw() 
	{
    	Texture.mainMenuTexture.bind(OGLManager.gl);
    	OGLManager.DrawScreen();

	}

	@Override
	public UIComponent GetDefaultFocusedUIElement() 
	{
		return UIManager.GetInstance().GetDefaultFocusedUIElement(this);
	}

	@Override
	public GameState GetExitState() 
	{
		return exitState;
	}
	
}
