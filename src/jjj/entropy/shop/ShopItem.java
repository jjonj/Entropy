package jjj.entropy.shop;




public class ShopItem 
{
	
	
	
	private boolean avaliable;
	
	private String name,
				   description;
	
	private int itemID,
				priceBT,
				priceGT;
	
	
	
	public ShopItem(int itemID, String name, int priceBattleTokens, int priceGoldTokens)
	{
		this(itemID, name, priceBattleTokens, priceGoldTokens, "");
	}
	
	public ShopItem(int itemID, String name, int priceBattleTokens, int priceGoldTokens, String description)
	{
		this.itemID = itemID;
		this.name = name;
		this.description = description;
		priceBT = priceBattleTokens;
		priceGT = priceGoldTokens;
	}
	
	
	public void Enable()
	{
		avaliable = true;
	}
	
	public void Disable()
	{
		avaliable = false;
	}
	
	public boolean IsAvaliable()
	{
		return avaliable;
	}
	
	public String GetName()
	{
		return name;
	}
	
	public String GetDescription()
	{
		return description;
	}
	
	public int GetGoldTokenPrice()
	{
		return priceGT;
	}
	
	public int GetBattleTokenPrice()
	{
		return priceBT;
	}
	
	
	
	
}
