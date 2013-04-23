package jjj.entropy.ui;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GLException;

import jjj.entropy.EntMouseListener;
import jjj.entropy.GameState;
import jjj.entropy.OGLManager;
import jjj.entropy.Game;
import jjj.entropy.SimpleCollection;
import jjj.entropy.Texture;
import jjj.entropy.classes.Const;



public class Table extends Clickable implements MouseListener, MouseMotionListener
{
	
	
	private EntFont  font;
	private SimpleCollection<TableRow> dataSource;	//The actual source of the data
	private List<TableRow> orderedData; 		//An internal sorted list of the data
	private String[] data;					//A cache of the string data (no need to recompute that every render call)
	
	private Texture texture;
	private Texture scrollHandleTexture,
					selectedFieldTexture;
	private float glScrollHandleOffsetY,	//The offset of the scroll handle in GL float coordinates
				  glRowHeight,
				  glScrollHandleHeight,
				  glScrollHandleWidth,
				  screenRowHeight;
	
	//private GameState activeGameState;
	
	
	private int rowHeight,
				textX,
				textY,
				selectedIndex = 2,
				screenScrollHandleX,
				screenScrollHandleTop,
				screenScrollHandleWidth,
				screenScrollHandleHeight,
				maxLines,
				displayLineCount,
				offsetScrollHandleTop,
				mouseOffSetFromTaTop,
				lineOffset = 0;	// Used for scrolling
	
	
	
	private boolean scrolling = false;
	private GameState activeGameState;
	private int glSelectedFieldDisplayList;
	private int glScrollHandleDisplayList;

	public Table(int x, int y, int width, int rowHeight, GameState activeGameState)	// Table supports a null dataSource
	{
		this(x, y, width, rowHeight, null, 0, activeGameState);
	}
	public Table(int x, int y, int width, int rowHeight,  SimpleCollection<TableRow> dataSource, GameState activeGameState)
	{
		this(x, y, width, rowHeight, dataSource, dataSource.Size(), activeGameState);
	}
	public Table(int x, int y, int width, int rowHeight, SimpleCollection<TableRow> dataSource, int maxLines, GameState activeGameState)
	{
		//Width should be adjustable, probably like button size "is"
		super(x, y, width, rowHeight*maxLines);
		
		
		
		
		
		this.maxLines = maxLines;
		this.font = new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16);
		
		
		this.dataSource = dataSource;
		this.activeGameState = activeGameState;
		
	
		

		Rectangle2D textBounds = font.GetRenderer().getBounds("A");

		this.textX = screenX - screenWidth/2 + (int)textBounds.getWidth();	//Left orientation
	    this.textY = screenY - (int) (textBounds.getHeight()/2.5);
        
        
        this.rowHeight = rowHeight;
        
        //Uses MapPercentToScreenX/FloatWidth instead of MapPercentToScreenY/FloatHeight to make it easier for users to estimate proportions. This needs to match method used in the Clickable class
        screenRowHeight = ((float)Game.GetInstance().GetGameWidth())*((float)rowHeight/100)-1.2f;	//0.9 added as a magic adjustment value, note that this assignment is always overwritten by OnResize assignment
        
     //   this.screenRowHeight = OGLManager.MapPercentToScreenX(rowHeight);
        this.glRowHeight = OGLManager.MapPercentToFloatWidth(rowHeight);
		
        
        screenScrollHandleWidth = OGLManager.MapPercentToScreenX(1);	//Update in OnResize aswell
		screenScrollHandleHeight = (int) (screenRowHeight*1.5);
		glScrollHandleWidth = OGLManager.MapPercentToFloatHeight(1);
		glScrollHandleHeight = glRowHeight*1.5f;
		
		
        texture = Texture.tableEntryTexture;
        scrollHandleTexture = Texture.scrollHandleTexture;
        selectedFieldTexture = Texture.selectedTableEntryTexture;

        
        glSelectedFieldDisplayList = OGLManager.GenerateRectangularSurface(this.glWidth, this.glRowHeight);
    	glScrollHandleDisplayList = OGLManager.GenerateRectangularSurface(glScrollHandleWidth, glScrollHandleHeight);
        
     
    	screenScrollHandleX = screenX+screenWidth/2-screenScrollHandleWidth/2;
	    screenScrollHandleTop = screenY+(int)screenRowHeight/2;
	    
	    
	    glScrollHandleOffsetY = 0;
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
		Game.GetInstance().AddMouseListener(this);
		Game.GetInstance().AddMouseMotionListener(this);
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
				data = new String[dataSource.Size()];
				orderedData = new ArrayList<TableRow>(dataSource.Size());
			}
			
			orderedData.clear();
			dataSource.AddAllTo(orderedData);
			Collections.sort(orderedData);
			
			for (int i = 0; i < orderedData.size(); i++)
			{
				data[i] = orderedData.get(i).GenRow();
			}
		}
		else
			data = new String[0];
	}
	
	@Override
	public void Render(Game game)
	{
		int i;
		float yOffset = textY;
		

		texture.bind(OGLManager.gl);
		
		OGLManager.DrawVerticallyRepeatingRectanglularShape(glX, glY, glWidth, glRowHeight, displayLineCount);
		
		//OGLManager.DrawUITable(OGLManager.gl, this);
		
		
		 if (selectedIndex != -1)
		 {
			 
			 int selectedIndexShown = selectedIndex - lineOffset;	//Ensures that the correct index is drawn when scrolled
			 if (selectedIndexShown < displayLineCount && selectedIndexShown >= 0)
			 {
				 selectedFieldTexture.bind(OGLManager.gl);
				 OGLManager.DrawShape(glX, glY - glRowHeight*selectedIndexShown, 0, glSelectedFieldDisplayList);
			 }
		 }
		 if (displayLineCount < data.length)
		 {
			 scrollHandleTexture.bind(OGLManager.gl);
			 OGLManager.DrawShape(glX+glWidth/2-glScrollHandleWidth/2, (glY+(glRowHeight/2)-glScrollHandleHeight/2)-glScrollHandleOffsetY, 0, glScrollHandleDisplayList);
		 }
			
		
		for (int k = lineOffset; k < displayLineCount+lineOffset; k++)
		{
			font.Render(textX, (int)yOffset, data[k]);
			
			yOffset -= screenRowHeight;
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
	
	

	
	@Override
	public void OnResize(int[] view, double[] model , double[] proj) 
	{
		super.OnResize(view, model, proj);
		this.screenRowHeight = OGLManager.MapPercentToScreenX(rowHeight);
		
		screenScrollHandleWidth = OGLManager.MapPercentToScreenX(1);
		screenScrollHandleHeight = (int) (screenRowHeight*1.5);
		screenScrollHandleX = screenX+screenWidth/2-screenScrollHandleWidth/2;
	    screenScrollHandleTop = screenY+(int)screenRowHeight/2;
		
	}
	
	@Override
	public boolean CheckCollision(int mouseX, int mouseY) 
	{
		if ( mouseX > screenX-screenWidth/2 && mouseX < screenX+screenWidth/2 &&
			 mouseY < screenY+screenRowHeight/2 && mouseY > screenY - screenRowHeight*(data.length-0.5))
		{
			if ( mouseX > screenScrollHandleX-screenScrollHandleWidth/2 && mouseX < screenScrollHandleX+screenScrollHandleWidth/2 &&
			     mouseY < screenScrollHandleTop && mouseY > screenScrollHandleTop - screenScrollHandleHeight)
			{
				offsetScrollHandleTop = screenScrollHandleTop - EntMouseListener.MouseY;
				scrolling = true;
			}
			else
				selectedIndex = (screenY + (int)screenRowHeight/2 - mouseY) / (int)screenRowHeight+lineOffset;
		}

		return false;
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
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			scrolling = false;
	}
	
	


	
	@Override
	public void mouseDragged(MouseEvent e) 
	{
	
		if (scrolling)
		{
			int originalScrollHandleTop = screenY+(int)screenRowHeight/2;
			//Subtracting the height of a scroll handle allows mouse offset on the handle to be taken into account for positioning the handle. This also require the handle offset be added to the mouse Y coordinate
			int adjH = screenHeight - screenScrollHandleHeight;	//Adjusted height = the height of the entire scroll area minus the height of the scroller, this tells how much space the scroller has to move on
			
			mouseOffSetFromTaTop =  originalScrollHandleTop - (EntMouseListener.MouseY+offsetScrollHandleTop);
			if (mouseOffSetFromTaTop < 0 )
				mouseOffSetFromTaTop = 0;
			else if (mouseOffSetFromTaTop > adjH)
				mouseOffSetFromTaTop = adjH;
			
			screenScrollHandleTop = originalScrollHandleTop - mouseOffSetFromTaTop;
		
			//glScrollHandleHeight/glRowHeight is here used to substract how much larger the scroll handle is in relation to a single column
			glScrollHandleOffsetY = (glRowHeight*(displayLineCount-(glScrollHandleHeight/glRowHeight))) * ((float)(mouseOffSetFromTaTop)/(adjH));
			
			
			lineOffset = (int) ((float)(mouseOffSetFromTaTop)/(adjH)*(dataSource.Size()-displayLineCount));
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
		if (selectedIndex < data.length)
			return orderedData.get(selectedIndex);	//Ordered data is in the same order as Data so there is a 1 to 1 correspondence
		return null;
	}


	public SimpleCollection<TableRow> GetDatSource() {
		return dataSource;
	}
	


	

}
