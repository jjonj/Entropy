package jjj.entropy;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jjj.entropy.classes.Const;


public class Main 
{
	public static void main(String[] args) throws IllegalAccessException 
	{
		
		
		//Basic openGL initialization
		GLProfile.initSingleton();
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		
		final JFrame jframe = new JFrame( "Entropy ALPHA" ); 
		
		Container contentPane = jframe.getContentPane();
		

		
		contentPane.add( canvas, BorderLayout.CENTER );
		 
		jframe.setSize( Const.START_GAME_WIDTH, Const.START_GAME_HEIGHT );
	    jframe.setVisible( true );

		
	    
		//Game window initialization
		//Frame frame = new Frame(Const.GAME_TITLE);
       // frame.setSize(Const.START_GAME_WIDTH, Const.START_GAME_HEIGHT);
      //  frame.add(canvas);
      //  frame.setVisible(true);
        
   
        
        //Initialize game
        Game.InitSingleton("Entropy", Const.START_GAME_WIDTH, Const.START_GAME_HEIGHT, canvas);

        //Initialize input/output
        EntMouseListener MListener = new EntMouseListener(Game.GetInstance());
        canvas.addMouseListener(MListener);
        canvas.addMouseWheelListener(MListener);
        canvas.addMouseMotionListener(MListener);
        canvas.addKeyListener(new EntKeyListener());
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
				Game.GetInstance().Cleanup();
                System.exit(0);
            }
        });

        //Register listener with openGL
        canvas.addGLEventListener(Game.GetInstance());
	}

}
