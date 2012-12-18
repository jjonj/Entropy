package jjj.entropy;

import java.util.List;
import java.util.Random;


public class CardCollection {

	
	List<Card> cards;
	
	public CardCollection()
	{
		
	}
	
	public void AddCard(Card card)
	{
		cards.add(card);
	}
	public void RemoveCard(int index)
	{
		cards.remove(index);
	}
	
	public Card GetRandomCard()
	{
		return cards.get(EntUtilities.GetRandom(0, cards.size()));
	}
	
	public int GetSize()
	{
		return cards.size();
	}
	
}
