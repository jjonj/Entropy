package jjj.entropy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

import jjj.entropy.classes.Const;
import jjj.entropy.ui.UIComponent;
import jjj.entropy.ui.UIManager;

public class InGameState implements GameState 
{

	
	
	//private float rotator = 0.0f;
	
	private Set<Card> cardsToRender;
	private int iteratingCardsToRender = 0;
	    
	private int FPSDisplayCounter = 0;
	private ByteArrayOutputStream FPSCounter;
	private FPSAnimator animator;
	private GLAutoDrawable canvas;
	
	private GameState exitState;
	
	public InGameState(GLCanvas canvas, GameState exitState)
	{
		this.exitState = exitState;
		this.canvas = canvas;
		FPSCounter = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(FPSCounter);
        
		animator = new FPSAnimator(canvas, 60);
        animator.setUpdateFPSFrames(60, ps);
        animator.add(canvas);
        animator.start();

        cardsToRender = new HashSet<Card>();
	}
	
	@Override
	public void Activate(Game gamete) 
	{
	}
	
	
	@Override
	public void Draw() 
	{
	
		 //     ---------------------------------         INIT FRAME       ------------------------------------------
	
	   
	   	OGLManager.gl.glLoadIdentity();
	   	OGLManager.glu.gluLookAt(	1, 5, -4,
	    				0, 0f, 4,
	    				0.0f, 1.0f,  0.0f);
	   	OGLManager.gl.glTranslatef(0,0,5);
	   	//OpenGL.gl.glRotatef(rotator, 0, 1, 0);
	   	
	   	OGLManager.gl.glPushMatrix();
	   	 
	   	 //     ---------------------------------         DRAW 3D LINES       ------------------------------------------
	   	
	   	 OGLManager.gl.glDisable(GL2.GL_TEXTURE_2D);     
	   	
	   	
		    
	   	 OGLManager.gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			 
		 OGLManager.gl.glBegin(GL2.GL_LINES);
		 
		 OGLManager.gl.glVertex3f(-10.0f,0.0f,0.0f);
		 OGLManager.gl.glVertex3f(10.0f,0.0f,0.0f);
		 
		 OGLManager.gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		 OGLManager.gl.glVertex3f(0.0f,10.0f,0.0f);
		 OGLManager.gl.glVertex3f(0.0f,-10.0f,0.0f);
		 
		 OGLManager.gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		 	OGLManager.gl.glVertex3f(0.0f,0.0f,-10.0f);
		 	OGLManager.gl.glVertex3f(0.0f,0.0f,10.0f);
		 OGLManager.gl.glEnd();
		   OGLManager.gl.glColor4f(1.0f, 1.0f, 1.0f, 1f);
		 OGLManager.gl.glEnable(GL2.GL_TEXTURE_2D);     
		 
		 //     ---------------------------------         DRAW OTHERS       ------------------------------------------
		 
		 Texture.board.bind(OGLManager.gl);
	   	 OGLManager.DrawTable(OGLManager.gl, -Const.BOARD_WIDTH/2, Const.BOARD_HEIGHT);
	   	 OGLManager.DrawDeck(OGLManager.gl, -3.0f, 0.5f, 1.0f);
	   	 OGLManager.DrawDeck(OGLManager.gl, 3.0f, 0.5f, 10.0f);
	   	 	
		 OGLManager.gl.glPopMatrix();
		 
		 //     ---------------------------------        DRAW/UPDATE CARDS      ------------------------------------------
		 ++iteratingCardsToRender;
		 for(Card ca : cardsToRender)
		 {
			OGLManager.DrawCard(OGLManager.gl, OGLManager.glu, ca);
			ca.Update();
		 }
		 --iteratingCardsToRender;
		 
		 //     ---------------------------------         DRAW UI       ------------------------------------------
		 OGLManager.gl.glPushMatrix();
		 OGLManager.gl.glLoadIdentity();
		 Texture.uiTexture.bind(OGLManager.gl);
		 OGLManager.gl.glBegin(GL2.GL_QUADS);
		 	OGLManager.gl.glTexCoord2f(0.0f, 0.0f); OGLManager.gl.glVertex3f(-0.74f,-0.415f, -1);
		 	OGLManager.gl.glTexCoord2f(1.0f, 0.0f); OGLManager.gl.glVertex3f(0.74f,-0.415f,-1);
		 	OGLManager.gl.glTexCoord2f(1.0f, 1.0f); OGLManager.gl.glVertex3f(0.74f,-0.25f, -1);
		 	OGLManager.gl.glTexCoord2f(0.0f, 1.0f); OGLManager.gl.glVertex3f(-0.74f,-0.25f, -1);
		 OGLManager.gl.glEnd();
		 OGLManager.gl.glPopMatrix();
		 
		 
		 FPSDisplayCounter++;

         if (FPSDisplayCounter == 120)
		 {
			try {
	    		UIManager.GetInstance().GetFPSLabel().SetText(FPSCounter.toString("UTF8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FPSCounter.reset();
			FPSDisplayCounter = 0;
		}
	}
/*
	@Override
	public void Update() 
	{
		/*
	      //     ---------------------------------         EXTRA STUFF       ------------------------------------------
        rotator += 0.01;
        if (rotator >= 360f)
         	rotator = 0f;
        
        OGLManager.gl.glEnable(GL2.GL_TEXTURE_2D);     
		break;
		
	}
	*/
	public void ShowCard(Card card)
	{
    	while (iteratingCardsToRender > 0)	//Busywait until the cards have been iterated so we don't get an exception from modifying while iterating. TODO: Slow? other solutions?
		{
		}
		cardsToRender.add(card);
		card.SetGLMIndex(cardsToRender.size()-1);	//TODO: Remove all GLMIndex since they shouldn't be used with new set implementation??
	}
	
	public void RemoveCard(Card card)
	{

		while (iteratingCardsToRender > 0)	//Busywait until the cards have been iterated so we don't get an exception from modifying while iterating
		{
		}
		cardsToRender.remove(card);
		/*	OLD CODE USING GLMINDEX FOR A LIST TODO: Remove when independance of GLMINDEX is proven
		for (int i = decrementFrom; i < cardsToRender.size(); i++)
		{
			cardsToRender.get(i).SetGLMIndex(i);
		}*/
	}
	

	public Card CheckCardCollision()
	{
		//NOTE: Depth testing not tested, and only tests for card center
		Card rCard = null;
		
		double  px = EntMouseListener.MouseX, 
				py = (EntMouseListener.MouseY);
		++iteratingCardsToRender;
		for(Card ca : cardsToRender)
		{
			// This code was copied and modified with permission, i can't remember all the details (Work out later)
			// Each cards rectangle are divided into two rectangles that are checked for intersection with the mouse position
			double  x1 = ca.GetWinX(0),	// triangle vertice coordinates
					y1 = ca.GetWinY(0),
					x2 = ca.GetWinX(1),
					y2 = ca.GetWinY(1),
					x3 = ca.GetWinX(2),
					y3 = ca.GetWinY(2);
			for (int i = 0; i < 2; i++)	//Do for the two triangles
			{
				double dABx = x2-x1, 
					   dABy = y2-y1,
					   dBCx = x3-x2,
					   dBCy = y3-y2;
		        	
			   if ((dABx*dBCy - dABy*dBCx) < 0)	//Clockwise
			   {
			      if (dABx*(py-y1) >= dABy*(px-x1)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if (dBCx*(py-y2) >= dBCy*(px-x2)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if ((x1-x3)*(py-y3) >= (y1-y3)*(px-x3)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			   }
			   else								// Counter clockwise
			   {
			      if (dABx*(py-y1) < dABy*(px-x1)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if (dBCx*(py-y2) < dBCy*(px-x2)) 
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			      if ((x1-x3)*(py-y3) < (y1-y3)*(px-x3))
			      {
			    	  x2 = ca.GetWinX(3);
					  y2 = ca.GetWinY(3);
					  continue;
			      }
			   }
			   
			   if (rCard == null)
			   {
				   rCard = ca;
			   }
			   else
			   {
				   if (ca.GetZ() < rCard.GetZ())
					   rCard = ca;
			   }
			   break;
			}
		}
		--iteratingCardsToRender;

		return rCard;	// Returns null if no card was hit
	}
	
	
	public void Dispose()
	{
		animator.stop();
	}

	@Override
	public UIComponent GetDefaultFocusedUIElement() 
	{
		return UIManager.GetInstance().GetDefaultFocusedUIElement(this);
	}

	@Override
	public GameState GetExitState() 
	{
		return exitState;
	}


}
