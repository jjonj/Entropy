package jjj.entropy;


import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
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

import jjj.entropy.classes.Card;
import jjj.entropy.classes.CardTemplate;
import jjj.entropy.classes.Card.Facing;
import jjj.entropy.classes.Card.Status;
import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.classes.Player;
import jjj.entropy.ui.EntButton;
import jjj.entropy.ui.EntFont;
import jjj.entropy.ui.EntFont.FontTypes;
import jjj.entropy.ui.EntLabel;
//import jjj.entropy.ui.EntTextfield;
import jjj.entropy.ui.EntUIComponent;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
 
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.TextureIO.*;

public class Game implements GLEventListener  {
	
	final static float CARD_HEIGHT = 1.5f;
    final static float CARD_WIDTH = 0.9f;
    final static float CARD_THICKNESS = 0.001f;
    
    static float HALF_CARD_HEIGHT;
    static float HALF_CARD_WIDTH;
    
    public static final float BOARD_LENGTH = 12.0f;
    public static final float BOARD_WIDTH = 12.0f;
    public static final float BOARD_HEIGHT = -0.001f;
    public static final float BOARD_THICKNESS = 0.5f;

    public static final float BIG_BUTTON_WIDTH = 0.25f,
    					      BIG_BUTTON_HEIGHT = 0.075f;
    
    public static final int CHAT_LINE_WIDTH = 240;
    public static final int CHAT_LINES = 5;
    
    //Test var
    public static int mode;
    public static int modeNumber;
    
    public static GameState Gamestate = GameState.MAIN_MENU;
    
   
    
    
	private String title;
	private int gameWidth, 
				gameHeight;
	public static float AspectRatio;
	private boolean fullscreen = false;
	private boolean showFPS = false;
    public static int realGameHeight;	//Used for calculating the real Y values
	
	public static GL2 gl;
    public static GLU glu = new GLU();	//OpenGL utilities object
    private GLHelper glHelper;
    private FPSAnimator animator;
    private ByteArrayOutputStream FPSCounter;
	
	public static Player Player1,
						 Player2;
    
    private Panel panel;
    private GLCanvas canvas;
    private List<EntUIComponent> IngameUICompontents;	
    private List<EntUIComponent> MainMenuUICompontents;	
    private EntLabel FPSLabel;
    private List<Card> cardsToRender;
    
    private int c = 0;
    private float rotator = 0.0f;
    
    public Texture cardtestfront; //The texture with transparent parts.
    public Texture crawnidworkertexture; 
    public Texture cardBackside; //The texture with transparent parts.
    public Texture board;
    public Texture deckSideTexture;
    public Texture uiTexture;
    public Texture mainMenuTexture;
    public Texture bigButtonTexture;
    
    
    NetworkManager nwm;
    
    Game(String title, int width, int height, FPSAnimator animator, ByteArrayOutputStream FPSCounter, Panel panel, GLCanvas canvas)
    {
    	this.panel = panel;
    	this.FPSCounter = FPSCounter;
    	this.animator = animator;
    	glHelper = new GLHelper();
    	this.title = title;
    	gameWidth = width;
    	gameHeight = height;
    	AspectRatio = (float)width/height;
    	cardsToRender = new ArrayList<Card>();
    	HALF_CARD_HEIGHT = CARD_HEIGHT / 2;
    	HALF_CARD_WIDTH = CARD_WIDTH / 2;  
    	
    	
    	IngameUICompontents = new ArrayList<EntUIComponent>();
    	MainMenuUICompontents = new ArrayList<EntUIComponent>();
    	
    	Player1 = new Player();
    	Player2 = new Player();
    	
    	
    	nwm = new NetworkManager();
    	
  //  	nwm.Connect("10.0.0.5", 11759);
    	nwm.Connect("127.0.0.1", 54555);
    	
  //nwm.SendTextMessage("HELLO");
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
    
	//Depth testing not tested, and only tests for card center
	public Card CheckCardCollision()
	{
		Card rCard = null;
		
		double  px = (double)EntMouseListener.MouseX, 
				py = (double)(EntMouseListener.MouseY);	//calculating the real Y from the viewport value
		for(Card ca : cardsToRender)
		{
	//		System.out.println("Card coords: " + ca.GetWinX(0) + ", ")
			/*==============================================
			`Returns 1 if point is inside triangle, 0 if not
			`Point (px,py)
			`triangle (x1,y1),(x2,y2),(x3,y3)
			`==============================================*/
			double  x1 = ca.GetWinX(0),
					y1 = ca.GetWinY(0),
					x2 = ca.GetWinX(1),
					y2 = ca.GetWinY(1),
					x3 = ca.GetWinX(2),
					y3 = ca.GetWinY(2);
			for (int i = 0; i < 2; i++)	//Do for two triangles
			{
			
				double dABx = x2-x1, 
					   dABy = y2-y1,
					   dBCx = x3-x2,
					   dBCy = y3-y2;
		        	
			  // System.out.println("Card ID: " + ca.glMIndex + " - Point coords: " + px + ", " + py + " - " + "triangle coords: " + x1 + " " + y1 + ", " + x2 + " " + y2 + ", " + x3 + " " + y3);
			   if ((dABx*dBCy - dABy*dBCx) < 0)	//Clockwise
			   {
			      if (dABx*(py-y1) >= dABy*(px-x1)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if (dBCx*(py-y2) >= dBCy*(px-x2)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if ((x1-x3)*(py-y3) >= (y1-y3)*(px-x3)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			   }
			   else								// Counter clockwise
			   {
			      if (dABx*(py-y1) < dABy*(px-x1)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if (dBCx*(py-y2) < dBCy*(px-x2)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if ((x1-x3)*(py-y3) < (y1-y3)*(px-x3))
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			   }
			   
			   if (rCard == null)
			   {
				   rCard = ca;
			   }
			   else
			   {
				   if (ca.GetZ() < rCard.GetZ())
					   rCard = ca;
			   }
			   break;
			}
		}

		return rCard;	//no card found
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
    
    public GL2 GetGL()
    {
    	return gl;
    }
    public GLHelper getGLHelper() {
		return glHelper;
	}
	
    public CardTemplate TinidQueen;
    
    public void init(GLAutoDrawable gLDrawable) 
    {
    	 gl = gLDrawable.getGL().getGL2();
    	System.out.println("init() called");
    	
    	FPSLabel = new EntLabel(50, 150, "0", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14));
    	
    	
    
    	
    	
    	IngameUICompontents.add(EntKeyListener.GetChatTypelabel());
    	IngameUICompontents.add(EntKeyListener.GetChatWindowlabel());
    	
    //	UIComponents.add(new EntLabel(gameWidth / 2 - 120, gameHeight - 70, "Entropy", new EntFont(this, EntFont.FontTypes.MainTitle, Font.BOLD, 50)));
    	IngameUICompontents.add(FPSLabel);
 //   	IngameUICompontents.add(new EntTextfield(200, 200, canvas));
    	
   		try {
   			cardtestfront = TextureIO.newTexture(new File("resources/textures/card1.png"), true);
   			cardBackside = TextureIO.newTexture(new File("resources/textures/backside.png"), true);
   			board = TextureIO.newTexture(new File("resources/textures/board.jpg"), true);
   			crawnidworkertexture = TextureIO.newTexture(new File("resources/textures/card2.png"), true);
   			deckSideTexture = TextureIO.newTexture(new File("resources/textures/deckside.png"), true);
   			uiTexture = TextureIO.newTexture(new File("resources/textures/bottomPanel.png"), true);
   			mainMenuTexture = TextureIO.newTexture(new File("resources/textures/MainMenu.png"), true);
   			bigButtonTexture = TextureIO.newTexture(new File("resources/textures/BigButton.png"), true);
   		} catch (GLException e) {
   			e.printStackTrace();
   			System.exit(1);
   		} catch (IOException e) {
   			e.printStackTrace();
   			System.exit(1);
   		}
   		
   		
   		
		TinidQueen = new CardTemplate(cardtestfront, 2,2,2,2,2,2);
		CardTemplate CrawnidWorker = new CardTemplate(crawnidworkertexture, 2,2,2,2,2,2);
		
		Card card0 = new Card(-3, 0.51f, 1.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, Game.Player1);
		
		/*Card card1 = new Card(-0.5f, 0, 1.0f, 
				Facing.UP, TinidQueen, Status.IN_ZONE);
		
		Card card2 = new Card(0.5f, 0f, 3.0f, Facing.UP, TinidQueen, Status.IN_ZONE);
		Card card3 = new Card(1.5f, 0f, 3.0f, Facing.UP, CrawnidWorker, Status.IN_ZONE);
		Card card4 = new Card(2.5f, 0f, 3.0f, Facing.UP, CrawnidWorker, Status.IN_ZONE);

		Card card5 = new Card(0f, 2, 1.0f, 
				Facing.FRONT, TinidQueen, Status.IN_ZONE);
		Card card6 = new Card(1f, 2, 0.8f, 
				Facing.FRONT, CrawnidWorker, Status.IN_ZONE);
		Card card7 = new Card(-1f, 2, 0.8f, 
				Facing.FRONT, CrawnidWorker, Status.IN_ZONE);*/

		ShowCard(card0);
	/*	ShowCard(card1);
		ShowCard(card2);
		ShowCard(card3);
		ShowCard(card4);
		ShowCard(card5);
	    ShowCard(card6);
		ShowCard(card7);*/
		    	
       
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
     	glHelper.InitTexture(gl, crawnidworkertexture);
     	glHelper.InitTexture(gl, deckSideTexture);
     	glHelper.InitTexture(gl, board);
     	glHelper.InitTexture(gl, uiTexture);
     	glHelper.InitTexture(gl, mainMenuTexture);
     	glHelper.InitTexture(gl, bigButtonTexture);
     	
     	
     	glHelper.GenerateTable(gl, BOARD_WIDTH, BOARD_LENGTH, BOARD_THICKNESS);
     	glHelper.GenerateButtons(gl, bigButtonTexture);
     	glHelper.GenerateUI(gl, 0, 0, 0, uiTexture);
     	glHelper.GenerateDeck(gl, cardBackside, deckSideTexture, CARD_WIDTH, CARD_HEIGHT, 0.5f);

     	glHelper.GenerateCard(gl, cardBackside, CARD_WIDTH, CARD_HEIGHT, CARD_THICKNESS);
        
     	
     	MainMenuUICompontents.add(new EntButton(-0.16f, 0.05f, 48, -15, "Multiplayer", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), bigButtonTexture));
    	
    	
     	
     	int viewport[] = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        realGameHeight = viewport[3];
    }
 
    
   
    float x = 0;
    public void display(GLAutoDrawable gLDrawable) 
    {
    	
    	if (Gamestate == GameState.MAIN_MENU)
    	{
    		gl.glMatrixMode(GL2.GL_PROJECTION);	//Switch to camera adjustment mode
	    	gl.glLoadIdentity();
	    	glu.gluPerspective(45, AspectRatio, 1, 100);
	    	gl.glMatrixMode(GL2.GL_MODELVIEW);	//Switch to hand adjustment mode
			
    		gl.glLoadIdentity();   		
    		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    	gl.glLoadIdentity();
	    	gl.glTranslatef(0,0,-1);
 	    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
 	    	
 	  //  	gl.glDisable(GL2.GL_DEPTH_TEST);
 	    	gl.glEnable(GL2.GL_TEXTURE_2D);
 	    	
 	    	 mainMenuTexture.bind(gl);
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, 0f);
			 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f, 0f);
			 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,0.415f, 0f);
			 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,0.415f, 0f);
			 gl.glEnd();
			
 	    	
 	    	
			 
			 //     ---------------------------------         DRAW UI       ------------------------------------------
	         for (EntUIComponent c : MainMenuUICompontents)
	         {

	         	c.Render(this);
	         }
    	}
    	else if (Gamestate == GameState.IN_GAME)
    	{
	    	
	    	 //     ---------------------------------         INIT FRAME       ------------------------------------------
	    	
	    	gl.glMatrixMode(GL2.GL_PROJECTION);	//Switch to camera adjustment mode
	    	gl.glLoadIdentity();
	    	glu.gluPerspective(45, AspectRatio, 1, 100);
	    	gl.glMatrixMode(GL2.GL_MODELVIEW);	//Switch to hand adjustment mode
			
	    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    	gl.glLoadIdentity();
	    	glu.gluLookAt(	1, 5, -4,
	     				0, 0f, 4,
	     				0.0f, 1.0f,  0.0f);
	    	gl.glTranslatef(0,0,5);
	    	//gl.glRotatef(rotator, 0, 1, 0);
	    	
	    	gl.glPushMatrix();
	    	 
	    	 //     ---------------------------------         DRAW 3D LINES       ------------------------------------------
	    	
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
			 
			 //     ---------------------------------         DRAW OTHERS       ------------------------------------------
			 
			 board.bind(gl);
	    	 glHelper.DrawTable(gl, -BOARD_WIDTH/2, BOARD_HEIGHT);
	    	 glHelper.DrawDeck(gl, -3.0f, 0.5f, 1.0f);
	    	 
			 gl.glPopMatrix();
			 
			 //     ---------------------------------        DRAW/UPDATE CARDS      ------------------------------------------
			 
			 for(Card ca : cardsToRender)
			 {
				glHelper.DrawCard(gl, glu, ca);
				ca.Update();
			 }
			 
			 
			 //     ---------------------------------         DRAW UI       ------------------------------------------
			 gl.glPushMatrix();
			 gl.glLoadIdentity();
			 uiTexture.bind(gl);
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, -1);
			 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f,-1);
			 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,-0.25f, -1);
			 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,-0.25f, -1);
			 gl.glEnd();
			 gl.glPopMatrix();
	 		 
			 
	    	 c++;
	         for (EntUIComponent c : IngameUICompontents)
	         {
	         	c.Render(this);
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
	         
	         
	        //     ---------------------------------         EXTRA STUFF       ------------------------------------------
	        rotator += 0.01;
	        if (rotator >= 360f)
	         	rotator = 0f;
	        
	       
	       
	        //     ---------------------------------         2D CALCULATIONS      ------------------------------------------
	        /*
	        int x = EntMouseListener.MouseX, 
	        	y = EntMouseListener.MouseY;
	   //     System.out.println(x);
	   //     System.out.println(y);
	       
	        int viewport[] = new int[4];
	        double mvmatrix[] = new double[16];
	        double projmatrix[] = new double[16];
	        int realy = 0;// GL y coord pos
	        double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords
	        double wcoord2[] = new double[4];// wx, wy, wz;// returned xyz coords
	        
	        
	        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
	        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
	        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
	        // note viewport[3] is height of window in pixels 
	        realy = realGameHeight - (int) y - 1;
	    //    System.out.println("Coordinates at cursor are (" + x + ", " + realy);
	        
	        
	        
	        
	        glu.gluUnProject((double) x, (double) realy, 0.0, //
	            mvmatrix, 0,
	            projmatrix, 0, 
	            viewport, 0, 
	            wcoord, 0);
	
	        
	        glu.gluUnProject((double) x, (double) realy, 1.0, //
	            mvmatrix, 0,
	            projmatrix, 0,
	            viewport, 0, 
	            wcoord2, 0);
	        */
	      /*  gl.glBegin(GL2.GL_QUADS);
	        
	        	gl.glVertex3f(0.0f,0.0f,0.0f);
	        	gl.glVertex3f(0.3f,0.0f,0.0f);
	        	gl.glVertex3f(0.3f,0.3f,0.0f);
	        	gl.glVertex3f(0.0f,0.3f,0.0f);
	        gl.glEnd();*/
	        
	        
	        gl.glEnable(GL2.GL_TEXTURE_2D);     
	    	
    	}
        
        gl.glFlush();

	}
    
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    	//System.out.println("displayChanged called TEST TEST");
    }
 
  
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
    //	System.out.println("reshape() called: x = "+x+", y = "+y+", width = "+width+", height = "+height);
        final GL2 gl = gLDrawable.getGL().getGL2();
 
        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
        
        final float h = (float) width / (float) height;
 
      /*  gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();*/
    	int viewport[] = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        realGameHeight = viewport[3];
        
        int view[] = new int[4];
	    double model[] = new double[16];
	    double proj[] = new double[16];
		 Game.gl.glGetIntegerv(GL2.GL_VIEWPORT, view, 0);
		 Game.gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, model, 0);
		 Game.gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);
        
        for (EntUIComponent uic : MainMenuUICompontents)
        {
    		uic.OnResize(view, model, proj);
        }
        for (EntUIComponent uic : IngameUICompontents)
        {
    		uic.OnResize(view, model, proj);
        }
        
    }
 
 
	public void dispose(GLAutoDrawable arg0) 
	{
		animator.stop();
		/* RUN THIS
		
		 Assert.assertFalse(animator.isAnimating());
		 Assert.assertFalse(animator.isStarted());
		
		 Assert.assertEquals(true,  AWTRobotUtil.waitForRealized(glWindow, false));*/
	}


	public void Cleanup() {
		nwm.Disconnect();
	}


	public EntUIComponent CheckUICollision() {

		 
		
		if (Gamestate == GameState.MAIN_MENU)
		{
			for (EntUIComponent uic : MainMenuUICompontents)
			{
				if (uic instanceof EntButton) {
					EntButton btn = (EntButton)uic;
					float bx = btn.GetScreenX();
					float by = btn.GetScreenY();
					int bw = btn.GetWidth();
					int bh = btn.GetHeight();
					int my =EntMouseListener.MouseY; //720 - EntMouseListener.MouseY -1;
					if ( EntMouseListener.MouseX > btn.GetScreenX() )
					{
						if ( EntMouseListener.MouseX < btn.GetWidth()+btn.GetScreenX() )
						{
							if (my < by)
							{
								if (my > by - btn.GetHeight())
								{
									return uic;
								}
							}
						}
					}
				//	if (&&&& && )
				//	{
						
				//	}
				}
					
			}
		}
		return null;

	}


	public static void SetState(GameState state) {
		Gamestate = state;		
	}


	
}

