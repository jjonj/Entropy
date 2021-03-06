package jjj.entropy;

import java.util.ArrayList;
import java.util.List;

import jjj.entropy.ui.UIManager;


public class Player {

	private String name;
	private int id;
	private CardCollection allCards;
	private Deck activeDeck;
	private List<Deck> decks = null;
	private int NextCardID = 0;	//Card id's are linked to each owner
	
	
	private int battleTokens,	//Free currency
				goldTokens;		//Purchased currency
				
	
	
	
	public Player(int playerID, String name, int battleTokens, int goldTokens, int activeDeck, int[] allCards, int[] allCardCounts, int[][] decks, int[][] deckCounts, int[] deckDBIDs)	//Used for creating the player on this client
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
		
		this.battleTokens = battleTokens;
		this.goldTokens = goldTokens;
		
		this.decks = new ArrayList<Deck>();
		this.id = playerID;
		this.name = name;
		
		if (decks != null)
		{
			for (int i = 0; i < decks.length; i++)
			{
				this.decks.add(Deck.LoadDeck(this, "Deck "+(i+1), decks[i], deckCounts[i], deckDBIDs[i]));	//Temporary deck names
			}
			
			if (decks.length > 0)
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

	public void InitGame() 
	{
		activeDeck.GameResetDeck();
		activeDeck.LoadTextures();
	}

	public void AddCollection(CardCollection cards) 
	{
		allCards.AddCards(cards); //TODO: Let players choose when to open instead of adding to collection instantly as here
		
	}

	public void AddBattleTokens(int amount) 
	{
		battleTokens += amount;
		UIManager.GetInstance().SetBattleTokensLabel(battleTokens);
	
	}
	
	public void AddGoldTokens(int amount) 
	{
		goldTokens += amount;
		UIManager.GetInstance().SetGoldTokensLabel(goldTokens);
	}

	public int GetBattleTokens() 
	{
		return battleTokens;
	}
	public int GetGoldTokens() 
	{
		return goldTokens;
	}
}
