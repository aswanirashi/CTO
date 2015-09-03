/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package Two_agents;

import sim.util.Double2D;
import sim.engine.*;

public class KMeans implements Steppable
    {
    private static final long serialVersionUID = 1;

    final static double ALFA = 0.25;

    Double2D[] clusterPoints;// = new Double2D[ CooperativeObservation.NUM_AGENTS ];
    boolean[] usable; // = new boolean[ CooperativeObservation.NUM_AGENTS ];
    Double2D[] means;// = new Double2D[ CooperativeObservation.NUM_AGENTS ];
    int[] n;// = new int[ CooperativeObservation.NUM_AGENTS ];
    double[] weight;// = new double[ CooperativeObservation.NUM_AGENTS ];
    int[] flag= null;
    int flag_print=0;
    public int k =0, sensorRange=225 ;
    SimulationEnvironment co;

    public KMeans( SimulationEnvironment co )
        {
        this.co = co;
        clusterPoints = new Double2D[ co.NUM_AGENTS];
        co.observedTargets = new boolean[co.NUM_TARGETS];
        usable = new boolean[ co.NUM_AGENTS ];
        means = new Double2D[ co.NUM_AGENTS ];
        for( int i = 0 ; i < co.NUM_AGENTS ; i++ )
        {
            clusterPoints[i] = new Double2D();
            means[i] = new Double2D();
        }
        for( int i=0;i<co.NUM_TARGETS; i++)
        {
        	co.observedTargets[i]= false;
        }
        n = new int[ co.NUM_AGENTS];
        weight = new double[co.NUM_AGENTS ];
        flag = new int[co.NUM_TARGETS];
        }

    public Double2D[] getGoalPosition( int id )
        {
    	step(co);
        if( usable[id] )
            return clusterPoints;
        else
            return null; // for exploration purposes
        }

    public final double distanceBetweenPointsSQR( double x1, double y1, double x2, double y2 )
        {
        double dist= (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
        return dist;
        }

    public void step( final SimState state )
        {
       		for( int i = 0 ; i < co.NUM_AGENTS ; i++ )
       		{
       			means[i] = new Double2D( 0.0, 0.0 );
       			n[i] =0; 
       		}

    	//Calculating means of nearest target till 2 unit distance 
        for( int i = 0 ; i < co.NUM_AGENTS ; i++ )
        {
            for( int j = 0 ; j < co.NUM_TARGETS ; j++ )
            {
                double currDist = distanceBetweenPointsSQR( co.targetPos[j].x, co.targetPos[j].y,
                    co.agentPos[i].x, co.agentPos[i].y);
               if( currDist < sensorRange)
                {
                	 means[i] = new Double2D(means[i].x + co.targetPos[j].x, means[i].y + co.targetPos[j].y);
                     n[i]++;
                     co.observedTargets[j]=true;
                }
            }
        }
        /*int count_observed_targets = 0;
        for( int i = 0 ; i < co.NUM_TARGETS ; i++ ){
        	if(co.observedTargets[i]==true)count_observed_targets++;
        }
        System.out.println("Count : "+count_observed_targets);*/
        for( int i = 0 ; i < co.NUM_AGENTS ; i++ )
        {
            if( n[i] != 0 )
            {
                means[i] = new Double2D(means[i].x/n[i], means[i].y/n[i]);
                clusterPoints[i] = co.agentPos[i];				
                clusterPoints[i] = new Double2D( (1-ALFA)*clusterPoints[i].x + ALFA*means[i].x,
                    (1-ALFA)*clusterPoints[i].y + ALFA*means[i].y );
                usable[i] = true; 
            }
            else
            {
                usable[i] = true;
                //clusterPoints[i] = new Double2D(75.0,75.0);
            }
        }
      }
}
