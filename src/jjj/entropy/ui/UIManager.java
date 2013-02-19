package jjj.entropy.ui;

import jjj.entropy.NetworkManager;

public class UIManager 
{
	//Singleton pattern without lazy initialization
	private static UIManager instance = new UIManager();

	public static UIManager GetInstance()
	{
		return instance;
	}
	
	protected UIManager(){}
	
	
}
