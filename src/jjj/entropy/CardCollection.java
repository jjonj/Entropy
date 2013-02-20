package jjj.entropy;

import java.util.HashMap;
import java.util.Iterator;
import javax.media.opengl.GL2;


public class CardCollection implements Iterable<CardTemplate>
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
	

	public int GetSize()
	{
		return cards.size();
	}


	@Override
	public Iterator<CardTemplate> iterator() 
	{
		return cards.keySet().iterator();
	}

	
	
}
