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

    	OGLManager.gl.glLoadIdentity();
    	OGLManager.gl.glTranslatef(0,0,-1);
    	OGLManager.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    	
  //  	OpenGL.gl.glDisable(GL2.GL_DEPTH_TEST);
    	OGLManager.gl.glEnable(GL2.GL_TEXTURE_2D);
    	
    	Texture.loginScreenTexture.bind(OGLManager.gl);
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

}
