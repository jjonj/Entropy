package jjj.entropy;


import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import jjj.entropy.Card.Facing;
import jjj.entropy.Card.Status;
import jjj.entropy.CardTemplate.CardRace;
import jjj.entropy.CardTemplate.CardRarity;
import jjj.entropy.CardTemplate.CardType;
import jjj.entropy.shop.Shop;
import jjj.entropy.ui.*;


public class Game implements GLEventListener  
{
	
    
	public static int mode;
    public static int modeNumber;
    

    private static Game instance = null;
	private int gameWidth, 
				gameHeight;
	private float aspectRatio;
	@SuppressWarnings("unused")
	private boolean fullscreen = false;
	@SuppressWarnings("unused")
	private boolean showFPS = false;
    private int realGameHeight;	//Used for calculating the real Y values
	
    
    private GameState currentGameState,
    				  outerGameState;		//Used for exiting current gamestate
    
    private InGameState inGameState;
    private DeckScreen deckScreen;
    private LoginScreen loginScreen;
    private MainMenu mainMenu;
    private Shop shop;
    

    Match activeMatch = null;
    
    private GLCanvas canvas;
    
    
	private Player neutralPlayer,
				   player;
			   //    player2;


    private Deck buildingDeck;
    
   
    
    private List<OGLAction> glActionQueue;

    public CardTemplate TinidQueen;	//Temporary

  
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
    	this.canvas = canvas;
    	
    	instance = this;
    	
    	gameWidth = width;
    	gameHeight = height;
    	aspectRatio = (float)width/height;
    	
    	inGameState = new InGameState(canvas);
    	deckScreen = new DeckScreen();
    	loginScreen = new LoginScreen();
    	mainMenu = new MainMenu();
    	shop = new Shop();
    	
    	currentGameState = loginScreen;
    	
        
    	
        glActionQueue = new ArrayList<OGLAction>(4);
    	
        neutralPlayer = new Player(0, "Neutral", null, null);
    } 

    @Override
	public void init(GLAutoDrawable gLDrawable) 
    {
    	  //  	NetworkManager.Connect("10.0.0.5", 11759);
    	NetworkManager.GetInstance().Connect("127.0.0.1", 54555);	//Temporary location
    	
    //	gl = gLDrawable.getGL().getGL2();
    	System.out.println("init() called");

    	OGLManager.gl = gLDrawable.getGL().getGL2();
    	
    	Texture.LoadTextureList();	//Simply loads a string array of texturepaths from file.
    	
    	Texture.InitTextures();
   		
   		
   		//Creating cards require a template, this is just an old template still used below
		TinidQueen = new CardTemplate((short)1, "Tinid Queen", CardRace.CRAWNID, CardType.CREATURE, CardRarity.COMMON, (short)0,(short)0,(short)0,(short)0,(short)0,Texture.cardtestfront);

		// Easiest way atm to detect clicks on the deck pile, is to just place to cards there that cant move.
		Card card0 = new Card(-3, 0.51f, 1.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);
		Card card1 = new Card(3, 0.51f, 10.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);


		ShowCard(card0);
		ShowCard(card1);
       
        OGLManager.InitOpenGL();
     	
        
        UIManager.GetInstance().InitUIComponents(loginScreen, mainMenu, inGameState, deckScreen);


     	SetGameState(loginScreen);
     	
     	
     	//Setting the real window height as GL scaling changes it
     	int viewport[] = new int[4];
        OGLManager.gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        realGameHeight = viewport[3];
    }
 
    @Override
	public void display(GLAutoDrawable gLDrawable) 
    {
		OGLManager.gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);	//Switch to camera adjustment mode
    	OGLManager.gl.glLoadIdentity();
    	OGLManager.glu.gluPerspective(45, aspectRatio, 1, 100);
    	OGLManager.gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);	//Switch to hand adjustment mode
    	OGLManager.gl.glLoadIdentity();
    	OGLManager.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	

    	currentGameState.Draw();
    	
    	 //     ------------------------------------------          RENDER UI      ------------------------------------------------
    	
    	UIManager.GetInstance().RenderUI(this);
    	

    	for (OGLAction a : glActionQueue)	//Execute any tasks that need to be executed on the GL containing thread
    	{
    		a.Execute();
    	}
    	glActionQueue.clear();
    	
    	
    	OGLManager.gl.glFlush();		
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
	}

	//All non-openGL cleanup code
	public void Cleanup() 
	{
		NetworkManager.GetInstance().Disconnect();
	}

    public void ShowCard(Card card)
	{
    	inGameState.ShowCard(card);	//TODO: Proxy method bad?
	}
	
	public void RemoveCard(Card card)
	{
		inGameState.RemoveCard(card);
	}
    
	public void StartGame(int matchID, Player opponent, boolean player1Starts)
	{
		activeMatch = new Match(matchID, player, opponent, player1Starts);
		SetGameState(inGameState);
		
		glActionQueue.add(new OGLAction(){@Override
				public void Execute(){
					activeMatch.Start();			//starting the match requires loading of player textures, which require a GL context
		}});
		
	}
	
	public Card CheckCardCollision()
	{
		return inGameState.CheckCardCollision();
	}
	

	public GameState GetGameState() 
	{
		return currentGameState;
	}
	
	public void SetGameState(final GameState state) 
	{
			
		currentGameState = state;
		
		UIManager.GetInstance().SetFocusOnGameState(this);

		final Game game = this;
		
		glActionQueue.add(new OGLAction(){@Override
			public void Execute(){
				state.Activate(game);
		}});

	}

	public int GetRealGameHeight() 
	{
		return realGameHeight;
	}

	public Player GetPlayer()
	{
		return player;
	}
	
	/*
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
	}*/
	
	
	public void SetPlayer(Player player)
	{
		this.player = player;
	}
	
	
/*
	public void SetPlayer(int i, Player player) 
	{
		switch (i)
		{
		case 1:
			player = player;
		case 2:
			player2 = player;
		}
	}*/
	
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
    
	

	
	public int GetActiveMatchID()
	{
		return activeMatch.GetID();
	}
	/*
	public void SetGameID(int gameID) 
	{
		this.gameID = gameID;
	}*/


	

	public void Quit() 
	{
		System.exit(0);
	}

	public Match GetActiveMatch() 
	{
		return activeMatch;
	}

	public void ExitGameState() 
	{
		if (outerGameState == null)
			Quit();
		else
			currentGameState = outerGameState;
	}

	public boolean IsInGame() 
	{
		if (activeMatch == null)
			return false;
		return true;
	}

	
	public GameState GetInGameState()
	{
		return inGameState;
	}
	public GameState GetMainMenu() 
	{
		return mainMenu;
	}
	public GameState GetDeckScreen() 
	{
		return deckScreen;
	}

	public void AddMouseMotionListener(MouseMotionListener listener) 
	{
		canvas.addMouseMotionListener(listener);
	}

	public void AddMouseListener(MouseListener listener) 
	{
		canvas.addMouseListener(listener);
	}


	

	
}

