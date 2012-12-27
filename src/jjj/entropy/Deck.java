package jjj.entropy;


public class Deck extends CardCollection {

	
	private Player owner;
	
	public Deck(Player owner)
	{
		this.owner = owner;
	}

	public static Deck LoadDeck(Player owner, int[] deck) {
		Deck rDeck = new Deck(owner);
	
		try {
			for (int c : deck)
			{
				
				rDeck.AddCard(Card.LoadCard(c, owner));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return rDeck;
	}
	
}
