package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.media.opengl.GLException;

import jjj.entropy.Card;
import jjj.entropy.EntMouseListener;
import jjj.entropy.GLHelper;
import jjj.entropy.Game;
import jjj.entropy.classes.Enums.GameState;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

//MISSING: Add parameter for text offset coordinates or even better automatize it
public class EntTable extends EntClickable implements MouseListener, MouseMotionListener
{
	
	
	private EntFont  font;
	private List<IEntTableRow> dataSource;	//Two dimensional array of data
	private String[][] data;
	
	private Texture texture;
	private Texture scrollHandleTexture,
					selectedFieldTexture;
	private float lineHeight,	//The height of the font used
				  scrollHandleYOffsetGLFloat;	//The offset of the scroll handle in GL float coordinates
	private GameState activeGameState;
	private int fontLineHeight,
				textX,
				textY,
				selectedIndex = 2,
				scrollHandleY,
				scrollHandleWidth,
				scrollHandleHeight,
				maxLines,
				displayLineCount,
				offsetScrollHandleTop,
				mouseOffSetFromTaTop,
				lineOffset = 0;	// Used for scrolling
	private boolean scrolling = false;

	public EntTable(float x, float y,  int offsetX, int offsetY,  List<IEntTableRow> dataSource, GameState activeGameState)
	{
		this(x, y, offsetX, offsetY, dataSource, dataSource.size(), activeGameState);
	}
	public EntTable(float x, float y, int offsetX, int offsetY, List<IEntTableRow> dataSource, int maxLines, GameState activeGameState)
	{
		//Width should be adjustable, probably like button size "is"
		super(x, y, Game.TABLE_WIDTH, Game.TABLE_ROW_HEIGHT*maxLines);
		this.maxLines = maxLines;
		this.font = new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16);
		this.dataSource = dataSource;
		this.activeGameState = activeGameState;
		
		int[] temp =  GLHelper.ConvertGLFloatToGLScreen(0, 0);
		float zeroOnScreenX = temp[0],
		      zeroOnScreenY = temp[1];
		
		temp =  GLHelper.ConvertGLFloatToGLScreen(0, Game.TABLE_ROW_HEIGHT);
		lineHeight = temp[1] - zeroOnScreenY;
	
		scrollHandleWidth = (int) (temp[0] - zeroOnScreenX);
		
		temp = GLHelper.ConvertGLFloatToGLScreen(0, Game.SCROLL_HANDLE_HEIGHT);
		scrollHandleHeight = (int) (temp[1] - zeroOnScreenY);
		
		
		fontLineHeight = (int)lineHeight+1;
		//selectedIndex = -1;
		try {
			texture = TextureIO.newTexture(new File("resources/textures/TableEntry.png"), true);
			scrollHandleTexture = TextureIO.newTexture(new File("resources/textures/ScrollHandle.png"), true);
			selectedFieldTexture = TextureIO.newTexture(new File("resources/textures/SelectedTableEntry.png"), true);
		} catch (GLException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		GLHelper.InitTexture(Game.gl, texture);
		GLHelper.InitTexture(Game.gl, scrollHandleTexture);
		GLHelper.InitTexture(Game.gl, selectedFieldTexture);
		temp = GLHelper.ConvertGLFloatToGLScreen(x, y);
		this.textX = temp[0] + offsetX;
	    this.textY = temp[1] + offsetY;
		
	    scrollHandleY = screenY;
	    scrollHandleYOffsetGLFloat = 0;
		if (dataSource.size() < maxLines)
			displayLineCount = dataSource.size();
		else
			displayLineCount =  maxLines;
		
		UpdateData();
		
		 Game.GetInstance().GetCanvas().addMouseListener(this);
		 Game.GetInstance().GetCanvas().addMouseMotionListener(this);
	}
	
	//Method to update the internal data structure with the current data source
	public void UpdateData()
	{
		if (data == null || data.length != dataSource.size())
			data = new String[dataSource.size()][];
		
		for (int i = 0; i < dataSource.size(); i++)
		{
			data[i] = dataSource.get(i).GenRow();
		}
		
	}
	
	public void Render(Game game)
	{
		int i,
			xOffset = textX,
			yOffset = textY;
		
		
		if (texture != null)
		{
			texture.bind(Game.gl);
			GLHelper.DrawUITable(Game.gl, this);
		}
		
		
		for (int k = lineOffset; k < displayLineCount+lineOffset; k++)
		{
			i = 0;
			for (String cell : data[k])
			{
				xOffset = (int)textX + i * Game.TABLE_COLUMN_WIDTH_PX;
				font.Render(game, xOffset, yOffset, cell);
				++i;
			}
			yOffset -= fontLineHeight;
		}
	}
	
/*	@Override
	public void Activate(int mouseX, int mouseY)
	{
		if ((screenX+w - mouseX) < 7 && mouseY > scrollHandleY - Game.SCROLL_HANDLE_HEIGHT_PX && mouseY < scrollHandleY)
		{
			lineOffset = 1;
		}
	}*/
	
	
	public void SetDataSource(List<IEntTableRow> dataSource) 
	{
		this.dataSource = dataSource;
		if (dataSource.size() < maxLines)
			displayLineCount = dataSource.size();
		else
			displayLineCount =  maxLines;
		UpdateData();
	} 
	
	
	public int GetLineCount() 
	{
		return dataSource.size();
	}
	
	public int GetLineCountToRender() 
	{
		return displayLineCount;
	}
	
	public float GetScrollHandleGLYOffset()
	{
		return scrollHandleYOffsetGLFloat;
	}
	
	public Texture GetScrollHandleTexture() 
	{
		return scrollHandleTexture;
	}
	public boolean DisplayScrollbar() 
	{
		return displayLineCount < dataSource.size();
	}
	
	@Override
	public void OnResize(int[] view, double[] model , double[] proj) 
	{
		System.out.println("RESIZE!!!");
		UpdateScreenCoords();
		scrollHandleY = screenY - mouseOffSetFromTaTop;
		int[] temp =  GLHelper.ConvertGLFloatToGLScreen(0, 0);
		float zeroOnScreenX = temp[0],
		      zeroOnScreenY = temp[1];
		
		temp =  GLHelper.ConvertGLFloatToGLScreen(0, Game.TABLE_ROW_HEIGHT);
		lineHeight = temp[1] - zeroOnScreenY;
		
	
		scrollHandleWidth = (int) (temp[0] - zeroOnScreenX);
		
		temp = GLHelper.ConvertGLFloatToGLScreen(0, Game.SCROLL_HANDLE_HEIGHT);
		scrollHandleHeight = (int) (temp[1] - zeroOnScreenY);
		
		//Resetting the scrollbar position on resize simplifies problems with pixel calculations done in one window size transfering to another. It could be done with converting to GL coordinates before resize then recalculating the new pixel values using GLHelperConvert
		scrollHandleY = screenY;
	    lineOffset = 0;
	    scrollHandleYOffsetGLFloat = 0;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if ( Game.GetInstance().GetGameState() == activeGameState)	
		{
			if (e.getButton() == MouseEvent.BUTTON1 && screenX < EntMouseListener.MouseX && screenX+w > EntMouseListener.MouseX && screenY > EntMouseListener.MouseY && screenY-(displayLineCount*lineHeight) < EntMouseListener.MouseY)
			{																	
				if (screenX+w - EntMouseListener.MouseX < scrollHandleWidth && EntMouseListener.MouseY > scrollHandleY - scrollHandleHeight  && EntMouseListener.MouseY < scrollHandleY)
				{
					offsetScrollHandleTop = scrollHandleY - EntMouseListener.MouseY;
					scrolling = true;
				}
				else
				{
					selectedIndex = (int) ((screenY - EntMouseListener.MouseY) / (int)lineHeight)+lineOffset;
				}
			}
		}
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			scrolling = false;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
	
		if (scrolling)
		{
			//Subtracting the height of a scroll handle allows mouse offset on the handle to be taken into account for positioning the handle. This also require the handle offset be added to the mouse Y coordinate
			int adjH = h -  scrollHandleHeight;	//Adjusted height
			mouseOffSetFromTaTop = screenY - (EntMouseListener.MouseY+offsetScrollHandleTop);
			if (mouseOffSetFromTaTop < 0 )
				mouseOffSetFromTaTop = 0;
			else if (mouseOffSetFromTaTop > adjH)
				mouseOffSetFromTaTop = adjH;
			scrollHandleY = screenY - mouseOffSetFromTaTop;
			lineOffset = (int) ((float)(mouseOffSetFromTaTop)/(adjH)*(dataSource.size()-displayLineCount));
			scrollHandleYOffsetGLFloat = (Game.TABLE_ROW_HEIGHT*displayLineCount - Game.SCROLL_HANDLE_HEIGHT) * ((float)(mouseOffSetFromTaTop)/(adjH));
			
		}
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public int GetSelectedIndex() 
	{
		return selectedIndex;
	}
	
	//Assumes UpdateData has been called before the last change to the dataSource
	public IEntTableRow GetSelectedObject()
	{
		return dataSource.get(selectedIndex);
	}
	public Texture GetSelectedTexture() {
		return selectedFieldTexture;
	}
	public int GetLineOffset() 
	{
		return lineOffset;
	}
	


	

}
