package jjj.entropy.ui;

import java.awt.Color;
import java.awt.Font;

import jjj.entropy.Game;

import com.jogamp.opengl.util.awt.TextRenderer;

/*
 * Add Set color
 * Add Set size / style / type (create new TextRenderer)
 */
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
	
	private TextRenderer renderer;
	private Color color;

	
	
	public EntFont(FontTypes type, int style, int size) {
		this(type, style, size, new Color(0.0f, 0.0f, 0.0f, 1.0f));
	}
	public EntFont(FontTypes type, int style, int size, Color color) {
		super(type.GetFontName(), style, size);
		this.color = color;
		
		renderer = new TextRenderer(this);
	}
	
	public void Render(int x, int y, String text)
	{
		renderer.beginRendering(Game.TEMP_GetAGame().GetWidth(), Game.TEMP_GetAGame().GetHeight());
        // optionally set the color
        renderer.setColor(color);
        renderer.draw(text, x, y);
        renderer.endRendering();
	}

	
}
