package jjj.entropy.shop;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import jjj.entropy.Game;
import jjj.entropy.GameState;
import jjj.entropy.NetworkManager;
import jjj.entropy.OGLManager;
import jjj.entropy.Texture;
import jjj.entropy.messages.Purchase;
import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class Shop implements GameState
{

	
	private List<ShopItem> items;
	private GameState exitState;

	public Shop(GameState exitState)
	{
		this.exitState = exitState;
		items = new ArrayList<ShopItem>();
	}


	@Override
	public void Activate(Game game) 
	{
		NetworkManager.GetInstance().SendPurchase(1, 1);
	}


	@Override
	public void Draw() 
	{
		OGLManager.gl.glLoadIdentity();
    	OGLManager.gl.glTranslatef(0,0,-1);
	    OGLManager.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    	
	    Texture.shopTexture.bind(OGLManager.gl);
		 OGLManager.gl.glBegin(GL2.GL_QUADS);
		 	OGLManager.gl.glTexCoord2f(0.0f, 0.0f); OGLManager.gl.glVertex3f(-0.74f,-0.415f, 0f);
		 	OGLManager.gl.glTexCoord2f(1.0f, 0.0f); OGLManager.gl.glVertex3f(0.74f,-0.415f, 0f);
		 	OGLManager.gl.glTexCoord2f(1.0f, 1.0f); OGLManager.gl.glVertex3f(0.74f,0.415f, 0f);
		 	OGLManager.gl.glTexCoord2f(0.0f, 1.0f); OGLManager.gl.glVertex3f(-0.74f,0.415f, 0f);
		 OGLManager.gl.glEnd();
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


	public void FinalizePurchase(Purchase purchase) 
	{
		//TODO: Implement
	}
	
	
}
