package jjj.entropy.ui;

import java.awt.Font;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import jjj.entropy.OGLManager;
import jjj.entropy.Game;
import jjj.entropy.Texture;
import jjj.entropy.classes.Const;

public class Preview extends Clickable
{
	
	UIAction onClick;
	
	private String text,
				   description;
	private EntFont titleFont,
					descFont;
	private Texture texture,
					imageTexture;

	private int textX,
				textY,
				descTextY,
				descTextX,
				screenTextWidth,
				glDisplayList,
				glImageDisplayList;
	
	private Rectangle2D textBounds;
	
	public Preview(int x, int y, int width, int height, String text, String description, UIAction action)
	{
		this(x, y, width, height, text, description, null,  action);
	}
	
	public Preview(int x, int y, int width, int height, String text, String description, Texture image, UIAction action)
	{
		this(x, y, width, height, text, description, Texture.previewTexture, image, action);
	}

	public Preview(int x, int y, int width, int height, String text, String description, Texture texture, Texture image, UIAction action)
	{
		this(x, y, width, height, text, description, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 14), new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 12), texture, image, action);
	}
	
	public Preview(int x, int y, int width, int height, String text, String description, EntFont titleFont, EntFont descFont, Texture texture, Texture image, UIAction action)
	{
		super(x, y, width, height);
		this.text = text;
		this.description = description;
		this.titleFont = titleFont;
		this.descFont = descFont;
		this.onClick = action;

		this.texture = texture;
		this.imageTexture = image;
		
		textBounds = descFont.GetRenderer().getBounds(text);
        
		this.glDisplayList = OGLManager.GenerateRectangularSurface(this.glWidth, this.glHeight);
		this.glImageDisplayList =  OGLManager.GenerateRectangularSurface(this.glHeight*0.75f, this.glHeight*0.75f);
        this.textX = screenX - screenWidth/2 + screenHeight + ((int)(screenWidth*0.86)-screenHeight)/2 - (int)textBounds.getWidth()/2;	//0.86 is used to discount the darker edge part of the width
        this.descTextX = screenX - screenWidth/2 + screenHeight;
        this.textY = screenY + (int)(screenHeight/2.8) - (int) (textBounds.getHeight());
        this.descTextY = textY - (int)(textBounds.getHeight()*1.7);
        
        screenTextWidth = screenWidth - (int)	(screenHeight*1.08);
        
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
		if (imageTexture != null)
		{
			imageTexture.bind(OGLManager.gl);
			OGLManager.DrawShape(glX-glWidth/2+glHeight*0.525f, glY, 0, glImageDisplayList);
		}
		titleFont.Render(textX, textY, text);
	//	titleFont.RenderBox(textX, textY, 2, screenTextWidth, text);	//For this to be functional, it needs to be computable how much the title fills on Y to determine the offset of description
		descFont.RenderBox(descTextX, descTextY, 10, screenTextWidth,description);
		
	}
	
	@Override
	public void Activate(int mouseX, int mouseY)
	{
		if (onClick != null)
			onClick.Activate();
	}

}
