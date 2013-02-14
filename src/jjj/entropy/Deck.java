package jjj.entropy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;

import jjj.entropy.classes.EntUtilities;


public class Deck extends CardCollection {

	
	private Player owner;
	private String name;
	private List<Card> remainingCards;	//Cards unplayed in the deck in a given game
	
	public Deck(Player owner, String name)
	{
		super();
		this.name = name;
		remainingCards = new ArrayList<Card>();
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
	
	public void GameResetDeck()
	{
		remainingCards.clear();
		remainingCards.addAll(cards);
		GameShuffleDeck();
	}
	
	
	public Card GameGetCard(int cardID) {	//Ineffecient method
		for (Card card : cards)
		{
			if (card.GetID() == cardID)
			{
				remainingCards.remove(card);
				return card;
			}
		}
		return null;
	}
	
	public static Deck LoadDeck(Player owner, String deckName, int[] deck) 
	{
		Deck rDeck = new Deck(owner, deckName);
	
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


	//Gets the index of the argument card in the deck. This is used as the index of the card for the other players, identifying the card
	public int GetDeckIndex(Card card) 
	{
		return cards.indexOf(card);
	}


	
	@Override 
	public String toString() 
	{
	    return name;
	}
	
	
}
