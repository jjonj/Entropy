package jjj.entropy;

import java.util.HashMap;
import java.util.List;

import jjj.entropy.effects.EventContext;

import jjj.entropy.classes.Enums.TriggerEvent;
import jjj.entropy.effects.TriggerAction;


public class Match 
{
	
	private Player neutralPlayer,
				   player1,
			       player2;
	int id;
	
	//Possible events mapped to a list of actions to execute upon that evnet
	//TODO: Make the list a hashset for constant removal time
	private HashMap<TriggerEvent, List<TriggerAction>> triggerActionMap;
	
	
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
	
	
	public void AddTrigger(TriggerEvent event, TriggerAction action)
	{
		triggerActionMap.get(event).add(action);
	}
	
	public void RemoveTrigger(TriggerEvent event, TriggerAction actionToRemove)
	{
		triggerActionMap.get(event).remove(actionToRemove);	//Not running in constant time until above todo is resolved
	}
	
	public void ActivateTrigger(TriggerEvent event, EventContext context)
	{
		List<TriggerAction> actionsToExecute = triggerActionMap.get(event);
		
		if (actionsToExecute != null)
		{
			for (TriggerAction ta : actionsToExecute)
			{
				ta.Execute(context);	//TODO: Figure out how to give eventcontext
			}
		}
	}
	
	
}
