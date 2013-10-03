package jjj.entropy.server;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import jjj.entropy.messages.*;



import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;




public class Main {

	public static final int PORT = 54555;

	static boolean quit = false;
	
	static DatabaseManager dbm = null;

	static Server server= null;
	
	static GameInstance[] games;
	static List<Integer> gameIndexPool;
	
	
	public static void main(String[] args) 
	{

	
		
		// ------------------------------------------    INIT JFRAME     ----------------------------------------------
		
		
	    JFrame frame = new JFrame();
        frame.add( new JLabel(" Outout" ), BorderLayout.NORTH );

        
  //     WindowListener[] x = frame.getWindowListeners();
        
//      frame.removeWindowListener(x[0]);
       
       
       frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	if (dbm.Connected)
            		dbm.Disconnect();
            	if (server != null)
        	     server.close();
            	System.exit(0);
            }
          });
        
       
        JTextArea ta = new JTextArea();
   //     ta.setBounds(0, 0, 1, 1);	
        ta.setPreferredSize(new Dimension(400, 400));
        
        
        TextAreaOutputStream taos;
        taos = null;
		try {
			taos = new TextAreaOutputStream( ta, 60 );
		} catch (Exception e) {
			e.printStackTrace();
		}
        PrintStream ps = new PrintStream( taos );
        //System.setOut( ps );
       // System.setErr( ps );

        
        frame.add( new JScrollPane( ta )  );

        frame.pack();
        frame.setVisible( true );

        // ------------------------------------------    INIT OBJECTS     ----------------------------------------------
        
        
        System.out.println("Entropy server starting...");
        
        games = new GameInstance[1];
        gameIndexPool = new ArrayList<Integer>();
        gameIndexPool.add(0);

        
        System.out.println("Connecting to database...");
    	dbm = DatabaseManager.GetInstance();
		
    	
    	if (dbm.Connect())
    	{
    		System.out.println("Connected to database!");


	        server = new Server();
			
			Kryo kryo = server.getKryo();
			kryo.register(ChatMessage.class);
			kryo.register(GameMessage.class);
			kryo.register(ActionMessage.class);
			kryo.register(CardDataMessage.class);
			kryo.register(LoginMessage.class);
			kryo.register(PlayerDataMessage.class);
			kryo.register(ShopDataMessage.class);
			kryo.register(Purchase.class);
			kryo.register(String[].class);
			kryo.register(int[].class);
			kryo.register(int[][].class);
			
			server.addListener(new EntServerListener());
			
			System.out.println("Binding port...");
			
			try {
				server.bind(PORT);
				System.out.println("Bound port!");
				
				server.start();

		    	String ll = System.getProperty("sun.arch.data.model");
		    	
				System.out.println("Server is functional!");
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Server failed to start! [CONNECTION ERROR]");
			}
		  
    	}
    	else
    		System.out.println("Server failed to start! [DATABASE ERROR]");
		

	}
}
