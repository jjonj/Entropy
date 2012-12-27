package jjj.entropy.ui;

import jjj.entropy.Game;

public abstract class EntUIComponent {

	
	protected float x;
	protected float y;
	
	public EntUIComponent(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float GetX()
	{
		return x;
	}
	public float GetY()
	{
		return y;
	}
	public void SetX(float x)
	{
		this.x = x;
	}
	public void SetY(float y)
	{
		this.y = y;
	}
	public void Move(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public abstract void Render(Game game);



	public void Activate() {}

	public void OnResize(int[] view, double[] model , double[] proj) {}

	
	}
