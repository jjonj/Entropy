package jjj.entropy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL2;

import jjj.entropy.classes.EntUtilities;


public class Deck extends CardCollection {

	
	private Player owner;
	private String name;
	private List<Card> remainingCards;	//Cards unplayed in the deck in a given game
	
	private HashMap<Card, Integer> CardToDeckIDMap;		//	Maps cards to their deck ids and back
	private HashMap<Integer, Card> DeckIDToCardMap;		//
	
	public Deck(Player owner, String name)
	{
		super();
		this.name = name;
		remainingCards = new ArrayList<Card>();
		CardToDeckIDMap = new HashMap<Card, Integer>();
		DeckIDToCardMap = new HashMap<Integer, Card>();
		this.owner = owner;
	}


	//      The Game*** methods affect the deck for a specific game temporarily, and not the actual deck itself.
	
	
	
	
	//Returns a card to the deck to a random index
	public void GameReturnCard(Card card)
	{
		int randToUse = -1;
		if (owner == Game.GetInstance().GetPlayer(1))
			randToUse = 1;
		else if (owner == Game.GetInstance().GetPlayer(2))
			randToUse = 2;
		GameReturnCard(card, EntUtilities.GetRandom(0, remainingCards.size()-1, randToUse));
	}
	
	//Returns a card to the deck to the specific index
	public void GameReturnCard(Card card, int index)
	{
		//Consider checking that the card is in the original deck with cards.contains(card)
		remainingCards.add(card);
	}
	
	public void GameShuffleDeck()
	{
		int randToUse = -1;
		if (owner == Game.GetInstance().GetPlayer(1))
			randToUse = 1;
		else if (owner == Game.GetInstance().GetPlayer(2))
			randToUse = 2;
		Collections.shuffle(remainingCards, EntUtilities.GetRndObject(randToUse));	// How trustworthy is this shuffle procedure?
	}
	
	public Card GameGetRandomCard()
	{
		int randToUse = -1;
		if (owner == Game.GetInstance().GetPlayer(1))
			randToUse = 1;
		else if (owner == Game.GetInstance().GetPlayer(2))
			randToUse = 2;
		if (remainingCards.size() > 0)
			return remainingCards.remove(EntUtilities.GetRandom(0, remainingCards.size()-1, randToUse));
		return null;
	}
	
	public Card GameGetTopCard()
	{
		if (remainingCards.size() > 0)
			return remainingCards.remove(0);
		return null;
	}
	
	//Instantiates the abstract deck with just templates and counts, into actual cards. Also maps them in the CardToDeckIDMap
	public void GameResetDeck()
	{		
		remainingCards = new ArrayList<Card>();
		for (CardTemplate c : this)
		{
			for (int i = 0; i < cards.get(c); i++)	//Add card 'count' (which is the value in the hashmap) times
			{
				remainingCards.add(new Card(0,0,0, c, Game.GetInstance().GetPlayer(1)));
			}
			
			Collections.sort(remainingCards);	//Sort list for consistent ordering across clients before Deck ids are added
			for (int i = 0; i < remainingCards.size(); i++)
			{
				CardToDeckIDMap.put(remainingCards.get(i), i);	//This mapping will be synchronized as the card IDs come in sorted order from the DB-->Server-->Client
				DeckIDToCardMap.put(i, remainingCards.get(i));
				System.out.println("--- For player: " + owner.GetName() + " - Card: " + remainingCards.get(i).GetTemplate().Title + " gets ID: " + i);
			}	


		}
		
		//TODO: DEBUG CODE; REMOVE
		System.out.println(owner.GetName() + " deck: ");
		for (Card cc : remainingCards)
		{
			System.out.println(cc.GetTemplate().Title + " ");
		}
		
		GameShuffleDeck();
	}
	
	
	public Card GameGetCard(int cardDeckID) 
	{
		remainingCards.remove(DeckIDToCardMap.get(cardDeckID));	//remove method a bit ineffecient
		return DeckIDToCardMap.get(cardDeckID);
	}
	
	public static Deck LoadDeck(Player owner, String deckName, int[] deck, int[] deckCounts) 
	{
		Deck rDeck = new Deck(owner, deckName);
		try {
			for (int i = 0; i < deck.length; i++)
			{
				rDeck.AddCard(CardTemplate.GetTemplate(deck[i]), deckCounts[i]);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return rDeck;
	}


	//Gets the index of the argument card in the deck. This is used as the index of the card for the other players, identifying the card
	public int GetCardDeckID(Card card) 
	{
		return CardToDeckIDMap.get(card);
	}


	
	@Override 
	public String toString() 
	{
	    return name;
	}
	
	
}
