package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLException;

import jjj.entropy.EntMouseListener;
import jjj.entropy.GLHelper;
import jjj.entropy.Game;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class EntTable<T> extends EntClickable implements MouseListener, MouseMotionListener
{
	
	
	private EntFont  font;
	T[][] dataSource;	//Two dimensional array of data
	
	
	private Texture texture;
	private Texture scrollHandleTexture; 
	private float lineHeight,	//The height of the font used
				  scrollHandleYOffsetGLFloat;	//The offset of the scroll handle in GL float coordinates
	private int textX,
				textY,
				scrollHandleY,
				maxLines,
				displayLineCount,
				offsetScrollHandleTop,
				mouseOffSetFromTaTop,
				lineOffset = 0;	// Used for scrolling
	private boolean scrolling = false;

	public EntTable(float x, float y,  T[][] dataSource)
	{
		this(x, y, dataSource, dataSource.length);
	}
	public EntTable(float x, float y,  T[][] dataSource, int maxLines)
	{
		//Width should be adjustable, probably like button size "is"
		super(x, y, Game.TABLE_WIDTH, Game.TABLE_ROW_HEIGHT*maxLines);
		this.maxLines = maxLines;
		this.font = new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16);
		lineHeight = font.getSize2D()+4;
		this.dataSource = dataSource;
	
		try {
			texture = TextureIO.newTexture(new File("resources/textures/TableEntry.png"), true);
			scrollHandleTexture = TextureIO.newTexture(new File("resources/textures/ScrollHandle.png"), true);
		} catch (GLException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		GLHelper.InitTexture(Game.gl, texture);
		GLHelper.InitTexture(Game.gl, scrollHandleTexture);
		
		int[] temp = GLHelper.ConvertGLFloatToGLScreen(x, y);
		this.textX = temp[0] + 20;
	    this.textY = temp[1] + 13;
		
	    scrollHandleY = screenY;
	    scrollHandleYOffsetGLFloat = 0;
		if (dataSource.length < maxLines)
			displayLineCount = dataSource.length;
		else
			displayLineCount =  maxLines;
		
		 Game.GetInstance().GetCanvas().addMouseListener(this);
		 Game.GetInstance().GetCanvas().addMouseMotionListener(this);
	}
	
	public void Render(Game game)
	{
		int i,
			xOffset = (int)textX,
			yOffset = (int)textY;
		
		
		if (texture != null)
		{
			texture.bind(Game.gl);
			GLHelper.DrawTable(Game.gl, this);
		}
		
		
		for (int k = lineOffset; k < displayLineCount+lineOffset; k++)
		{
			i = 0;
			for (T cell : dataSource[k])
			{
				xOffset = (int)textX + i * Game.TABLE_COLUMN_WIDTH_PX;
				font.Render(game, xOffset, yOffset, cell.toString());
				++i;
			}
			yOffset -= lineHeight;
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
	
	
	public int GetLineCount() 
	{
		return dataSource.length;
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
		return displayLineCount < dataSource.length;
	}
	
	@Override
	public void OnResize(int[] view, double[] model , double[] proj) 
	{
		UpdateScreenCoords();
		scrollHandleY = screenY - mouseOffSetFromTaTop;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && (screenX+w - EntMouseListener.MouseX) < 7 && EntMouseListener.MouseY > scrollHandleY - Game.SCROLL_HANDLE_HEIGHT_PX && EntMouseListener.MouseY < scrollHandleY)
		{
			offsetScrollHandleTop = scrollHandleY - EntMouseListener.MouseY;
			scrolling = true;
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
			int adjH = h -  Game.SCROLL_HANDLE_HEIGHT_PX;	
			mouseOffSetFromTaTop = screenY - (EntMouseListener.MouseY+offsetScrollHandleTop);
			if (mouseOffSetFromTaTop < 0 )
				mouseOffSetFromTaTop = 0;
			else if (mouseOffSetFromTaTop > adjH)
				mouseOffSetFromTaTop = adjH;
			scrollHandleY = screenY - mouseOffSetFromTaTop;
			lineOffset = (int) ((float)(mouseOffSetFromTaTop)/(adjH)*(dataSource.length-displayLineCount));
			scrollHandleYOffsetGLFloat = (Game.TABLE_ROW_HEIGHT*displayLineCount - Game.SCROLL_HANDLE_HEIGHT) * ((float)(mouseOffSetFromTaTop)/(adjH));
			
		}
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
