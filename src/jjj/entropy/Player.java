package jjj.entropy;


public class Player {

	private String name;
	private int id;
	private Deck deck = null;
	private int NextCardID = 0;
	
	public Player(int playerID, String name, int[] deck)
	{
		this.id = playerID;
		this.name = name;
		if (deck != null)
			this.deck = Deck.LoadDeck(this, deck);
	}


	public int GetID() {
		return id;
	}

	public Deck GetDeck() {
		return deck;
	}


	public int GetNextCardID() {
		return NextCardID++;	//Increment after returning
	}
	
	
}
