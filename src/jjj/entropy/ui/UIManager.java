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
import jjj.entropy.ui.Button.ButtonSize;
import jjj.entropy.ui.EntFont.FontTypes;

public class UIManager 
{
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
     	
    	chatWindow = new Label(129, 116,  Const.CHAT_LINES, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE));
    	chatTextbox = new Textbox(-0.59f, -0.389f, 5,  10, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE), null);
    	
    	IngameUIComponents.add(chatWindow);
    	IngameUIComponents.add(chatTextbox);
    	defaultFocusedUIElementMap.put(inGameState, chatTextbox);
    	FPSLabel = new Label(50, 150, "0", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14));
    	IngameUIComponents.add(FPSLabel);
    	
    	
    	//Main menu UI components
    	
     	MainMenuUIComponents.add(new Button(-0.16f, 0.05f, 48, 15, "Multiplayer", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
	     				NetworkManager.GetInstance().JoinGame();
     				}
     			}
     	));
     	defaultFocusedUIElementMap.put(mainMenu, MainMenuUIComponents.get(MainMenuUIComponents.size()-1));	//Set the default uicomponent to the last added one (just above)
     	MainMenuUIComponents.add(new Button(-0.16f, -0.05f, 60, 22, "My decks", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     					Game.GetInstance().SetGameState(Game.GetInstance().GetDeckScreen());
     				}
     			}
     	));

     	MainMenuUIComponents.add(new Button(-0.16f, -0.15f, 80, 27, "Shop", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.orange), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     					Game.GetInstance().SetGameState(Game.GetInstance().GetShop());
     				}
     			}
     	));
     
     	
     	//Shop UI components

     	defaultFocusedUIElementMap.put(shop, null);

     	battleTokensLabel = new Label(1120, 638, "0", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black));
     	goldTokensLabel = new Label(1120, 580, "0", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black));
     	ShopUIComponents.add(battleTokensLabel);
     	ShopUIComponents.add(goldTokensLabel);
     	
     	
     	
     	//Deck screen UI components
     	
     	
     	
     	//Initiate the player card table UI element with an empty list of data. The players cards are added once logged in.
     	SimpleCollection<TableRow> tempDeck = new CardCollection();
     	
     	playerCardTable = new Table(-0.715f, 0.39f, 20, 21, tempDeck, 20, deckScreen);
     	DeckScreenUIComponents.add(playerCardTable);
     	defaultFocusedUIElementMap.put(deckScreen, playerCardTable);
     	//The card table for the current deck
     	playerDeckTable = new Table(-0.283f, 0.16f, 20, 12, tempDeck, 20, deckScreen);
     	DeckScreenUIComponents.add(playerDeckTable);
     	
    	//Dropdown initiated with a temporary data source that is updated on login ( game.OnLogin(); )
     	playerDeckDropdown = new Dropdown<Deck>(-0.275f, 0.21f, 12, 14, new ArrayList<Deck>(),
				new UIAction() {@Override
				public void Activate(){
					Deck newActiveDeck = playerDeckDropdown.GetSelectedObject();
     				Game.GetInstance().GetPlayer().SetActiveDeck(newActiveDeck);
     				playerDeckTable.SetDataSource(newActiveDeck);
     				playerCardTable.UpdateData();
 				}});

     	DeckScreenUIComponents.add(playerDeckDropdown);

     	
      	DeckScreenUIComponents.add(new Button(-0.06f, 0.21f, 25, -7, "Save deck", new EntFont(FontTypes.MainParagraph, Font.PLAIN, 16, Color.black), ButtonSize.SMALL, Texture.smallButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     					NetworkManager.GetInstance().SendDeckUpdate(Game.GetInstance().GetPlayer().GetActiveDeck());
     				}
     			}
     	));
     	
     	
     	//transfer card left arrow
     	DeckScreenUIComponents.add(new Button(-0.35f, -0.05f, 0, 0, "", ButtonSize.TINY_SQUARE, Texture.arrow1ButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     				
     				Player player = Game.GetInstance().GetPlayer();
 					Deck activeDeck = player.GetActiveDeck();
 					CardTemplate transferCard = (CardTemplate)playerDeckTable.GetSelectedObject();
 					if ( activeDeck.GetCount(transferCard) > 0 )
 					{

     					activeDeck.ChangeCount(transferCard, -1);
     					
     					playerDeckTable.UpdateData();
     					playerCardTable.UpdateData();
 					}
 					
     				}
     			}
     	));
     	//transfer card right arrow
     	DeckScreenUIComponents.add(new Button(-0.35f, -0.12f, 0, 0, "", ButtonSize.TINY_SQUARE, Texture.arrow2ButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     				
     					Player player = Game.GetInstance().GetPlayer();
     					Deck activeDeck = player.GetActiveDeck();
     					CardTemplate transferCard = (CardTemplate)playerCardTable.GetSelectedObject();
     					if ( (player.GetAllCards().GetCount(transferCard) - activeDeck.GetCount(transferCard)) > 0 )
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
     	
     	
        usernameTextbox = new Textbox(-0.155f, 0.05f, 15, 8, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), Texture.textboxTexture);
     	passwordTextbox = new Textbox(-0.155f, -0.06f, 15, 8, "", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black), Texture.textboxTexture);
     	LoginScreenUIComponents.add(usernameTextbox);
     	defaultFocusedUIElementMap.put(loginScreen, usernameTextbox);
     	LoginScreenUIComponents.add(passwordTextbox);
     	LoginScreenUIComponents.add(new Label(555, 415, "Username", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	LoginScreenUIComponents.add(new Label(555, 320, "Password", new EntFont(FontTypes.MainParagraph, Font.BOLD, 24, Color.black)));
     	
     	LoginScreenUIComponents.add(new Button(-0.155f, -0.155f, 85, 28, "Login", new EntFont(FontTypes.MainParagraph, Font.BOLD, 22, Color.black), Texture.bigButtonTexture,
     			new UIAction() {@Override
				public void Activate(){
     				NetworkManager.GetInstance().Login(usernameTextbox.GetText(), passwordTextbox.GetText());
     				//Game.GetInstance().SetGameState(GameState.MAIN_MENU);
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
	
	
	public UIComponent CheckUICollision() 	//MOVE COLLISION DETECTION LOGIC TO EntUIComponent
	{
		List<UIComponent> toIterate = null;
		
		
		toIterate = GameStateUIComponenstMap.get(Game.GetInstance().GetGameState());
		
		
		
		//First test for collision with the focused UI component, this helps as caching and also fixes a problem with clicking dropdown overlapping other clickable
		Clickable ecl = (Clickable)focusedUIComponent;
		if (focusedUIComponent != null)
		{
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
		return null;
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

	public Table GetPlayerDeckTable() 
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
