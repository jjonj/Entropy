package jjj.entropy;


import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
/*import javax.swing.JFrame;
import javax.swing.JLabel;


import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.opengl.GLWindow;*/

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

//http://www.land-of-kain.de/docs/jogl/
//http://schabby.de/jogl-example-hello-world/
//http://www.youtube.com/watch?v=2EaaQrgvsL0
// https://sites.google.com/site/justinscsstuff/jogl-tutorial-1
//http://jogamp.org/jogl/doc/NEWT-Overview.html
public class Main {

	final static int GAME_WIDTH = 1280;
	final static int GAME_HEIGHT = 720;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// setup OpenGL Version 2
		
		
		
		String FPSCounter ="";
		
		GLProfile.initSingleton();
		
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		
		GLCanvas canvas = new GLCanvas(capabilities);
		

		
		 Panel panel = new Panel();
		
	//	 TextField tf1 = new TextField("I am TextField tf1!", 30);
	//	 panel.add(canvas);
	//	 panel.add(tf1);
		 
		 
		 
		 
		Frame frame = new Frame("AWT Window Test");
        frame.setSize(GAME_WIDTH, GAME_HEIGHT);
       
   //     frame.add(panel);
        
        frame.add(canvas);
        
        frame.setVisible(true);
       
        
        
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        
    //    frame.addMouseListener(new EntMouseListener());
        
        
        
        
        canvas.addKeyListener(new EntKeyListener());
      
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
       
        
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.setUpdateFPSFrames(60, ps);
        animator.add(canvas);
        animator.start();
        
      //  OpenGLManager glManager = new OpenGLManager(GAME_WIDTH, GAME_HEIGHT, "resources/textures/backside.png");
        
        Game game = new Game("Entropy", GAME_WIDTH, GAME_HEIGHT, animator, baos, panel, canvas);
        
        EntMouseListener MListener = new EntMouseListener(game);
        
        canvas.addMouseListener(MListener);
        canvas.addMouseMotionListener(MListener);
        
       //lManager.RegisterGame(game);
        canvas.addGLEventListener(game);
        
        
        
        
	}

}
