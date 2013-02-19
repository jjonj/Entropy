package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;

import jjj.entropy.Game;
import jjj.entropy.classes.Const;

import com.jogamp.opengl.util.awt.TextRenderer;

public class Label extends UIComponent{
	

	
	private String text;
	private EntFont  font;
	private boolean boxed;
	private int maxLines; 
	
	public Label(Game game, int x, int y)
	{
		this(x, y, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16));
	}
	public Label(Game game, int x, int y, String text)
	{
		this(x, y, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16));
	}
	public Label(int x, int y, String text, EntFont font)
	{
		super(x, y);
		this.text = text;
		this.font = font;
	}
	public Label(int x, int y, int maxLines, String text, EntFont font)
	{
		super(x, y);
		this.text = text;
		this.font = font;
		boxed = true;
		this.maxLines = maxLines;
	}
	
	public void Render(Game game)
	{
		if (boxed)
			font.RenderBox(game, (int)x, (int)y, maxLines, Const.CHAT_LINE_WIDTH, text);
		else
			font.Render(game, (int)x, (int)y, text);
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