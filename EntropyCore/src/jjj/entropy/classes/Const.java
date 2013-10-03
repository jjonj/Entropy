package jjj.entropy.classes;

//import jjj.entropy.classes.Enums.GameState;



public class Const {

	public static final String GAME_TITLE = "Entropy";
	
	public final static int START_GAME_WIDTH = 1280;
	public final static int START_GAME_HEIGHT = 720;
	public static final int MAX_GAME_COUNT = 100;
	public final static int MAX_PLAYERS_CONNECTED = 10;
	public static final int MAX_CARD_COUNT = 10000;

	//public static final GameState INIT_GAMESTATE = GameState.LOGIN;
	
	public final static float SCREEN_GL_WIDTH = 2*0.736f,
							  SCREEN_GL_HEIGHT = 2*0.414f;
	
	public final static float CARD_HEIGHT = 1.5f;
    public final static float CARD_WIDTH = 0.9f;
    public final static float HALF_CARD_HEIGHT = CARD_HEIGHT/2;	//I don't trust the compiler to optimize this
    public final static float HALF_CARD_WIDTH = CARD_WIDTH/2;
    public final static float CARD_THICKNESS = 0.001f;
    
    public static final float BOARD_LENGTH = 12.0f;
    public static final float BOARD_WIDTH = 12.0f;
    public static final float BOARD_HEIGHT = -0.001f;
    public static final float BOARD_THICKNESS = 0.5f;
    

  
    public static final int CHAT_LINE_WIDTH = 240;
    public static final int CHAT_LINES = 5;


	public static final float CARD_SPACING = 2.2f;

	public static final int SHOP_DISPLAYGRID_WIDTH = 4;
	
	public static final int MINIMUM_DECK_SIZE_FOR_PLAY = 5;


}
