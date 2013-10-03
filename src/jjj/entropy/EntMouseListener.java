package jjj.entropy;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


import jjj.entropy.classes.Enums.GameLocation;
import jjj.entropy.ui.Clickable;
import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class EntMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static int MouseX;
	public static int MouseY;
	private Game game;
	
	
	public EntMouseListener(Game game)
	{
		this.game = game;
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		
		Game game = Game.GetInstance();
		
		if (game.IsInGame())
		{
			Card c = game.CheckCardCollision();
			
			if (c != null)
			{
				if (c == game.deckDrawCardDummy1)
				{

					c = game.GetPlayer().GetActiveDeck().GameGetTopCard();
					
					if (c == null)	//If deck is empty
						return;


					c.MoveToDeck(1);
					game.ShowCard(c);	//Show if not already shown (HashSet implementation makes sure that it isn't added twice)
					
				}
				if (!(c == game.deckDrawCardDummy2))
				{
					
					
					boolean swapTurn = false;
					boolean actionPerformed = true;
					
					
					boolean movingOutOfZone = false;
					GameLocation cardOldZone = c.GetZone();
					float oldCardX = 0;
					if (cardOldZone != null)	//If the card is in a combat zone
					{
						oldCardX = c.GetTargetX();
						movingOutOfZone = true;	//assume the card is moving out of the combat zone (checked below)
						c.SetZone(null);		//
					}
					
					switch(Game.mode)
					{
					case 1:
						
						switch(Game.modeNumber)
						{
						case 1:
							c.PlayToLife(GameLocation.LIFE1);
							break;
						case 2:
							c.PlayToLife(GameLocation.LIFE2);
							break;
						case 3:
							c.PlayToLife(GameLocation.LIFE3);
							break;
						case 4:
							c.PlayToLife(GameLocation.LIFE4);
							break;
						}
						break;
					case 2:
						c.PlayToLimbo();
						break;
					case 3:
						if (game.activeMatch.GetActiveTurnPlayer() == game.GetPlayer())
						{
							swapTurn = true;							//TODO: Always swaps action, fix
							Game.GetInstance().activeMatch.SwapTurn();
							switch(Game.modeNumber)
							{
							case 1:
								if (cardOldZone != GameLocation.ZONE1)
								{
									c.PlayToZone(GameLocation.ZONE1);
									c.SetZone(GameLocation.ZONE1);
								}
								else
									movingOutOfZone = false;
								break;
							case 2:
								if (cardOldZone != GameLocation.ZONE2)
								{
									c.PlayToZone(GameLocation.ZONE2);
									c.SetZone(GameLocation.ZONE2);
								}
								else
									movingOutOfZone = false;
								break;
							case 3:
								if (cardOldZone != GameLocation.ZONE3)
								{
									c.PlayToZone(GameLocation.ZONE3);
									c.SetZone(GameLocation.ZONE3);
								}
								else
									movingOutOfZone = false;
								break;
							case 4:
								if (cardOldZone != GameLocation.ZONE4)
								{
									c.PlayToZone(GameLocation.ZONE4);
									c.SetZone(GameLocation.ZONE4);
								}
								else
									movingOutOfZone = false;
								break;
							}
							break;
						}
						else
							actionPerformed = false;
					case 4:
						c.PlayToHand(Game.modeNumber);
						break;
					}
							
					if (movingOutOfZone)
					{
						GameLocation.ReturnAvailCardX(cardOldZone, oldCardX, false);
					}
						
					
					
					if (actionPerformed)
					{
						//The card has an ID that is individual to the client but is synchronized across clients in each decks unique ID system so the deck id is used here
						NetworkManager.GetInstance().SendAction(game.GetPlayer().GetActiveDeck().GetCardDeckID(c), Game.mode, Game.modeNumber, swapTurn);	
					}
					
				}
				
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		UIComponent uicmm = UIManager.GetInstance().CheckUICollision();
		if (uicmm != null && uicmm instanceof Clickable)
		{
			UIManager.GetInstance().SetFocusedUIComponent(uicmm);
			((Clickable)uicmm).Activate(MouseX, MouseY);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		MouseX = e.getX();
		MouseY = Game.GetInstance().GetRealGameHeight() - e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		MouseX = e.getX();
		MouseY = Game.GetInstance().GetRealGameHeight() - e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Card c = game.CheckCardCollision();
		if (c != null && (!c.IsDummy()))
		{
			if (e.getWheelRotation() > 0)
			{
				if (!c.Zoomed())
				{
					c.SetZoomed(true);
					c.SavePosition();
					if (c != null)
						c.PlayToHighlight();
				}
			}
			else
			{
				if (c.Zoomed())
				{
					c.SetZoomed(false);
					c.MoveBack();
				}
			}
		}
		
	}


	
	
}
