package jjj.entropy.ui;

import java.awt.Color;
import java.awt.Font;

import jjj.entropy.Game;

import com.jogamp.opengl.util.awt.TextRenderer;


@SuppressWarnings("serial")
public class EntFont extends Font {

	public enum FontTypes{
		MainTitle("SansSerif"),
		MainParagraph("SansSerif");
		
		private String font;
		
		private FontTypes(String font) 
		{	
			this.font = font;
		}
		
		public String GetFontName() 
		{
		   	 return font;
		}
	}
	
	private Game game;
	private TextRenderer renderer;
	private Color color;

	
	
	public EntFont(Game game, FontTypes type, int style, int size) {
		this(game, type, style, size, new Color(0.0f, 0.0f, 0.0f, 1.0f));
	}
	public EntFont(Game game, FontTypes type, int style, int size, Color color) {
		super(type.GetFontName(), style, size);
		this.color = color;
		this.game = game;
		renderer = new TextRenderer(this);
	}
	
	public void Render(int x, int y, String text)
	{
		renderer.beginRendering(game.GetWidth(), game.GetHeight());
        // optionally set the color
        renderer.setColor(color);
        renderer.draw(text, x, y);
        renderer.endRendering();
	}

	
}
