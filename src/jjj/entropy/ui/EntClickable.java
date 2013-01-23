package jjj.entropy.ui;

import jjj.entropy.GLHelper;

/**
 * @author      Jacob Jensen <jjonj@hotmail.com>
 * @version     1.0a                 (current version number of program)
 * @since       2013-01-01          (the version of the package this class was first added to)
 */
public abstract class EntClickable extends EntUIComponent
{
	
	//Optional virtual method for action when activated
	public void Activate(int mouseX, int mouseY) {}

	//Optional virtual method for certain code that need to be run on window resize, such as updating gl/screen relative coordinates.
	// Note that UpdateScreenCoords() should be called from a overriding method
	public void OnResize(int[] view, double[] model , double[] proj) 
	{
		UpdateScreenCoords();
	}
	
	
	protected int w, h,
				screenX,
				screenY;
	
	protected float glW, glH;	//OpenGL width & Height
	
	public EntClickable(float x, float y, float width, float height)
	{
		super(x, y);
		glW = width;
		glH = height;
		
	    int[] temp = GLHelper.ConvertGLFloatToGLScreen(x, y);
	    this.screenX = temp[0];
        this.screenY = temp[1];
        
        
    	temp = GLHelper.ConvertGLFloatToGLScreen(x+glW, y+glH);

    	
        int screenRight = temp[0],
        	screenTop = temp[1];	// Note: OpenGL screen coordinates are from bottom to top so screenY = bottom
        
        w = screenRight - screenX;
        h = screenTop - screenY;
		/*		Remove this as soon as functionality is confirmed
		int view[] = new int[4];
	    double model[] = new double[16];
	    double proj[] = new double[16];
	    double winPos[] = new double[4];// wx, wy, wz;// returned xyz coords
	    
		 Game.gl.glGetIntegerv(GL2.GL_VIEWPORT, view, 0);
		 Game.gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, model, 0);
		 Game.gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);

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
       */
       
    
         
         
	}
	
	// Update screen X, Y, W and H which changes on resize
	protected void UpdateScreenCoords()
	{
		
		int[] temp = GLHelper.ConvertGLFloatToGLScreen(x, y);
	    this.screenX = temp[0];
        this.screenY = temp[1];
        
        temp = GLHelper.ConvertGLFloatToGLScreen(x+glW, y+glH);

    	
        int screenRight = temp[0],
        	screenTop = temp[1];	// Note: OpenGL screen coordinates are from bottom to top so screenY = bottom
        
        w = screenRight - screenX;
        h = screenTop - screenY;
        
        /*
		
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
	        this.h = this.screenTop - this.screenBottom ;*/
	}
	

	
	public int GetScreenWidth() {
		return w;
	}
	public int GetScreenHeight() {
		return h;
	}
	
	public float GetScreenX() {
		return screenX;
	}
	public float GetScreenY() {
		return screenY;
	}
	
}
