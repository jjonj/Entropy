package jjj.entropy.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Enums {
//	 public enum GameState{
	//    	LOGIN, MAIN_MENU, IN_GAME, DECK_SCREEN
	//    }
	    
	
		public enum GameLocation
		{
			LIFE1, LIFE2,LIFE3,LIFE4,
			O_LIFE1, O_LIFE2,O_LIFE3,O_LIFE4,
			
			//TODO: ASsert Zone1 is player1 start zone
			ZONE1,ZONE2,ZONE3,ZONE4,
			
			HAND, O_HAND,
			
			LIMBO, O_LIMBO;
			
			
			
			public static float GetXLoc(GameLocation l) {
		        switch(l) {
		        case LIFE1:
		            return -1.0f;
		        case LIFE2:
		            return 0.0f;
		        case LIFE3:
		            return 1.0f;
		        case LIFE4:
		            return 2f;
		        case O_LIFE1:
		            return 2.0f;
		        case O_LIFE2:
		            return 1.0f;
		        case O_LIFE3:
		            return 0.0f;
		        case O_LIFE4:
		            return -1f;
		        }
		        return -3;
		    }
			
			
			
			
			private static List<ArrayList<Float>> availZoneXCoords = new ArrayList<ArrayList<Float>>(4);
			
			private static boolean initialized = false;
			private static int[][] coordsAssigned = {{0,0,0,0},{0,0,0,0}};
			
			//Reverse comparator to sort in reverse order
			private static Comparator<Float> reverseFloatComparator = new Comparator<Float>(){
				@Override
				public int compare(Float o1, Float o2)
				{
					return (int) (o2-o1);	
				}
			};
			
			private static void Init()
			{
				ArrayList<Float> zone1Coords = new ArrayList<Float>();		//avaliable coords for each zone for the client player
				ArrayList<Float> zone2Coords = new ArrayList<Float>();
				ArrayList<Float> zone3Coords = new ArrayList<Float>();
				ArrayList<Float> zone4Coords = new ArrayList<Float>();
				ArrayList<Float> oppZone1Coords = new ArrayList<Float>();	//And for the opponent player
				ArrayList<Float> oppZone2Coords = new ArrayList<Float>();
				ArrayList<Float> oppZone3Coords = new ArrayList<Float>();
				ArrayList<Float> oppZone4Coords = new ArrayList<Float>();
				availZoneXCoords.add(zone1Coords);	//Index 0 - 3 is for the client player
				availZoneXCoords.add(zone2Coords);
				availZoneXCoords.add(zone3Coords);
				availZoneXCoords.add(zone4Coords);
				availZoneXCoords.add(oppZone1Coords);	//Index 4 - 7 is the opponent
				availZoneXCoords.add(oppZone2Coords);
				availZoneXCoords.add(oppZone3Coords);
				availZoneXCoords.add(oppZone4Coords);
				initialized = true;
			}
			
			public static float GetZLoc(GameLocation z) {
		        switch(z) 
		        {
		        case ZONE1:
		            return 3f;
		        case ZONE2:
		            return 4.8f;
		        case ZONE3:
		            return 6.6f;
		        case ZONE4:
		            return 8.2f;
		        }
		        return -3;
		    }
			
			
			public static void ReturnAvailCardX(GameLocation zone, float X, boolean opponent)
			{
				ArrayList<Float> coords = null;
				int oppIndexModifier = opponent ? 4 : 0; 	//Simply adds 4 to the indices if coord is returned for enemy
				switch (zone)
				{
				case ZONE1:
					coords = availZoneXCoords.get(0+oppIndexModifier);
					break;
				case ZONE2:
					coords = availZoneXCoords.get(1+oppIndexModifier);
					break;
				case ZONE3:
					coords = availZoneXCoords.get(2+oppIndexModifier);
					break;
				case ZONE4:
					coords = availZoneXCoords.get(3+oppIndexModifier);
					break;
				}
				coords.add(X);
				if (opponent)
					Collections.sort(coords, reverseFloatComparator);
				else
					Collections.sort(coords);
			}
			
			public static float GetAvailCardX(GameLocation zone, boolean opponent) 
			{
				if (!initialized)
					Init();
				int zoneID = -1;
				ArrayList<Float> coords = null;
				int oppIndexModifier = opponent ? 4 : 0; 	//Simply adds 4 to the indices if coord is returned for enemy
				switch (zone)
				{
				case ZONE1:
					coords = availZoneXCoords.get(0+oppIndexModifier);
					zoneID = 1;
					break;
				case ZONE2:
					coords = availZoneXCoords.get(1+oppIndexModifier);
					zoneID = 2;
					break;
				case ZONE3:
					coords = availZoneXCoords.get(2+oppIndexModifier);
					zoneID = 3;
					break;
				case ZONE4:
					coords = availZoneXCoords.get(3+oppIndexModifier);
					zoneID = 4;
					break;
				}
				
				float retVal = 0;
				if (coords.size() == 0)
				{
					//return the next avaliable space to the left/right of the cards already in the zone and increment coordsAssigned for the player
					if (opponent)
						retVal = -0.5f-coordsAssigned[1][zoneID-1]++*Const.CARD_SPACING/2;	
					else
						retVal = 0.5f+coordsAssigned[0][zoneID-1]++*Const.CARD_SPACING/2;
					
				}
				else
				{
					retVal = coords.remove(0);
				}
				return retVal;
			}
			
			
			
		}

		 public enum TriggerEvent
		 {
		    CARD_DEATH, CARD_ATTACK, CARD_TARGETED, CARD_PLAYED, CARD_ENTER, CARD_DRAWED
		 }
		 

}
