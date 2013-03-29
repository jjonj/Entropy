package jjj.entropy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.media.opengl.GLException;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class TextureManager 
{

	private TextureManager(){} //Shouldn't be instantiated.
	
	private static String[] texturePaths = new String[100];
	
	
	
	public static Texture   cardtestfront,
							crawnidworkertexture, 
						    cardBackside, 
						    board,
						    deckSideTexture,
						    uiTexture,
						    mainMenuTexture,
						    deckScreenTexture,
						    loginScreenTexture,
						    smallButtonTexture,
						    bigButtonTexture,
						    arrow1ButtonTexture,
						    arrow2ButtonTexture,
						    textboxTexture;


	
	//private static HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
	
	public static void InitTextures()
	{
		try {
   			cardtestfront = TextureIO.newTexture(new File("resources/textures/card1.png"), true);
   			cardBackside = TextureIO.newTexture(new File("resources/textures/backside.png"), true);
   			board = TextureIO.newTexture(new File("resources/textures/board.jpg"), true);
   			crawnidworkertexture = TextureIO.newTexture(new File("resources/textures/card2.png"), true);
   			deckSideTexture = TextureIO.newTexture(new File("resources/textures/deckside.png"), true);
   			uiTexture = TextureIO.newTexture(new File("resources/textures/bottomPanel.png"), true);
   			mainMenuTexture = TextureIO.newTexture(new File("resources/textures/MainMenu.png"), true);
   			deckScreenTexture = TextureIO.newTexture(new File("resources/textures/DeckScreen.png"), true);
   			loginScreenTexture = TextureIO.newTexture(new File("resources/textures/LoginScreen.png"), true);
   			smallButtonTexture = TextureIO.newTexture(new File("resources/textures/SmallButton.png"), true);
   			bigButtonTexture = TextureIO.newTexture(new File("resources/textures/BigButton.png"), true);
   			arrow1ButtonTexture = TextureIO.newTexture(new File("resources/textures/ArrowButton1.png"), true);
   			arrow2ButtonTexture = TextureIO.newTexture(new File("resources/textures/ArrowButton2.png"), true);
   			textboxTexture = TextureIO.newTexture(new File("resources/textures/Textbox.png"), true);
   		} catch (GLException e) {
   			e.printStackTrace();
   			System.exit(1);
   		} catch (IOException e) {
   			e.printStackTrace();
   			System.exit(1);
   		}
		
		GLHelper.InitTexture(Game.gl, cardBackside);
     	GLHelper.InitTexture(Game.gl, cardtestfront);
     	GLHelper.InitTexture(Game.gl, crawnidworkertexture);
     	GLHelper.InitTexture(Game.gl, deckSideTexture);
     	GLHelper.InitTexture(Game.gl, board);
     	GLHelper.InitTexture(Game.gl, uiTexture);
     	GLHelper.InitTexture(Game.gl, mainMenuTexture);
     	GLHelper.InitTexture(Game.gl, deckScreenTexture);
     	GLHelper.InitTexture(Game.gl, loginScreenTexture);
     	GLHelper.InitTexture(Game.gl, bigButtonTexture);
      	GLHelper.InitTexture(Game.gl, smallButtonTexture);
     	GLHelper.InitTexture(Game.gl, arrow1ButtonTexture);
     	GLHelper.InitTexture(Game.gl, arrow2ButtonTexture);
     	GLHelper.InitTexture(Game.gl, textboxTexture);
	}
	
	
	public static void LoadTextureList()
	{
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream("TextureList.etxl");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  int i = 1;	//index 0 is unused
			  while ((strLine = br.readLine()) != null)   
			  {
				  texturePaths[i] = strLine;
				  ++i;
			  }
			  in.close();
		}catch (Exception e){//Catch exception if any
			System.out.println("Error loading texture list from file 'TextureList.etxl'!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static String GetTexturePath(int id)
	{
		return texturePaths[id];
	}
	
	
	
}
