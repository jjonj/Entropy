package jjj.entropy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLException;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import javax.media.opengl.Threading;

public class TextureManager 
{

	private TextureManager(){} //Shouldn't be instantiated.
	
	private static String[] texturePaths = new String[100];
	
/*
	private static String[] texturePaths = {	//Load from file instead! (Which probably means singleton, which probably means all statics = singleton
		"resources/textures/templates/Crawnid/AnidQueen.png",
		"resources/textures/templates/Crawnid/CrawnidWorker.png",
		"resources/textures/templates/Crawnid/AnidLarvaeSwarm.png",
		"resources/textures/templates/Crawnid/DarwinisticInnihilation.png"		
	};
	*/
	
	
	//private static HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
	
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
