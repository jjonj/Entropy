package jjj.entropy;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.ui.EntFont;
import jjj.entropy.ui.EntLabel;
import jjj.entropy.ui.EntUIComponent;

public class EntKeyListener implements KeyListener {

	
    private static EntLabel chatTypeArea;
    private static EntLabel chatWindow;
	
    public EntKeyListener()
    {
    	chatWindow = new EntLabel(129, 116,  Game.CHAT_LINES, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE));
    	chatTypeArea = new EntLabel(122, 10, 1, "",  new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 12, Color.BLUE));
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
	        	
	        //	if (e.getKeyChar() != '\n')
	        	if (chatTypeArea.GetText() != "")
	        		chatWindow.AppendText("You:\u00A0" + chatTypeArea.GetText() + '\n');	// \f is replaced with a space
	        	chatTypeArea.SetText("");
	            break;
	        case KeyEvent.VK_BACK_SPACE:
	        	chatTypeArea.RemoveFromEnd();
	        	break;
	        case KeyEvent.VK_ESCAPE:
	        	switch (Game.Gamestate)
	        	{
	        	case LOGIN:
	        		System.exit(0);
	        		break;
	        	case MAIN_MENU:
	        		Game.SetState(GameState.LOGIN);
	        		break;
	        	case IN_GAME:
	        		Game.SetState(GameState.MAIN_MENU);
	        		break;
	        	}
	        	
	        	
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
		// TODO Auto-generated method stub

		//System.out.print(Character.toString(e.getKeyChar()).replace('\n', 'å'));
		if (e.getKeyChar() != '\n' && e.getKeyChar() != '\b')
			chatTypeArea.AppendText(Character.toString(e.getKeyChar()));
		
		
	}

	public static EntUIComponent GetChatTypelabel() {
		return chatTypeArea;
	}
	public static EntUIComponent GetChatWindowlabel() {
		return chatWindow;
	}
	
	
}
