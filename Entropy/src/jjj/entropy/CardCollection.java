package jjj.entropy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import jjj.entropy.ui.TableRow;


public class CardCollection implements SimpleCollection<TableRow>, Iterable<CardTemplate>
{

	protected HashMap<CardTemplate, Integer> cards;
	
	//The size of the collection must sum all the counts of all card types in the collection
	private int cardCount;

	public CardCollection()
	{
		cardCount = 0;
		cards = new HashMap<CardTemplate, Integer>();
	}
	
	
	public void AddCard(CardTemplate card, int count)
	{
		if (!cards.containsKey(card))
			cards.put(card, count);
		else
			cards.put(card, count+cards.get(card));	//Increment the count to the already existing ones
		cardCount += count;
	}
	public void AddCards(CardCollection collection)
	{
		for (CardTemplate c : collection)
		{
			AddCard(c, collection.GetCount(c));
		}
	}
	
	public void RemoveCardType(Card card)
	{
		if (cards.containsKey(card))
		{
			//Reduce the count by the number of cards actually being removed from the collection (count of card type)
			cardCount -= cards.get(card);
			cards.remove(card);
		}
		
	}
	
	
	//TODO: Consider when these textures are unlaoded
	public void LoadTextures()
	{
		for (CardTemplate card : cards.keySet())
		{
			card.LoadTexture();
			
			System.out.println("Loading texture for card: " +card.Title);
		}
	}
	
	//Returns a 2xSize array as a trick to return two arrays. One for the IDs one for the Counts
	public int[][] ToIDCountArray()
	{
		int[][] arr = new int[2][cards.size()];
		
		int i = 0;
		for (CardTemplate c : this)
		{
			arr[0][i] = c.ID;			//Set ID's 
			arr[1][i] = cards.get(c);	//Set counts
			i++;
		}
		return arr;
	}


	@Override
	public Iterator<CardTemplate> iterator() 
	{
		return cards.keySet().iterator();
	}


	public int CardCount() {
		return cardCount;
	}
	
	
	//Returns number of different cards
	@Override
	public int Size() {
		return cards.size();
	}


	@Override
	public void AddAllTo(Collection<TableRow> c) 
	{
		for (CardTemplate ct : this)
		{
			c.add(ct);
		}
	}

	public int GetCount(CardTemplate cardTemplate) 
	{
		if (cards.containsKey(cardTemplate))
			return cards.get(cardTemplate);
		return 0;
	}
	
}
