package jjj.entropy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class EntKeyListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.print(e.toString());
		
	//	Game.GetObjectID(500, 500);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("LOL");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.print("LOL");
	}

	
	
}
