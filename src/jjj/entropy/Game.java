package jjj.entropy;


import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import jjj.entropy.Card.Facing;
import jjj.entropy.Card.Status;
import jjj.entropy.CardTemplate.CardRace;
import jjj.entropy.CardTemplate.CardType;
import jjj.entropy.classes.*;
import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.ui.*;
import jjj.entropy.ui.Button.ButtonSize;
import jjj.entropy.ui.EntFont.FontTypes;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.*;


public class Game implements GLEventListener  
{
	
    
	public static int mode;
    public static int modeNumber;
    
    private static GameState gamestate;
    
    private static Game instance = null;
	private String title;
	private int gameWidth, 
				gameHeight;
	private float aspectRatio;
	private boolean fullscreen = false;
	private boolean showFPS = false;
	private boolean loggedIn = false;
    private int realGameHeight;	//Used for calculating the real Y values
	
	public static GL2 gl;				//These are made 'public static' as they can be accessed that way regardless.
    public static GLU glu = new GLU();	//OpenGL utilities object
    private FPSAnimator animator;
    private ByteArrayOutputStream FPSCounter;
	
	private Player neutralPlayer,
				   player1,
			       player2;

    private GLCanvas canvas;
    private List<UIComponent> IngameUIComponents;	
    private List<UIComponent> MainMenuUIComponents;	
    private List<UIComponent> LoginScreenUIComponents;	
    private List<UIComponent> DeckScreenUIComponents;	
    private UIComponent focusedUIComponent;
    Table playerCardTable,
    		 playerDeckTable;
    Dropdown<Deck> playerDeckDropdown;
    private Label FPSLabel;
    private Set<Card> cardsToRender;
    private Textbox chatTextbox;
    private Label chatWindow;
    private Textbox usernameTextbox,
    				   passwordTextbox;
    
    Deck buildingDeck;
    
    private int c = 0;
    private float rotator = 0.0f;
    
    

    public CardTemplate TinidQueen;	//Temporary
	private int gameID = -1;
	private int iteratingCardsToRender = 0;
    
    
    public static void InitSingleton(String title, int width, int height, GLCanvas canvas)
    {
    	new Game(title, width, height, canvas);
    }
    
    public static Game GetInstance()
    {
    	if (instance != null)
    		return instance;
    	System.out.println("ERROR: Please call Game.InitSingleton in your intialization.");
    	System.exit(1);
    	return null;
    }
    
    protected Game(String title, int width, int height, GLCanvas canvas)
    {
    	instance = this;
    	
    	this.canvas = canvas;
    	this.title = title;
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
    	IngameUIComponents = new ArrayList<UIComponent>();
    	MainMenuUIComponents = new ArrayList<UIComponent>();
        LoginScreenUIComponents = new ArrayList<UIComponent>();
        DeckScreenUIComponents = new ArrayList<UIComponent>();
    	
        neutralPlayer = new Player(0, "Neutral", null);
        
  //  	NetworkManager.Connect("10.0.0.5", 11759);
    	NetworkManager.GetInstance().Connect("127.0.0.1", 54555);	//Temporary location
    } 

    public void init(GLAutoDrawable gLDrawable) 
    {
    	gl = gLDrawable.getGL().getGL2();
    	System.out.println("init() called");

    	TextureManager.LoadTextureList();	//Simply loads a string array of texturepaths from file.
    	
   		TextureManager.InitTextures();
   		
   		
   		//Creating cards require a template, this is just an old template still used below
		TinidQueen = new CardTemplate((short)1, "Tinid Queen", CardRace.CRAWNID, CardType.CREATURE, (short)0,(short)0,(short)0,(short)0,(short)0,(short)0,TextureManager.cardtestfront);

		// Easiest way atm to detect clicks on the deck pile, is to just place to cards there that cant move.
		Card card0 = new Card(-3, 0.51f, 1.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);
		Card card1 = new Card(3, 0.51f, 10.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);


		ShowCard(card0);
		ShowCard(card1);
       
        GLHelper.InitOpenGL();
     	
        
        //Ingame UI components
     	
    	chatWindow = new Label(129, 116,  Const.CHAT_LINES, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE));
    	chatTextbox = new Textbox(-0.59f, -0.389f, 5,  10, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE), null);
    	IngameUIComponents.add(chatWindow);
    	IngameUIComponents.add(chatTextbox);
    	
    	FPSLabel = new Label(50, 150, "0", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14));
    	IngameUIComponents.add(FPSLabel);
    	
    	
    	//Main menu UI components
    	
     	MainMenuUIComponents.add(new Button(-0.16f, 0.05f, 48, 15, "Multiplayer", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), TextureManager.bigButtonTexture,
     			new UIAction() {public void Activate(){
	     				NetworkManager.GetInstance().JoinGame();
     				}
     			}
     	));

     	MainMenuUIComponents.add(new Button(-0.16f, -0.05f, 60, 22, "My decks", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), TextureManager.bigButtonTexture,
     			new UIAction() {public void Activate(){
	     				SetGameState(GameState.DECK_SCREEN);
     				}
     			}
     	));
     	
     	
     	//Deck screen UI components
     	

       
     	
     	
     	//Initiate the player card table UI element with an empty list of data. The players cards are added once logged in.
     	List<TableRow> tempTable = new ArrayList<TableRow>();
     	playerCardTable = new Table(-0.715f, 0.39f, 20, 21, tempTable, 20, GameState.DECK_SCREEN);
     	DeckScreenUIComponents.add(playerCardTable);
     	
     	//The card table for the current deck
     	playerDeckTable = new Table(-0.283f, 0.16f, 20, 12, tempTable, 20, GameState.DECK_SCREEN);
     	DeckScreenUIComponents.add(playerDeckTable);
     	
    	//Dropdown initiated with a temporary data source that is updated on login ( game.OnLogin(); )
     	playerDeckDropdown = new Dropdown<Deck>(-0.275f, 0.21f, 12, 14, new ArrayList<Deck>(),
				new UIAction() {public void Activate(){
					Deck newActiveDeck = playerDeckDropdown.GetSelectedObject();
     				GetPlayer(1).SetActiveDeck(newActiveDeck);
     				
     				
     				List<TableRow> deckCards = new ArrayList<TableRow>();
     				//Add the newly created players cards to it
     				for (Card c : newActiveDeck)
     				{
     					deckCards.add(c);
     					System.out.println(c);
     				}
     				playerDeckTable.SetDataSource(deckCards);
 				}});
		
     	DeckScreenUIComponents.add(playerDeckDropdown);
     	
     	DeckScreenUIComponents.add(new Button(-0.35f, -0.05f, 0, 0, "", ButtonSize.TINY_SQUARE, TextureManager.arrow1ButtonTexture,
     			new UIAction() {public void Activate(){
	     				
     				}
     			}
     	));
     	DeckScreenUIComponents.add(new Button(-0.35f, -0.12f, 0, 0, "", ButtonSize.TINY_SQUARE, TextureManager.arrow2ButtonTexture,
     			new UIAction() {public void Activate(){
	     				
     				}
     			}
     	));
     	
     	

     	
     	//Login screen UI components
     	
     	
        usernameTextbox = new Textbox(-0.155f, 0.05f, 15, 8, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), TextureManager.textboxTexture);
     	passwordTextbox = new Textbox(-0.155f, -0.06f, 15, 8, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), TextureManager.textboxTexture);
     	LoginScreenUIComponents.add(usernameTextbox);
     	LoginScreenUIComponents.add(passwordTextbox);
     	LoginScreenUIComponents.add(new Label(555, 415, "Username", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	LoginScreenUIComponents.add(new Label(555, 320, "Password", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	
     	LoginScreenUIComponents.add(new Button(-0.155f, -0.155f, 85, 28, "Login", new EntFont(FontTypes.MainParagraph, Font.BOLD, 22, Color.black), TextureManager.bigButtonTexture,
     			new UIAction() {public void Activate(){
     				NetworkManager.GetInstance().Login(usernameTextbox.GetText(), passwordTextbox.GetText());
     				//Game.GetInstance().SetGameState(GameState.MAIN_MENU);
 				}
 			}
     	));


     	SetGameState(Const.INIT_GAMESTATE);
     	
     	
     	//Setting the real window height as GL scaling changes it
     	int viewport[] = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        realGameHeight = viewport[3];
    }
 
    public void display(GLAutoDrawable gLDrawable) 
    {
		gl.glMatrixMode(GL2.GL_PROJECTION);	//Switch to camera adjustment mode
    	gl.glLoadIdentity();
    	glu.gluPerspective(45, aspectRatio, 1, 100);
    	gl.glMatrixMode(GL2.GL_MODELVIEW);	//Switch to hand adjustment mode
		
    	switch (gamestate)
    	{
    		case LOGIN:
    			
	    		gl.glLoadIdentity();   		
	    		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	gl.glLoadIdentity();
		    	gl.glTranslatef(0,0,-1);
	 	    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	    	
	 	  //  	gl.glDisable(GL2.GL_DEPTH_TEST);
	 	    	gl.glEnable(GL2.GL_TEXTURE_2D);
	 	    	
	 	    	TextureManager.loginScreenTexture.bind(gl);
				 gl.glBegin(GL2.GL_QUADS);
				 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,0.415f, 0f);
				 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,0.415f, 0f);
				 gl.glEnd();

				 //     ---------------------------------         DRAW UI       ------------------------------------------
		         for (UIComponent c : LoginScreenUIComponents)
		         {
		        	
		         	c.Render(this);
		         }		     
		         break;
    		case MAIN_MENU:

    			if (!loggedIn)	//If a player has just logged in
    			{
    				loggedIn = true;
    				OnLogin();
    			}
    			
	    		gl.glLoadIdentity();   		
	    		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	gl.glLoadIdentity();
		    	gl.glTranslatef(0,0,-1);
	 	    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	    	
	 	  //  	gl.glDisable(GL2.GL_DEPTH_TEST);
	 	    	gl.glEnable(GL2.GL_TEXTURE_2D);
	 	    	
	 	    	TextureManager.mainMenuTexture.bind(gl);
				 gl.glBegin(GL2.GL_QUADS);
				 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,0.415f, 0f);
				 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,0.415f, 0f);
				 gl.glEnd();

				 //     ---------------------------------         DRAW UI       ------------------------------------------
		         for (UIComponent c : MainMenuUIComponents)
		         {
		         	c.Render(this);
		         }
    			break;
    		case DECK_SCREEN:
    			
    		
    			
    			
    			//   -------------------------------------- LOAD ANY MISSING TEXTURES   ----------------------------------
    			

    			if (Const.INIT_GAMESTATE == GameState.LOGIN)	//Check that makes ingame debugging easier
    			{
	    			player1.GetAllCards().LoadTextures(gl);
    			}
    			
    			gl.glLoadIdentity();   		
	    		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	gl.glLoadIdentity();
		    	gl.glTranslatef(0,0,-1);
	 	    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	 	    	
	 	    	TextureManager.deckScreenTexture.bind(gl);
				 gl.glBegin(GL2.GL_QUADS);
				 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,0.415f, 0f);
				 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,0.415f, 0f);
				 gl.glEnd();
		 	    	
			    
	 	    	 //     ---------------------------------         DRAW UI       ------------------------------------------
	 	    	for (UIComponent c : DeckScreenUIComponents)
		        {
		        	c.Render(this);
		        }
	 	    	break;
    		case IN_GAME:
    			
    			//   -------------------------------------- LOAD ANY MISSING TEXTURES   ----------------------------------
    			
    			
    			if (Const.INIT_GAMESTATE == GameState.LOGIN)	//Check that makes ingame debugging easier
    			{
	    			player1.GetActiveDeck().LoadTextures(gl);
	    			player2.GetActiveDeck().LoadTextures(gl);
    			}
    			
    			 //     ---------------------------------         INIT FRAME       ------------------------------------------

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
    			 
    			 TextureManager.board.bind(gl);
    	    	 GLHelper.DrawTable(gl, -Const.BOARD_WIDTH/2, Const.BOARD_HEIGHT);
    	    	 GLHelper.DrawDeck(gl, -3.0f, 0.5f, 1.0f);
    	    	 GLHelper.DrawDeck(gl, 3.0f, 0.5f, 10.0f);
    	    	 
    			 gl.glPopMatrix();
    			 
    			 //     ---------------------------------        DRAW/UPDATE CARDS      ------------------------------------------
    			 ++iteratingCardsToRender;
    			 for(Card ca : cardsToRender)
    			 {
    				GLHelper.DrawCard(gl, glu, ca);
    				ca.Update();
    			 }
    			 --iteratingCardsToRender;
    			 
    			 //     ---------------------------------         DRAW UI       ------------------------------------------
    			 gl.glPushMatrix();
    			 gl.glLoadIdentity();
    			 TextureManager.uiTexture.bind(gl);
    			 gl.glBegin(GL2.GL_QUADS);
    			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, -1);
    			 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f,-1);
    			 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,-0.25f, -1);
    			 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,-0.25f, -1);
    			 gl.glEnd();
    			 gl.glPopMatrix();
    	 		 
    			 
    	    	 c++;
    	         for (UIComponent ui : IngameUIComponents)
    	         {
    	         	 ui.Render(this);
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
    	        
    	        gl.glEnable(GL2.GL_TEXTURE_2D);     
    			break;
    	}
    	gl.glFlush();		
	}
    
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    }
 
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
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, model, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);
        
		for (UIComponent uic : LoginScreenUIComponents)	//Should probably just let a static list handle every EntClickable resize call
        {
			if (uic instanceof Clickable)
				((Clickable)uic).OnResize(view, model, proj);
        }
        for (UIComponent uic : MainMenuUIComponents)
        {
        	if (uic instanceof Clickable)
				((Clickable)uic).OnResize(view, model, proj);
        }
        for (UIComponent uic : IngameUIComponents)
        {
        	if (uic instanceof Clickable)
				((Clickable)uic).OnResize(view, model, proj);
        }
        for (UIComponent uic : DeckScreenUIComponents)
        {
        	if (uic instanceof Clickable)
				((Clickable)uic).OnResize(view, model, proj);
        }
    }
 
    //openGL specific cleanup code
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
		
		double  px = (double)EntMouseListener.MouseX, 
				py = (double)(EntMouseListener.MouseY);
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
	
	public UIComponent CheckUICollision() 	//MOVE COLLISION DETECTION LOGIC TO EntUIComponent
	{
		if (gamestate != GameState.IN_GAME)
		{
			List<UIComponent> toIterate = null;
			
			switch (gamestate)
			{
			case MAIN_MENU:
				toIterate = MainMenuUIComponents;
				break;
			case LOGIN:
				toIterate = LoginScreenUIComponents;
				break;
			case IN_GAME:
				toIterate = IngameUIComponents;
			case DECK_SCREEN:
				toIterate = DeckScreenUIComponents;
			}
			
			//First test for collision with the focused UI component, this helps as caching and also fixes a problem with clicking dropdown overlapping other clickable
			Clickable ecl = (Clickable)focusedUIComponent;
			int mx = EntMouseListener.MouseX;
			int my = EntMouseListener.MouseY; //720 - EntMouseListener.MouseY -1;
			if ( mx > ecl.GetScreenX() )
			{
				if ( mx < ecl.GetScreenWidth()+ecl.GetScreenX() )
				{
					if (my < ecl.GetScreenY())
					{
						if (my > ecl.GetScreenY() - ecl.GetScreenHeight())
						{
							return focusedUIComponent;
						}
					}
				}
			}
			//After check for collision with all components in the current context (toIterate)
			for (UIComponent uic : toIterate)
			{
				if (uic instanceof Clickable) 
				{
					ecl = (Clickable)uic;
					if ( mx > ecl.GetScreenX() )
					{
						if ( mx < ecl.GetScreenWidth()+ecl.GetScreenX() )
						{
							if (my < ecl.GetScreenY())
							{
								if (my > ecl.GetScreenY() - ecl.GetScreenHeight())
								{
									return uic;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	public GameState GetGameState() {
		return gamestate;
	}
	
	public void SetGameState(GameState state) {
		gamestate = state;		
		switch (gamestate)
		{
		case LOGIN:
			SetFocusedUIComponent(LoginScreenUIComponents.get(0));
			break;
		case MAIN_MENU:
			SetFocusedUIComponent(MainMenuUIComponents.get(0));
			break;
		case IN_GAME:
			SetFocusedUIComponent(IngameUIComponents.get(0));
			break;
		case DECK_SCREEN:
			SetFocusedUIComponent(DeckScreenUIComponents.get(0));
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
    
	public Textbox GetChatTextbox() {
		return chatTextbox;
	}
	
	public Label GetChatWindowlabel() {
		return chatWindow;
	}
	
	public void SetFocusedUIComponent(UIComponent newFocus)
	{
		focusedUIComponent = newFocus;
	}
		
	public UIComponent GetFocusedUIComponent()
	{
		return focusedUIComponent;
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

	
	//Returns the UI EntTable object that manages the players cards
	public Table GetCardTable() 
	{
		return playerCardTable;
	}
	
	//Returns the UI EntTable object that manages the players cards
	public Table GetDeckTable() 
	{
		return playerDeckTable;
	}
	
	
	
	public void OnDeckScreen()
	{
		buildingDeck = GetPlayer(1).GetActiveDeck();
		
		
		List<TableRow> deckCards = new ArrayList<TableRow>();
		//Add the newly created players cards to it
		
		for (Card c : buildingDeck)
		{
		
			deckCards.add(c);
		
		}
		playerDeckTable.SetDataSource(deckCards);
		
	}

	public void OnLogin()
	{
		//Add the players deck to the dropdown of decks
		playerDeckDropdown.SetDataSource(GetPlayer(1).GetAllDecks());
	}
	
}

