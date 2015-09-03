package Two_agents; 

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.FieldPortrayal2D;
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;

import javax.swing.*;
import javax.swing.plaf.nimbus.State;

import com.lowagie.text.Font;

import java.awt.Color;
import java.awt.geom.Line2D;

public class ParticlesWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;
	ContinuousPortrayal2D agentPortrayal = new ContinuousPortrayal2D();
	ContinuousPortrayal2D targetPortrayal = new ContinuousPortrayal2D();
	FieldPortrayal2D overlay;
	SimulationEnvironment hb;
	/*public static void main(String[] args) {
	
	new ParticlesWithUI().createController();
	}*/

	public static void main(String[] args) {
		
		//System.out.println("Start Simulation");
	    //new ParticlesWithUI().createController();
		double speeds[]={0.1,0.25,0.5,0.75,0.9};
		int ranges[] = {5,10,15,20,25};
		int i , j;		
		
		System.out.println("Sensor Range : "+ranges[4]);
		System.out.println("Speed : "+speeds[0]);
		for(i = 0; i<1; i++){
			for(j = 0; j<1; j++){
	
				System.out.println("\n");
				
				ParticlesWithUI temp = new ParticlesWithUI(ranges[1]*ranges[1], speeds[0]);
		        Console c = new Console(temp);
		        c.setVisible(true);
		       // c.pressPlay();
		        
		        try {
	    		    Thread.sleep(2500);                 //1000 milliseconds is one second.
	    		} catch(InterruptedException ex) {
	    		    Thread.currentThread().interrupt();
	    		}
		        
	    		//((Console)c).pressStop();
			}
		}
	}


	public ParticlesWithUI(int i, double speed) {
		super(new SimulationEnvironment(System.currentTimeMillis(), i, speed));
	}


	public void quit() {
		super.quit(); 
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	public void start()
    {
	    super.start();
	    // set up our portrayals
	    setupPortrayals();
		
    }

	
	/*public void start() {
		 int i=0,count=0;
		 //SimulationEnvironment hb = null;
		 Controller c;
		 c= createController();
		 super.start();
		 setupPortrayals();
		 SimState s;
		 s= (SimState) getSimulationInspectedObject();
		 	while(count<3001){	
		 		try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 		count++;
		 	
		 	if(count == 1500){
		 	count=0;
			if (displayFrame!=null) displayFrame.dispose();
		 	super.start();
			 init(c);
			 load(s);
		 	setupPortrayals();
		 	}
	       SimulationEnvironment firstThread = new SimulationEnvironment(i);
	        Thread thread1 = new Thread(firstThread);
	        thread1.start();
		 	}
		}*/

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {
                SimulationEnvironment se = (SimulationEnvironment)state;
               agentPortrayal.setField(se.agentSpace);
               targetPortrayal.setField(se.targetSpace);

               // OvalPortrayal2D o = new OvalPortrayal2D(Color.red);
          
                display.reset();
                display.repaint();
	}

	public void init(Controller c){
		super.init(c);
		display = new Display2D(400,400,this);
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.setBackdrop(Color.white);
		display.attach(agentPortrayal,"Agents");
		display.attach(targetPortrayal,"Targets");	
	}

	public Object getSimulationInspectedObject() {
		return state;
	}
}