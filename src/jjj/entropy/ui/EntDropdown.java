package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import jjj.entropy.GLHelper;
import jjj.entropy.Game;
import jjj.entropy.NetworkManager;
import jjj.entropy.Game.*;
import jjj.entropy.classes.Enums.*;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class EntDropdown
{
	
	
	private List<Object> dataSource;	//The data in the list 	
	private String[] data;				//The internal data, preventing side effects. UpdateData must be called to update it
	
	
	
	private EntFont  font;
	private Texture texture,
					selectedFieldTexture;
	private boolean selecting;	// True if menu is open/dropped down
	
	private int textX,
				textY,
				fontLineHeight,
				selectedIndex;
	private float lineHeight;
	
	
	
	public void Render(Game game)
	{
	
	
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




	public Texture GetSelectedTexture() 
	{
		return selectedFieldTexture;
	}

	public int GetSelectedIndex() 
	{
		return selectedIndex;
	}
	
	//Assumes UpdateData has been called before the last change to the dataSource
	public Object GetSelectedObject()
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

/*	//The dimensions of the dropdown cant be adjusted on event thread, so disabling this functionality

	public void SetDataSource(List<Object> dataSource) 
	{
		this.dataSource = dataSource;
		data = null;
		UpdateData();
	}*/
}
