package jjj.entropy.ui;

import jjj.entropy.Game;
import jjj.entropy.OGLManager;

public abstract class UIComponent {

	
	
	protected int x, y,
				  screenX, screenY;			//Coordinates in screen coordinates
					  
	protected float glX, glY;
	
	
	public UIComponent(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.glX = OGLManager.MapPercentToFloatX(x);
		this.glY = OGLManager.MapPercentToFloatY(y);
		screenX = OGLManager.MapPercentToScreenX(x);
		screenY = OGLManager.MapPercentToScreenY(y);
		
	}
	
	public int GetScreenX() 
	{
		return screenX;
	}
	public int GetScreenY() 
	{
		return screenY;
		
	}
	public int GetPercentX()
	{
		return x;
	}
	public int GetPercentY()
	{
		return y;
	}
	public float GetGLX()
	{
		return glX;
	}
	public float GetGLY()
	{
		return glY;
	}

	public abstract void Render(Game game);


	
	}
