package Two_agents;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;

import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;

public class SimulationEnvironment extends SimState implements Runnable{
     protected PropertyChangeSupport propertyChangeSupport;
	 public Continuous2D agentSpace; 
	 public Continuous2D targetSpace;

	 public double gridWidth = 150.0;  //the width of the grid
     public double gridHeight = 150.0; //the height of the grid
     public static int NUM_AGENTS = 12; //The number of particles
     public static int NUM_TARGETS = 24;
     Double2D[] agentPos;
     Double2D[] targetPos;
     Double2D[] subsetPos;
     public int NUM_SUBSET = 3;
     public int[] subsetIndex= null,count;
     public int[] subsetFlag;
     public static int [] stepsRemaining = new int[NUM_AGENTS];
     Double2D[] Diagonal;
     public static final double KMEANS_REPEAT_INTERVAL = 20;
     public int min=100;	
     public int c = 1, n=1,index,NUM_AGENTS_IN_SUBSET;
     public double dist, DIAMETER = 0.5, speed;
     public boolean[] observedTargets;
     public int stopFlagAgents=0;
     public int stopFlagTargets=0;
     public int startAgain=0;
     int counter=1, range;
     startSimulation ss;
     public int countSteps=0;
     public int countTargets=0;
     public boolean[] observerreachedDestination;
     KMeans kmeans;
     Target t;
	 public SimulationEnvironment(long seed, int SensorRange, double speed) {
		super(seed);
		this.range = SensorRange;
		this.speed = speed;
	 }
		// TODO Auto-generated constructor stub		
	 public final double distanceBetweenPointsSQR( double x1, double y1, double x2, double y2 )
     {
		 double dist= (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
		 return dist;
     }
	 public void run(){
		 start();
	 }
	public void start(){
		System.out.println("Simulationenvionemt");
	     Arrays.fill(stepsRemaining, 100);
		 //System.out.print("here in main file STARRRRRRRRRRRRRRRRRRRRRRRRRRRRR\n");
			super.start();
		counter = 0;
		subsetFlag = new int[NUM_SUBSET];
		subsetIndex = new int[NUM_AGENTS];
		count = new int[NUM_SUBSET];
		observerreachedDestination = new boolean[NUM_AGENTS];
		
		subsetPos = new Double2D[NUM_AGENTS];
		for( int i = 0 ; i < NUM_AGENTS ; i++ ){
            subsetPos[i] = new Double2D();
            observerreachedDestination[i]=false;
		}

		Diagonal = new Double2D[NUM_SUBSET];
		for(int i=0; i < NUM_SUBSET; i++){
			Diagonal[i] = new Double2D();
			subsetIndex[i] = 0;
			subsetFlag[i]=0;
			count[i]=0;
		}
		agentPos = new Double2D[ NUM_AGENTS ];
        for( int i = 0 ; i < NUM_AGENTS ; i++ )
            agentPos[i] = new Double2D();
        
        targetPos = new Double2D[ NUM_TARGETS ];
        for( int i = 0 ; i < NUM_TARGETS ; i++ )
            targetPos[i] = new Double2D();
       
        
        kmeans = new KMeans(this);
		agentSpace = new Continuous2D(8.0,gridWidth, gridHeight); //create a 2D
		targetSpace = new Continuous2D(8.0,gridWidth, gridHeight);
		for(int i=0; i<NUM_SUBSET; i++)
		{
			Diagonal[i] = new Double2D((i+1)* (double)gridWidth/NUM_SUBSET,(i+1)* (double)gridHeight/NUM_SUBSET);
			System.out.print("Diagonal points are "+ Diagonal[i] +"\n" );
		}
		
		//System.out.println("Repeating");
			
		for(int i=0;i<NUM_TARGETS;i++){
			t = new Target(new Double2D(50.0,50.0),"Target"+i, this.speed);
			Double2D loc = new Double2D();
			do{			
				loc.x = (random.nextDouble()*(gridWidth));
				loc.y = (random.nextDouble()*(gridHeight));
			}while( !acceptablePosition( t, loc ) );

			t = new Target(loc,"Target"+i, this.speed);
			targetSpace.setObjectLocation(t,loc);
			targetPos[i]=loc;
		//	System.out.print("target pos is" + targetPos[i] + "\n");
            schedule.scheduleRepeating(t);
		}
		
		System.out.print("Created Target\n");
		for(int i=0;i<NUM_AGENTS;i++){
			Double2D loc = new Double2D(random.nextDouble()*gridWidth,random.nextDouble()*gridHeight);
			agentPos[i]=loc;
		}
		
		for(int i=0;i<NUM_AGENTS;i++)
		{
			double min=999999;
			for(int j=0;j<NUM_SUBSET;j++)
			{
				dist= distanceBetweenPointsSQR (agentPos[i].x, agentPos[i].y, Diagonal[j].x, Diagonal[j].y);
				if(dist < min){
					min= dist;
					index=j;
				}
			}
			subsetIndex[i]=index;
			count[index]++;
			System.out.println("subsetnumber" + subsetIndex[i]);
		}
		
	//	for(int i=0;i<NUM_SUBSET;i++)
	//		System.out.print("count before is " + count[i]+ "\n");
		NUM_AGENTS_IN_SUBSET= NUM_AGENTS/(NUM_SUBSET);
		for(int i=0;i<NUM_SUBSET-1;i++)
		{
			if(count[i] < NUM_AGENTS_IN_SUBSET)
			{
				while(count[i] != NUM_AGENTS_IN_SUBSET){
					int d=1;
				int difference = NUM_AGENTS_IN_SUBSET-count[i];
				for(int j=0;j<NUM_AGENTS;j++)
				{
					if(subsetIndex[j]==i+d)
					{
						subsetIndex[j]=i;
						count[i]++;
						count[i+d]--;
						difference--;
					}
					if(difference==0)
						break;
				}
				d++;
			}
			}
			else if(count[i] > NUM_AGENTS_IN_SUBSET)
			{
			    int difference = count[i]-NUM_AGENTS_IN_SUBSET;
				count[i+1]=count[i+1]+difference;
				count[i] = NUM_AGENTS_IN_SUBSET;
				for(int j=0;j<NUM_AGENTS;j++)
				{ 
					if(subsetIndex[j]==i && difference>0)
					{
						subsetIndex[j]=i+1;
						difference--;
					}
				}
			}
		//	else 
		//		System.out.print("Count is perfect\n");		
		}
		int newSubsetFlag=1;
		for(int i=0;i<NUM_SUBSET;i++)
		{
			newSubsetFlag=1;
			for(int j=0;j<NUM_AGENTS;j++)
			{	
				if(subsetIndex[j]==i)
				{
					if(newSubsetFlag==1)
					{
						Particle1 p = new Particle1(agentPos[j],"Agent"+j);
						newSubsetFlag=0;
					}
					Particle p1  = new Particle(agentPos[j],"Agent"+j);
					agentSpace.setObjectLocation(p1,agentPos[j]);
					//System.out.print("calling partile schedule repeating\n");
					schedule.scheduleRepeating(p1);
				}
			}
		}
		
		//System.out.println("Control at the end\n");

	     
	///		start();
		/*	while(countSteps<1500){
			System.out.print("StopFlag "+ countSteps + "\n");

		}
		finish();
		schedule.clear();
		start();
		*/
		}
    boolean conflict( final Object agent1, final Double2D a, final Object agent2, final Double2D b )
    {
        if( ( ( a.x > b.x && a.x < b.x+DIAMETER ) ||
                ( a.x+DIAMETER > b.x && a.x+DIAMETER < b.x+DIAMETER ) ) &&
                ( ( a.y > b.y && a.y < b.y+DIAMETER ) ||
                ( a.y+DIAMETER > b.y && a.y+DIAMETER < b.y+DIAMETER ) ) )
            {
            return false;
            }
        return true;
        }

boolean acceptablePosition( final Object agent, final Double2D location )
    {
    if( location.x > (gridWidth) || location.y > (gridHeight) )
        return false;
    Bag misteriousObjects = agentSpace.getNeighborsWithinDistance( location, /*Strict*/Math.max( 2*1, 2*1 ) );
    if( misteriousObjects != null )
        {
        for( int i = 0 ; i < misteriousObjects.numObjs ; i++ )
            {
            if( misteriousObjects.objs[i] != null && misteriousObjects.objs[i] != agent )
                {
                Object ta = (Particle)(misteriousObjects.objs[i]);
                if( conflict( agent, location, ta, agentSpace.getObjectLocation(ta) ) )
                    return false;
                }
            }
        }
    return true;
    }
	
	}

