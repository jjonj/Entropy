package jjj.entropy;

import com.jogamp.opengl.util.texture.Texture;

public class CardType {

	private Texture texture;
	
	
	public CardType(Texture texture)
	{
		this.texture = texture;
	}
	
	public Texture GetTexture()
	{
		return texture;
	}
	
}
