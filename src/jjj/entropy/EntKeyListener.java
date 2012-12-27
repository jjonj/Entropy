package jjj.entropy;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import jjj.entropy.classes.Enums.GameState;
import jjj.entropy.ui.EntFont;
import jjj.entropy.ui.EntLabel;
import jjj.entropy.ui.EntTextbox;
import jjj.entropy.ui.EntUIComponent;

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
	        	
	        	EntTextbox chatTextbox = Game.GetInstance().GetChatTextbox();
	        	if (chatTextbox.GetText() != "")
	        		Game.GetInstance().GetChatWindowlabel().AppendText("You:\u00A0" + chatTextbox.GetText() + '\n');	// \f is replaced with a space
	        	chatTextbox.SetText("");
	            break;
	        case KeyEvent.VK_BACK_SPACE:
	        	EntUIComponent ui = Game.GetInstance().GetFocusedUIComponent();
				if (ui instanceof EntTextbox)
					((EntTextbox) ui).RemoveFromEnd();
	        	break;
	        case KeyEvent.VK_ESCAPE:
	        	switch (Game.GetInstance().GetGameState())
	        	{
	        	case LOGIN:
	        		System.exit(0);
	        		break;
	        	case MAIN_MENU:
	        		Game.GetInstance().SetGameState(GameState.LOGIN);
	        		break;
	        	case IN_GAME:
	        		Game.GetInstance().SetGameState(GameState.MAIN_MENU);
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

		//System.out.print(Character.toString(e.getKeyChar()).replace('\n', '�'));
		if (e.getKeyChar() != '\n' && e.getKeyChar() != '\b')
		{
			EntUIComponent ui = Game.GetInstance().GetFocusedUIComponent();
			if (ui instanceof EntTextbox)
				((EntTextbox) ui).AppendText(Character.toString(e.getKeyChar()));
		}
		
	}
	
}
