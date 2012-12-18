package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;

import jjj.entropy.Game;

import com.jogamp.opengl.util.awt.TextRenderer;

public class EntLabel extends EntUIComponent{
	

	
	private String text;
	private EntFont  font;
	
	
	
	public EntLabel(Game game, int x, int y)
	{
		this(x, y, "", new EntFont(game, EntFont.FontTypes.MainParagraph, Font.PLAIN, 16));
	}
	public EntLabel(Game game, int x, int y, String text)
	{
		this(x, y, text, new EntFont(game, EntFont.FontTypes.MainParagraph, Font.PLAIN, 16));
	}
	public EntLabel(int x, int y, String text, EntFont font)
	{
		super(x, y);
		this.text = text;
		this.font = font;
	}
	 
	
	public void Render()
	{
		// optionally set the color
		font.Render(x, y, text);
	}
	
	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
	}
	
	
}
