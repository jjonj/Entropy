package jjj.entropy.ui;

import jjj.entropy.OGLManager;

/**
 * @author      Jacob Jensen <jjonj@hotmail.com>
 * @version     1.0a                 (current version number of program)
 * @since       2013-01-01          (the version of the package this class was first added to)
 */
public abstract class Clickable extends UIComponent
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
	
	public Clickable(float x, float y, float width, float height)
	{
		super(x, y);
		glW = width;
		glH = height;
		
	    int[] temp = OGLManager.ConvertGLFloatToGLScreen(x, y);
	    this.screenX = temp[0];
        this.screenY = temp[1];
        
        
    	temp = OGLManager.ConvertGLFloatToGLScreen(x+glW, y+glH);

    	
        int screenRight = temp[0],
        	screenTop = temp[1];	// Note: OpenGL screen coordinates are from bottom to top so screenY = bottom
        
        w = screenRight - screenX;
        h = screenTop - screenY;
	
    
         
         
	}
	
	// Update screen X, Y, W and H which changes on resize
	protected void UpdateScreenCoords()
	{
		
		int[] temp = OGLManager.ConvertGLFloatToGLScreen(x, y);
	    this.screenX = temp[0];
        this.screenY = temp[1];
        
        temp = OGLManager.ConvertGLFloatToGLScreen(x+glW, y+glH);

    	
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
	

	public void SetGLWidth(float w)
	{

		glW = w;
		int[] temp = OGLManager.ConvertGLFloatToGLScreen(x+glW, y+glH);


        int screenRight = temp[0];
        
        this.w = screenRight - screenX;
	}
	
	public void SetGLHeight(float h)
	{
		
		
		glH = h;
		int[] temp = OGLManager.ConvertGLFloatToGLScreen(x+glW, y+glH);


	    int screenTop = temp[1];	// Note: OpenGL screen coordinates are from bottom to top so screenY = bottom
	        
	    this.h = screenTop - screenY;

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
