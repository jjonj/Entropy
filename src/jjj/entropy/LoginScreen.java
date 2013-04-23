package jjj.entropy;

import javax.media.opengl.GL2;

import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class LoginScreen implements GameState 
{
	
	private GameState exitState;
	
	public LoginScreen(GameState exitState)
	{
		this.exitState = exitState;
	}
	
	
	@Override
	public void Activate(Game game) 
	{
	}


	@Override
	public void Draw() 
	{
    	Texture.loginScreenTexture.bind(OGLManager.gl);
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
