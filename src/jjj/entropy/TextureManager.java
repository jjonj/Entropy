package jjj.entropy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.media.opengl.GLException;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class TextureManager 
{

	private TextureManager(){} //Shouldn't be instantiated.
	
	private static String[] texturePaths = {	//Load from file instead! (Which probably means singleton, which probably means all statics = singleton
		"resources/textures/templates/Crawnid/CrawnidWorker.png",
		"resources/textures/templates/Crawnid/AnidQueen.png",
		"resources/textures/templates/Crawnid/AnidLarvaeSwarm.png",
		"resources/textures/templates/Crawnid/DarwinisticInnihilation.png"		
	};
	
	private static HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
	
	/*try {
   			cardtestfront = TextureIO.newTexture(new File("resources/textures/card1.png"), true);
   			cardBackside = TextureIO.newTexture(new File("resources/textures/backside.png"), true);
   			board = TextureIO.newTexture(new File("resources/textures/board.jpg"), true);
   			crawnidworkertexture = TextureIO.newTexture(new File("resources/textures/card2.png"), true);
   			deckSideTexture = TextureIO.newTexture(new File("resources/textures/deckside.png"), true);
   			uiTexture = TextureIO.newTexture(new File("resources/textures/bottomPanel.png"), true);
   			mainMenuTexture = TextureIO.newTexture(new File("resources/textures/MainMenu.png"), true);
   			loginScreenTexture = TextureIO.newTexture(new File("resources/textures/LoginScreen.png"), true);
   			bigButtonTexture = TextureIO.newTexture(new File("resources/textures/BigButton.png"), true);
   			textboxTexture = TextureIO.newTexture(new File("resources/textures/Textbox.png"), true);
   		} catch (GLException e) {
   			e.printStackTrace();
   			System.exit(1);
   		} catch (IOException e) {
   			e.printStackTrace();
   			System.exit(1);
   		}*/
	
	
	public static Texture LoadTexture(int id)
	{
		return LoadTexture(texturePaths[id]);
	}
	
	public static Texture LoadTexture(String path)
	{
		Texture tex = textureMap.get(path);
		if (tex == null)	//If the texture isn't already loaded, load it!
		{
			try {
				tex = TextureIO.newTexture(new File(path), true);
			} catch (GLException | IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			GLHelper.InitTexture(Game.gl, tex);
		}
		return tex;
	}
	
}
