package jjj.entropy;

import java.util.ArrayList;
import java.util.List;


public class Player {

	private String name;
	private int id;
	private CardCollection allCards;
	private Deck activeDeck;
	private List<Deck> decks = null;
	private int NextCardID = 0,	//Card id's are linked to each owner
			    deckCount;
				
	
	
	
	public Player(int playerID, String name, int activeDeck, int[] allCards, int[][] decks )	//Used for creating the player on this client
	{

		this.allCards = new CardCollection();
		try {
			for (int c : allCards)
			{
				this.allCards.AddCard(Card.LoadCard(c, this));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.decks = new ArrayList<Deck>();
		this.id = playerID;
		this.name = name;
		
		if (decks != null)
		{
			for (int i = 0; i < decks.length; i++)
			{
				this.decks.add(Deck.LoadDeck(this, "Deck "+(i+1), decks[i]));	//Temporary deck names
			}
			this.activeDeck = this.decks.get(activeDeck);
		}
		
		
	}
	
	public Player(int playerID, String name, int[] activeDeck)	//Used for creating opponent players that only need an active deck
	{
		this.decks = new ArrayList<Deck>();
		this.id = playerID;
		this.name = name;
		
		if (activeDeck != null)
			this.activeDeck = Deck.LoadDeck(this, "Default deck",activeDeck);

	}

	public int GetID() {
		return id;
	}

	public Deck GetActiveDeck() 
	{
		return activeDeck;
	}
	

	public List<Deck> GetAllDecks()
	{
		return decks;
	}
	
	public CardCollection GetAllCards() 
	{	
		return allCards;
	}

	public int GetNextCardID() 
	{
		return NextCardID++;	//Increment after returning
	}
	
	
}
