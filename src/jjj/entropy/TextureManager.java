package jjj.entropy;

import java.io.File;
import java.io.IOException;
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

	private static String[] texturePaths = {	//Load from file instead! (Which probably means singleton, which probably means all statics = singleton
		"resources/textures/templates/Crawnid/AnidQueen.png",
		"resources/textures/templates/Crawnid/CrawnidWorker.png",
		"resources/textures/templates/Crawnid/AnidLarvaeSwarm.png",
		"resources/textures/templates/Crawnid/DarwinisticInnihilation.png"		
	};
	
	//private static HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
	
	
	public static String GetTexturePath(int id)
	{
		return texturePaths[id];
	}
	
	
	
}
