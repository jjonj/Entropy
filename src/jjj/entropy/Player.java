package jjj.entropy;


public class Player {

	private String name;
	private int id;
	private Deck deck = null;

	
	public Player(int playerID, String name, int[] deck)
	{
		this.id = playerID;
		this.name = name;
		if (deck != null)
			this.deck = Deck.LoadDeck(this, deck);
	}
	
	
}
