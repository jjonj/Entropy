package jjj.entropy.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import jjj.entropy.CardCollection;
import jjj.entropy.CardTemplate;
import jjj.entropy.Game;
import jjj.entropy.GameState;
import jjj.entropy.NetworkManager;
import jjj.entropy.OGLAction;
import jjj.entropy.OGLManager;
import jjj.entropy.Texture;
import jjj.entropy.CardTemplate.CardRace;
import jjj.entropy.CardTemplate.CardRarity;
import jjj.entropy.CardTemplate.CardType;
import jjj.entropy.classes.Const;
import jjj.entropy.messages.CardDataMessage;
import jjj.entropy.messages.Purchase;
import jjj.entropy.messages.ShopDataMessage;
import jjj.entropy.shop.ShopItem.ShopItemType;
import jjj.entropy.ui.Preview;
import jjj.entropy.ui.UIAction;
import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class Shop implements GameState
{

	private HashMap<Integer, ShopItem> items;
	
	private GameState exitState;
	private final int DisplayGridWidth;
	
	private static int nextPurchaseID = 0;
	private static HashMap<Integer, ShopItem> purchases = new HashMap<Integer, ShopItem>();	//PurchaseID --> ShopItem
	
	public Shop(GameState exitState)
	{
		this.exitState = exitState;
		items = new HashMap<Integer, ShopItem>();
		
		DisplayGridWidth = Const.SHOP_DISPLAYGRID_WIDTH;
	}

	@Override
	public void Activate(Game game) 
	{
	}


	@Override
	public void Draw() 
	{
	    Texture.shopTexture.bind(OGLManager.gl);
	    OGLManager.DrawScreen();
	}

	public void LoadShopData(final ShopDataMessage sdm) 
	{
		final Shop shop = this;
		OGLManager.QueueOGLAction(new OGLAction(){
			@Override public void Execute() {
				Texture t;
				int i = 0;
				for(String item : sdm.shopItems)
				{
					
					int X = 20 + 22 * (i % DisplayGridWidth);
					int	Y = 37 + 15 * (i / DisplayGridWidth);
					// Encoding: itemID;itemType;itemName;itemDescription;costBT;costGT
					final String[] data = item.split(";");
	
					try {
						t = Texture.Load(new File("resources/textures/items/"+data[2].replaceAll(" ", "")+".png"), true); //TODO: Perhaps preload?
						items.put(Integer.parseInt(data[0]), new ShopItem(Integer.parseInt(data[0]), ShopItemType.GetType(Integer.parseInt(data[1])),data[2], Integer.parseInt(data[4]), Integer.parseInt(data[5]), t));
						UIManager.GetInstance().AddUIComponent(shop, new Preview(X, Y, 20, 9, data[2],  data[3], t, new UIAction() {@Override
							public void Activate(){
								if (Game.GetInstance().GetPlayer().GetBattleTokens() >= Integer.parseInt(data[4]))//TODO: Not always iron price
									NetworkManager.GetInstance().SendPurchase(Integer.parseInt(data[0]), shop.RegisterPurchase(Integer.parseInt(data[0])), true);	//TODO: Not always iron price
	     				
						}}));
						
					} catch (GLException | IOException e) {
						e.printStackTrace();
						Game.GetInstance().Quit();
					}	
					
					i++;
				
				}
		
		
			}});
	}

	
	protected int RegisterPurchase(int itemID) 
	{
		purchases.put(nextPurchaseID, items.get(itemID));
		return nextPurchaseID++;
	}

	@Override
	public UIComponent GetDefaultFocusedUIElement() 
	{
		return UIManager.GetInstance().GetDefaultFocusedUIElement(this);
	}
	
	
	@Override
	public GameState GetExitState() 
	{
		return exitState;
	}


	public void FinalizePurchase(Purchase purchase) 
	{
		if (GetPurchaseItem(purchase.purchaseID).GetType() == ShopItemType.CARDS)	//TODO: Handle other cases (later)
		{
			CardCollection newCards = new CardCollection();
			
			for (String s : purchase.data) //Load each cardtype/template 
			{
				newCards.AddCard(CardTemplate.LoadCardTemplate(s), 1);	//TODO: Only adding 1 at a time is an encoding problem. Include count in the incoding --> some amount of refactoring needed
			}
	
			Game.GetInstance().GetPlayer().AddCollection(newCards);
			UIManager.GetInstance().GetCardTable().UpdateData();
			
			if (purchase.ironPrice)
			{
				Game.GetInstance().GetPlayer().AddBattleTokens(-GetPurchaseItem(purchase.purchaseID).GetBattleTokenPrice());
			}
			else
			{
				Game.GetInstance().GetPlayer().AddBattleTokens(-GetPurchaseItem(purchase.purchaseID).GetGoldTokenPrice());

			}
			
		}
	}

	private ShopItem GetPurchaseItem(int purchaseID) 
	{
		return purchases.get(purchaseID);
	}


	
	
	
}
