package jjj.entropy;

import java.util.ArrayList;
import java.util.List;


public class Player {

	private String name;
	private int id;
	private CardCollection allCards;
	private Deck activeDeck;
	private List<Deck> decks = null;
	private int NextCardID = 0;	//Card id's are linked to each owner
	
				
	
	
	
	public Player(int playerID, String name, int activeDeck, int[] allCards, int[] allCardCounts, int[][] decks, int[][] deckCounts, int[] deckDBIDs)	//Used for creating the player on this client
	{

		this.allCards = new CardCollection();
		try {
			for (int i = 0; i < allCards.length; i++)
			{
				this.allCards.AddCard(CardTemplate.GetTemplate(allCards[i]), allCardCounts[i]);
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
				this.decks.add(Deck.LoadDeck(this, "Deck "+(i+1), decks[i], deckCounts[i], deckDBIDs[i]));	//Temporary deck names
			}
			this.activeDeck = this.decks.get(activeDeck);
		}
		
		
	}
	
	public Player(int playerID, String name, int[] activeDeck, int[] activeDeckCounts)	//Used for creating opponent players that only need an active deck
	{
		this.decks = new ArrayList<Deck>();
		this.id = playerID;
		this.name = name;
		
		if (activeDeck != null)
			this.activeDeck = Deck.LoadDeck(this, "Default deck",activeDeck, activeDeckCounts, -1);

	}

	public int GetID() 
	{
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

	public void SetActiveDeck(Deck activeDeck) 
	{
		this.activeDeck = activeDeck;
	}

	public String GetName() {
		return name;
	}
	
	
}
