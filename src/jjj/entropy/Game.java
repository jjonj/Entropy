package jjj.entropy;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import jjj.entropy.Card.Facing;
import jjj.entropy.Card.Status;
import jjj.entropy.CardTemplate.CardRace;
import jjj.entropy.CardTemplate.CardType;
import jjj.entropy.classes.*;
import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.ui.*;
import com.jogamp.opengl.util.FPSAnimator;


public class Game implements GLEventListener  
{
	
    
	public static int mode;
    public static int modeNumber;
    
    private static GameState gamestate;
    
    private static Game instance = null;
	private int gameWidth, 
				gameHeight;
	private float aspectRatio;
	@SuppressWarnings("unused")
	private boolean fullscreen = false;
	@SuppressWarnings("unused")
	private boolean showFPS = false;
	private boolean loggedIn = false;
    private int realGameHeight;	//Used for calculating the real Y values
	
	    private FPSAnimator animator;
    private ByteArrayOutputStream FPSCounter;
	
	private Player neutralPlayer,
				   player1,
			       player2;

    private GLCanvas canvas;

    private Set<Card> cardsToRender;
   
    
    Deck buildingDeck;
    
    private int c = 0;
    private float rotator = 0.0f;
    
    

    public CardTemplate TinidQueen;	//Temporary
	private int gameID = -1;
	private int iteratingCardsToRender = 0;
    
    
    public static void InitSingleton(int width, int height, GLCanvas canvas)
    {
    	new Game(width, height, canvas);
    }
    
    public static Game GetInstance()
    {
    	if (instance != null)
    		return instance;
    	System.out.println("ERROR: Please call Game.InitSingleton in your intialization.");
    	System.exit(1);
    	return null;
    }
    
    protected Game(int width, int height, GLCanvas canvas)
    {
    	instance = this;
    	
    	this.canvas = canvas;
    	gameWidth = width;
    	gameHeight = height;
    	aspectRatio = (float)width/height;
    	
    	FPSCounter = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(FPSCounter);
        animator = new FPSAnimator(canvas, 60);
        animator.setUpdateFPSFrames(60, ps);
        animator.add(canvas);
        animator.start();

        cardsToRender = new HashSet<Card>();
    	
    	
        neutralPlayer = new Player(0, "Neutral", null, null);
        
  //  	NetworkManager.Connect("10.0.0.5", 11759);
    	NetworkManager.GetInstance().Connect("127.0.0.1", 54555);	//Temporary location
    } 

    @Override
	public void init(GLAutoDrawable gLDrawable) 
    {
    //	gl = gLDrawable.getGL().getGL2();
    	System.out.println("init() called");

    	OpenGL.gl = gLDrawable.getGL().getGL2();
    	
    	Texture.LoadTextureList();	//Simply loads a string array of texturepaths from file.
    	
    	Texture.InitTextures();
   		
   		
   		//Creating cards require a template, this is just an old template still used below
		TinidQueen = new CardTemplate((short)1, "Tinid Queen", CardRace.CRAWNID, CardType.CREATURE, (short)0,(short)0,(short)0,(short)0,(short)0,Texture.cardtestfront);

		// Easiest way atm to detect clicks on the deck pile, is to just place to cards there that cant move.
		Card card0 = new Card(-3, 0.51f, 1.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);
		Card card1 = new Card(3, 0.51f, 10.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);


		ShowCard(card0);
		ShowCard(card1);
       
        OpenGL.InitOpenGL();
     	
        
        UIManager.GetInstance().InitUIComponents();


     	SetGameState(Const.INIT_GAMESTATE);
     	
     	
     	//Setting the real window height as GL scaling changes it
     	int viewport[] = new int[4];
        OpenGL.gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        realGameHeight = viewport[3];
    }
 
    @Override
	public void display(GLAutoDrawable gLDrawable) 
    {
		OpenGL.gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);	//Switch to camera adjustment mode
    	OpenGL.gl.glLoadIdentity();
    	OpenGL.glu.gluPerspective(45, aspectRatio, 1, 100);
    	OpenGL.gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);	//Switch to hand adjustment mode
		
    	switch (gamestate)
    	{
    		case LOGIN:
    			
	    		OpenGL.gl.glLoadIdentity();   		
	    		OpenGL.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	OpenGL.gl.glLoadIdentity();
		    	OpenGL.gl.glTranslatef(0,0,-1);
	 	    	OpenGL.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	    	
	 	  //  	OpenGL.gl.glDisable(GL2.GL_DEPTH_TEST);
	 	    	OpenGL.gl.glEnable(GL2.GL_TEXTURE_2D);
	 	    	
	 	    	Texture.loginScreenTexture.bind(OpenGL.gl);
				 OpenGL.gl.glBegin(GL2.GL_QUADS);
				 	OpenGL.gl.glTexCoord2f(0.0f, 0.0f); OpenGL.gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(1.0f, 0.0f); OpenGL.gl.glVertex3f(0.74f,-0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(1.0f, 1.0f); OpenGL.gl.glVertex3f(0.74f,0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(0.0f, 1.0f); OpenGL.gl.glVertex3f(-0.74f,0.415f, 0f);
				 OpenGL.gl.glEnd();
				 
		         break;
    		case MAIN_MENU:

    			if (!loggedIn)	//If a player has just logged in
    			{
    				loggedIn = true;
    				OnLogin();
    			}
    			
	    		OpenGL.gl.glLoadIdentity();   		
	    		OpenGL.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	OpenGL.gl.glLoadIdentity();
		    	OpenGL.gl.glTranslatef(0,0,-1);
	 	    	OpenGL.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	    	
	 	  //  	OpenGL.gl.glDisable(GL2.GL_DEPTH_TEST);
	 	    	OpenGL.gl.glEnable(GL2.GL_TEXTURE_2D);
	 	    	
	 	    	Texture.mainMenuTexture.bind(OpenGL.gl);
				 OpenGL.gl.glBegin(GL2.GL_QUADS);
				 	OpenGL.gl.glTexCoord2f(0.0f, 0.0f); OpenGL.gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(1.0f, 0.0f); OpenGL.gl.glVertex3f(0.74f,-0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(1.0f, 1.0f); OpenGL.gl.glVertex3f(0.74f,0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(0.0f, 1.0f); OpenGL.gl.glVertex3f(-0.74f,0.415f, 0f);
				 OpenGL.gl.glEnd();

    			break;
    		case DECK_SCREEN:
    			
    			
    			//   -------------------------------------- LOAD ANY MISSING TEXTURES   ----------------------------------
    			

    			if (Const.INIT_GAMESTATE == GameState.LOGIN)	//Check that makes ingame debugging easier
    			{
	    			player1.GetAllCards().LoadTextures();
    			}
    			
    			OpenGL.gl.glLoadIdentity();   		
	    		OpenGL.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	OpenGL.gl.glLoadIdentity();
		    	OpenGL.gl.glTranslatef(0,0,-1);
	 	    	OpenGL.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	    	
	 	    	Texture.deckScreenTexture.bind(OpenGL.gl);
				 OpenGL.gl.glBegin(GL2.GL_QUADS);
				 	OpenGL.gl.glTexCoord2f(0.0f, 0.0f); OpenGL.gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(1.0f, 0.0f); OpenGL.gl.glVertex3f(0.74f,-0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(1.0f, 1.0f); OpenGL.gl.glVertex3f(0.74f,0.415f, 0f);
				 	OpenGL.gl.glTexCoord2f(0.0f, 1.0f); OpenGL.gl.glVertex3f(-0.74f,0.415f, 0f);
				 OpenGL.gl.glEnd();
		 	    	
			    
	 	    	break;
    		case IN_GAME:
    			
    			//   -------------------------------------- LOAD ANY MISSING TEXTURES   ----------------------------------
    			
    			
    			if (Const.INIT_GAMESTATE == GameState.LOGIN)	//Check that makes ingame debugging easier
    			{
	    			player1.GetActiveDeck().LoadTextures();
	    			player2.GetActiveDeck().LoadTextures();
    			}
    			
    			 //     ---------------------------------         INIT FRAME       ------------------------------------------

    	    	OpenGL.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	    	OpenGL.gl.glLoadIdentity();
    	    	OpenGL.glu.gluLookAt(	1, 5, -4,
    	     				0, 0f, 4,
    	     				0.0f, 1.0f,  0.0f);
    	    	OpenGL.gl.glTranslatef(0,0,5);
    	    	//OpenGL.gl.glRotatef(rotator, 0, 1, 0);
    	    	
    	    	OpenGL.gl.glPushMatrix();
    	    	 
    	    	 //     ---------------------------------         DRAW 3D LINES       ------------------------------------------
    	    	
    	    	 OpenGL.gl.glDisable(GL2.GL_TEXTURE_2D);     
    	    	
    	    	
    	 	    
    	    	 OpenGL.gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
    			 
    			 OpenGL.gl.glBegin(GL2.GL_LINES);
    			 
    			 OpenGL.gl.glVertex3f(-10.0f,0.0f,0.0f);
    			 OpenGL.gl.glVertex3f(10.0f,0.0f,0.0f);
    			 
    			 OpenGL.gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
    			 OpenGL.gl.glVertex3f(0.0f,10.0f,0.0f);
    			 OpenGL.gl.glVertex3f(0.0f,-10.0f,0.0f);
    			 
    			 OpenGL.gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
    			 	OpenGL.gl.glVertex3f(0.0f,0.0f,-10.0f);
    			 	OpenGL.gl.glVertex3f(0.0f,0.0f,10.0f);
    			 OpenGL.gl.glEnd();
    			   OpenGL.gl.glColor4f(1.0f, 1.0f, 1.0f, 1f);
    			 OpenGL.gl.glEnable(GL2.GL_TEXTURE_2D);     
    			 
    			 //     ---------------------------------         DRAW OTHERS       ------------------------------------------
    			 
    			 Texture.board.bind(OpenGL.gl);
    	    	 OpenGL.DrawTable(OpenGL.gl, -Const.BOARD_WIDTH/2, Const.BOARD_HEIGHT);
    	    	 OpenGL.DrawDeck(OpenGL.gl, -3.0f, 0.5f, 1.0f);
    	    	 OpenGL.DrawDeck(OpenGL.gl, 3.0f, 0.5f, 10.0f);
    	    	 
    			 OpenGL.gl.glPopMatrix();
    			 
    			 //     ---------------------------------        DRAW/UPDATE CARDS      ------------------------------------------
    			 ++iteratingCardsToRender;
    			 for(Card ca : cardsToRender)
    			 {
    				OpenGL.DrawCard(OpenGL.gl, OpenGL.glu, ca);
    				ca.Update();
    			 }
    			 --iteratingCardsToRender;
    			 
    			 //     ---------------------------------         DRAW UI       ------------------------------------------
    			 OpenGL.gl.glPushMatrix();
    			 OpenGL.gl.glLoadIdentity();
    			 Texture.uiTexture.bind(OpenGL.gl);
    			 OpenGL.gl.glBegin(GL2.GL_QUADS);
    			 	OpenGL.gl.glTexCoord2f(0.0f, 0.0f); OpenGL.gl.glVertex3f(-0.74f,-0.415f, -1);
    			 	OpenGL.gl.glTexCoord2f(1.0f, 0.0f); OpenGL.gl.glVertex3f(0.74f,-0.415f,-1);
    			 	OpenGL.gl.glTexCoord2f(1.0f, 1.0f); OpenGL.gl.glVertex3f(0.74f,-0.25f, -1);
    			 	OpenGL.gl.glTexCoord2f(0.0f, 1.0f); OpenGL.gl.glVertex3f(-0.74f,-0.25f, -1);
    			 OpenGL.gl.glEnd();
    			 OpenGL.gl.glPopMatrix();
    	 		 
    			 
    	    	 c++;
   
    	         if (c == 120)
    	 		 {
    	 			try {
    	 	    		UIManager.GetInstance().GetFPSLabel().SetText(FPSCounter.toString("UTF8"));
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
    	        
    	        OpenGL.gl.glEnable(GL2.GL_TEXTURE_2D);     
    			break;
    	}
    	
    	 //     ------------------------------------------          RENDER UI      ------------------------------------------------
    	
    	UIManager.GetInstance().RenderUI(this);
    	
    	
    	OpenGL.gl.glFlush();		
	}
    
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    }
 
    @Override
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
 
        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
        
    	int viewport[] = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        realGameHeight = viewport[3];
        
        int view[] = new int[4];
	    double model[] = new double[16];
	    double proj[] = new double[16];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, view, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, model, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, proj, 0);
    
		
		UIManager.GetInstance().OnResize(view, model, proj);
		

    }
 
    //openGL specific cleanup code
	@Override
	public void dispose(GLAutoDrawable arg0)
	{
		animator.stop();
	}

	//All non-openGL cleanup code
	public void Cleanup() {
		NetworkManager.GetInstance().Disconnect();
	}

    public void ShowCard(Card card)
	{
    	while (iteratingCardsToRender > 0)	//Busywait until the cards have been iterated so we don't get an exception from modifying while iterating
		{
		}
		cardsToRender.add(card);
		card.SetGLMIndex(cardsToRender.size()-1);
	}
	
	public void RemoveCard(Card card)
	{
		while (iteratingCardsToRender > 0)	//Busywait until the cards have been iterated so we don't get an exception from modifying while iterating
		{
		}
		cardsToRender.remove(card);
		
		/*cardsToRender.remove(card.GetGLMIndex());		LIST IMPLEMENTATION
		int decrementFrom = card.GetGLMIndex();
		card.SetGLMIndex(0);
		for (int i = decrementFrom; i < cardsToRender.size(); i++)
		{
			cardsToRender.get(i).SetGLMIndex(i);
		}*/
	}
    
	public void StartGame()
	{
		player1.GetActiveDeck().GameResetDeck();
		player2.GetActiveDeck().GameResetDeck();
		SetGameState(GameState.IN_GAME);
	}
	
	public Card CheckCardCollision()
	{
		//NOTE: Depth testing not tested, and only tests for card center
		Card rCard = null;
		
		double  px = EntMouseListener.MouseX, 
				py = (EntMouseListener.MouseY);
		++iteratingCardsToRender;
		for(Card ca : cardsToRender)
		{
			// This code was copied and modified with permission, i can't remember all the details (Work out later)
			// Each cards rectangle are divided into two rectangles that are checked for intersection with the mouse position
			double  x1 = ca.GetWinX(0),	// triangle vertice coordinates
					y1 = ca.GetWinY(0),
					x2 = ca.GetWinX(1),
					y2 = ca.GetWinY(1),
					x3 = ca.GetWinX(2),
					y3 = ca.GetWinY(2);
			for (int i = 0; i < 2; i++)	//Do for the two triangles
			{
				double dABx = x2-x1, 
					   dABy = y2-y1,
					   dBCx = x3-x2,
					   dBCy = y3-y2;
		        	
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
		--iteratingCardsToRender;

		return rCard;	// Returns null if no card was hit
	}
	
	

	public GameState GetGameState() {
		return gamestate;
	}
	
	public void SetGameState(GameState state) {
			
		gamestate = state;
		
		UIManager.GetInstance().SetFocusOnGameState(this);

		switch (gamestate)
		{
		case LOGIN:
			break;
		case MAIN_MENU:
			break;
		case IN_GAME:
			break;
		case DECK_SCREEN:
			OnDeckScreen();	//Call the method for 
			break;
		}

	}

	public int GetRealGameHeight() {
		return realGameHeight;
	}

	public Player GetPlayer(int i) 
	{
		switch (i)
		{
		case 0:
			return neutralPlayer;
		case 1:
			return player1;
		case 2:
			return player2;
		}
		return null;
	}
	

	public void SetPlayer(int i, Player player) 
	{
		switch (i)
		{
		case 1:
			player1 = player;
		case 2:
			player2 = player;
		}
	}
	
    public int GetWidth()
    {
    	return gameWidth;
    }
    
    public int GetHeight()
    {
    	return gameHeight;
    }

    public float GetAspectRatio()
    {
    	return aspectRatio;
    }
    
	

	
	public int GetGameID()
	{
		return gameID;
	}
	public void SetGameID(int gameID) {
		this.gameID = gameID;
	}

	public GLCanvas GetCanvas()
	{
		return canvas;
	}

	
	public void OnDeckScreen()
	{
		UIManager.GetInstance().GetPlayerDeckTable().SetDataSource(GetPlayer(1).GetActiveDeck());

		
	}

	public void OnLogin()
	{
		//Add the players deck to the dropdown of decks
		UIManager.GetInstance().GetPlayerDeckDropdown().SetDataSource(GetPlayer(1).GetAllDecks());
	}

	

	
}

