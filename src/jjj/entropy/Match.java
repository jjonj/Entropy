package jjj.entropy;


public class Match 
{
	
	private Player neutralPlayer,
				   player1,
			       player2;
	int id;
	
	private Player activePlayer; //Player whose turn it is
	public Match(int id, Player player1, Player opponent, boolean player1Starts)
	{
		this.player1 = player1;
		this.player2 = opponent;
		this.id = id;
		if (player1Starts)
		{
			activePlayer = player1;
		}
		else
		{
			activePlayer = player2;
		}
	}
	
	
	public void Start()
	{
		player1.InitGame();
		player2.InitGame();
		
	}
	
	public void SwapTurn()
	{
		if (activePlayer == player1)
			activePlayer = player2;
		else
			activePlayer = player1;
	}


	public int GetID() 
	{
		return id;
	}


	public Player GetOpponent() 
	{
		return player2;
	}


	public Player GetActiveTurnPlayer() 
	{
		return activePlayer;
	}
	
	
}
