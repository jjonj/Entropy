package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;

import javax.media.opengl.GL2;

import jjj.entropy.GLHelper;
import jjj.entropy.Game;
import jjj.entropy.NetworkManager;
import jjj.entropy.classes.Const;
import jjj.entropy.classes.Enums.GameState;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class Textbox extends Clickable{
	

	
	private String text;
	private EntFont  font;
	
	private int textOffsetX,
				textOffsetY;
	private Texture texture;
	
	private int textX,
				textY;

	
	
	public Textbox(float x, float y, int xOffset, int yOffset, Texture texture)
	{
		this(x, y, xOffset, yOffset, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16), texture);
	}
	public Textbox(float x, float y, int xOffset, int yOffset, String text, Texture texture)
	{
		this(x, y, xOffset, yOffset, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16), texture);
	}
	
	public Textbox(float x, float y, int xOffset, int yOffset, String startText, EntFont font, Texture texture )
	{
		super(x, y, Const.TEXTBOX_WIDTH, Const.TEXTBOX_HEIGHT);
		this.text = startText;
		this.font = font;
		this.texture = texture;
			
	    textOffsetY = yOffset;
	    textOffsetX = xOffset;
	    this.textX = screenX;
	    this.textY = screenY;
	}

  
	public void Render(Game game)
	{
		if (texture != null)
		{
			Game.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			if (texture != null)
				texture.bind(Game.gl);
			 GLHelper.DrawUITextbox(Game.gl, this);
		}
		font.RenderBox(game, textX + textOffsetX, textY - textOffsetY, 1, Const.TEXTBOX_LINE_WIDTH, text);
	}
	
	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
	}
	public void AppendText(String text)
	{
		this.text = this.text + text;
	}
	public void RemoveFromEnd(int number)
	{
		if (text.length() >= number)
			this.text = this.text.substring(0, this.text.length() - number);
	}
	public void RemoveFromEnd()
	{
		RemoveFromEnd(1);
	}



	
} 