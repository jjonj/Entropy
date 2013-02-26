package jjj.entropy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.media.opengl.GL2;

import jjj.entropy.ui.TableRow;


public class CardCollection implements SimpleCollection<TableRow>, Iterable<CardTemplate>
{

	protected HashMap<CardTemplate, Integer> cards;
	

	public CardCollection()
	{
		cards = new HashMap<CardTemplate, Integer>();
	}
	
	
	public void AddCard(CardTemplate card, int count)
	{
		cards.put(card, count);
	}
	public void RemoveCard(Card card)
	{
		cards.remove(card);
	}

	public void LoadTextures(GL2 gl)
	{
		for (CardTemplate card : cards.keySet())
		{
			card.LoadTexture(gl);
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

	public int GetSize()
	{
		return cards.size();
	}


	@Override
	public Iterator<CardTemplate> iterator() 
	{
		return cards.keySet().iterator();
	}


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
