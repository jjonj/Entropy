package jjj.entropy;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


import jjj.entropy.classes.Card;
import jjj.entropy.classes.Card.Facing;
import jjj.entropy.classes.Card.Status;
import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.classes.Enums.Life;
import jjj.entropy.classes.Enums.Zone;
import jjj.entropy.ui.EntUIComponent;

public class EntMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static int MouseX;
	public static int MouseY;
	private Game game;
	
	
	public EntMouseListener(Game game)
	{
		this.game = game;
	}
	
	
	static int wtf = 0;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if (game.Gamestate == GameState.IN_GAME)
		{
			Card c = game.CheckCardCollision();
			
			
			if (c != null)
			{
				if (c.glMIndex == 0)
				{
					c = new Card(-3, 0.51f, 1.0f, 
							Facing.DOWN, game.TinidQueen, Status.IN_ZONE, Game.Player1);
					
					game.ShowCard(c);
					
				}
				//c.SetTarget(c.GetX(), 0, c.GetZ()+3, 90, 0, 0);
				
				
	
				switch(Game.mode)
				{
				case 1:
					switch(Game.modeNumber)
					{
					case 1:
						c.PlayToLife(Life.LIFE1);
						break;
					case 2:
						c.PlayToLife(Life.LIFE2);
						break;
					case 3:
						c.PlayToLife(Life.LIFE3);
						break;
					case 4:
						c.PlayToLife(Life.LIFE4);
						break;
					}
					break;
				case 2:
					c.PlayToLimbo();
					break;
				case 3:
					switch(Game.modeNumber)
					{
					case 1:
						c.PlayToZone(Zone.ZONE1);
						break;
					case 2:
						c.PlayToZone(Zone.ZONE2);
						break;
					case 3:
						c.PlayToZone(Zone.ZONE3);
						break;
					case 4:
						c.PlayToZone(Zone.ZONE4);
						break;
					}
					break;
				case 4:
					c.PlayToHand(Game.modeNumber);
					break;
				}
					
				
			}
		}
		else if (game.Gamestate == GameState.MAIN_MENU)
		{
			EntUIComponent uic = game.CheckUICollision();
			if (uic != null)
				uic.Activate(game);
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
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		MouseX = e.getX();
		MouseY = Game.realGameHeight - e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Card c = game.CheckCardCollision();
		if (c != null && !(c.glMIndex == 0))
		{
			if (e.getWheelRotation() > 0)
			{
				if (!c.Zoomed())
				{
					c.SetZoomed(true);
					c.SavePosition();
					if (c != null && c.glMIndex != 0)
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
