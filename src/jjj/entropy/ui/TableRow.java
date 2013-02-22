package jjj.entropy.ui;

public interface TableRow extends Comparable<TableRow>
{
	//Returns a cell for each column to write in a table, like an advanced toString
	public String[] GenRow();

	
}
