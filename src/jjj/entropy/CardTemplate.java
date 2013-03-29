package jjj.entropy;


import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import jjj.entropy.classes.Const;
import jjj.entropy.ui.TableRow;
import jjj.entropy.ui.UIManager;

import com.jogamp.opengl.util.texture.*;



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
	
	private static CardTemplate[] allCardTemplates = new CardTemplate[Const.MAX_CARD_COUNT];
	
	
	public final CardType Type;		//Fields are public since they can be final, no need for clutter Getters
	public final CardRace Race;
	
	public Texture texture = null; //Loaded when needed from texturePath
	
	public final String Title,
						texturePath;
	
	public final short  ID,			//Global ID, not client based
						RaceCost,
						AnyCost,
						Income,
						Defense,
						DmgBase,
						DmgDice;
	
	// ArrayList<Specials>
	
	public CardTemplate(short id, String title, CardRace race, CardType type, short raceCost, short anyCost, short income, short defense, short dmgBase, short dmgDice, Texture texture)
	{
		this(id, title, race, type, raceCost, anyCost, income, defense, dmgBase, dmgDice, "");
		this.texture = texture;
	}
	
	public CardTemplate(short id, String title, CardRace race, CardType type, short raceCost, short anyCost, short income, short defense, short dmgBase, short dmgDice, String texturePath)
	{
		this.ID = id;
		this.texturePath = texturePath;
		Title = title;
		Race = race;
		Type = type;
		RaceCost = raceCost;
		AnyCost = anyCost;
		Income = income;
		Defense = defense;
		DmgBase = dmgBase;
		DmgDice = dmgDice;
	}
	
	public void LoadTexture(GL2 gl)
	{
		if (texture == null)
		{
			try {
				texture = TextureIO.newTexture(new File(texturePath), true);
			} catch (GLException | IOException e) {
				e.printStackTrace();
				System.exit(1);	
			}
			GLHelper.InitTexture(gl, texture);
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
	public static void LoadCardTemplate(String encodedTemplate) {

		String[] data = encodedTemplate.split(",");
		
		short id = Short.parseShort(data[0]);
		
		if (allCardTemplates[id] != null)	// If the template already is loaded
			return;
		
		CardRace race = CardRace.GetRace(Short.parseShort(data[2]));
		CardType type = CardType.GetType(Short.parseShort(data[3]));
		
		
		short raceCost = Short.parseShort(data[4]),
			  anyCost = Short.parseShort(data[5]),
			  income = Short.parseShort(data[6]),
			  defense = Short.parseShort(data[7]),
			  dmgBase = Short.parseShort(data[8]),
			  dmgDice = Short.parseShort(data[9]);
		
		
		String title = data[1];
		
		//HANDLE SPECIALS
		
		String texturePath = TextureManager.GetTexturePath(id);
		CardTemplate ct = new CardTemplate(id,title, race, type, raceCost, anyCost, income, defense, dmgBase, dmgDice, texturePath);
		allCardTemplates[id] = ct;
	}

	@Override
	public String[] GenRow() 
	{
		// Using a slightly complicated method of extracting the count of this CardTemplate in the dataSource being used for generating rows for a table
		CardCollection dataSource = (CardCollection)(UIManager.GetInstance().GetActiveDataSource());
		if (dataSource != null)
		{
			if (dataSource == Game.GetInstance().GetPlayer(1).GetAllCards())	//The representation is the number of cards in the players card not already in the active deck
				return new String[] { Title, ""+(dataSource.GetCount(this)-Game.GetInstance().GetPlayer(1).GetActiveDeck().GetCount(this))+"/"+dataSource.GetCount(this)};
			else	//It is assumed here that the collection is then the active deck (TODO: NOT PRETTY)
				return new String[] { Title, ""+dataSource.GetCount(this)+"/"+Game.GetInstance().GetPlayer(1).GetAllCards().GetCount(this)};
		}
			
		return new String[] { Title, "0"};
	}

	@Override
	public int compareTo(TableRow other) 	//Compares alphabetically
	{	
		if (other instanceof CardTemplate)	//Should only be false if tables are misused
			return Title.compareTo(((CardTemplate)other).Title);			
		return 0;
	}
	
}
