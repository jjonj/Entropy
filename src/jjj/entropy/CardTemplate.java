package jjj.entropy;


import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLException;

import jjj.entropy.classes.Const;
import jjj.entropy.ui.TableRow;
import jjj.entropy.ui.UIManager;



public class CardTemplate implements TableRow {

	public enum CardType{
		UNDEFINED,
		RESOURCE,
		CREATURE,
		EFFECT,
		ZONE_EFFECT,
		SPELL;
		
		public static CardType GetType(short index) {
			switch (index)
			{
			case 1:
				return RESOURCE;
			case 2:
				return CREATURE;
			case 3:
				return EFFECT;
			case 4:
				return ZONE_EFFECT;
			case 5:
				return SPELL;
			}
			return UNDEFINED;
		}
	}
	 
	public enum CardRace{
		UNDEFINED,
		ORC,
		CRAWNID;

		public static CardRace GetRace(short index) {
			switch (index)
			{
			case 1:
				return ORC;
			case 2:
				return CRAWNID;
			}
			return UNDEFINED;
		}
	}
	
	public enum CardRarity{
		UNDEFINED,
		COMMON,
		UNCOMMON,
		RARE,
		LEGENDARY;

		public static CardRarity GetRarity(short index) {
			switch (index)
			{
			case 1:
				return COMMON;
			case 2:
				return UNCOMMON;
			case 3:
				return RARE;
			case 4:
				return LEGENDARY;
			}
			return UNDEFINED;
		}
	}
	
	private static CardTemplate[] allCardTemplates = new CardTemplate[Const.MAX_CARD_COUNT];
	
	
	public final CardType Type;		//Fields are public since they can be final, no need for clutter Getters
	public final CardRace Race;
	public final CardRarity Rarity;
	
	
	public Texture texture = null; //Loaded when needed from texturePath
	
	public final String Title,
						texturePath;
	
	public final short  ID,			//Global ID, not client based
						RaceCost,
						AnyCost,
						Strength,
						Intelligence,
						Vitality;


	

	
	public CardTemplate(short id, String title, CardRace race, CardType type, CardRarity rarity,  short raceCost, short anyCost, short strength, short intelligence, short vitality, String texturePath)
	{
		this.ID = id;
		this.texturePath = texturePath;
		Title = title;
		Race = race;
		Type = type;
		Rarity = rarity;
		RaceCost = raceCost;
		AnyCost = anyCost;
		Strength = strength;
		Intelligence = intelligence;
		Vitality = vitality;
	}
	
	public void LoadTexture()
	{
		if (texture == null)
		{
			try {
				texture = Texture.Load(new File(texturePath), true);
			} catch (GLException | IOException e) {
				e.printStackTrace();
				System.exit(1);	
			}
			OGLManager.InitTexture(texture);
		}
	}
	
	public Texture GetTexture()
	{
		return texture;
	}
	
	
	
	public static CardTemplate GetTemplate(int id) throws IllegalAccessException
	{
		if (allCardTemplates[id] == null)
			throw new IllegalAccessException("Card template was refered but it was not loaded. Server should send and force a load of any cards before any card is used. ID was: " + id);
		return allCardTemplates[id];
	}
	
	
	//Takes an encoded card template and loads it
	public static CardTemplate LoadCardTemplate(String encodedTemplate) 
	{
		// Encoding: 	ID,TITLE,RACE,TYPE,RARITY,COSTR,COSTA,STR,INT,VIT
		
		String[] data = encodedTemplate.split(",");
		
		short id = Short.parseShort(data[0]);
		
		if (allCardTemplates[id] != null)	// If the template already is loaded
			return allCardTemplates[id];
		
		CardRace race = CardRace.GetRace(Short.parseShort(data[2]));
		CardType type = CardType.GetType(Short.parseShort(data[3]));
		CardRarity rarity = CardRarity.GetRarity(Short.parseShort(data[4]));
		
		
		short raceCost = Short.parseShort(data[5]),
			  anyCost = Short.parseShort(data[6]),
			  strenght = Short.parseShort(data[7]),
			  intelligence = Short.parseShort(data[8]),
			  vitality = Short.parseShort(data[9]);
		
		
		String title = data[1];
		
		//HANDLE SPECIALS
		
		String texturePath = Texture.GetTexturePath(id);
		CardTemplate ct = new CardTemplate(id,title, race, type, rarity, raceCost, anyCost, strenght, intelligence, vitality, texturePath);
		allCardTemplates[id] = ct;
		return ct;
		
	}

	@Override
	public String GenRow() 
	{
		// Using a slightly complicated method of extracting the count of this CardTemplate in the dataSource being used for generating rows for a table
		CardCollection dataSource = (CardCollection)(UIManager.GetInstance().GetActiveDataSource());
		if (dataSource != null)
		{
			if (dataSource == Game.GetInstance().GetPlayer().GetAllCards())	//The representation is the number of cards in the players card not already in the active deck
				return Title + "		"+(dataSource.GetCount(this)-Game.GetInstance().GetPlayer().GetActiveDeck().GetCount(this))+"/"+dataSource.GetCount(this);
			else	//It is assumed here that the collection is then the active deck (TODO: NOT PRETTY)
				return Title + "		"+dataSource.GetCount(this)+"/"+Game.GetInstance().GetPlayer().GetAllCards().GetCount(this);
		}
			
		return Title + "		0";
	}

	@Override
	public int compareTo(TableRow other) 	//Compares alphabetically
	{	
		if (other instanceof CardTemplate)	//Should only be false if tables are misused
			return Title.compareTo(((CardTemplate)other).Title);			
		return 0;
	}
	
}
