package jjj.entropy.ui;

public abstract class EntUIComponent {

	
	protected int x;
	protected int y;
	
	public EntUIComponent(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int GetX()
	{
		return x;
	}
	public int GetY()
	{
		return y;
	}
	public void SetX(int x)
	{
		this.x = x;
	}
	public void SetY(int y)
	{
		this.y = y;
	}
	public void Move(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	public abstract void Render();
	
}
