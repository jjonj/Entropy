package jjj.entropy.ui;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GLException;

import jjj.entropy.EntMouseListener;
import jjj.entropy.OpenGL;
import jjj.entropy.Game;
import jjj.entropy.OpenGL;
import jjj.entropy.SimpleCollection;
import jjj.entropy.Texture;
import jjj.entropy.classes.Const;
import jjj.entropy.classes.Enums.GameState;



//MISSING: Add parameter for text offset coordinates or even better automatize it
public class Table extends Clickable implements MouseListener, MouseMotionListener
{
	
	
	private EntFont  font;
	private SimpleCollection<TableRow> dataSource;	//The actual source of the data
	private List<TableRow> orderedData; 		//An internal sorted list of the data
	private String[][] data;					//A cache of the string data (no need to recompute that every render call)
	
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

	public Table(float x, float y,  int offsetX, int offsetY, GameState activeGameState)	// Table supports a null dataSource
	{
		this(x, y, offsetX, offsetY, null, 0, activeGameState);
	}
	public Table(float x, float y,  int offsetX, int offsetY,  SimpleCollection<TableRow> dataSource, GameState activeGameState)
	{
		this(x, y, offsetX, offsetY, dataSource, dataSource.Size(), activeGameState);
	}
	public Table(float x, float y, int offsetX, int offsetY, SimpleCollection<TableRow> dataSource, int maxLines, GameState activeGameState)
	{
		//Width should be adjustable, probably like button size "is"
		super(x, y, Const.TABLE_WIDTH, Const.TABLE_ROW_HEIGHT*maxLines);
		this.maxLines = maxLines;
		this.font = new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16);
		lineHeight = Const.TABLE_ROW_HEIGHT_PX;
		this.dataSource = dataSource;
		this.activeGameState = activeGameState;
		
		int[] temp =  OpenGL.ConvertGLFloatToGLScreen(0, 0);
		float zeroOnScreenX = temp[0],
		      zeroOnScreenY = temp[1];
		
		temp =  OpenGL.ConvertGLFloatToGLScreen(0, Const.TABLE_ROW_HEIGHT);
		lineHeight = temp[1] - zeroOnScreenY;
		
		temp = OpenGL.ConvertGLFloatToGLScreen(Const.SCROLL_HANDLE_WIDHT, 0);
		scrollHandleWidth = (int) (temp[0] - zeroOnScreenX);
		
		temp = OpenGL.ConvertGLFloatToGLScreen(0, Const.SCROLL_HANDLE_HEIGHT);
		scrollHandleHeight = (int) (temp[1] - zeroOnScreenY);
		
		
		fontLineHeight = (int)lineHeight+1;
		//selectedIndex = -1;
		try {
			texture = Texture.Load(new File("resources/textures/TableEntry.png"), true);
			scrollHandleTexture = Texture.Load(new File("resources/textures/ScrollHandle.png"), true);
			selectedFieldTexture = Texture.Load(new File("resources/textures/SelectedTableEntry.png"), true);
		} catch (GLException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		OpenGL.InitTexture(texture);
		OpenGL.InitTexture(scrollHandleTexture);
		OpenGL.InitTexture(selectedFieldTexture);
		temp = OpenGL.ConvertGLFloatToGLScreen(x, y);
		this.textX = temp[0] + offsetX;
	    this.textY = temp[1] + offsetY;
		
	    scrollHandleY = screenY;
	    scrollHandleYOffsetGLFloat = 0;
	    if (dataSource != null)
	    {
			if (dataSource.Size() < maxLines)
				displayLineCount = dataSource.Size();
			else
				displayLineCount =  maxLines;
	    }
	    else
	    	displayLineCount = 0;
	    UpdateData();
		 Game.GetInstance().GetCanvas().addMouseListener(this);
		 Game.GetInstance().GetCanvas().addMouseMotionListener(this);
	}
	
	//Method to update the internal data structure with the current data source
	//TODO: Find a more reasonable approach than just reload the entire collection
	public void UpdateData()
	{
		UIManager.GetInstance().SetActiveDataSource(dataSource);	// Helping UIManager manage the currently updating dataSource for use with rowgeneration of CardTemplates
		if (dataSource != null)
		{
			if (dataSource.Size() < maxLines)
				displayLineCount = dataSource.Size();
			else
				displayLineCount =  maxLines;
			
			if (data == null || data.length != dataSource.Size())
			{
				data = new String[dataSource.Size()][];
				orderedData = new ArrayList<TableRow>(dataSource.Size());
			}
			
			orderedData.clear();
			dataSource.AddAllTo(orderedData);
			Collections.sort(orderedData);
			
			for (int i = 0; i < orderedData.size(); i++)
			{
				try {
				data[i] = orderedData.get(i).GenRow();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else
			data = new String[0][0];
	}
	
	@Override
	public void Render(Game game)
	{
		int i,
			xOffset = textX,
			yOffset = textY;
		
		
		if (texture != null)
		{
			texture.bind(OpenGL.gl);
			OpenGL.DrawUITable(OpenGL.gl, this);
		}

		for (int k = lineOffset; k < displayLineCount+lineOffset; k++)
		{
			i = 0;
			for (String cell : data[k])
			{
				xOffset = textX + i * Const.TABLE_COLUMN_WIDTH_PX;
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
	
	
	public void SetDataSource(SimpleCollection<TableRow> dataSource) 
	{
		this.dataSource = dataSource;
		UpdateData();
	} 
	
	
	public int GetLineCount() 
	{
		return dataSource.Size();
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
		return displayLineCount < dataSource.Size();
	}
	
	@Override
	public void OnResize(int[] view, double[] model , double[] proj) 
	{
		System.out.println("RESIZE!!!");
		UpdateScreenCoords();
		scrollHandleY = screenY - mouseOffSetFromTaTop;
		int[] temp =  OpenGL.ConvertGLFloatToGLScreen(0, 0);
		float zeroOnScreenX = temp[0],
		      zeroOnScreenY = temp[1];
		
		temp =  OpenGL.ConvertGLFloatToGLScreen(0, Const.TABLE_ROW_HEIGHT);
		lineHeight = temp[1] - zeroOnScreenY;
		
		temp = OpenGL.ConvertGLFloatToGLScreen(Const.SCROLL_HANDLE_WIDHT, 0);
		scrollHandleWidth = (int) (temp[0] - zeroOnScreenX);
		
		temp = OpenGL.ConvertGLFloatToGLScreen(0, Const.SCROLL_HANDLE_HEIGHT);
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
					selectedIndex = (screenY - EntMouseListener.MouseY) / (int)lineHeight+lineOffset;
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
			lineOffset = (int) ((float)(mouseOffSetFromTaTop)/(adjH)*(dataSource.Size()-displayLineCount));
			scrollHandleYOffsetGLFloat = (Const.TABLE_ROW_HEIGHT*displayLineCount - Const.SCROLL_HANDLE_HEIGHT) * ((float)(mouseOffSetFromTaTop)/(adjH));
			
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
	public TableRow GetSelectedObject()
	{
		return orderedData.get(selectedIndex);	//Ordered data is in the same order as Data so there is a 1 to 1 correspondence
	}
	public Texture GetSelectedTexture() {
		return selectedFieldTexture;
	}
	public int GetLineOffset() 
	{
		return lineOffset;
	}
	public SimpleCollection<TableRow> GetDatSource() {
		return dataSource;
	}
	


	

}
