package jjj.entropy;

import java.util.ArrayList;
import java.util.List;

public class CardComparisonCollection implements TableCollection<CardTemplate>
{

	List<String> rows;
	
	CardCollection primaryCollection,
				   comparisonCollection;
	
	public CardComparisonCollection(CardCollection primaryCollection, CardCollection comparisonCollection)
	{
		this.comparisonCollection = comparisonCollection;
		this.primaryCollection = primaryCollection;
		
		Update();
	}
	
	@Override
	public int Size() 
	{
		return primaryCollection.Size();
	}

	@Override
	public List<String> GetRows() 
	{
		return rows;
	}

	@Override
	public void Update() 
	{
		rows = new ArrayList<String>(primaryCollection.Size());
		
		for (CardTemplate ct : primaryCollection)
		{
			StringBuilder sb = new StringBuilder(30);
			sb.append(ct.Title);
			for (int i = 0; i <  30 - ct.Title.length(); i++)	//Append spaces so the count part below gets alligned
				sb.append(" ");
			sb.append(primaryCollection.GetCount(ct) + "/" + (primaryCollection.GetCount(ct) + comparisonCollection.GetCount(ct)));
		}
	}

	/*
	@Override
	public void AddAllTo(List<TableRow> target) 
	{
		for (CardTemplate ct : this)
			target.add(ct);
	}*/

	

}
