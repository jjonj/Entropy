package jjj.entropy;


import com.jogamp.opengl.util.texture.Texture;

public class CardTemplate {

	public enum CardType{
		RESOURCE,
		CREATURE,
		ZONE_EFFECT,
		EFFECT,
		SPELL
	}
	 
	public enum CardRace{
		ORC,
		CRAWNID
	}
	
	private CardType type;
	private CardRace race;
	
	private Texture texture;
	
	private String title;
	
	public final int RaceCost,
					AnyCost,
					Income,
					Defense,
					DmgBase,
					DmgDice;
				
	// ArrayList<Specials>
	
	
	public CardTemplate(Texture texture, int raceCost, int anyCost, int income, int defense, int dmgBase, int dmgDice)
	{
		this.texture = texture;
		
		RaceCost = raceCost;
		AnyCost = anyCost;
		Income = income;
		Defense = defense;
		DmgBase = dmgBase;
		DmgDice = dmgDice;
	}
	
	public Texture GetTexture()
	{
		return texture;
	}
	
}
