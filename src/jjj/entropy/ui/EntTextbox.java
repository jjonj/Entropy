package jjj.entropy.ui;

import java.awt.Font;
import java.awt.Color;

import javax.media.opengl.GL2;

import jjj.entropy.GLHelper;
import jjj.entropy.Game;
import jjj.entropy.NetworkManager;
import jjj.entropy.classes.Enums.GameState;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class EntTextbox extends EntUIComponent{
	

	
	private String text;
	private EntFont  font;
	private int w, h;
	private int screenLeft,
				screenRight,
				screenBottom,
				screenTop,
				textOffsetX,
				textOffsetY;
	private Texture texture;
	
	private int textX,
				textY;

	
	
	public EntTextbox(float x, float y, int xOffset, int yOffset, Texture texture)
	{
		this(x, y, xOffset, yOffset, "", new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16), texture);
	}
	public EntTextbox(float x, float y, int xOffset, int yOffset, String text, Texture texture)
	{
		this(x, y, xOffset, yOffset, text, new EntFont(EntFont.FontTypes.MainParagraph, Font.PLAIN, 16), texture);
	}
	
	public EntTextbox(float x, float y, int xOffset, int yOffset, String startText, EntFont font, Texture texture )
	{
		super(x, y);
		this.text = startText;
		this.font = font;
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
       this.screenTop = (int) winPos[1];
       
       this.textX = screenLeft;
       this.textY = screenTop;
       
       
       double right = 0, bottom = 0;
		right = (double) x+Game.TEXTBOX_WIDTH;
		bottom = (double) y-Game.TEXTBOX_HEIGHT;
       
       Game.glu.gluProject(right, bottom, 0f, //
      		 model, 0,
      		 proj, 0, 
      		 view, 0, 
      		 winPos, 0);
      
      this.screenRight = (int) winPos[0];
      this.screenBottom =(int) winPos[1];
      
      this.w = this.screenRight - this.screenLeft;
      this.h = this.screenTop - this.screenBottom ;
      
      textOffsetY = yOffset;
      textOffsetX = xOffset;
	}
	
	public void Render(Game game)
	{
		Game.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		if (texture != null)
			texture.bind(Game.gl);
		 GLHelper.DrawTextbox(Game.gl, this);
		font.RenderBox(game, textX + textOffsetX, textY - textOffsetY, 1, Game.TEXTBOX_LINE_WIDTH, text);
	}
	
	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
	}
	public void AppendText(String text)
	{
		this.text = this.text + text;
	}
	public void RemoveFromEnd(int number)
	{
		if (text.length() >= number)
			this.text = this.text.substring(0, this.text.length() - number);
	}
	public void RemoveFromEnd()
	{
		RemoveFromEnd(1);
	}

	public int GetWidth() {
		return w;
	}
	public int GetHeight() {
		return h;
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
		Game.GetInstance().SetFocusedUIComponent(this);
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
	   right = (double) x+Game.TEXTBOX_WIDTH;
	   bottom = (double) y-Game.TEXTBOX_HEIGHT;
       
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

	
} 
