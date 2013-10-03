package jjj.entropy.effects;

import jjj.entropy.Card;
import jjj.entropy.classes.Enums.GameLocation;

public class MoveEffect implements TriggerAction
{
	
	int cardIdentifier,
		locationIdentifier;
	Card card;
	GameLocation target;
	

	//Card identifier: 0 = parameter card, 1 = primary context card, 2 = secondary context card
	//Location identifier: 0 = parameter location, 1 = context location
	public MoveEffect(int cardIdentifier, Card card, int locationIdentifier, GameLocation target)
	{
		this.cardIdentifier = cardIdentifier;
		this.card = card;
		this.target = target;
		this.locationIdentifier = locationIdentifier;
	}
	
	
	@Override
	public void Execute(EventContext context) 
	{
		if (locationIdentifier == 1)
			target = context.location;
		
		/*
		
		switch (target)
		{
		case LIFE1:	case LIFE2:	case LIFE3:	case LIFE4:
			card.PlayToLife(target);
			break;
		case O_LIFE1:	case O_LIFE2:	case O_LIFE3:	case O_LIFE4:
			break;
		case ZONE1: case ZONE2: case ZONE3: case ZONE4:
			break;
			
		case HAND:
			break;
		case O_HAND:
			break;
		case LIMBO:
			break;
		case O_LIMBO:
			break;
		}
		
	*/	
	}
	
	

}
