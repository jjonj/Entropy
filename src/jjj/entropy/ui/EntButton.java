package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import jjj.entropy.GLHelper;
import jjj.entropy.Game;
import jjj.entropy.NetworkManager;
import jjj.entropy.Game.*;
import jjj.entropy.classes.Enums.*;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class EntButton extends EntUIComponent{
	

	
	public enum ButtonSize{
		SMALL, MEDIUM, BIG
	}
	
	UIAction onClick;
	
	private String text;
	private EntFont  font;
	private Texture texture;
	
	private ButtonSize buttonSize;
	private int w, h;
	private int screenLeft,
				screenRight,
				screenBottom,
				screenTop;
	private int textOffsetX,
				textOffsetY;
	private int textX,
				textY;
	
	
	public EntButton(float x, float y, int xOffset, int yOffset, String text, Texture texture, UIAction action)
	{
		this(x, y, xOffset, yOffset, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 24), ButtonSize.BIG, texture, action);
	}
	public EntButton(float x, float y, int xOffset, int yOffset, String text, EntFont font, Texture texture, UIAction action)
	{
		this(x, y, xOffset, yOffset, text, font, ButtonSize.BIG, texture, action);
	}
	public EntButton(float x, float y, int xOffset, int yOffset, String text, ButtonSize size, Texture texture, UIAction action)
	{
		this(x, y, xOffset, yOffset, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.BOLD, 24), size, texture, action);
	}
	public EntButton(float x, float y, int xOffset,int yOffset, String text, EntFont font, ButtonSize size, Texture texture, UIAction action)
	{
		super(x, y);
		this.text = text;
		this.font = font;
		this.buttonSize = size;
		this.onClick = action;

		this.texture = texture;

		 Game.gl.glMatrixMode(GL2.GL_PROJECTION);	//Switch to camera adjustment mode
		 Game.gl.glLoadIdentity();
		 Game.glu.gluPerspective(45, Game.GetInstance().GetAspectRatio(), 1, 100);
		 Game.gl.glMatrixMode(GL2.GL_MODELVIEW);	//Switch to hand adjustment mode
		
		 Game.gl.glLoadIdentity();   		
		 Game.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		 Game.gl.glLoadIdentity();
		 Game.gl.glTranslatef(0,0,-1);
		 
	
		 
		int view[] = new int[4];
	    double model[] = new double[16];
	    double proj[] = new double[16];
	    double winPos[] = new double[4];// wx, wy, wz;// returned xyz coords
	    
		 Game.gl.glGetIntegerv(GL2.GL_VIEWPORT, view, 0);
		 Game.gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, model, 0);
		 Game.gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);
         // note viewport[3] is height of window in pixels 
    //     realy = view[3] - (int) y - 1;

         Game.glu.gluProject((double) x, (double) y, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        this.screenLeft = (int) winPos[0];
        this.screenTop =(int) winPos[1];
        
        this.textX = screenLeft;
        this.textY = screenTop;
        
        double right = 0, bottom = 0;
		switch (buttonSize)
		{
		case BIG:
			right = (double) x+Game.BIG_BUTTON_WIDTH;
			bottom = (double) y-Game.BIG_BUTTON_HEIGHT;
			break;
		default:
			break;
		}
        
        Game.glu.gluProject(right, bottom, 0f, //
       		 model, 0,
       		 proj, 0, 
       		 view, 0, 
       		 winPos, 0);
       
       this.screenRight = (int) winPos[0];
       this.screenBottom =(int) winPos[1];
       
       this.w = this.screenRight - this.screenLeft;
       this.h = this.screenTop - this.screenBottom ;
       
       
      //  int temp = (int)( font.GetRenderer().getBounds(text).getWidth()*0.5f);
       //System.out.println(w + "-" +  (int)(temp) + " "+ (w - (int)(temp))/2);
       // textOffsetX = (w - (int)((temp)))/2;
     textOffsetX = xOffset;
     textOffsetY = yOffset;

         
         
	}
	
	public void Render(Game game)
	{
		
		Game.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    	
		switch (buttonSize)
		{
		case BIG:
			if (texture != null)
			{
				texture.bind(Game.gl);
				GLHelper.DrawBigButton(Game.gl, this);
			}
			font.Render(game, textX + textOffsetX, textY - textOffsetY, text);
			break;
		default:
			break;
		}
		
		
	}
	
	@Override
	public void OnResize(int[] view, double[] model , double[] proj)
	{
	    double winPos[] = new double[4];// wx, wy, wz;// returned xyz coords
        Game.glu.gluProject((double) x, (double) y, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        this.screenLeft = (int) winPos[0];
        this.screenTop = (int) winPos[1];
        double right = 0, bottom = 0;
 		switch (buttonSize)
 		{
 		case BIG:
 			right = (double) x+Game.BIG_BUTTON_WIDTH;
 			bottom = (double) y-Game.BIG_BUTTON_HEIGHT;
 			break;
 		default:
 			break;
 		}
         
         Game.glu.gluProject(right, bottom, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        this.screenRight = (int) winPos[0];
        this.screenBottom =(int) winPos[1];
        
        this.w = this.screenRight - this.screenLeft;
        this.h = this.screenTop - this.screenBottom ;
	}
	
	public int GetWidth() {
		return w;
	}
	public int GetHeight() {
		return h;
	}
	
	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
	}
	
	public float GetScreenX() {
		return screenLeft;
	}
	public float GetScreenY() {
		return screenTop;
	}
	
	@Override
	public void Activate()
	{
		onClick.Activate();
	}
}
