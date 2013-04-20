package jjj.entropy.ui;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.media.opengl.GLException;

import jjj.entropy.OGLManager;
import jjj.entropy.Game;
import jjj.entropy.classes.Const;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Dropdown <T> extends Clickable
{
	
	
	private List<T> dataSource;	//The data in the list 	
	private String[] data;				//The internal data, preventing side effects. UpdateData must be called to update it
	
	
	private UIAction onSelection;
	
	private EntFont  font;
	jjj.entropy.Texture texture,
						selectedTexture;
	private boolean selecting;	// True if menu is open/dropped down
	
	private int textX,
				textY,
				rowHeight,
				
				selectedIndex,
				glDisplayListRow;
	
	private float glRowHeight,	
	screenRowHeight;
	

	
	public Dropdown(int x, int y, int width, int rowHeight, List<T> dataSource, UIAction onSelection)
	{
		super(x, y, width, rowHeight * dataSource.size());	//Set the height to the height it would be at when selecting
		this.dataSource = dataSource;
		this.font = new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16);
		this.rowHeight = rowHeight;
		
		this.onSelection = onSelection;
		
		data = null;
		UpdateData();
		

		this.rowHeight = rowHeight;
		Rectangle2D textBounds = font.GetRenderer().getBounds("A");
		
		this.textX = screenX - screenWidth/2 + (int)textBounds.getWidth();	//Left orientation
	    this.textY = screenY - (int) (textBounds.getHeight()/2.5);
        
	    
        selecting = false;
        selectedIndex = 0;
        
        
        //Uses MapPercentToScreenX/FloatWidth instead of MapPercentToScreenY/FloatHeight to make it easier for users to estimate proportions. This needs to match method used in the Clickable class
        screenRowHeight = ((float)Game.GetInstance().GetGameWidth())*((float)rowHeight/100)-1.2f;	//0.9 added as a magic adjustment value, note that this assignment is always overwritten by OnResize assignment
        

        glRowHeight = OGLManager.MapPercentToFloatWidth(rowHeight);
        
        
        this.glDisplayListRow = OGLManager.GenerateRectangularSurface(this.glWidth, this.glRowHeight);

        texture = jjj.entropy.Texture.dropdownEntry;
        selectedTexture = jjj.entropy.Texture.dropdownEntrySelected;
        
	}
	
	
	
	
	
	@Override
	public void Render(Game game)
	{
		texture.bind(OGLManager.gl);
	
		if (selecting)
		{
			texture.bind(OGLManager.gl);
			OGLManager.DrawVerticallyRepeatingRectanglularShape(glX, glY, glWidth, glRowHeight, data.length);
			selectedTexture.bind(OGLManager.gl);
			OGLManager.DrawShape(glX, glY-(glRowHeight*selectedIndex), 0, glDisplayListRow);
			float yOffset = textY;
			
			for (Object o : dataSource)
			{
				font.Render(textX, (int)yOffset, o.toString());
				yOffset -= (screenRowHeight);
			}
		}
		else
		{
			OGLManager.DrawShape(glX, glY, 0, glDisplayListRow);
			font.Render(textX, textY, dataSource.get(selectedIndex).toString());
		}
		
	}
	
	@Override
	public boolean CheckCollision(int mouseX, int mouseY) 
	{
		if ( mouseX > screenX-screenWidth/2 && mouseX < screenX+screenWidth/2 &&
			 mouseY < screenY+screenRowHeight/2 && ((selecting && mouseY > screenY - screenRowHeight*(data.length-0.5)) || (!selecting && mouseY > screenY - screenRowHeight/2)))
		{
			return true;
		}
		return false;
	}
	
	
	
	public void UpdateData()
	{
		if (data == null || data.length != dataSource.size())
			data = new String[dataSource.size()];
		
		for (int i = 0; i < dataSource.size(); i++)
		{
			data[i] = dataSource.get(i).toString();
		}
	
	}


	@Override
	public void Activate(int mouseX, int mouseY)
	{
		if (!selecting)
		{
			selecting = true;
		}
		else
		{
			selectedIndex = (int) ((screenY + screenRowHeight/2 - mouseY) / (screenRowHeight)); 
			selecting = false;
			if (onSelection != null)
				onSelection.Activate();
		}
	}


	public int GetSelectedIndex() 
	{
		return selectedIndex;
	}
	
	//Assumes UpdateData has been called before the last change to the dataSource
	public T GetSelectedObject()
	{
		return dataSource.get(selectedIndex);
	}


	public void SetDataSource(List<T> dataSource) 
	{
		this.dataSource = dataSource;
		data = null;
		UpdateData();
		SetPercentHeight(rowHeight * dataSource.size());
	}
}
