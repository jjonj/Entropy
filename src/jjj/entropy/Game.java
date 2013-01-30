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
import jjj.entropy.ui.EntFont.FontTypes;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.*;


public class Game implements GLEventListener  {
	
	final static float CARD_HEIGHT = 1.5f;
    final static float CARD_WIDTH = 0.9f;
    final static float HALF_CARD_HEIGHT = CARD_HEIGHT/2;	//I don't trust the compiler to optimize this
    final static float HALF_CARD_WIDTH = CARD_WIDTH/2;
    final static float CARD_THICKNESS = 0.001f;
    
    public static final float BOARD_LENGTH = 12.0f;
    public static final float BOARD_WIDTH = 12.0f;
    public static final float BOARD_HEIGHT = -0.001f;
    public static final float BOARD_THICKNESS = 0.5f;

    public static final float BIG_BUTTON_WIDTH = 0.25f,
    					      BIG_BUTTON_HEIGHT = 0.075f;
    
    public static final float TABLE_WIDTH = 0.35f,
    						  TABLE_ROW_HEIGHT = 0.0233f,
    						  TABLE_ROW_HEIGHT_PX = 19;
    
    
    public static final float SCROLL_HANDLE_HEIGHT = 0.035f;
    public static final int SCROLL_HANDLE_HEIGHT_PX = 25;	// The height in pixels of the scrollbars handler
    
    public static final int TABLE_COLUMN_WIDTH_PX = 100;	//How much space each column has in a table. Should be editable FIX
    
    
    public static final int TEXTBOX_LINE_WIDTH = 205;
    
    public static final int CHAT_LINE_WIDTH = 240;
    public static final int CHAT_LINES = 5;
	public static final float TEXTBOX_HEIGHT = 0.05f,
							  TEXTBOX_WIDTH = 0.25f;
	
    
    
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
    private int realGameHeight;	//Used for calculating the real Y values
	
	public static GL2 gl;				//These are made 'public static' as they can be accessed that way regardless.
    public static GLU glu = new GLU();	//OpenGL utilities object
    private FPSAnimator animator;
    private ByteArrayOutputStream FPSCounter;
	
	private Player neutralPlayer,
				   player1,
			       player2;

    private GLCanvas canvas;
    private List<EntUIComponent> IngameUIComponents;	
    private List<EntUIComponent> MainMenuUIComponents;	
    private List<EntUIComponent> LoginScreenUIComponents;	
    private List<EntUIComponent> DeckScreenUIComponents;	
    private EntUIComponent focusedUIComponent;
    EntTable playerCardTable;
    private EntLabel FPSLabel;
    private Set<Card> cardsToRender;
    private EntTextbox chatTextbox;
    private EntLabel chatWindow;
    private EntTextbox usernameTextbox,
    				   passwordTextbox;
    
    private int c = 0;
    private float rotator = 0.0f;
    
    public Texture cardtestfront; 	//Temporary texture location.
    public Texture crawnidworkertexture; 
    public Texture cardBackside; 
    public Texture board;
    public Texture deckSideTexture;
    public Texture uiTexture;
    public Texture mainMenuTexture;
    public Texture deckScreenTexture;
    public Texture loginScreenTexture;
    public Texture bigButtonTexture;
    public Texture textboxTexture;

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
    	IngameUIComponents = new ArrayList<EntUIComponent>();
    	MainMenuUIComponents = new ArrayList<EntUIComponent>();
        LoginScreenUIComponents = new ArrayList<EntUIComponent>();
        DeckScreenUIComponents = new ArrayList<EntUIComponent>();
    	
        neutralPlayer = new Player(0, "Neutral", null);
        
  //  	NetworkManager.Connect("10.0.0.5", 11759);
    	NetworkManager.GetInstance().Connect("127.0.0.1", 54555);	//Temporary location
    } 

    public void init(GLAutoDrawable gLDrawable) 
    {
    	gl = gLDrawable.getGL().getGL2();
    	System.out.println("init() called");

    	TextureManager.LoadTextureList();	//Simply loads a string array of texturepaths from file.
    	
   		try {
   			cardtestfront = TextureIO.newTexture(new File("resources/textures/card1.png"), true);
   			cardBackside = TextureIO.newTexture(new File("resources/textures/backside.png"), true);
   			board = TextureIO.newTexture(new File("resources/textures/board.jpg"), true);
   			crawnidworkertexture = TextureIO.newTexture(new File("resources/textures/card2.png"), true);
   			deckSideTexture = TextureIO.newTexture(new File("resources/textures/deckside.png"), true);
   			uiTexture = TextureIO.newTexture(new File("resources/textures/bottomPanel.png"), true);
   			mainMenuTexture = TextureIO.newTexture(new File("resources/textures/MainMenu.png"), true);
   			deckScreenTexture = TextureIO.newTexture(new File("resources/textures/DeckScreen.png"), true);
   			loginScreenTexture = TextureIO.newTexture(new File("resources/textures/LoginScreen.png"), true);
   			bigButtonTexture = TextureIO.newTexture(new File("resources/textures/BigButton.png"), true);
   			textboxTexture = TextureIO.newTexture(new File("resources/textures/Textbox.png"), true);
   		} catch (GLException e) {
   			e.printStackTrace();
   			System.exit(1);
   		} catch (IOException e) {
   			e.printStackTrace();
   			System.exit(1);
   		}
   		
   		
   		//Creating cards require a template, this is just an old template still used below
		TinidQueen = new CardTemplate((short)1, "Tinid Queen", CardRace.CRAWNID, CardType.CREATURE, (short)0,(short)0,(short)0,(short)0,(short)0,(short)0,cardtestfront);

		// Easiest way atm to detect clicks on the deck pile, is to just place to cards there that cant move.
		Card card0 = new Card(-3, 0.51f, 1.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);
		Card card1 = new Card(3, 0.51f, 10.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);


		ShowCard(card0);
		ShowCard(card1);
       
        gl.glClearColor(0.9f, 0.78f, 0.6f, 1.0f);
        
    	gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
       
        gl.glEnable(GL2.GL_TEXTURE_2D);                            
        gl.glDepthFunc(GL2.GL_LEQUAL);                             // The Type Of Depth Testing To Do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  // Really Nice Perspective Calculations
         
         
    	gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
    	GLHelper.InitTexture(gl, cardBackside);
     	GLHelper.InitTexture(gl, cardtestfront);
     	GLHelper.InitTexture(gl, crawnidworkertexture);
     	GLHelper.InitTexture(gl, deckSideTexture);
     	GLHelper.InitTexture(gl, board);
     	GLHelper.InitTexture(gl, uiTexture);
     	GLHelper.InitTexture(gl, mainMenuTexture);
     	GLHelper.InitTexture(gl, deckScreenTexture);
     	GLHelper.InitTexture(gl, loginScreenTexture);
     	GLHelper.InitTexture(gl, bigButtonTexture);
     	GLHelper.InitTexture(gl, textboxTexture);

     	
     	//Calling generate methods that initiate the displaylists in openGL for fast rendering
     	GLHelper.GenerateTable(gl, BOARD_WIDTH, BOARD_LENGTH, BOARD_THICKNESS);
     	GLHelper.GenerateButtons(gl, bigButtonTexture);
     	GLHelper.GenerateUI(gl, 0, 0, 0, uiTexture);
     	GLHelper.GenerateDeck(gl, cardBackside, deckSideTexture, CARD_WIDTH, CARD_HEIGHT, 0.5f);
       	GLHelper.GenerateTextbox(gl, textboxTexture);
     	GLHelper.GenerateCard(gl, cardBackside, CARD_WIDTH, CARD_HEIGHT, CARD_THICKNESS);
        
     	
     	
    	chatWindow = new EntLabel(129, 116,  Game.CHAT_LINES, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE));
    	chatTextbox = new EntTextbox(-0.59f, -0.389f, 5,  10, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE), null);
     	
    	IngameUIComponents.add(chatWindow);
    	IngameUIComponents.add(chatTextbox);
    	FPSLabel = new EntLabel(50, 150, "0", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14));
    	IngameUIComponents.add(FPSLabel);
    	
     	MainMenuUIComponents.add(new EntButton(-0.16f, 0.05f, 48, 15, "Multiplayer", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), bigButtonTexture,
     			new UIAction() {public void Activate(){
	     				NetworkManager.GetInstance().JoinGame();
     				}
     			}
     	));

     	MainMenuUIComponents.add(new EntButton(-0.16f, -0.05f, 60, 22, "My decks", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), bigButtonTexture,
     			new UIAction() {public void Activate(){
	     				SetGameState(GameState.DECK_SCREEN);
     				}
     			}
     	));
     	
     	
        usernameTextbox = new EntTextbox(-0.155f, 0.05f, 15, 8, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), textboxTexture);
     	passwordTextbox = new EntTextbox(-0.155f, -0.06f, 15, 8, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), textboxTexture);
     	
     	/*
     	String[][] data = new String[8][];
     	
     	
     	String[] row0 = {"LOL1", "IDD1", "IDD1"};
     	String[] row1 = {"LOL2", "IDD2", "IDD2"};
     	String[] row2 = {"LOL3", "IDD3", "IDD3"};
     	String[] row3 = {"LOL4", "IDD4", "IDD4"};
     	String[] row4 = {"LOL5", "IDD5", "IDD5"};
     	String[] row5 = {"LOL6", "IDD3", "IDD3"};
     	String[] row6 = {"LOL7", "IDD4", "IDD4"};
     	String[] row7 = {"LOL8", "IDD5", "IDD5"};
     	
     	data[0] = row0;
     	data[1] = row1;
     	data[2] = row2;
     	data[3] = row3;
     	data[4] = row4;
    	data[5] = row5;
     	data[6] = row6;
     	data[7] = row7;*/
     	
     	
     	
     	//Initiate the player card table UI element with an empty list of data. The players cards are added once logged in.
     	List table = new ArrayList<IEntTableRow>();
     	playerCardTable = new EntTable(-0.73f, 0.4f, table, 20, GameState.DECK_SCREEN);
     	DeckScreenUIComponents.add(playerCardTable);
     	
     	//LoginScreenUIComponents.add(new EntTable<String>(0.1f, 0.2f, data,4));
     	
     	LoginScreenUIComponents.add(usernameTextbox);
     	LoginScreenUIComponents.add(passwordTextbox);
     	LoginScreenUIComponents.add(new EntLabel(555, 415, "Username", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	LoginScreenUIComponents.add(new EntLabel(555, 320, "Password", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	
     	LoginScreenUIComponents.add(new EntButton(-0.155f, -0.155f, 85, 28, "Login", new EntFont(FontTypes.MainParagraph, Font.BOLD, 22, Color.black), bigButtonTexture,
     			new UIAction() {public void Activate(){
     				NetworkManager.GetInstance().Login(usernameTextbox.GetText(), passwordTextbox.GetText());
     				//Game.GetInstance().SetGameState(GameState.MAIN_MENU);
 				}
 			}
     	));

     	
     
     	
     	SetGameState(Const.INIT_GAMESTATE);
     	
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
	 	    	
	 	    	loginScreenTexture.bind(gl);
				 gl.glBegin(GL2.GL_QUADS);
				 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,0.415f, 0f);
				 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,0.415f, 0f);
				 gl.glEnd();

				 //     ---------------------------------         DRAW UI       ------------------------------------------
		         for (EntUIComponent c : LoginScreenUIComponents)
		         {
		        	
		         	c.Render(this);
		         }		     
		         break;
    		case MAIN_MENU:

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
		         for (EntUIComponent c : MainMenuUIComponents)
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
	 	    	
		 	  	 deckScreenTexture.bind(gl);
				 gl.glBegin(GL2.GL_QUADS);
				 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f, 0f);
				 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,0.415f, 0f);
				 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,0.415f, 0f);
				 gl.glEnd();
		 	    	

			//	 float xCounter = -0.1f;
				gl.glPushMatrix();

				 gl.glTranslatef(-0.6f, -0.15f, 0);
				

    			for (Deck de : Game.GetInstance().GetPlayer(1).GetAllDecks())
				{
    				 cardBackside.bind(gl);
	   				 gl.glBegin(GL2.GL_QUADS);
		   			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/15, -CARD_HEIGHT/15,  0.0f);
					    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/15, -CARD_HEIGHT/15,  0.0f);
					    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-CARD_WIDTH/15,  CARD_HEIGHT/15, 0.0f);
					    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/15,  CARD_HEIGHT/15,  0.0f);
	   				 gl.glEnd();
				}
    			
    			 gl.glTranslatef(0f, 0.3f, 0);
 				

				 
			    for (Card ca : GetPlayer(1).GetAllCards())
			    {
			   // 	 ca.SetX(xCounter);
			    	 
					 
					 ca.GetTemplate().GetTexture().bind(gl);

					 gl.glTranslatef(0.15f, 0, 0);
					
					 
				     gl.glBegin(GL2.GL_QUADS);

			        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/15, -CARD_HEIGHT/15,  0.0f);
				    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/15, -CARD_HEIGHT/15,  0.0f);
				    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-CARD_WIDTH/15,  CARD_HEIGHT/15, 0.0f);
				    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/15,  CARD_HEIGHT/15,  0.0f);
				    
					gl.glEnd();   
					 
			  // 	xCounter += 0.05f;
			    }
			    gl.glPopMatrix();
			    
	 	    	 //     ---------------------------------         DRAW UI       ------------------------------------------
	 	    	for (EntUIComponent c : DeckScreenUIComponents)
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
    			 
    			 board.bind(gl);
    	    	 GLHelper.DrawTable(gl, -BOARD_WIDTH/2, BOARD_HEIGHT);
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
    			 uiTexture.bind(gl);
    			 gl.glBegin(GL2.GL_QUADS);
    			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.74f,-0.415f, -1);
    			 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.74f,-0.415f,-1);
    			 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.74f,-0.25f, -1);
    			 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.74f,-0.25f, -1);
    			 gl.glEnd();
    			 gl.glPopMatrix();
    	 		 
    			 
    	    	 c++;
    	         for (EntUIComponent ui : IngameUIComponents)
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
        
		for (EntUIComponent uic : LoginScreenUIComponents)	//Should probably just let a static list handle every EntClickable resize call
        {
			if (uic instanceof EntClickable)
				((EntClickable)uic).OnResize(view, model, proj);
        }
        for (EntUIComponent uic : MainMenuUIComponents)
        {
        	if (uic instanceof EntClickable)
				((EntClickable)uic).OnResize(view, model, proj);
        }
        for (EntUIComponent uic : IngameUIComponents)
        {
        	if (uic instanceof EntClickable)
				((EntClickable)uic).OnResize(view, model, proj);
        }
        for (EntUIComponent uic : DeckScreenUIComponents)
        {
        	if (uic instanceof EntClickable)
				((EntClickable)uic).OnResize(view, model, proj);
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
	
	public EntUIComponent CheckUICollision() 	//MOVE COLLISION DETECTION LOGIC TO EntUIComponent
	{
		if (gamestate == GameState.MAIN_MENU || gamestate == GameState.LOGIN)
		{
			List<EntUIComponent> toIterate = null;
			
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
			}
			
			for (EntUIComponent uic : toIterate) //MOVE COLLISION DETECTION LOGIC TO EntUIComponent polymophism duh
			{
				if (uic instanceof EntButton) {
					EntButton btn = (EntButton)uic;
					int mx = EntMouseListener.MouseX;
					int my = EntMouseListener.MouseY; //720 - EntMouseListener.MouseY -1;
					if ( mx > btn.GetScreenX() )
					{
						if ( mx < btn.GetScreenWidth()+btn.GetScreenX() )
						{
							if (my < btn.GetScreenY())
							{
								if (my > btn.GetScreenY() - btn.GetScreenHeight())
								{
									return uic;
								}
							}
						}
					}
				}
				else if (uic instanceof EntTextbox)
				{
					EntTextbox tbx = (EntTextbox)uic;
					int mx = EntMouseListener.MouseX;
					int my = EntMouseListener.MouseY; //720 - EntMouseListener.MouseY -1;
					if ( mx > tbx.GetScreenX() )
					{
						if ( mx < tbx.GetScreenWidth()+tbx.GetScreenX() )
						{
							if (my < tbx.GetScreenY())
							{
								if (my > tbx.GetScreenY() - tbx.GetScreenHeight())
								{
									return uic;
								}
							}
						}
					}
				}
				else if (uic instanceof EntTable)
				{
					EntTable tbl = (EntTable)uic;
					int mx = EntMouseListener.MouseX;
					int my = EntMouseListener.MouseY; //720 - EntMouseListener.MouseY -1;
					if ( mx > tbl.GetScreenX() )
					{
						if ( mx < tbl.GetScreenWidth()+tbl.GetScreenX() )
						{
							if (my < tbl.GetScreenY())
							{
								if (my > tbl.GetScreenY() - tbl.GetScreenHeight())
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
    
	public EntTextbox GetChatTextbox() {
		return chatTextbox;
	}
	
	public EntLabel GetChatWindowlabel() {
		return chatWindow;
	}
	
	public void SetFocusedUIComponent(EntUIComponent newFocus)
	{
		focusedUIComponent = newFocus;
	}
		
	public EntUIComponent GetFocusedUIComponent()
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
	public EntTable GetCardTable() 
	{
		return playerCardTable;
	}


	
}

