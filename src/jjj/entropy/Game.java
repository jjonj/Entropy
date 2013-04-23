package jjj.entropy;


import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import jjj.entropy.Card.Facing;
import jjj.entropy.Card.Status;
import jjj.entropy.CardTemplate.CardRace;
import jjj.entropy.CardTemplate.CardRarity;
import jjj.entropy.CardTemplate.CardType;
import jjj.entropy.messages.Purchase;
import jjj.entropy.messages.ShopDataMessage;
import jjj.entropy.shop.Shop;
import jjj.entropy.ui.*;


public class Game
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
	
    
    private GameState currentGameState;
    
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
    
     
        neutralPlayer = new Player(0, "Neutral", null, null);
        

    } 

   
    public void Init(int realGameHeight)
    {
    	this.realGameHeight = realGameHeight;
    
   	  //  	NetworkManager.Connect("10.0.0.5", 11759);
    	NetworkManager.GetInstance().Connect("127.0.0.1", 54555);	//Temporary location

    	
    	loginScreen = new LoginScreen(null);
    	mainMenu = new MainMenu(null);
    	inGameState = new InGameState(canvas, mainMenu);
    	deckScreen = new DeckScreen(mainMenu);
		shop = new Shop(mainMenu);
   
    	UIManager.GetInstance().InitUIComponents(loginScreen, mainMenu, inGameState, deckScreen, shop);

    	
		
		currentGameState = loginScreen;
		
	    SetGameState(loginScreen);
		
		//Creating cards require a template, this is just an old template still used below
		TinidQueen = new CardTemplate((short)1, "Tinid Queen", CardRace.CRAWNID, CardType.CREATURE, CardRarity.COMMON, (short)0,(short)0,(short)0,(short)0,(short)0,"TinidQueen");

		// Easiest way atm to detect clicks on the deck pile, is to just place to cards there that cant move.
		Card card0 = new Card(-3, 0.51f, 1.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);
		Card card1 = new Card(3, 0.51f, 10.0f, 
				Facing.DOWN, TinidQueen, Status.IN_ZONE, neutralPlayer);


		ShowCard(card0);
		ShowCard(card1);
		 
    }
    
    
    
    public void Update()
    {
    	
    	
    	
    }
    
    public void Draw()
    {
    	
    	currentGameState.Draw();
    	UIManager.GetInstance().RenderUI(this);
    }
    
    //Certain things will need to be recomputed upon resize of the game window. This method will automatically be called by the OGLManager
    public void HandleResize(int[] view, double[] model, double[] proj) 
	{
    	
		UIManager.GetInstance().OnResize(view, model, proj);
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
		
		OGLManager.QueueOGLAction(new OGLAction(){@Override
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
		state.GetExitState();
		currentGameState = state;
		
		UIManager.GetInstance().SetFocusOnGameState(this);

		final Game game = this;
		
		OGLManager.QueueOGLAction(new OGLAction(){@Override
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
		if (currentGameState.GetExitState() == null)
			Quit();
		else
			currentGameState = currentGameState.GetExitState();
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

	public GameState GetShop() 
	{
		return shop;
	}

	public void SetScreenDimensions(int width, int height, int realHeight) 
	{
		this.gameWidth = width;
		this.gameHeight = height;
		realGameHeight = realHeight;
	}

	public void FinalizePurchase(Purchase purchase) 
	{
		shop.FinalizePurchase(purchase);
	}

	public int GetGameWidth() 
	{
		return gameWidth;
	}
	public int GetGameHeight() 
	{
		return gameHeight;
	}

	public void LoadShopData(ShopDataMessage sdm) //TODO: Just a proxy method
	{
		shop.LoadShopData(sdm);		
	}
	


	

	
}

