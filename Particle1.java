package Two_agents;

import java.awt.Color;

import sim.app.cto.CooperativeObservation;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;

public class Particle1 extends sim.portrayal.simple.OvalPortrayal2D implements Steppable{
    private static final Object[] Min = null;
	protected int agentState;
    public Double2D agentLocation;
    public String id;
    public int intID = -1;
    protected Color agentColor = new Color(0,0,255);
    Double2D desiredLocation = null;
    Double2D[] suggestedLocation = null;
    int steps = 100;
    double dx,dy, slope, xincre=0.1, yincre=0.1, xin=0.1, yin=0.1;
    public int number;
    public int count=0;
    public double speed=0.1;
    
	public Particle1(final Double2D loc,String id) {
         this.agentLocation=loc;
         try
         {
        	 intID = Integer.parseInt( id.substring(5) ); // "AGENT"
         }
     catch( Exception e )
         {
         throw new RuntimeException(e);
         }
	}
	public final double distanceBetweenPointsSQR( double x1, double y1, double x2, double y2 )
    {
    return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
    }
	public void step(SimState state) {
		SimulationEnvironment hb = (SimulationEnvironment)state;
		count++;
		if(count==1500){
			count=0;			
			hb.finish();
			//hb.start();
		}
		//System.out.print("agent file\n");

		paint = agentColor;
		Double2D location = agentLocation;
		int count1=0;
        for(int i=0;i<hb.NUM_AGENTS;i++)
        {
        	if(hb.observerreachedDestination[i]==true)
        	{
        		count1++;
        	}
        	else
        		break;
        	
        }
        if(count1==hb.NUM_AGENTS && count%15==0)
    	{
    		suggestedLocation = hb.kmeans.getGoalPosition( intID );
    		System.out.println("HEREEEEE");
    		count1=0;
    	}
        if( suggestedLocation != null )
        {
            desiredLocation = suggestedLocation[intID];
        }
        else
            {
            /*if( steps <= 0 || steps == 100)
                {
            	double xmin, xmax, ymin, ymax;
            	desiredLocation = new Double2D();
    			if(location.x - (hb.gridWidth/4) < 0)xmin=0;
    			else xmin = location.x - (hb.gridWidth/4);
    			if(location.x + (hb.gridWidth/4) > hb.gridWidth)xmax=0;
    			else xmax = location.x + (hb.gridWidth/4);
    			
    			if(location.y - (hb.gridHeight/4) < 0)ymin=0;
    			else ymin = location.y - (hb.gridHeight/4);
    			if(location.y + (hb.gridHeight/4) > hb.gridHeight)ymax=0;
    			else ymax = location.y + (hb.gridHeight/4);
    			
    			desiredLocation.x = (hb.random.nextDouble()*(xmax-xmin))+xmin;
    			desiredLocation.y = (hb.random.nextDouble()*(ymax-ymin))+ymin;
            	//desiredLocation = new Double2D(50.0,50.0);
                steps = 100;
                }
                */
            }
        //System.out.println(desiredLocation);
        double dx = desiredLocation.x - location.x;
        double dy = desiredLocation.y - location.y;
        if( dx > 0.5 )
            dx = 0.5;
        else if( dx < -0.5 )
            dx = -0.5;
        if( dy > 0.5 )
            dy = 0.5;
        else if( dy < -0.5 )
            dy = -0.5;
        if( dx < 0.5 && dx > -0.5 && dy < 0.5 && dy > -0.5 )
            steps = 0;

        dx *= 2.0;
        dy *= 2.0;
        dx *= speed;
        dy *= speed;
        
		
        if( ! hb.acceptablePosition( this, new Double2D( location.x + dx, location.y + dy ) ) )
        {
        steps = 0;
        }
        else
        {
        agentLocation = new Double2D(location.x + dx, location.y + dy);
        steps--;
		if(steps==0)
			steps=100;
		
		hb.agentPos[intID] = agentLocation;
		for(int i=0;i<hb.NUM_AGENTS_IN_SUBSET;i++)
			hb.agentPos[i]=suggestedLocation[i];
        hb.agentSpace.setObjectLocation(this,agentLocation);
        }
	}

	
}