package jjj.entropy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;

import jjj.entropy.*;
import jjj.entropy.classes.EntUtilities;


public class CardCollection implements Iterable<Card>
{

	
	List<Card> cards;
	
	public CardCollection()
	{
		cards = new ArrayList<Card>();
	}
	
	
	public void AddCard(Card card)
	{
		cards.add(card);
	}
	public void RemoveCard(int index)
	{
		cards.remove(index);
	}
	
	public void LoadTextures(GL2 gl)
	{
		for (Card card : cards)
		{
			card.GetTemplate().LoadTexture(gl);
		}
	}
	
	
	public Card GetRandomCard()
	{
		return cards.get(EntUtilities.GetRandom(0, cards.size()-1, 1));
	}
	
	public int GetSize()
	{
		return cards.size();
	}


	@Override
	public Iterator<Card> iterator() 
	{
		return cards.iterator();
	}

	
	
	//Returns the internal list for use with table
	public List<Card> GetList() 
	{
		return cards;
	}
	
}
