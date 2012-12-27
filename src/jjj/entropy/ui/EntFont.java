package jjj.entropy.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jjj.entropy.Game;

import com.jogamp.opengl.util.awt.TextRenderer;


@SuppressWarnings("serial")
public class EntFont extends Font {

	private float lengthOfAChar;
	
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
	
	public void Render(Game game, int x, int y, String text)
	{
		if (text.length() > 0)
		{
			renderer.beginRendering(game.GetWidth(), game.GetHeight());
	        renderer.setColor(color);
	        renderer.draw(text, x, y);
	        renderer.endRendering();
		}
	}

	private Rectangle2D rec;
	List<String> cLines = new ArrayList<String>();
	public void RenderBox(Game game, int x, int y, int maxLines, int lineWidth, String text)
	{
		if (text.length() > 0)
		{
			cLines.clear();
		
			lengthOfAChar = 8f;
			int heightLineSkip = 0;
			
			String[] lines = text.split("\n");
		
	        
	        for (String s : lines)
	        {
	        	SplitStringRec(s, lineWidth);
	        }
	        
	        
	        int index = cLines.size() - maxLines;
	        if (index < 0) 
	        	index = 0;
	        
	        renderer.setColor(color);
	    	renderer.beginRendering(game.GetWidth(), game.GetHeight());

	        for (;index < cLines.size(); index++)
	        {
	        	   renderer.draw(cLines.get(index), x, y-heightLineSkip);
	        	   heightLineSkip += super.size+5;
	        }
	        renderer.endRendering();
		}
	}

	private void SplitStringRec(String s, int lineWidth) {
		//rec = renderer.getBounds(s);
		
		//if (s.length() > charsPerLine)
		if (renderer.getBounds(s).getWidth() > lineWidth)
		{
			String[] words = s.split(" ");
			int i = 0;
			
			String addString = "";
			String tempString = "";
			while (i < words.length)
			{
				if (renderer.getBounds(words[i]).getWidth() > lineWidth)
				{
					String s2 = words[i];
					//Split word into lines of correct length, add them and put the rest back into the word
					do 
					{
						int charsPerLine = GetLengthOfStringWidth(s2, lineWidth);
						cLines.add(s2.substring(0, charsPerLine-1) + "-");
						s2 = s2.substring(charsPerLine-1, s2.length());
					} while (renderer.getBounds(s2).getWidth() > lineWidth);
					words[i] = s2;
				}
				tempString = addString + " " +  words[i];
				if (renderer.getBounds(tempString).getWidth() > lineWidth)
				{
					cLines.add(addString);
					addString = "";
				}
				else
				{
					addString = tempString;
					++i;
				}
			}
			cLines.add(addString);
			addString = "";
		}
		else
		{
			
			cLines.add(s);	
		}
	}
	
	//Finds the number of chars frmo a string to include to obtain a given length
	public int GetLengthOfStringWidth(String s, float width) 
	{
		int i = s.length()/2;
		while (renderer.getBounds(s.substring(0, i)).getWidth() > width)
		{
			i /= 2;
		}
		while(renderer.getBounds(s.substring(0, i)).getWidth() < width )
		{
			++i;
		}
		return i-1;
		
	}
	public TextRenderer GetRenderer() {
		return renderer;
	}
	
}
