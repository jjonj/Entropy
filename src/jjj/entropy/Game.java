package jjj.entropy;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.opengl.GLWindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import jjj.entropy.ui.EntFont;
import jjj.entropy.ui.EntLabel;
import jjj.entropy.ui.EntTextfield;
import jjj.entropy.ui.EntUIComponent;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
 
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.TextureIO.*;

public class Game implements GLEventListener  {

	
	
	
	
	private int gameWidth, 
				gameHeight;
	private float aspectRatio;
	private String title;
	private boolean fullscreen = false;
	private boolean showFPS = false;
    private GLU glu = new GLU();	//OpenGL utilities object
    private FPSAnimator animator;
    private ByteArrayOutputStream FPSCounter;
    private Panel panel;
    private GLCanvas canvas;
    private List<EntUIComponent> UIComponents;	
    private EntLabel FPSLabel;
    GL2 gl;
    private List<Card> cardsToRender;
	
    
    private GLHelper glHelper;
    
    private int cardModel;
    private int c = 0;
    private float rotator = 0.0f;
    
    private Texture cardtestfront; //The texture with transparent parts.
    private Texture cardBackside; //The texture with transparent parts.
    private Texture board;
    
    
    
    final static float CARD_HEIGHT = 1.5f;
    final static float CARD_WIDTH = 0.9f;
    final static float CARD_THICKNESS = 0.001f;
    
    final static float BOARD_LENGTH = 12.0f;
    final static float BOARD_WIDTH = 12.0f;
    final static float BOARD_HEIGHT = -0.001f;
    final static float BOARD_THICKNESS = 0.5f;
    
    // Temporary hack to get the game globally
    private static Game aGame;
    public static Game TEMP_GetAGame()
    {
    	return aGame;
    }
    
    
    Game(String title, int width, int height, FPSAnimator animator, ByteArrayOutputStream FPSCounter, Panel panel, GLCanvas canvas)
    {
    	
    	
    	//	START TESTING CODE
    	
    	//  END TESTING CODE
    	
    	this.panel = panel;
    	this.FPSCounter = FPSCounter;
    	this.animator = animator;
    	
    	glHelper = new GLHelper();
    	
    	aGame = this;	//TEMP TEMP TEMP
    	this.title = title;
    	gameWidth = width;
    	gameHeight = height;
    	aspectRatio = width/height;
    	cardsToRender = new ArrayList<Card>();
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
    
    public String GetTitle()
    {
    	return title;
    }
    public int GetWidth()
    {
    	return gameWidth;
    }
    public int GetHeight()
    {
    	return gameHeight;
    }
    
    public void init(GLAutoDrawable gLDrawable) 
    {
    	UIComponents = new ArrayList<EntUIComponent>();
    	
    	FPSLabel = new EntLabel(50, 50, "0", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14));
    	
    	UIComponents.add(new EntLabel(gameWidth / 2 - 120, gameHeight - 70, "Entropy", new EntFont(EntFont.FontTypes.MainTitle, Font.BOLD, 50)));
    	UIComponents.add(FPSLabel);
    	UIComponents.add(new EntTextfield(200, 200, canvas));
    	

     

           File img = glHelper.LoadTexture(gl, cardBackside, "resources/textures/card1.png");
        	File img2 = glHelper.LoadTexture(gl, cardtestfront, "resources/textures/backside.png");
        	File boardFile = glHelper.LoadTexture(gl, board, "resources/textures/board.jpg");
        	

           
   		try {
   			cardtestfront = TextureIO.newTexture(img, true);
   			cardBackside = TextureIO.newTexture(img2, true);
   			board = TextureIO.newTexture(boardFile, true);
   		} catch (GLException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
   		
 

		
		CardType TinidQueen = new CardType(cardtestfront);
		
		
		
		
		Card card1 = new Card(-0.5f, 0, 1.0f, 
				0f, -1f ,0f, 
				1, 0, 0, TinidQueen);
		
		
		Card card2 = new Card(0.5f, 0f, 3.0f, TinidQueen);
		Card card3 = new Card(1.5f, 0f, 3.0f, TinidQueen);
		Card card4 = new Card(2.5f, 0f, 3.0f, TinidQueen);

		Card card5 = new Card(0f, 2, 1.0f, 
				0f, 0f ,0f, 
				1, 0, 0, TinidQueen);
		Card card6 = new Card(1f, 2, 0.8f, 
				0.3f, 0f ,0f, 
				1, 0, 0, TinidQueen);
		Card card7 = new Card(-1f, 2, 0.8f, 
				-0.3f, 0f ,0f, 
				1, 0, 0, TinidQueen);

		ShowCard(card1);
		ShowCard(card2);
		ShowCard(card3);
		ShowCard(card4);
		ShowCard(card5);
		ShowCard(card6);
		ShowCard(card7);
		
    	System.out.println("init() called");
        gl = gLDrawable.getGL().getGL2();
        gl.glClearColor(0.9f, 0.78f, 0.6f, 1.0f);
        
     
    	gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
         
        gl.glEnable(GL2.GL_TEXTURE_2D);                            // Enable Texture Mapping ( NEW )
        gl.glDepthFunc(GL2.GL_LEQUAL);                             // The Type Of Depth Testing To Do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  // Really Nice Perspective Calculations
         
         
    	gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
    	glHelper.InitTexture(gl, cardBackside);
     	glHelper.InitTexture(gl, cardtestfront);
     	glHelper.InitTexture(gl, board);

     	glHelper.GenerateTable(gl, BOARD_WIDTH, BOARD_LENGTH, BOARD_THICKNESS);
        
     	
     	
     	cardModel = gl.glGenLists(1);
        
        gl.glNewList(cardModel, GL2.GL_COMPILE);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);

	    // Front Face	
        gl.glNormal3f(0, 0, 1);
	    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/2, -CARD_HEIGHT/2,  0.0f);
	    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, -CARD_HEIGHT/2,  0.0f);
	    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-CARD_WIDTH/2,  CARD_HEIGHT/2, 0.0f);
	    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/2,  CARD_HEIGHT/2,  0.0f);
	    
	gl.glEnd();   
	
	cardBackside.bind(gl);
	gl.glBegin(GL2.GL_QUADS);
		// Back Face
	    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/2,  CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/2,  -CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( -CARD_WIDTH/2, -CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2,  CARD_HEIGHT/2, CARD_THICKNESS);
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
    
	    gl.glEnd();
        
        gl.glEndList();

        
   

    }
 
    
   
    float x = 0;
    public void display(GLAutoDrawable gLDrawable) 
    {
    	gl.glMatrixMode(GL2.GL_PROJECTION);	//Switch to camera adjustment mode
    	gl.glLoadIdentity();
    	glu.gluPerspective(45, 1.6, 0.1, 100);
    	gl.glMatrixMode(GL2.GL_MODELVIEW);	//Switch to hand adjustment mode
		
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	gl.glLoadIdentity();
    	glu.gluLookAt(	-1, 5, -4,
     				0, 0f, 4,
     				0.0f, 1.0f,  0.0f);
    	gl.glTranslatef(0,0,5);
    	gl.glRotatef(rotator/2, 0, 1, 0);
    	
    	gl.glPushMatrix();
    	 
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
		   gl.glColor4f(1.0f, 1.0f, 1.0f, 1f);
		   
		 gl.glEnable(GL2.GL_TEXTURE_2D);     
		 
		 board.bind(gl);
    	 glHelper.DrawTable(gl, -BOARD_WIDTH/2, BOARD_HEIGHT);
    	 
		 
		 
		 gl.glPopMatrix();
		 gl.glPushMatrix();
	
		 for(Card ca : cardsToRender)
		 {
			 
			 gl.glPushMatrix();
			 
		//	 glu.gluLookAt(	rotator/100, rotator/100, -3,
	   // 				c.GetFX(), c.GetFY(), c.GetFZ(),
	   // 				0.0f, 1.0f,  0.0f);
			 
			
		//	 glu.gluLookAt(	2, 1, -3,
	    // 				0, 0f, 0,
	    // 				0.0f, 1.0f,  0.0f);
			 
			 gl.glTranslatef(ca.GetX(), ca.GetY(), ca.GetZ());
			
			 

			 
			 
			 if (ca.GetGLMIndex() == 0)
				 ca.SetFX(rotator/350);

			 

	
			 gl.glRotatef(ca.GetFX()*90, 0.0f, 1.0f, 0.0f);
			 gl.glRotatef(-ca.GetFY()*90, 1.0f, 0.0f, 0.0f);
			 gl.glRotatef(ca.GetFZ()*180, 0.0f, 1.0f, 0.0f);
		//	 gl.glRotatef(c.GetFX(), 1.0, 0.0, 0.0);
		
		
			 
			 ca.GetType().GetTexture().bind(gl);
			 gl.glCallList(cardModel);
			
			 gl.glPopMatrix();
		 }
		 gl.glPopMatrix();
 		 
    	 
    	 c++;
         
         for (EntUIComponent c : UIComponents)
         {
         	c.Render();
         }
         
         if (c == 120)
 		 {
 			try {
 	    		FPSLabel.SetText(FPSCounter.toString("UTF8"));
 			} catch (UnsupportedEncodingException e) {
 				
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 			FPSCounter.reset();
 			c = 0;
 		}
        rotator += 0.6;
        if (rotator >= 360f)
         	rotator = 0f;
        
        gl.glFlush();
    }
    
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    	//System.out.println("displayChanged called TEST TEST");
    }
 
  
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
    	System.out.println("reshape() called: x = "+x+", y = "+y+", width = "+width+", height = "+height);
        final GL2 gl = gLDrawable.getGL().getGL2();
 
        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
        
        final float h = (float) width / (float) height;
 
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
 
 
	public void dispose(GLAutoDrawable arg0) 
	{
		
		/* RUN THIS
		animator.stop();
		 Assert.assertFalse(animator.isAnimating());
		 Assert.assertFalse(animator.isStarted());
		 glWindow.destroy();
		 Assert.assertEquals(true,  AWTRobotUtil.waitForRealized(glWindow, false));*/
	}
	
	
	
	
	
	

}

