package jjj.entropy.ui;

import java.awt.Font;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import jjj.entropy.OGLManager;
import jjj.entropy.Game;
import jjj.entropy.Texture;
import jjj.entropy.classes.Const;

public class Button extends Clickable
{
	
	UIAction onClick;
	
	private String text;
	private EntFont  font;
	private Texture texture;

	private int textX,
				textY,
				glDisplayList;
	
	private Rectangle2D textBounds;
	
	public Button(int x, int y, int width, int height, String text, UIAction action)
	{
		this(x, y, width, height, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 28), Texture.bigButtonTexture, action);
	}
	
	public Button(int x, int y, int width, int height, String text, Texture texture, UIAction action)
	{
		this(x, y, width, height, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 28), texture, action);
	}
	
	public Button(int x, int y, int width, int height, String text, EntFont font, Texture texture, UIAction action)
	{
		super(x, y, width, height);
		this.text = text;
		this.font = font;
		this.onClick = action;

		this.texture = texture;
		
		//textProperties = font.getLineMetrics(text, font.GetFontRenderContext());	//CODE RESERVED FOR COPY AND USE IN OTHER CLASSES
		textBounds = font.GetRenderer().getBounds(text);
        
		this.glDisplayList = OGLManager.GenerateRectangularSurface(this.glWidth, this.glHeight);
		
        this.textX = screenX - (int) (textBounds.getWidth()/2+12);
        this.textY = screenY - (int) (textBounds.getHeight()/4);
	}
	
	
	@Override
	public void Render(Game game)
	{
		
		OGLManager.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    	
		if (texture != null)
		{
			texture.bind(OGLManager.gl);
			OGLManager.DrawShape(glX, glY, 0, glDisplayList);
		}
		font.Render(textX, textY, text);
		
		
	}


	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
		//textProperties = font.getLineMetrics(text, font.GetFontRenderContext());	//CODE RESERVED FOR COPY AND USE IN OTHER CLASSES
		textBounds = font.GetRenderer().getBounds(text);
		this.textX = x - ((int) textBounds.getWidth()/2);
        this.textY = y - (int) (textBounds.getHeight()/4);
				     
	}
	
	@Override
	public void Activate(int mouseX, int mouseY)
	{
		if (onClick != null)
			onClick.Activate();
	}

}
