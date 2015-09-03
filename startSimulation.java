package Two_agents;

import sim.engine.SimState;

public class startSimulation extends SimState{
	public startSimulation(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}
	public void start(){
		System.out.println("Another simulation");

	SimulationEnvironment se = null;
	se.start();
//	se.finish();
//	se.start();
	}

}
