package jjj.entropy.shop;

import java.util.ArrayList;
import java.util.List;

import jjj.entropy.Game;
import jjj.entropy.GameState;
import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class Shop implements GameState
{

	
	private List<ShopItem> items;
	

	public Shop()
	{
		items = new ArrayList<ShopItem>();
	}


	@Override
	public void Activate(Game game) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void Draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UIComponent GetDefaultFocusedUIElement() 
	{
		return UIManager.GetInstance().GetDefaultFocusedUIElement(this);
	}
	
	
	
	
	
}
