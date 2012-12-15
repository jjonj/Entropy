package jjj.entropy;

public class Card {

	private CardType type;
	
	private float x, y, z,
				  faceX, faceY, faceZ,
				  upX, upY, upZ;
	
	int glMIndex = 0;
	
	
	public Card(float x, float y, float z, CardType type)	
	{
		this(x,y,z,
				0f, -1f, 0f, 
				0f, 0f, -1f, type);
	}
	
	public Card(float x, float y, float z, float faceX, float faceY, float faceZ, float upX, float upY, float upZ, CardType type)	
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.faceX = faceX;
		this.faceY = faceY;
		this.faceZ = faceZ;
		this.upX = upX;
		this.upY = upY;
		this.upZ = upZ;
	}
	
	public CardType GetType()
	{
		return type;
	}
	
	public int GetGLMIndex()
	{
		return glMIndex;
	}
	public void SetGLMIndex(int index)
	{
		glMIndex = index;
	}
	
	
	public float GetX()
	{
		return x;
	}
	public float GetY()
	{
		return y;
	}
	public float GetZ()
	{
		return z;
	}
	public void SetX(float x)
	{
		this.x = x;
	}
	public void SetY(float y)
	{
		this.y = y;
	}
	public void SetZ(float z)
	{
		this.z = z;
	}
	public float GetFX()
	{
		return faceX;
	}
	public float GetFY()
	{
		return faceY;
	}
	public float GetFZ()
	{
		return faceZ;
	}
	public void SetFX(float faceX)
	{
		this.faceX = faceX;
	}
	public void SetFY(float faceY)
	{
		this.faceY = faceY;
	}
	public void SetFZ(float faceZ)
	{
		this.faceZ = faceZ;
	}
	public float GetUX()
	{
		return upX;
	}
	public float GetUY()
	{
		return upY;
	}
	public float GetUZ()
	{
		return upZ;
	}
	public void SetUX(float upX)
	{
		this.upX = upX;
	}
	public void SetUY(float upY)
	{
		this.upY = upY;
	}
	public void SetUZ(float upZ)
	{
		this.upZ = upZ;
	}
	public void Move(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
