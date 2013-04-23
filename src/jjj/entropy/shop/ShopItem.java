package jjj.entropy.shop;
import jjj.entropy.Texture;
import jjj.entropy.shop.ShopItem.ShopItemType;




public class ShopItem 
{
	
	public enum ShopItemType {
		CARDS,
		BOOST,
		UNDEFINED;

		public static ShopItemType GetType(int i) {
			switch (i)
			{
			case 1:
				return CARDS;
			case 2:
				return BOOST;
			default:
				return UNDEFINED; 
			}
		}
	}

	
	
	private boolean avaliable;
	
	private String name,
				   description;
	
	private Texture previewImage;
	
	private int itemID,
				priceBT,
				priceGT;

	private ShopItemType type;
	
	
	
	public ShopItem(int itemID, ShopItemType type, String name, int priceBattleTokens, int priceGoldTokens, Texture image)
	{
		this(itemID, type, name, priceBattleTokens, priceGoldTokens, "", image);
	}
	
	public ShopItem(int itemID, ShopItemType type, String name, int priceBattleTokens, int priceGoldTokens, String description, Texture image)
	{
		this.itemID = itemID;
		this.name = name;
		this.type = type;
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
	
	public ShopItemType GetType()
	{
		return type;
	}
	
	public Texture GetTexture()
	{
		return previewImage;
	}
	
	
	
}
