package jjj.entropy.server;

import java.util.ArrayList;
import java.util.List;

import jjj.entropy.messages.Purchase;

public class ShopManager 
{

	
	
	
	private static final int 
					  startersCommon = 7,
					  startersUnCommon = 2,
					  startersRare = 0,
					  startersLegendary = 0,
					  
					  collectorsCommon = 5,
					  collectorsUncommon = 3,
					  collectorsRare = 1,
					  collectorsLegendary = 0,
					  
					  goldCommon = 3,
					  goldUncommon = 3,
					  goldRare = 3,
					  goldLegendary = 0;
	
	
	public static String[] GenerateStartersPack()
	{
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i < startersCommon; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomCommon()+",");
		}
		for (int i = 0; i < startersUnCommon; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomUncommon()+",");
		}
		
		for (int i = 0; i < startersRare; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomRare()+",");
		}
		for (int i = 0; i < startersLegendary; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomLegendary()+",");
		}
		
		resultList.add(DatabaseManager.GetInstance().GetRandomProbability(0.8f, 0f, 19.9f));
		
		return resultList.toArray(new String[resultList.size()]);
	}
	
	public static String[] GenerateCollectorsPack()
	{
		List<String> resultList = new ArrayList<String>();
		
		for (int i = 0; i < collectorsCommon; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomCommon()+",");
		}
		for (int i = 0; i < collectorsUncommon; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomUncommon()+",");
		}
		for (int i = 0; i < collectorsRare; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomRare()+",");
		}
		for (int i = 0; i < collectorsLegendary; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomLegendary()+",");
		}
		
		resultList.add(DatabaseManager.GetInstance().GetRandomProbability(0.8f, 0f, 19.5f));
		
		return resultList.toArray(new String[resultList.size()]);
	}
	
	public static String[] GenerateGoldPack()
	{
		List<String> resultList = new ArrayList<String>();
		
		for (int i = 0; i < goldCommon; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomCommon()+",");
		}
		for (int i = 0; i < goldUncommon; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomUncommon()+",");
		}
		for (int i = 0; i < goldRare; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomRare()+",");
		}
		for (int i = 0; i < goldLegendary; i++)
		{
			resultList.add(DatabaseManager.GetInstance().GetRandomLegendary()+",");
		}
		
		resultList.add(DatabaseManager.GetInstance().GetRandomProbability(0.5f, 0.3f, 18.5f));
		
		return resultList.toArray(new String[resultList.size()]);
	}

	//Validates and finishes purchase
	public static boolean ValidatePurchase(Purchase purchase) 
	{
		purchase.accepted = true;
		purchase.data = ShopItem.shopItemMap.get(purchase.itemID).GetData();
		
		return DatabaseManager.GetInstance().FinalizePurchase(purchase);
	
	}
	
}
