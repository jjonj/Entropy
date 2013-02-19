package jjj.entropy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import jjj.entropy.classes.Const;
import jjj.entropy.ui.Button;
import jjj.entropy.ui.Dropdown;
import jjj.entropy.ui.Table;
import jjj.entropy.ui.Textbox;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class GLHelper {

	private GLHelper(){}	//Should not be instantiated.
	
	
	private static int tableModel,
				cardModel,
				deckModel,
				UIModel,
				BigButtonModel,
				MediumButtonModel,
				SmallButtonModel,
				TinySquareButtonModel,
				TextboxModel;
	
	private static float HALF_CARD_HEIGHT;
	private static float HALF_CARD_WIDTH;

	private static int viewport[] = new int[4];
	private static double mvmatrix[] = new double[16];
	private static double projmatrix[] = new double[16];
	private static double wincoord[] = new double[4];
	
	public static void InitTexture(GL2 gl, Texture texture)
	{
		texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	public static void InitOpenGL() {
		Game.gl.glClearColor(0.9f, 0.78f, 0.6f, 1.0f);
        
		Game.gl.glEnable(GL.GL_BLEND);
    	Game.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        Game.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        Game.gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
       
        Game.gl.glEnable(GL2.GL_TEXTURE_2D);                            
        Game.gl.glDepthFunc(GL2.GL_LEQUAL);                             		// The Type Of Depth Testing To Do
        Game.gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  // Really Nice Perspective Calculations
         
         
        Game.gl.glShadeModel(GL2.GL_SMOOTH);
        Game.gl.glEnable(GL2.GL_DEPTH_TEST);
     	
     	//Calling generate methods that initiate the displaylists in openGL for fast rendering
     	GLHelper.GenerateTable(Game.gl, Const.BOARD_WIDTH, Const.BOARD_LENGTH, Const.BOARD_THICKNESS);
     	GLHelper.GenerateButtons(Game.gl, TextureManager.bigButtonTexture);
     	GLHelper.GenerateUI(Game.gl, 0, 0, 0, TextureManager.uiTexture);
     	GLHelper.GenerateDeck(Game.gl, TextureManager.cardBackside, TextureManager.deckSideTexture, Const.CARD_WIDTH, Const.CARD_HEIGHT, 0.5f);
       	GLHelper.GenerateTextbox(Game.gl, TextureManager.textboxTexture);
     	GLHelper.GenerateCard(Game.gl, TextureManager.cardBackside, Const.CARD_WIDTH, Const.CARD_HEIGHT, Const.CARD_THICKNESS);
        
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
		
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
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
		 
		 gl.glEnable(GL2.GL_TEXTURE_2D);
		 
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
	     gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
	     gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
	     gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
	     
	     glu.gluProject(0-HALF_CARD_WIDTH, 0-HALF_CARD_HEIGHT, 0, //
	             mvmatrix, 0,
	             projmatrix, 0, 
	             viewport, 0, 
	             wincoord, 0);
	     card.SetWinPos(0, wincoord[0], wincoord[1]);			//How effecient is this? :( CPU rounding mode switch???
         glu.gluProject(0+HALF_CARD_WIDTH, 0-HALF_CARD_HEIGHT, 0, //
	                mvmatrix, 0,
	                projmatrix, 0, 
	                viewport, 0, 
	                wincoord, 0);
         card.SetWinPos(1, wincoord[0], wincoord[1]);
         glu.gluProject(0+HALF_CARD_WIDTH, 0+HALF_CARD_HEIGHT, 0, //
	                mvmatrix, 0,
	                projmatrix, 0, 
	                viewport, 0, 
	                wincoord, 0);
         card.SetWinPos(2, wincoord[0], wincoord[1]);
         glu.gluProject(0-HALF_CARD_WIDTH, 0+HALF_CARD_HEIGHT, 0, //
	                mvmatrix, 0,
	                projmatrix, 0, 
	                viewport, 0, 
	                wincoord, 0);
         card.SetWinPos(3, wincoord[0], wincoord[1]);
	     
		 gl.glPopMatrix();
		 
		 
	}
	
	public static void GenerateCard(GL2 gl, Texture backSideTexture, float CARD_WIDTH, float CARD_HEIGHT, float CARD_THICKNESS)
	{
		HALF_CARD_HEIGHT = CARD_HEIGHT / 2;
		HALF_CARD_WIDTH = CARD_WIDTH / 2;
			
     	cardModel = gl.glGenLists(1);
        
        gl.glNewList(cardModel, GL2.GL_COMPILE);
      //  gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);

	    // Front Face	
        gl.glNormal3f(0, 0, 1);
	    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/2, -CARD_HEIGHT/2,  0.0f);
	    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2, -CARD_HEIGHT/2,  0.0f);
	    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-CARD_WIDTH/2,  CARD_HEIGHT/2, 0.0f);
	    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/2,  CARD_HEIGHT/2,  0.0f);
	    
		gl.glEnd();   
		
		backSideTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		// Back Face
	    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( CARD_WIDTH/2,  CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( CARD_WIDTH/2,  -CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( -CARD_WIDTH/2, -CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-CARD_WIDTH/2,  CARD_HEIGHT/2, CARD_THICKNESS);
	    gl.glEnd();   
	    /*tpTexture2.destroy(gl);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		 	gl.glBegin(GL2.GL_QUADS);
	   // Top Face
	    gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f, 0.0f);
	    gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
	    gl.glVertex3f( CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
	    gl.glVertex3f( CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f, 0.0f);
	    // Bottom Face
	    gl.glVertex3f(0.02f, 0.02f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f,  CARD_THICKNESS);
	    gl.glVertex3f(0.02f, 0.02f,  CARD_THICKNESS);
	    // Right face
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f, 0.0f);
	    gl.glVertex3f(CARD_WIDTH-0.04f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
	    gl.glVertex3f(CARD_WIDTH-0.04f, 0.02f,  CARD_THICKNESS);
	    // Left Face
        gl.glVertex3f(0.02f,0.02f, 0.0f);
        gl.glVertex3f(0.02f, 0.02f,  CARD_THICKNESS);
        gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f,  CARD_THICKNESS);
        gl.glVertex3f(0.02f,  CARD_HEIGHT-0.04f, 0.0f);
	  gl.glEnd();*/
    
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

	
	public static void GenerateButtons(GL2 gl, Texture texture)
	{
		BigButtonModel = gl.glGenLists(1);
        gl.glNewList(BigButtonModel, GL2.GL_COMPILE);

		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0, 0f );
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.BIG_BUTTON_WIDTH, 0, 0f );
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.BIG_BUTTON_WIDTH, -Const.BIG_BUTTON_HEIGHT, 0f  );
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Const.BIG_BUTTON_HEIGHT, 0f  );
		 gl.glEnd();
		 
		 gl.glEndList();
		 
		TinySquareButtonModel = gl.glGenLists(1);
        gl.glNewList(TinySquareButtonModel, GL2.GL_COMPILE);

		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0, 0f );
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.TINY_SQUARE_BUTTON_WIDTH, 0, 0f );
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.TINY_SQUARE_BUTTON_WIDTH, -Const.TINY_SQUARE_BUTTON_HEIGHT, 0f  );
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Const.TINY_SQUARE_BUTTON_HEIGHT, 0f  );
		 gl.glEnd();
		 
		 gl.glEndList();
	}
	
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
	
	
	
	public static void DrawUIButton(GL2 gl, Button button)
	{
		 gl.glPushMatrix();
		 

		 gl.glTranslatef(button.GetX(), button.GetY(), 0);
		 
		 switch ( button.GetButtonSize() )
		 {
		 case TINY_SQUARE:
			 gl.glCallList(TinySquareButtonModel);
			 break;
		 case BIG:
			 gl.glCallList(BigButtonModel);
			 break;
		 default:
			 
		 }
		 
		
		 gl.glPopMatrix();
	}
	
	public static void DrawUITextbox(GL2 gl, Textbox textbox)
	{
		 gl.glPushMatrix();

		 gl.glTranslatef(textbox.GetX(), textbox.GetY(), 0);

		 gl.glCallList(TextboxModel);
		 gl.glPopMatrix();
	}
	
	@SuppressWarnings("rawtypes")	//The fact that EntDropdown is generic, is not important for the rendering method
	public static void DrawUIDropdown(GL2 gl, Dropdown dropdown) 
	{
		
		 gl.glPushMatrix();
		 gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);	//Why is it needed to re-set this seting?
		 gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		 gl.glTranslatef(dropdown.GetX(), dropdown.GetY(), 0);

		 if (dropdown.IsSelecting())
		 {
			 dropdown.GetTexture().bind(Game.gl);
			
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, 0, 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, 0,  0);
			    gl.glTexCoord2f(1.0f, (float)dropdown.GetDataSize()); gl.glVertex3f(Const.DROPDOWN_WIDTH, -(float)dropdown.GetDataSize()*Const.TABLE_ROW_HEIGHT, 0 );
			    gl.glTexCoord2f(0.0f, (float)dropdown.GetDataSize()); gl.glVertex3f(0, -(float)dropdown.GetDataSize()*Const.TABLE_ROW_HEIGHT, 0);
			 gl.glEnd();
			 //Showing the selected element
			 dropdown.GetSelectedTexture().bind(gl);
		
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, -dropdown.GetSelectedIndex()*Const.DROPDOWN_ROW_HEIGHT, 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, -dropdown.GetSelectedIndex()*Const.DROPDOWN_ROW_HEIGHT,  0);
			    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, -(dropdown.GetSelectedIndex()+1)*Const.DROPDOWN_ROW_HEIGHT, 0 );
			    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -(dropdown.GetSelectedIndex()+1)*Const.DROPDOWN_ROW_HEIGHT,  0);
			 gl.glEnd();
		 }
		 else
		 {
		//	 dropdown.GetTexture().bind(Game.gl);
			 gl.glBegin(GL2.GL_QUADS);
			 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, 0, 0);
			    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, 0,  0);
			    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Const.DROPDOWN_WIDTH, -Const.TABLE_ROW_HEIGHT, 0 );
			    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Const.TABLE_ROW_HEIGHT,  0);
			 gl.glEnd();
		 }
		 gl.glPopMatrix();
		 

	}

	
	public static void DrawUITable(GL2 gl, Table table)
	{
		 gl.glPushMatrix();

		 gl.glTranslatef(table.GetX(), table.GetY(), 0);
		 gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		 gl.glBegin(GL2.GL_QUADS);
		 	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0, 0, 0);
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Const.TABLE_WIDTH, 0,  0);
		    gl.glTexCoord2f(1.0f, (float)table.GetLineCountToRender()); gl.glVertex3f(Const.TABLE_WIDTH, -(float)table.GetLineCountToRender()*Const.TABLE_ROW_HEIGHT, 0 );
		    gl.glTexCoord2f(0.0f, (float)table.GetLineCountToRender()); gl.glVertex3f(0, -(float)table.GetLineCountToRender()*Const.TABLE_ROW_HEIGHT,  0);
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
		 
		 
	}
	

	
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
		 Game.gl.glMatrixMode(GL2.GL_PROJECTION);	
		 Game.gl.glLoadIdentity();
		 Game.glu.gluPerspective(45, Game.GetInstance().GetAspectRatio(), 1, 100);
		 Game.gl.glMatrixMode(GL2.GL_MODELVIEW);	
		
		 Game.gl.glLoadIdentity();   		
		 Game.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		 Game.gl.glLoadIdentity();
		 Game.gl.glTranslatef(0,0,-1);
		
		 Game.gl.glGetIntegerv(GL2.GL_VIEWPORT, view, 0);
		 Game.gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, model, 0);
		 Game.gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);

         Game.glu.gluProject(x, y, 0f, //
        		 model, 0,
        		 proj, 0, 
        		 view, 0, 
        		 winPos, 0);
        
        return new int[] {(int) winPos[0], (int) winPos[1]};
	}



	

	
	

}
