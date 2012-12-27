package jjj.entropy;

import java.lang.Math;

import jjj.entropy.classes.EntUtilities;
import jjj.entropy.classes.Enums;
import jjj.entropy.classes.Enums.Life;
import jjj.entropy.classes.Enums.Zone;


public class Card {


	public enum Status{
		IN_DECK,
		IN_HAND,
		IN_LIMBO,
		IN_ZONE,
		IS_LIFE
	}
	
	public enum Facing{
		UP,
		DOWN,
		FRONT,
		BACK
	}
	
	private Player originalOwner;
	private Player currentOwner;
	
	private CardTemplate template;
	
	
	private static int NextID = 0;
	

	private float oldX, oldY, oldZ,
				  oldRX, oldRY, oldRZ,
				  x, y, z,
				  targetX, targetY, targetZ,
				  dirVecX, dirVecY, dirVecZ,
				  rotX, rotY, rotZ,
				  targetRotX, targetRotY, targetRotZ,
				  rotDirX, rotDirY, rotDirZ;
				  

	
	private boolean zoomed,
					moving;
	
	private double[] winX,
				     winY;
	
	private int id;
	public int glMIndex = 0;
	
	private short zone;
	
	private Status status;
	
	public Card(float x, float y, float z, CardTemplate type, Player owner)	
	{
		this(x,y,z,Facing.FRONT, type, Status.IN_DECK, owner);
	}
	
	public Card(float x, float y, float z, Facing face, CardTemplate template, Status status, Player owner)	
	{
		this.id = NextID;
		NextID++;

		this.status = status;
		
		this.currentOwner = owner;
		this.originalOwner = owner;
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.template = template;
		/*this.faceX = faceX;
		this.faceY = faceY;
		this.faceZ = faceZ;*/
		
		//this.upX = upX;
	//	this.upY = upY;
	//	this.upZ = upZ;
		
		switch(face)
		{
		case UP:
			this.rotX = 90;
			break;
		case DOWN:
			this.rotX = -90;
			break;
		case FRONT:
			//Do nothing
			break;
		case BACK:
			this.rotY = 180;
			break;
		}
		
		winX = new double[4];
		winY = new double[4];
	}
	
	public void Update()
	{
		if (moving)
		{
			
			x -= dirVecX;
			y -= dirVecY;
			z -= dirVecZ;
			rotX += rotDirX;
			rotY += rotDirY;
			rotZ += rotDirZ;
			
			float dist = (float) Math.sqrt(Math.pow(x-targetX, 2) + Math.pow(y-targetY, 2) + Math.pow(z-targetZ, 2));

			if (dist < 0.05)
			{
				moving = false;
				x = targetX;
				y = targetY;
				z = targetZ;
				rotX = targetRotX;
				rotY = targetRotY;
				rotZ = targetRotZ;
			}
		
				
		}
	}

	public void PlayToZone(Zone zone) {PlayToZone(zone, false);}
	public void PlayToZone(Zone zone, boolean enemy)
	{
		if (!enemy)	
			SetTarget(x, 0, Zone.GetZLoc(zone), 90, 0, 0);
		else
			SetTarget(x, 0, Zone.GetZLoc(zone), -90, 180, 0);
	}
	public void PlayToLife(Life life){PlayToLife(life, false);}
	public void PlayToLife(Life life, boolean enemy)
	{
		if (!enemy)	
			SetTarget(Life.GetXLoc(life), 0, 1, -90, 0, 0);
		else
			SetTarget(Life.GetXLoc(life), 0, 10, -90, 0, 180);
	}
	public void PlayToHand(int cardsOnHand){PlayToHand(cardsOnHand, false);}
	public void PlayToHand(int cardsOnHand, boolean enemy)
	{
		if (!enemy)	
			SetTarget(0.3f*cardsOnHand-1, 1f, -3+0.001f*cardsOnHand, 0, 0, 0);
		else
			SetTarget(-0.3f*cardsOnHand+1, 1f, 12-0.001f*cardsOnHand, 0, 180, 0);
	}
	public void PlayToLimbo(){PlayToLimbo(false);}
	public void PlayToLimbo(boolean enemy)
	{
		if (!enemy)	
			SetTarget(5f, 0, 1f, 90, 0, 0);
		else
			SetTarget(-5f, 0, 10f, -90, 180, 0);
	}
	public void PlayToHighlight()
	{
		SetTarget(1f, 3.6f, -6.7f, 22, 0, 0);
	}
	
	
	public int Attack(Card target)
	{
		int dmg = template.DmgBase + EntUtilities.GetRandom(0, template.DmgDice);
		target.Damage(dmg);
		return dmg;
	}
	public void Damage(int amount)
	{
		// Do stuffz
	}
	
	public CardTemplate GetTemplate()
	{
		return template;
	}
	
	
	public int GetGLMIndex()
	{
		return glMIndex;
	}
	
	public void SetGLMIndex(int index)
	{
		glMIndex = index;
	}
	
	

	
	public void SetTarget(float x, float y, float z, float rotX, float rotY, float rotZ)
	{
		
		double steps = 90;	//1.5 sec animation time
		
		targetX = x;
		targetY = y;
		targetZ = z;
		targetRotX = rotX;
		targetRotY = rotY;
		targetRotZ = rotZ;
		
		float dirDiffX = this.x - targetX;
		float dirDiffY = this.y - targetY;
		float dirDiffZ = this.z - targetZ;
		
		
		dirVecX = (float) (dirDiffX/steps);
		dirVecY = (float) (dirDiffY/steps);
		dirVecZ = (float) (dirDiffZ/steps);
		
		rotDirX = (float) ((targetRotX - this.rotX )/steps);
		rotDirY = (float) ((targetRotY - this.rotY)/steps);
		rotDirZ = (float) ((targetRotZ - this.rotZ)/steps);
		
		moving = true;
	}
	
	public float GetTargetX()
	{
		return targetX;
	}
	public float GetTargetZ()
	{
		return targetZ;
	}
	public float GetTargetY()
	{
		return targetY;
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
	public void Move(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean Zoomed()
	{
		return zoomed;
	}
	public void SetZoomed(boolean val)
	{
		zoomed = val;
	}
/*	public float GetFX()
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
	public void SetFacing(float faceX, float faceY, float faceZ)
	{
		this.faceX = faceX;
		this.faceY = -faceY;
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
	}*/
	public double GetWinX(int posIndex)
	{
		return winX[posIndex];
	}
	public double GetWinY(int posIndex)
	{
		return winY[posIndex];
	}
	public void SetWinPos(int posIndex, double x, double y)
	{
		winX[posIndex] = x;
		winY[posIndex] = y;
	}

	public float GetRotX() {
		return rotX;
	}
	public float GetRotY() {
		return rotY;
	}
	public float GetRotZ() {
		return rotZ;
	}
	
	public void SetRot(float x, float y, float z) {
		rotX = x;
		rotY = y;
		rotZ = z;
	}
	
	public void SavePosition()
	{
		oldX = targetX;
		oldY = targetY;
		oldZ = targetZ;
		oldRX = targetRotX;
		oldRY = targetRotY;
		oldRZ = targetRotZ;
		
	}
	public void MoveBack()
	{
		SetTarget(oldX, oldY, oldZ, oldRX, oldRY, oldRZ);
	}

	public static Card LoadCard(int id, Player owner) throws IllegalAccessException {
		CardTemplate template = CardTemplate.GetTempalte(id);
		return new Card(0, 0, 0, template, owner);
	}

}
