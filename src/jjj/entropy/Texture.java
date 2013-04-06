package jjj.entropy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;


//import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Texture 
{
	
	private com.jogamp.opengl.util.texture.Texture glTexture;
	
	
	
	public Texture(GL2 gl, TextureData data) throws GLException 
	{
		glTexture = new com.jogamp.opengl.util.texture.Texture(gl, data);
	}

	private Texture(com.jogamp.opengl.util.texture.Texture texture)
	{
		glTexture = texture;
	}

	
	private static String[] texturePaths = new String[100];
	
	public static Texture   
					cardtestfront,
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
   			cardtestfront = new Texture(TextureIO.newTexture(new File("resources/textures/card1.png"), true));
   			cardBackside = new Texture(TextureIO.newTexture(new File("resources/textures/backside.png"), true));
   			board = new Texture(TextureIO.newTexture(new File("resources/textures/board.jpg"), true));
   			crawnidworkertexture = new Texture(TextureIO.newTexture(new File("resources/textures/card2.png"), true));
   			deckSideTexture = new Texture(TextureIO.newTexture(new File("resources/textures/deckside.png"), true));
   			uiTexture = new Texture(TextureIO.newTexture(new File("resources/textures/bottomPanel.png"), true));
   			mainMenuTexture = new Texture(TextureIO.newTexture(new File("resources/textures/MainMenu.png"), true));
   			deckScreenTexture =  new Texture(TextureIO.newTexture(new File("resources/textures/DeckScreen.png"), true));
   			loginScreenTexture = new Texture(TextureIO.newTexture(new File("resources/textures/LoginScreen.png"), true));
   			smallButtonTexture = new Texture(TextureIO.newTexture(new File("resources/textures/SmallButton.png"), true));
   			bigButtonTexture = new Texture(TextureIO.newTexture(new File("resources/textures/BigButton.png"), true));
   			arrow1ButtonTexture = new Texture(TextureIO.newTexture(new File("resources/textures/ArrowButton1.png"), true));
   			arrow2ButtonTexture = new Texture(TextureIO.newTexture(new File("resources/textures/ArrowButton2.png"), true));
   			textboxTexture = new Texture(TextureIO.newTexture(new File("resources/textures/Textbox.png"), true));
   		} catch (GLException e) {
   			e.printStackTrace();
   			System.exit(1);
   		} catch (IOException e) {
   			e.printStackTrace();
   			System.exit(1);
   		}
		
		OpenGL.InitTexture(cardBackside);
     	OpenGL.InitTexture(cardtestfront);
     	OpenGL.InitTexture(crawnidworkertexture);
     	OpenGL.InitTexture(deckSideTexture);
     	OpenGL.InitTexture(board);
     	OpenGL.InitTexture(uiTexture);
     	OpenGL.InitTexture(mainMenuTexture);
     	OpenGL.InitTexture(deckScreenTexture);
     	OpenGL.InitTexture(loginScreenTexture);
     	OpenGL.InitTexture(bigButtonTexture);
      	OpenGL.InitTexture(smallButtonTexture);
     	OpenGL.InitTexture(arrow1ButtonTexture);
     	OpenGL.InitTexture(arrow2ButtonTexture);
     	OpenGL.InitTexture(textboxTexture);
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


	public static Texture Load(File file, boolean mipmaps) throws GLException, IOException 
	{
		return new Texture(com.jogamp.opengl.util.texture.TextureIO.newTexture(file, mipmaps));
	}

	//Proxy method
	public void bind(GL2 gl) 
	{
		glTexture.bind(gl);
	}
	
	//Proxy method
	public void setTexParameteri(GL2 gl, int glTextureWrapS, int glRepeat) 
	{
		glTexture.setTexParameterf(gl, glTextureWrapS, glRepeat);
	}
	
	
	
}
