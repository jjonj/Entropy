package jjj.entropy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import jjj.entropy.ui.Textbox;
import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class EntKeyListener implements KeyListener {

	
	
    public EntKeyListener()
    {
    }
    
	@Override
	public void keyPressed(KeyEvent e) {
		
		
		int keyCode = e.getKeyCode();
	
			

		
	    switch( keyCode ) { 
	        case KeyEvent.VK_L:
	            // handle up 
	        	Game.mode = 1;
	            break;
	        case KeyEvent.VK_G:
	        	Game.mode = 2;
	            break;
	        case KeyEvent.VK_Z:
	        	Game.mode = 3;
	            break;
	        case KeyEvent.VK_H :
	        	Game.mode = 4;
	            break;
	        case KeyEvent.VK_ENTER:
	        	
	        	Textbox chatTextbox = UIManager.GetInstance().GetChatTextbox();
	        	if (chatTextbox.GetText() != "")
	        		UIManager.GetInstance().GetChatWindowlabel().AppendText("You:\u00A0" + chatTextbox.GetText() + '\n');	// unicode is replaced with a space
	        	chatTextbox.SetText("");
	            break;
	        case KeyEvent.VK_BACK_SPACE:
	        	UIComponent ui = UIManager.GetInstance().GetFocusedUIComponent();
				if (ui instanceof Textbox)
					((Textbox) ui).RemoveFromEnd();
	        	break;
	        case KeyEvent.VK_ESCAPE:
	        	Game.GetInstance().ExitGameState();
	        
	        	
	        	
	        	break;
	        default:
	            Game.modeNumber = keyCode-48; //Numbers
	        	break;
	     }
		
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void keyTyped(KeyEvent e) {

		//System.out.print(Character.toString(e.getKeyChar()).replace('\n', 'å'));
		if (e.getKeyChar() != '\n' && e.getKeyChar() != '\b')
		{
			UIComponent ui = UIManager.GetInstance().GetFocusedUIComponent();
			if (ui instanceof Textbox)
				((Textbox) ui).AppendText(Character.toString(e.getKeyChar()));
		}
		
	}
	
}
