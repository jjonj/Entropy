package jjj.entropy;


import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import jjj.entropy.classes.Const;
import jjj.entropy.ui.TableRow;

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
	
	public final short  ID,
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
		
		short id = (short) Short.parseShort(data[0]);
		
		if (allCardTemplates[id] != null)	// If the template already is loaded
			return;
		
		CardRace race = CardRace.GetRace((short) Short.parseShort(data[2]));
		CardType type = CardType.GetType((short) Short.parseShort(data[3]));
		
		
		short raceCost = (short) Short.parseShort(data[4]),
			  anyCost = (short) Short.parseShort(data[5]),
			  income = (short) Short.parseShort(data[6]),
			  defense = (short) Short.parseShort(data[7]),
			  dmgBase = (short) Short.parseShort(data[8]),
			  dmgDice = (short) Short.parseShort(data[9]);
		
		
		String title = data[1];
		
		//HANDLE SPECIALS
		
		String texturePath = TextureManager.GetTexturePath(id);
		CardTemplate ct = new CardTemplate(id,title, race, type, raceCost, anyCost, income, defense, dmgBase, dmgDice, texturePath);
		allCardTemplates[id] = ct;
	}

	@Override
	public String[] GenRow() 
	{
		return new String[] { Title, "1"};
	}
	
}
