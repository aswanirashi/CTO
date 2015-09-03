package Two_agents;

import java.awt.Color;

import sim.app.cto.CooperativeObservation;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

public class Target extends sim.portrayal.simple.OvalPortrayal2D implements Steppable{
    public Double2D targetLocation;
    protected Color targetColor = new Color(255,0,0);
    public String id;
    public int intID = -1;
	int steps=100;
	Double2D desiredLocation = null;
    Double2D suggestedLocation = null;
    Double speed;
    double xmax,xmin,ymax,ymin;
    public Target(final Double2D loc,String id, double speed) {

        this.targetLocation=loc;
        this.speed = speed;
        try
        {
       	 intID = Integer.parseInt( id.substring(6) ); // "TARGET"
        }
        catch( Exception e )
        {
        throw new RuntimeException(e);
        }
	}

    public final double distanceBetweenPointsSQR( double x1, double y1, double x2, double y2 )
    {
		 double dist= (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
		 return dist;
    }
    
	public void step(SimState state) {
		// TODO Auto-gen9erated method stub
	//	System.out.println("target" + targetLocation + " " + steps);
		paint = targetColor;
		SimulationEnvironment hb = (SimulationEnvironment)state;
		Double2D location = targetLocation;
		
		
		if(steps==100 ){
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
			
	        }
		steps--;
	//	System.out.println("desired Location target" + desiredLocation);

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

        
            targetLocation = new Double2D(location.x + dx, location.y + dy);


        if(steps==0)steps=100;

        
        for(int i = 0; i < hb.NUM_AGENTS; i++){
        	double temp = distanceBetweenPointsSQR(targetLocation.x, targetLocation.y, hb.agentPos[i].x, hb.agentPos[i].y);
        	double range = hb.range * hb.range;
        	if(temp  < range ){
        		hb.countTargets++;
        	//	System.out.println( hb.countTargets);
        		break;
        	}
        }
        hb.targetPos[intID] = targetLocation;
		hb.targetSpace.setObjectLocation(this,targetLocation);
		}
	}
	