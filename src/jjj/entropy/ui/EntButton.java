package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import jjj.entropy.GLHelper;
import jjj.entropy.Game;
import jjj.entropy.NetworkManager;
import jjj.entropy.Game.*;
import jjj.entropy.classes.Enums.*;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class EntButton extends EntClickable
{
	
	public enum ButtonSize{
		SMALL, MEDIUM, BIG
	}
	
	UIAction onClick;
	
	private String text;
	private EntFont  font;
	private Texture texture;
	
	private ButtonSize buttonSize;

	private int textOffsetX,
				textOffsetY;
	private int textX,
				textY;
	
	
	public EntButton(float x, float y, int xOffset, int yOffset, String text, Texture texture, UIAction action)
	{
		this(x, y, xOffset, yOffset, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 24), ButtonSize.BIG, texture, action);
	}
	public EntButton(float x, float y, int xOffset, int yOffset, String text, EntFont font, Texture texture, UIAction action)
	{
		this(x, y, xOffset, yOffset, text, font, ButtonSize.BIG, texture, action);
	}
	public EntButton(float x, float y, int xOffset, int yOffset, String text, ButtonSize size, Texture texture, UIAction action)
	{
		this(x, y, xOffset, yOffset, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 24), size, texture, action);
	}
	
	public EntButton(float x, float y, int xOffset,int yOffset, String text, EntFont font, ButtonSize size, Texture texture, UIAction action)
	{
		super(x, y, GetButtonWidth(size),  GetButtonHeight(size));	//Using static methods as a "hack" to give conditional parameters to super class
		this.text = text;
		this.font = font;
		this.buttonSize = size;
		this.onClick = action;

		this.texture = texture;

		textOffsetX = xOffset;
	    textOffsetY = yOffset;
        
        this.textX = screenX;
        this.textY = screenY;
	}
	
	
	//Following two methods are used as a "hack" to get some conditional parameters to the super class constructor
	private static float GetButtonWidth(ButtonSize size)
	{
	 	switch (size)
		{
		case BIG:
			return Game.BIG_BUTTON_WIDTH;
		default:
			return 0.0f;
		}
	}
	private static float GetButtonHeight(ButtonSize size)
	{
	 	switch (size)
		{
		case BIG:
			return Game.BIG_BUTTON_HEIGHT;
		default:
			return 0.0f;
		}
	}
	
	
	
	public void Render(Game game)
	{
		
		Game.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    	
		switch (buttonSize)
		{
		case BIG:
			if (texture != null)
			{
				texture.bind(Game.gl);
				GLHelper.DrawUIBigButton(Game.gl, this);
			}
			font.Render(game, textX + textOffsetX, textY - textOffsetY, text);
			break;
		default:
			break;
		}
		
		
	}
	
		
	/*	Shouldn't be needed
	@Override
	public void OnResize(int[] view, double[] model , double[] proj)
	{
		super.UpdateScreenCoords();
	    double winPos[] = new double[4];// wx, wy, wz;// returned xyz coords
        Game.glu.gluProject((double) x, (double) y, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        this.screenLeft = (int) winPos[0];
        this.screenTop = (int) winPos[1];
        double right = 0, bottom = 0;
 		switch (buttonSize)
 		{
 		case BIG:
 			right = (double) x+Game.BIG_BUTTON_WIDTH;
 			bottom = (double) y-Game.BIG_BUTTON_HEIGHT;
 			break;
 		default:
 			break;
 		}
         
         Game.glu.gluProject(right, bottom, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        this.screenRight = (int) winPos[0];
        this.screenBottom =(int) winPos[1];
        
        this.w = this.screenRight - this.screenLeft;
        this.h = this.screenTop - this.screenBottom ;
	}*/
	

	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
	}
	
	@Override
	public void Activate(int mouseX, int mouseY)
	{
		onClick.Activate();
	}
}
