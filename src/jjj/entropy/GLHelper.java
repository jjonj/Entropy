package jjj.entropy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import jjj.entropy.ui.EntButton;
import jjj.entropy.ui.EntTextbox;

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
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Game.BIG_BUTTON_WIDTH, 0, 0f );
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Game.BIG_BUTTON_WIDTH, -Game.BIG_BUTTON_HEIGHT, 0f  );
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Game.BIG_BUTTON_HEIGHT, 0f  );
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
		    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(Game.TEXTBOX_WIDTH, 0, 0f );
		    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(Game.TEXTBOX_WIDTH, -Game.TEXTBOX_HEIGHT, 0f  );
		    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -Game.TEXTBOX_HEIGHT, 0f  );		    
		 gl.glEnd();
		 
		 gl.glEndList();
	}
	
	
	public static void DrawBigButton(GL2 gl, EntButton button)
	{
		 gl.glPushMatrix();
		 

		 gl.glTranslatef(button.GetX(), button.GetY(), 0);
		 
		 gl.glCallList(BigButtonModel);
		 gl.glPopMatrix();
	}
	
	public static void DrawTextbox(GL2 gl, EntTextbox textbox)
	{
		 gl.glPushMatrix();

		 gl.glTranslatef(textbox.GetX(), textbox.GetY(), 0);

		 gl.glCallList(TextboxModel);
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
	
	

}
