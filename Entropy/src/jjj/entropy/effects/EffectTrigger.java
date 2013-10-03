package jjj.entropy.effects;

import jjj.entropy.classes.Enums.TriggerEvent;

public class EffectTrigger implements TriggerAction
{

	
	
	public final TriggerEvent event;
	
	
	public EffectTrigger(TriggerEvent event)
	{
		this.event = event;
	}


	@Override
	public void Execute(EventContext context) 
	{
		// TODO Auto-generated method stub
		
	}
	
}
