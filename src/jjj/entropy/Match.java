package jjj.entropy;

import jjj.entropy.classes.Enums.GameState;

public class Match 
{
	
	private Player neutralPlayer,
				   player1,
			       player2;
	int id;
	
	public Match(int id, Player player1, Player opponent)
	{
		this.player1 = player1;
		this.player2 = opponent;
		this.id = id;
	}
	
	
	public void Start()
	{
		player1.InitGame();
		player2.InitGame();
		
	}


	public int GetID() 
	{
		return id;
	}


	public Player GetOpponent() 
	{
		return player2;
	}
	
	
}
