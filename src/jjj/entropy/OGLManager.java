package jjj.entropy;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import jjj.entropy.classes.Const;
import jjj.entropy.ui.Dropdown;
import jjj.entropy.ui.Textbox;


public class OGLManager implements GLEventListener  
{

	
	public static GL2 gl;				
    public static GLU glu = new GLU();
    
	private OGLManager(){}	//Should not be instantiated.
	
	
	@SuppressWarnings("unused")
	private static int tableModel,
				cardModel,
				deckModel,
				UIModel,
				BigButtonModel,
				MediumButtonModel,
				SmallButtonModel,
				TinySquareButtonModel,
				TextboxModel,
				screen;
	
	private static List<OGLAction> glActionQueue;
	 

	private static int viewport[] = new int[4];
	private static double mvmatrix[] = new double[16];
	private static double projmatrix[] = new double[16];
	private static double wincoord[] = new double[4];
	
	public static void InitTexture(Texture texture)
	{
		texture.setTexParameteri(OGLManager.gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		texture.setTexParameteri(OGLManager.gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		texture.setTexParameteri(OGLManager.gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		texture.setTexParameteri(OGLManager.gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	private static OGLManager eventInstance;

	public static GLEventListener GetEvenListenerInstance() 
	{
		if (eventInstance == null)
			eventInstance = new OGLManager();
		return eventInstance;
	}

	
	
	@Override
	public void init(GLAutoDrawable gLDrawable) 
    {
   
		
    	gl = gLDrawable.getGL().getGL2();
    	
    	Texture.LoadTextureList();	//Simply loads a string array of texturepaths from file.
    	
    	Texture.InitTextures();
   		
    	OGLManager.gl.glClearColor(0.9f, 0.78f, 0.6f, 1.0f);
        
		OGLManager.gl.glEnable(GL.GL_BLEND);
    	OGLManager.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        OGLManager.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        OGLManager.gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
       
        OGLManager.gl.glEnable(GL.GL_TEXTURE_2D);                            
        OGLManager.gl.glDepthFunc(GL.GL_LEQUAL);                             		// The Type Of Depth Testing To Do
        OGLManager.gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  // Really Nice Perspective Calculations
         
         
        OGLManager.gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        OGLManager.gl.glEnable(GL.GL_DEPTH_TEST);
     	
     	//Calling generate methods that initiate the displaylists in openGL for fast rendering
     	OGLManager.GenerateTable(OGLManager.gl, Const.BOARD_WIDTH, Const.BOARD_LENGTH, Const.BOARD_THICKNESS);
     	OGLManager.GenerateUI(OGLManager.gl, 0, 0, 0, Texture.uiTexture);
     	OGLManager.GenerateDeck(OGLManager.gl, Texture.cardBackside, Texture.deckSideTexture, Const.CARD_WIDTH, Const.CARD_HEIGHT, 0.5f);
     	OGLManager.GenerateCard(OGLManager.gl, Texture.cardBackside);
     	
     	//Setting the real window height as GL scaling changes it
     	int viewport[] = new int[4];
        OGLManager.gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        int realGameHeight = viewport[3];
        
        glActionQueue = new ArrayList<OGLAction>(4);
    	
        Game.GetInstance().Init(realGameHeight);	//Exerting responsibility of Game object

        screen = gl.glGenLists(1);
        gl.glNewList(screen, GL2.GL_COMPILE);
        
        OGLManager.gl.glBegin(GL2.GL_QUADS);
		 	OGLManager.gl.glTexCoord2f(0.0f, 0.0f); OGLManager.gl.glVertex3f(-Const.SCREEN_GL_WIDTH/2,-Const.SCREEN_GL_HEIGHT/2, 0f);
		 	OGLManager.gl.glTexCoord2f(1.0f, 0.0f); OGLManager.gl.glVertex3f(Const.SCREEN_GL_WIDTH/2,-Const.SCREEN_GL_HEIGHT/2, 0f);
		 	OGLManager.gl.glTexCoord2f(1.0f, 1.0f); OGLManager.gl.glVertex3f(Const.SCREEN_GL_WIDTH/2,Const.SCREEN_GL_HEIGHT/2, 0f);
		 	OGLManager.gl.glTexCoord2f(0.0f, 1.0f); OGLManager.gl.glVertex3f(-Const.SCREEN_GL_WIDTH/2,Const.SCREEN_GL_HEIGHT/2, 0f);
	 	OGLManager.gl.glEnd();
	 	
	 	gl.glEndList();
    }
 
    @Override
	public void display(GLAutoDrawable gLDrawable) 
    {
		OGLManager.gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);	//Switch to camera adjustment mode
    	OGLManager.gl.glLoadIdentity();
    	OGLManager.glu.gluPerspective(45, Game.GetInstance().GetAspectRatio(), 1, 100);
    	OGLManager.gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);	//Switch to hand adjustment mode
    	OGLManager.gl.glLoadIdentity();
    	OGLManager.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	
    	for (OGLAction a : glActionQueue)	//Execute any tasks that need to be executed on the GL containing thread
    	{
    		a.Execute();
    	}
    	glActionQueue.clear();
    	
    	Game.GetInstance().Update();		//Exerting responsibility of Game object
    	Game.GetInstance().Draw();			//Exerting responsibility of Game object
    	
    	OGLManager.gl.glFlush();	
	}
    
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    }
 
    @Override
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
 
        if (height <= 0) // avoid a divide by zero error hack!
        {
            height = 1;
        }
        
    	int viewport[] = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        Game.GetInstance().SetScreenDimensions(width, height, viewport[3]);
  
        int view[] = new int[4];
	    double model[] = new double[16];
	    double proj[] = new double[16];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, view, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, model, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, proj, 0);
    
		Game.GetInstance().HandleResize(view, model, proj);
    }
 
    //openGL specific cleanup code
	@Override
	public void dispose(GLAutoDrawable arg0)
	{
	}



	
	
	
	public static void DrawTable(GL2 gl, float x, float BOARD_HEIGHT )
	{
		gl.glPushMatrix();
		
		gl.glTranslatef(x, BOARD_HEIGHT, 0);
		gl.glCallList(tableModel);
			
		gl.glPopMatrix();
	}
	
	public static void GenerateTable(GL2 gl, float BOARD_WIDTH, float BOARD_LENGTH, float BOARD_THICKNESS)
	{
		
		tableModel = gl.glGenLists(1);
        gl.glNewList(tableModel, GL2.GL_COMPILE);
        
		// Drawing the Table
		 
		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,0, BOARD_LENGTH);
		 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,0,BOARD_LENGTH);
		 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(BOARD_WIDTH,0, 0);
		 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0,0, 0);
		 	
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,0,0);
		 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,0,0);
		 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(BOARD_WIDTH,-BOARD_THICKNESS, 0);
		 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0,-BOARD_THICKNESS, 0);
		 	
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,-BOARD_THICKNESS,BOARD_LENGTH);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,-BOARD_THICKNESS,0);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,0,0);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,0,BOARD_LENGTH);
		 	
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,-BOARD_THICKNESS,BOARD_LENGTH);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,-BOARD_THICKNESS,0);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,0,0);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,0,BOARD_LENGTH);
		 	
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,0,BOARD_LENGTH);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,0,BOARD_LENGTH);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(BOARD_WIDTH,-BOARD_THICKNESS,BOARD_LENGTH);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,-BOARD_THICKNESS,BOARD_LENGTH);
		 gl.glEnd();
		 
		 gl.glEndList();
	}
	
	public static void GenerateUI(GL2 gl, float screenZ, float screenWidth, float screenHeight, Texture bottomPanelTexture)
	{
		
		UIModel = gl.glGenLists(1);
		
		
		gl.glDisable(GL.GL_TEXTURE_2D);
		
        gl.glNewList(UIModel, GL2.GL_COMPILE);
        bottomPanelTexture.bind(gl);
		// Drawing the Table
        
        gl.glColor3f(0.5f, 0.1f, 0);
        
		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0,0, screenZ);
		 	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(screenWidth,0,screenZ);
		 	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(screenWidth,screenHeight, screenZ);
		 	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0,screenHeight, screenZ);
		 gl.glEnd();

		 gl.glEndList();
		 
		 gl.glEnable(GL.GL_TEXTURE_2D);
		 
	}
	
	
	public static void DrawCard(GL2 gl, GLU glu, Card card)
	{
		
		 gl.glPushMatrix();
		 

			 
		 gl.glTranslatef(card.GetX(), card.GetY(), card.GetZ());
		

		/* gl.glRotatef(card.GetFX()*90, 0.0f, 1.0f, 0.0f);
		 gl.glRotatef(-card.GetFY()*90, 1.0f, 0.0f, 0.0f);
		 gl.glRotatef(card.GetFZ()*180, 0.0f, 1.0f, 0.0f);*/
		
		 gl.glRotatef(card.GetRotX(), 1.0f, 0.0f, 0.0f);
		 gl.glRotatef(card.GetRotY(), 0.0f, 1.0f, 0.0f);
		 gl.glRotatef(card.GetRotZ(), 0.0f, 0.0f, 1.0f);
		 
		 
		 
		 
		 card.GetTemplate().GetTexture().bind(gl);
		 
		 gl.glCallList(cardModel);
		
		 
		//Save the cards window position to be used for click collision detection
	     gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
	     gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
	     gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);
	     
	     glu.gluProject(0-Const.CARD_WIDTH/2, 0+Const.CARD_HEIGHT/2, 0, //
	             mvmatrix, 0,
	             projmatrix, 0, 
	             viewport, 0, 
	             wincoord, 0);
	     card.SetWinPos(0, wincoord[0], wincoord[1]);			//How effecient is this? :( CPU rounding mode switch???
	     glu.gluProject(0-Const.CARD_WIDTH/2, 0+Const.CARD_HEIGHT/2, 0, //
	                mvmatrix, 0,
	                projmatrix, 0, 
	                viewport, 0, 
	                wincoord, 0);
         card.SetWinPos(1, wincoord[0], wincoord[1]);
         glu.gluProject(0-Const.CARD_WIDTH/2, 0+Const.CARD_HEIGHT/2, 0, //
	                mvmatrix, 0,
	                projmatrix, 0, 
	                viewport, 0, 
	                wincoord, 0);
         card.SetWinPos(2, wincoord[0], wincoord[1]);
         glu.gluProject(0-Const.CARD_WIDTH/2, 0+Const.CARD_HEIGHT/2, 0, //
	                mvmatrix, 0,
	                projmatrix, 0, 
	                viewport, 0, 
	                wincoord, 0);
         card.SetWinPos(3, wincoord[0], wincoord[1]);
	     
		 gl.glPopMatrix();
		 
		 
	}
	
	public static void GenerateCard(GL2 gl, Texture backSideTexture)
	{
			
     	cardModel = gl.glGenLists(1);
        
        gl.glNewList(cardModel, GL2.GL_COMPILE);
      //  gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);

	    // Front Face	
        gl.glNormal3f(0, 0, 1);
	    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( Const.CARD_WIDTH/2, -Const.CARD_HEIGHT/2,  0.0f);
	    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-Const.CARD_WIDTH/2, -Const.CARD_HEIGHT/2,  0.0f);
	    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-Const.CARD_WIDTH/2,  Const.CARD_HEIGHT/2, 0.0f);
	    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( Const.CARD_WIDTH/2,  Const.CARD_HEIGHT/2,  0.0f);
	    
		gl.glEnd();   
		
		backSideTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		// Back Face
	    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( Const.CARD_WIDTH/2,  Const.CARD_HEIGHT/2, Const.CARD_THICKNESS);
	    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( Const.CARD_WIDTH/2,  -Const.CARD_HEIGHT/2, Const.CARD_THICKNESS);
	    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( -Const.CARD_WIDTH/2, -Const.CARD_HEIGHT/2, Const.CARD_THICKNESS);
	    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-Const.CARD_WIDTH/2,  Const.CARD_HEIGHT/2, Const.CARD_THICKNESS);
	    gl.glEnd();   
    
	    gl.glEnd();
        
        gl.glEndList();

	}
	
	
	public static void DrawUI(GL2 gl)
	{
		
		 gl.glPushMatrix();
		 

			 
		 gl.glLoadIdentity();

		 gl.glCallList(UIModel);
		 gl.glPopMatrix();
	}

	

	public static int GenerateRectangularSurface(float width, float height)
	{
		int dsl = gl.glGenLists(1);
        gl.glNewList(dsl, GL2.GL_COMPILE);

		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-width/2, height/2, 0f );
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(width/2, height/2, 0f );
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(width/2, -height/2, 0f  );
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-width/2, -height/2, 0f  );
		gl.glEnd();
		 
		gl.glEndList();

		return dsl;
	}
	
/*
	public static void GenerateTextbox(GL2 gl, Texture texture)
	{
	    	
		texture.bind(gl);
		TextboxModel = gl.glGenLists(1);
        gl.glNewList(TextboxModel, GL2.GL_COMPILE);

		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0, 0f );
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.TEXTBOX_WIDTH, 0, 0f );
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.TEXTBOX_WIDTH, -Const.TEXTBOX_HEIGHT, 0f  );
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Const.TEXTBOX_HEIGHT, 0f  );		    
		 gl.glEnd();
		 
		 gl.glEndList();
	}
	
	*/
	
	public static void DrawShape(float x, float y, float z, int displayList)
	{
		 gl.glPushMatrix();
		 
		 gl.glTranslatef(x, y, z);
		 
		 gl.glCallList(displayList);	
		
		 gl.glPopMatrix();
	}
	
	public static void DrawUITextbox(GL2 gl, Textbox textbox)
	{
		 gl.glPushMatrix();

		// gl.glTranslatef(textbox.GetX(), textbox.GetY(), 0);

		 gl.glCallList(TextboxModel);
		 gl.glPopMatrix();
	}
	
	
	public static void DrawVerticallyRepeatingRectanglularShape(float x, float y, float width, float rowHeight, float repeatCount)
	{
		gl.glPushMatrix();
		 
		gl.glTranslatef(x, y, 0);
		 
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-width/2, rowHeight/2, 0f );
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(width/2, rowHeight/2, 0f );
			gl.glTexCoord2f(1.0f, repeatCount); gl.glVertex3f(width/2, -rowHeight * repeatCount + rowHeight/2, 0f  );
			gl.glTexCoord2f(0.0f, repeatCount); gl.glVertex3f(-width/2, -rowHeight * repeatCount + rowHeight/2, 0f  );
		gl.glEnd();
		
		gl.glPopMatrix();
	}
	
	
	
	/*
	
	@SuppressWarnings("rawtypes")	//The fact that EntDropdown is generic, is not important for the rendering method
	public static void DrawUIDropdown(GL2 gl, Dropdown dropdown) 
	{
		
		 gl.glPushMatrix();
		 gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);	//Why is it needed to re-set this seting?
		 gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// gl.glTranslatef(dropdown.GetX(), dropdown.GetY(), 0);

		// if (dropdown.IsSelecting())
		 {
		//	 dropdown.GetTexture().bind(OGLManager.gl);
			
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, 0, 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, 0,  0);
	//		    gl.glTexCoord2f(1.0f, dropdown.GetDataSize()); gl.glVertex3f(Const.DROPDOWN_WIDTH, -dropdown.GetDataSize()*Const.TABLE_ROW_HEIGHT, 0 );
	//		    gl.glTexCoord2f(0.0f, dropdown.GetDataSize()); gl.glVertex3f(0, -dropdown.GetDataSize()*Const.TABLE_ROW_HEIGHT, 0);
			 gl.glEnd();
			 //Showing the selected element
			// dropdown.GetSelectedTexture().bind(gl);
		
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, -dropdown.GetSelectedIndex()*Const.DROPDOWN_ROW_HEIGHT, 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, -dropdown.GetSelectedIndex()*Const.DROPDOWN_ROW_HEIGHT,  0);
			    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, -(dropdown.GetSelectedIndex()+1)*Const.DROPDOWN_ROW_HEIGHT, 0 );
			    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -(dropdown.GetSelectedIndex()+1)*Const.DROPDOWN_ROW_HEIGHT,  0);
			 gl.glEnd();
		 }
	//	 else
		 {
		//	 dropdown.GetTexture().bind(OpenGL.gl);
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, 0, 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, 0,  0);
			    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, -Const.TABLE_ROW_HEIGHT, 0 );
			    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Const.TABLE_ROW_HEIGHT,  0);
			 gl.glEnd();
		 }
		 gl.glPopMatrix();
		 

	}
*/
	/*
	public static void DrawUITable(GL2 gl, Table table)
	{
		 gl.glPushMatrix();

		// gl.glTranslatef(table.GetX(), table.GetY(), 0);
		 gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, 0, 0);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.TABLE_WIDTH, 0,  0);
		    gl.glTexCoord2f(1.0f, table.GetLineCountToRender()); gl.glVertex3f(Const.TABLE_WIDTH, -(float)table.GetLineCountToRender()*Const.TABLE_ROW_HEIGHT, 0 );
		    gl.glTexCoord2f(0.0f, table.GetLineCountToRender()); gl.glVertex3f(0, -(float)table.GetLineCountToRender()*Const.TABLE_ROW_HEIGHT,  0);
		 gl.glEnd();
		 if (table.GetSelectedIndex() != -1)
		 {
			 table.GetSelectedTexture().bind(gl);
			 int selectedIndexShown = table.GetSelectedIndex() - table.GetLineOffset();	//Ensures that the correct index is drawn when scrolled
			 if (selectedIndexShown < table.GetLineCountToRender() && selectedIndexShown >= 0)
			 {
				 gl.glBegin(GL2.GL_QUADS);
				 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, -selectedIndexShown*Const.TABLE_ROW_HEIGHT, 0);
				    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.TABLE_WIDTH, -selectedIndexShown*Const.TABLE_ROW_HEIGHT,  0);
				    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.TABLE_WIDTH, -(selectedIndexShown+1)*Const.TABLE_ROW_HEIGHT, 0 );
				    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -(selectedIndexShown+1)*Const.TABLE_ROW_HEIGHT,  0);
				 gl.glEnd();
			 }
		 }
		 if (table.DisplayScrollbar())
		 {
			 table.GetScrollHandleTexture().bind(gl);
			 
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(Const.TABLE_WIDTH-0.008f, -table.GetScrollHandleGLYOffset(), 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.TABLE_WIDTH, -table.GetScrollHandleGLYOffset(),  0);
			    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.TABLE_WIDTH, -table.GetScrollHandleGLYOffset()-Const.SCROLL_HANDLE_HEIGHT, 0 );
			    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(Const.TABLE_WIDTH-0.008f, -table.GetScrollHandleGLYOffset()-Const.SCROLL_HANDLE_HEIGHT,  0);
			 gl.glEnd();
		 }
		 gl.glPopMatrix();
		 
		 
	}*/
	

	
	public static void DrawDeck(GL2 gl, float x,  float y, float z)
	{
		
		 gl.glPushMatrix();
		 

			 
		 gl.glTranslatef(x, y, z);
		

		 gl.glCallList(deckModel);
		
		 
		 gl.glPopMatrix();
		 
		 
	}
	
	
	public static void GenerateDeck(GL2 gl, Texture backSideTexture, Texture deckSideTexture, float CARD_WIDTH, float CARD_HEIGHT, float DECK_THICKNESS)
	{
		deckModel = gl.glGenLists(1);
        gl.glNewList(deckModel, GL2.GL_COMPILE);
        
		// Drawing the deck
		
        backSideTexture.bind(gl);
        
		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/2, 0, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, 0,  -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-CARD_WIDTH/2, 0, CARD_HEIGHT/2);
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/2, 0,  CARD_HEIGHT/2);
		 gl.glEnd();
		 
		 deckSideTexture.bind(gl);
		 
		 gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(CARD_WIDTH/2, 0, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(CARD_WIDTH/2, -DECK_THICKNESS, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 3f); gl.glVertex3f(CARD_WIDTH/2, -DECK_THICKNESS, CARD_HEIGHT/2);
		    gl.glTexCoord2f(0.0f, 3f); gl.glVertex3f(CARD_WIDTH/2, 0, CARD_HEIGHT/2);
		    
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, 0, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, -DECK_THICKNESS, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 3f); gl.glVertex3f(-CARD_WIDTH/2, -DECK_THICKNESS, CARD_HEIGHT/2);
		    gl.glTexCoord2f(0.0f, 3f); gl.glVertex3f(-CARD_WIDTH/2, 0, CARD_HEIGHT/2);
		    
		  
		    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(CARD_WIDTH/2, 0, CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, 0, CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 3); gl.glVertex3f(-CARD_WIDTH/2, -DECK_THICKNESS, CARD_HEIGHT/2);
		    gl.glTexCoord2f(0.0f, 3f); gl.glVertex3f(CARD_WIDTH/2, -DECK_THICKNESS, CARD_HEIGHT/2);
		    
		    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(CARD_WIDTH/2, 0, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, 0, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(1.0f, 3f); gl.glVertex3f(-CARD_WIDTH/2, -DECK_THICKNESS, -CARD_HEIGHT/2);
		    gl.glTexCoord2f(0.0f, 3f); gl.glVertex3f(CARD_WIDTH/2, -DECK_THICKNESS, -CARD_HEIGHT/2);
		 gl.glEnd();
		 
		 gl.glEndList();
	}
	
	private static int view[] = new int[4];
	private static double model[] = new double[16];
	private static double proj[] = new double[16];
	private static double winPos[] = new double[4];
	public static int[] ConvertGLFloatToGLScreen(double x, double y)
	{
		 // Resetting the matrices shouldn't be needed as nothing should be modifying them permanently, but code is provided in case
		 OGLManager.gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);	
		 OGLManager.gl.glLoadIdentity();
		 OGLManager.glu.gluPerspective(45, Game.GetInstance().GetAspectRatio(), 1, 100);
		 OGLManager.gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);	
		
		 OGLManager.gl.glLoadIdentity();   		
		 OGLManager.gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		 OGLManager.gl.glLoadIdentity();
		 OGLManager.gl.glTranslatef(0,0,-1);
		
		 OGLManager.gl.glGetIntegerv(GL.GL_VIEWPORT, view, 0);
		 OGLManager.gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, model, 0);
		 OGLManager.gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, proj, 0);

         OGLManager.glu.gluProject(x, y, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        return new int[] {(int) winPos[0], (int) winPos[1]};
	}


	public static void QueueOGLAction(OGLAction action) 
	{
		glActionQueue.add(action);
	}

	public static float MapPercentToFloatX(int x) 
	{
		//Gets the % of input x of the whole screen and applies that to the GL Coordinates
		return (Const.SCREEN_GL_WIDTH*((float)x/100))-Const.SCREEN_GL_WIDTH/2;
	}
	public static float MapPercentToFloatY(int y) 
	{
		return (-Const.SCREEN_GL_HEIGHT*((float)y/100))+Const.SCREEN_GL_HEIGHT/2;
	}

	public static float MapPercentToFloatWidth(int width) 
	{
		return Const.SCREEN_GL_WIDTH*((float)width/100);
	}
	public static float MapPercentToFloatHeight(int height) 
	{
		return Const.SCREEN_GL_HEIGHT*((float)height/100);
	}

	public static float MapScreenToFloatX(int x) 
	{
		//Gets the % of input x of the whole screen and applies that to the GL Coordinates
		return (Const.SCREEN_GL_WIDTH*((float)x/Game.GetInstance().GetGameWidth()))-Const.SCREEN_GL_WIDTH/2;
	}
	public static float MapScreenToFloatY(int y) 
	{
		return (-Const.SCREEN_GL_HEIGHT*((float)y/Game.GetInstance().GetRealGameHeight()))+Const.SCREEN_GL_HEIGHT/2;
	}



	public static int MapPercentToScreenX(int x) 
	{
		return (int) (Game.GetInstance().GetGameWidth()*((float)x/100));
	}

	public static int MapPercentToScreenY(int y) 
	{
		int rgh = Game.GetInstance().GetRealGameHeight();
		return rgh - (int) (rgh * ((float)y/100));
	}

	public static float MapPercentToScreenWidth(int x) 
	{
		return (Game.GetInstance().GetGameWidth()*((float)x/100));
	}
	
	public static float MapPercentToMagicScreenWidth(int x) 
	{
		return (1210*((float)x/100));	//The magic value 1210 is selected for screenwidth. While 1280 should be a working value, it has proven not so. TODO: Explain
	}
	
	public static float MapPercentToScreenHeight(int y) 
	{
		int rgh = Game.GetInstance().GetRealGameHeight();
		return (rgh * ((float)y/100));
	}

	public static void DrawScreen() 
	{
		OGLManager.gl.glTranslatef(0,0,-1);
		gl.glCallList(screen);
	}

	


	

	
	

}
