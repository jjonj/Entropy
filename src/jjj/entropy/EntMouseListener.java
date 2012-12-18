package jjj.entropy;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import jjj.entropy.Card.Facing;
import jjj.entropy.Card.Status;
import jjj.entropy.Game.Life;
import jjj.entropy.Game.Zone;

public class EntMouseListener implements MouseListener, MouseMotionListener {

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
		Card c = game.CheckCardCollision();
		
		
		if (c != null)
		{
			if (c.glMIndex == 0)
			{
				c = new Card(-3, 0.51f, 1.0f, 
						Facing.DOWN, game.TinidQueen, Status.IN_ZONE);
				
				game.ShowCard(c);
				
			}

			wtf++;
			//c.SetTarget(c.GetX(), 0, c.GetZ()+3, 90, 0, 0);
			
			switch(wtf)
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
			case 5:
				c.PlayToZone(Zone.ZONE1);
				break;
			case 6:
				c.PlayToZone(Zone.ZONE2);
				break;
			case 7:
				c.PlayToZone(Zone.ZONE3);
				break;
			case 8:
				c.PlayToZone(Zone.ZONE4);
				break;
			case 9:
				c.PlayToLimbo();
				break;
			case 10:
				c.PlayToLimbo();
				break;
			default:
				c.PlayToHand(wtf - 11);
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
		// TODO Auto-generated method stub
		MouseX = e.getX();
		MouseY = e.getY();
	}


	
	
}
