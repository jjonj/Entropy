package jjj.entropy.ui;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.media.opengl.GLException;

import jjj.entropy.GLHelper;
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
	private Texture texture,
					selectedFieldTexture;
	private boolean selecting;	// True if menu is open/dropped down
	
	private int textX,
				textY,
				fontLineHeight,
				selectedIndex;
	private float lineHeight;
	

	
	public Dropdown(float x, float y, int xOffset,int yOffset, List<T> dataSource, UIAction onSelection)
	{
		super(x, y, Const.DROPDOWN_WIDTH, Const.DROPDOWN_ROW_HEIGHT * dataSource.size());	//Set the height to the height it would be at when selecting
		this.dataSource = dataSource;
		this.font = new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16);

		
		this.onSelection = onSelection;
		
		data = null;
		UpdateData();
		

		this.textX = screenX+xOffset;
        this.textY = screenY+yOffset;
        
        selecting = false;
        selectedIndex = 0;

		int[] temp =  GLHelper.ConvertGLFloatToGLScreen(0, 0);
		@SuppressWarnings("unused")
		float zeroOnScreenX = temp[0],
		      zeroOnScreenY = temp[1];
		
		temp =  GLHelper.ConvertGLFloatToGLScreen(0, Const.DROPDOWN_ROW_HEIGHT);
		lineHeight = temp[1] - zeroOnScreenY;
		fontLineHeight = (int)lineHeight+1;
        
		
		try {	//Temporarily just using the textures from EntTable
			texture = TextureIO.newTexture(new File("resources/textures/TableEntry.png"), true);
			selectedFieldTexture = TextureIO.newTexture(new File("resources/textures/SelectedTableEntry.png"), true);
		} catch (GLException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	
	
	@Override
	public void Render(Game game)
	{
		if (texture != null)
		{
			texture.bind(Game.gl);
			GLHelper.DrawUIDropdown(Game.gl, this);
		}
		
	
		if (selecting)
		{		
			int yOffset = textY;
			for (Object o : dataSource)
			{
				font.Render(game, textX, yOffset, o.toString());
				yOffset -= fontLineHeight;
			}
		}
		else
			font.Render(game, textX, textY, dataSource.get(selectedIndex).toString());
				
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
			if (mouseY > screenY - h/data.length)	//Only accept clicks on the visible element when not selecting
				selecting = true;
		}
		else
		{
			selectedIndex = (screenY - mouseY) / (h/data.length); 
			selecting = false;
			if (onSelection != null)
				onSelection.Activate();
		}
	}

	public Texture GetSelectedTexture() 
	{
		return selectedFieldTexture;
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

	public float GetDataSize() {
		return data.length;
	}

	public boolean IsSelecting() {
		return selecting;
	}



	public Texture GetTexture() {
		return texture;
	}


	public void SetDataSource(List<T> dataSource) 
	{
		this.dataSource = dataSource;
		data = null;
		UpdateData();
		SetGLHeight(Const.DROPDOWN_ROW_HEIGHT * dataSource.size());
		
	}
}
