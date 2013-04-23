package jjj.entropy.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jjj.entropy.CardCollection;
import jjj.entropy.CardTemplate;
import jjj.entropy.Deck;
import jjj.entropy.EntMouseListener;
import jjj.entropy.Game;
import jjj.entropy.GameState;
import jjj.entropy.NetworkManager;
import jjj.entropy.Player;
import jjj.entropy.SimpleCollection;
import jjj.entropy.Texture;
import jjj.entropy.classes.Const;
import jjj.entropy.ui.EntFont.FontTypes;

public class UIManager 
{
	private class StateCompPair
	{
		public StateCompPair(GameState state, UIComponent component) 
		{
			this.state = state;
			this.component = component;
		}
		public GameState state;
		public UIComponent component;
	}
	
	
	//Singleton pattern without lazy initialization
	private static UIManager instance = new UIManager();

	public static UIManager GetInstance()
	{
		return instance;
	}
	
	protected UIManager(){}
	
	
	//Maximum number of gamestates set to 10 here (MONITOR)
	public HashMap<jjj.entropy.GameState, List<UIComponent>> GameStateUIComponenstMap = new HashMap<jjj.entropy.GameState, List<UIComponent>>(8);	
	
	private HashMap<jjj.entropy.GameState, UIComponent> defaultFocusedUIElementMap = new HashMap<jjj.entropy.GameState, UIComponent>(8);	
	
	//Used to avoid modifying uicomponents while they are getting looped through
	private List<StateCompPair> uiComponentsToAdd = new ArrayList<StateCompPair>();	
	    
	
    private List<UIComponent> IngameUIComponents = new ArrayList<UIComponent>();	
    private List<UIComponent> MainMenuUIComponents = new ArrayList<UIComponent>();	
    private List<UIComponent> LoginScreenUIComponents = new ArrayList<UIComponent>();	
    private List<UIComponent> DeckScreenUIComponents = new ArrayList<UIComponent>();	
    private List<UIComponent> ShopUIComponents = new ArrayList<UIComponent>();	
    
    private UIComponent focusedUIComponent;
    Table playerCardTable,
    		 playerDeckTable;
    Dropdown<Deck> playerDeckDropdown;
    private Label FPSLabel;
    private Textbox chatTextbox;
    private Label chatWindow;
    private Textbox usernameTextbox,
    				   passwordTextbox;
    
    
    private Label battleTokensLabel,
    			  goldTokensLabel;
    
    private SimpleCollection<TableRow> activeDataSource;	// The datasource currently being used for updating table data
    
    public void InitUIComponents(jjj.entropy.GameState loginScreen, jjj.entropy.GameState mainMenu, jjj.entropy.GameState inGameState, jjj.entropy.GameState deckScreen, jjj.entropy.GameState shop)
    {
    	
    	GameStateUIComponenstMap.put(loginScreen, LoginScreenUIComponents);
    	GameStateUIComponenstMap.put(mainMenu, MainMenuUIComponents);
    	GameStateUIComponenstMap.put(inGameState, IngameUIComponents);
    	GameStateUIComponenstMap.put(deckScreen, DeckScreenUIComponents);
    	GameStateUIComponenstMap.put(shop, ShopUIComponents);
    	
    	//Ingame UI components
     	
    	chatWindow = new Label(10, 80,  Const.CHAT_LINES, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE));
    	chatTextbox = new Textbox(20, 80, 15,  5, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE), null);
    	
    	IngameUIComponents.add(chatWindow);
    	IngameUIComponents.add(chatTextbox);
    	defaultFocusedUIElementMap.put(inGameState, chatTextbox);
    	FPSLabel = new Label(50, 150, "0", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14));
    	IngameUIComponents.add(FPSLabel);
    	
    	
    	//Main menu UI components
    	//-0.16f, 0.05f
     	MainMenuUIComponents.add(new Button(50, 35, 20, 6, "Multiplayer", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
	     				NetworkManager.GetInstance().JoinGame();
     				}
     			}
     	));
     	defaultFocusedUIElementMap.put(mainMenu, MainMenuUIComponents.get(MainMenuUIComponents.size()-1));	//Set the default uicomponent to the last added one (just above)
     	//-0.16f, -0.05f
     	MainMenuUIComponents.add(new Button(50, 47, 20, 6, "My decks", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     					Game.GetInstance().SetGameState(Game.GetInstance().GetDeckScreen());
     				}
     			}
     	));
//-0.16f, -0.15f
     	MainMenuUIComponents.add(new Button(50, 59, 20, 6, "Shop", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     					Game.GetInstance().SetGameState(Game.GetInstance().GetShop());
     				}
     			}
     	));
     
     	
     	//Shop UI components

     	defaultFocusedUIElementMap.put(shop, null);

     	battleTokensLabel = new Label(87, 11, "0", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black));
     	goldTokensLabel = new Label(87, 19, "0", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black));
     	ShopUIComponents.add(battleTokensLabel);
     	ShopUIComponents.add(goldTokensLabel);

     	
   
     	
     	//Deck screen UI components
     	
     	
     	
     	//Initiate the player card table UI element with an empty list of data. The players cards are added once logged in.
     	SimpleCollection<TableRow> tempDeck = new CardCollection();
     	
     	playerCardTable = new Table(13, 5, 23, 2, tempDeck, 20, deckScreen);
     	DeckScreenUIComponents.add(playerCardTable);
     	defaultFocusedUIElementMap.put(deckScreen, playerCardTable);
     	//The card table for the current deck
     	playerDeckTable = new Table(43, 33, 23, 2, tempDeck, 20, deckScreen);
     	DeckScreenUIComponents.add(playerDeckTable);
     	
    	//Dropdown initiated with a temporary data source that is updated on login ( game.OnLogin(); )
     	playerDeckDropdown = new Dropdown<Deck>(37, 28, 8, 2, new ArrayList<Deck>(),
				new UIAction() {@Override
				public void Activate(){
					Deck newActiveDeck = playerDeckDropdown.GetSelectedObject();
     				Game.GetInstance().GetPlayer().SetActiveDeck(newActiveDeck);
     				playerDeckTable.SetDataSource(newActiveDeck);
     				playerCardTable.UpdateData();
 				}});

     	DeckScreenUIComponents.add(playerDeckDropdown);

     	//-0.06f, 0.21f
      	DeckScreenUIComponents.add(new Button(48, 27, 12, 3, "Save deck", new EntFont(FontTypes.MainParagraph, Font.PLAIN, 16, Color.black), Texture.smallButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     					NetworkManager.GetInstance().SendDeckUpdate(Game.GetInstance().GetPlayer().GetActiveDeck());
     				}
     			}
     	));
     	
     	
     	//transfer card left arrow       -0.35f, -0.05f
     	DeckScreenUIComponents.add(new Button(28, 70, 3, 3, "", Texture.arrow1ButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     				
     				Player player = Game.GetInstance().GetPlayer();
 					Deck activeDeck = player.GetActiveDeck();
 					CardTemplate transferCard = (CardTemplate)playerDeckTable.GetSelectedObject();
 					if ( transferCard != null && activeDeck.GetCount(transferCard) > 0 )
 					{

     					activeDeck.ChangeCount(transferCard, -1);
     					
     					playerDeckTable.UpdateData();
     					playerCardTable.UpdateData();
 					}
 					
     				}
     			}
     	));
     	//transfer card right arrow -0.35f, -0.12f
     	DeckScreenUIComponents.add(new Button(28, 60, 3, 3, "", Texture.arrow2ButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     				
     					Player player = Game.GetInstance().GetPlayer();
     					Deck activeDeck = player.GetActiveDeck();
     					CardTemplate transferCard = (CardTemplate)playerCardTable.GetSelectedObject();
     					if ( (transferCard != null &&  (player.GetAllCards().GetCount(transferCard) - activeDeck.GetCount(transferCard)) > 0) )
     					{
     						if (activeDeck.Contains(transferCard))
         						activeDeck.ChangeCount(transferCard, 1);
         					else
         						activeDeck.AddCard(transferCard, 1);
         					
         					playerDeckTable.UpdateData();
         					playerCardTable.UpdateData();
     					}
     				}
     			}
     	));
     	
     	//Login screen UI components
     	
     	
        usernameTextbox = new Textbox(50, 40, 20, 4, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), Texture.textboxTexture);
     	passwordTextbox = new Textbox(50, 55, 20, 4, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), Texture.textboxTexture);
     	LoginScreenUIComponents.add(usernameTextbox);
     	defaultFocusedUIElementMap.put(loginScreen, usernameTextbox);
     	LoginScreenUIComponents.add(passwordTextbox);
     	LoginScreenUIComponents.add(new Label(50, 34, "Username", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	LoginScreenUIComponents.add(new Label(50, 49, "Password", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	//-0.155f, -0.155f
     	LoginScreenUIComponents.add(new Button(50, 68, 15, 5, "Login", new EntFont(FontTypes.MainParagraph, Font.BOLD, 22, Color.black), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     				NetworkManager.GetInstance().Login(usernameTextbox.GetText(), passwordTextbox.GetText());
 				}
 			}
     	));
     	


    }

	public void RenderUI(Game game) 
	{
		 for (UIComponent c : GameStateUIComponenstMap.get(game.GetGameState()))
         {
         	c.Render(game);
         }		
	}

	public Label GetFPSLabel() {
		return FPSLabel;
	}

	public void OnResize(int[] view, double[] model, double[] proj) 
	{
		EntFont.OnResize();
		
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
        for (UIComponent uic : ShopUIComponents)
        {
        	if (uic instanceof Clickable)
				((Clickable)uic).OnResize(view, model, proj);
        }
	}
	
	public void SetFocusOnGameState(Game game) 
	{

		SetFocusedUIComponent(game.GetGameState().GetDefaultFocusedUIElement());
		

	}
	
	public void SetBattleTokensLabel(Integer amount)
	{
		battleTokensLabel.SetText(amount.toString());
	}
	public void SetGoldTokensLabel(Integer amount)
	{
		goldTokensLabel.SetText(amount.toString());
	}
	
	
	public UIComponent CheckUICollision()
	{
		for (StateCompPair scp : uiComponentsToAdd)
			GameStateUIComponenstMap.get(scp.state).add(scp.component);
		
		List<UIComponent> toIterate = GameStateUIComponenstMap.get(Game.GetInstance().GetGameState());
		
		int mouseX = EntMouseListener.MouseX;
		int mouseY = EntMouseListener.MouseY;
		
		
		//First test for collision with the focused UI component, this helps as caching and also fixes a problem with clicking dropdown overlapping other clickable
		Clickable ecl = (Clickable)focusedUIComponent;
		if (focusedUIComponent != null)
		{
			if (ecl.CheckCollision(mouseX, mouseY))
			{
				return focusedUIComponent;
			}
				
		}
		
		
		//After check for collision with all components in the current context (toIterate)
		for (UIComponent uic : toIterate)
		{
			if (uic instanceof Clickable) 
			{
				ecl = (Clickable)uic;
				if (ecl.CheckCollision(mouseX, mouseY))
				{
					return uic;
				}
			}
		}
		return null;
	}
	
	
	public void AddUIComponent(GameState gamestate, UIComponent component)
	{
		uiComponentsToAdd.add(new StateCompPair(gamestate, component));
	}
	
	public Textbox GetChatTextbox() 
	{
		return chatTextbox;
	}
	
	public Label GetChatWindowlabel() 
	{
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

	public Dropdown<Deck> GetPlayerDeckDropdown() 
	{
		return playerDeckDropdown;
	}

	public SimpleCollection<TableRow> GetActiveDataSource() 
	{
		return activeDataSource;
	}

	public void SetActiveDataSource( SimpleCollection<TableRow> source) {
		activeDataSource = source;
	}

	public UIComponent GetDefaultFocusedUIElement(GameState gameState) 
	{
		return defaultFocusedUIElementMap.get(gameState);
	}

	
	
}
