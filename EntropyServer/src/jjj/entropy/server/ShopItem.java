package jjj.entropy.server;

import java.util.HashMap;

public class ShopItem 
{
	public static HashMap<Integer, ShopItem> shopItemMap = new HashMap<Integer, ShopItem>(16);
	
	
	private final DataMethod dataMethod;
	public final String name,
				  description;
	public final int itemID,
			   costBT,
			   costGT,
			   type;
	
	
	public ShopItem(String name, String description, int itemID, int type, int costBT, int costGT, DataMethod dataMethod)
	{
		this.type = type;
		this.name = name;
		this.description = description;
		this.itemID = itemID;
		this.costBT = costBT;
		this.costGT = costGT;
		this.dataMethod = dataMethod;
	}
	
	
	
	public String[] GetData() 
	{
		return dataMethod.GetData();
	}
}
