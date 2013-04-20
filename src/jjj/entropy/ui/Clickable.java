package jjj.entropy.ui;

import jjj.entropy.OGLManager;

/**
 * @author      Jacob Jensen <jjonj@hotmail.com>
 * @version     1.0a                 (current version number of program)
 * @since       2013-01-01          (the version of the package this class was first added to)
 */
public abstract class Clickable extends UIComponent
{
	
	//Optional virtual method for action when activated
	public void Activate(int mouseX, int mouseY) {}

	//Optional virtual method for certain code that need to be run on window resize, such as updating gl/screen relative coordinates.
	public void OnResize(int[] view, double[] model , double[] proj) 
	{
		screenX = OGLManager.MapPercentToScreenX(x);
		screenY = OGLManager.MapPercentToScreenY(y);
		screenWidth = OGLManager.MapPercentToScreenX(width);
		//Uses MapPercentToScreenX instead of MapPercentToScreenY to make it easier for users to estimate proportions
		screenHeight = OGLManager.MapPercentToScreenX(height);
	}
	
	
	protected int width, height,				//Percentage width/height
				  screenWidth, screenHeight;	//Width/height in pixels
	protected float glWidth, glHeight;			//Width/Height in openGL coordinates
	
	public Clickable(int x, int y, int width, int height)
	{
		super(x, y);
		
		this.width = width;
		this.height = height;
		screenWidth = OGLManager.MapPercentToScreenX(width);
		//Uses MapPercentToScreenX instead of MapPercentToScreenY to make it easier for users to estimate proportions
		screenHeight = OGLManager.MapPercentToScreenX(height);
		glWidth = OGLManager.MapPercentToFloatWidth(width);
		//Uses MapPercentToFloatWidth instead of MapPercentToFloatHeight to make it easier for users to estimate proportions
		glHeight = OGLManager.MapPercentToFloatWidth(height);
	}
	
	
	public int GetScreenWidth() 
	{
		return screenWidth;
	}
	public int GetScreenHeight() 
	{
		return screenHeight;
	}
	
	public int GetPercentWidth() 
	{
		return width;
	}
	public int GetPercentHeight() 
	{
		return height;
	}
	
	public float GetGLWidth() 
	{
		return glWidth;
	}
	public float GetGLHeight() 
	{
		return glHeight;
	}
	
	public void SetPercentWidth(int w)
	{
		this.width = w;
		screenWidth = OGLManager.MapPercentToScreenX(w);
		glWidth = OGLManager.MapPercentToFloatWidth(w);
	}
	
	public void SetPercentHeight(int h)
	{
		this.height = h;
		//Uses MapPercentToScreenX instead of MapPercentToScreenY to make it easier for users to estimate proportions
		screenHeight = OGLManager.MapPercentToScreenX(h);
		//Uses MapPercentToFloatWidth instead of MapPercentToFloatHeight to make it easier for users to estimate proportions
		glHeight = OGLManager.MapPercentToFloatWidth(h);
	}

	public boolean CheckCollision(int mouseX, int mouseY) 
	{
		if ( mouseX > screenX-screenWidth/2 && mouseX < screenX+screenWidth/2 &&
			 mouseY < screenY+screenHeight/2 && mouseY > screenY - screenHeight/2)
		{
			return true;
		}
		return false;
	}
	
}
