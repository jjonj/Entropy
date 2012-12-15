package jjj.entropy;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import jjj.entropy.ui.EntUIComponent;

public class OpenGLManager{

	
	private GL2 gl;
	private GLU glu;	//OpenGL utilities object
	
	private int gameWidth, 
				gameHeight;
	private float aspectRatio;
	
	
	private EntTexture backsideEntTexture;
	private Texture backsideTexture;
	private int glBacksideTexture;
	private int cardModel;
	String backsideTexturePath;
	
	private List<Card> cardsToRender;
	 
	Game game;
	
	
	
	public OpenGLManager(int gameWidth, int gameHeight, String backsideTexturePath)
	{
		this.gameWidth = gameWidth;
    	this.gameHeight = gameHeight;
    	aspectRatio = gameWidth/gameHeight;
    	
    	this.backsideTexturePath = backsideTexturePath;
    	
    	
    	cardsToRender = new ArrayList<Card>();
	}
	
	public void RegisterGame(Game game)
	{
		this.game = game;
	}
	
	public GL2 GetGL()
	{
		return gl;
	}
	
	
	public void InitTexture(Texture texture)
	{
		texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
     	texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
     	texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
     	texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	public void ShowCard(Card card)
	{
		cardsToRender.add(card);
		card.SetGLMIndex(cardsToRender.size()-1);
	}
	
	public void RemoveCard(Card card)
	{
		cardsToRender.remove(card.GetGLMIndex());
		int decrementFrom = card.GetGLMIndex();
		card.SetGLMIndex(0);
		for (int i = decrementFrom; i < cardsToRender.size(); i++)
		{
			cardsToRender.get(i).SetGLMIndex(i);
		}
	}
	
	
	public void init(GLAutoDrawable glDrawable) {
		gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		System.out.println("init() called");
        gl = glDrawable.getGL().getGL2();
        gl.glClearColor(0.9f, 0.78f, 0.6f, 1.0f);
        
      //  backsideEntTexture = new EntTexture(backsideTexturePath, this);
    	
        
        
        File img = new File(backsideTexturePath);
        try {
        	backsideTexture = TextureIO.newTexture(img, true);
    	} catch (GLException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
        InitTexture(backsideTexture);
        
     	gl.glMatrixMode(GL2.GL_PROJECTION);	//Switch to camera adjustment mode
    	gl.glLoadIdentity();
    	glu.gluPerspective(45, aspectRatio, 0.1, 100);
    	gl.glMatrixMode(GL2.GL_MODELVIEW);	//Switch to hand adjustment mode
		
    	gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
         
        gl.glEnable(GL2.GL_TEXTURE_2D);                            // Enable Texture Mapping ( NEW )
        gl.glDepthFunc(GL2.GL_LEQUAL);                             // The Type Of Depth Testing To Do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  // Really Nice Perspective Calculations
         
         
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
     	 
     	
     	
     	
     	cardModel = gl.glGenLists(1);
        
        gl.glNewList(cardModel, GL2.GL_COMPILE);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
	    // Front Face	
	        gl.glNormal3f(0, 0, 1);
		    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( Game.CARD_WIDTH, 0.0f,  0.0f);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.0f, 0.0f,  0.0f);
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.0f,  Game.CARD_HEIGHT, 0.0f);
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( Game.CARD_WIDTH,  Game.CARD_HEIGHT,  0.0f);
		gl.glEnd();   
	
		backsideTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		// Back Face
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( Game.CARD_WIDTH,  Game.CARD_HEIGHT, Game.CARD_THICKNESS);
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( Game.CARD_WIDTH, 0.0f, Game.CARD_THICKNESS);
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.0f, 0.0f, Game.CARD_THICKNESS);
		    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f,  Game.CARD_HEIGHT, Game.CARD_THICKNESS);
		gl.glEnd();   
		
	    /*tpTexture2.destroy(gl);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		 	gl.glBegin(GL2.GL_QUADS);
	   // Top Face
	    gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f, 0.0f);
	    gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
	    gl.glVertex3f( CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
	    gl.glVertex3f( CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f, 0.0f);
	    // Bottom Face
	    gl.glVertex3f(0.02f, 0.02f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f,  CARD_THICKNESS);
	    gl.glVertex3f(0.02f, 0.02f,  CARD_THICKNESS);
	    // Right face
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f,  CARD_THICKNESS);
	    // Left Face
        gl.glVertex3f(0.02f,0.02f, 0.0f);
        gl.glVertex3f(0.02f, 0.02f,  CARD_THICKNESS);
        gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
        gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f, 0.0f);
	  gl.glEnd();*/
    
	//    gl.glEnd();
        
        gl.glEndList();

	}

	
	public void display(GLAutoDrawable glDrawable) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	 	 
    	 gl.glLoadIdentity();
    	 
    	 glu.gluLookAt(	1f, 1f, 3f,
    				0, 0f, 0,
    				0.0f, 1.0f,  0.0f);
    	 
    	 gl.glDisable(GL2.GL_TEXTURE_2D);     
    	
 	    
    	 gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		 
		 gl.glBegin(GL2.GL_LINES);
		 
		 gl.glVertex3f(-10.0f,0.0f,0.0f);
		 gl.glVertex3f(10.0f,0.0f,0.0f);
		 
		 gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		 gl.glVertex3f(0.0f,10.0f,0.0f);
		 gl.glVertex3f(0.0f,-10.0f,0.0f);
		 
		 gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		 gl.glVertex3f(0.0f,0.0f,-10.0f);
		 gl.glVertex3f(0.0f,0.0f,10.0f);
		 gl.glEnd();
		 
		 gl.glEnable(GL2.GL_TEXTURE_2D);     
		 
    	 
	/*	 for(Card c : cardsToRender)
		 {
			 gl.glTranslatef(c.GetX(), c.GetY(), c.GetZ());
			 c.GetType().GetTexture().GetGLTexture().bind(gl);
			 gl.glCallList(cardModel);
			 gl.glLoadIdentity();
		 }
		 
    	 
    	 */
         
    	 
    	
        
        gl.glFlush();

        
	}

	
	public void dispose(GLAutoDrawable arg0) {
		/*animator.stop();
		glWindow.destroy();*/
	}

	

	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) 
	{
	 	System.out.println("reshape() called: x = "+x+", y = "+y+", width = "+w+", height = "+h);

        if (h <= 0) // avoid a divide by zero error!
        {
            h = 1;
        }
        
 
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, aspectRatio, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
		
	}

	
	
}
