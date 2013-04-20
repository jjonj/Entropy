package jjj.entropy.ui;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import jjj.entropy.OGLManager;
import jjj.entropy.Game;
import jjj.entropy.Texture;
import jjj.entropy.classes.Const;

public class Textbox extends Clickable{
	

	
	private String text;
	private EntFont  font;
	
	private Texture texture;
	
	private int textX,
				textY;
	
	private int glDisplayList;

	
	
	public Textbox(int x, int y, int width, int height, Texture texture)
	{
		this(x, y, width, height, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16), texture);
	}
	public Textbox(int x, int y, int width, int height, String text, Texture texture)
	{
		this(x, y, width, height, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16), texture);
	}
	
	public Textbox(int x, int y, int width, int height, String startText, EntFont font, Texture texture )
	{
		super(x, y, width, height);
		this.text = startText;
		this.font = font;
		this.texture = texture;
		
		this.glDisplayList = OGLManager.GenerateRectangularSurface(this.glWidth, this.glHeight);
		
		
		Rectangle2D textBounds = font.GetRenderer().getBounds("A");
		
		
		this.textX = screenX - screenWidth/2 + ((int) textBounds.getWidth()/2);
        this.textY = screenY - (int) (textBounds.getHeight()/2.5);
	}

  
	@Override
	public void Render(Game game)
	{
		if (texture != null)
		{
			texture.bind(OGLManager.gl);
			OGLManager.DrawShape(glX, glY, 0, glDisplayList);
		}
		font.RenderBox(game, textX, textY, 1, screenWidth, text);
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
