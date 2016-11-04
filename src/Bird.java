/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/


import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Bird extends Agent {
	
	protected static int SeparationRange;
	protected static int DetectionRange;
	private int currentTheta;
	private double currentSpeed= 2.5;
	private int maxTurnTheta=100;
	protected float x,y;
	protected Vector<Bird> List;
	
	// For making priority
	private int bossCount;
	
	// For remote agent
	ACLMessage msgp = null;
	
	protected void setup() {
		addBehaviour(new move(this, 1000/60));
		addBehaviour(new look(this, 100));		
		msgp = new ACLMessage(ACLMessage.INFORM);
	}
	
	private class look extends TickerBehaviour {
		public look(Agent a, long period) {
			super(a, period);
		}

		public void onTick() {
			Bird myLeader = null;
			double  distance, min = Double.MAX_VALUE;
			int theta = 0, idx = 0;
			int minPriority = 0;
			for( Bird i : List){
				distance = getDistance(i);
				if( distance < 300 && distance != 0){
					if(i.getBossCount() > minPriority){
						min = distance;
						myLeader = i;
						theta += i.getTheta();
						idx++;
						minPriority = i.getBossCount();
					} else if(distance < min ){
						min = distance;
						myLeader = i;
						theta += i.getTheta();
						idx++;
						minPriority = i.getBossCount();
					}
				}
			}
			turn( myLeader.getTheta()/2 + theta/idx/2 );
		}
	}
	
	public class move extends TickerBehaviour  {
		public move(Agent a, long period) {
			super(a, period);
		}
		public void onTick() {
			x += (currentSpeed * Math.cos(currentTheta * Math.PI/180));// + map.width;
			y += (currentSpeed * Math.sin(currentTheta * Math.PI/180));// - map.height;
			System.out.println("x: "+x+", y: "+y+", bossCount:"+bossCount);
			
//			//TODO how to know server ip
//		    msgp.setContent(getAID().getName()+") x: "+x+", y: "+y);
//		    AID remoteAMSf = new AID("salubhai@192.168.2.9:12349/JADE", AID.ISGUID);
//		    remoteAMSf.addAddresses("http://192.168.2.9:64505/acc");
//		    msgp.addReceiver(remoteAMSf);
//		    send(msgp);
		}
	}
	
	public void turn(int newHeading) {
        // determine if it is better to turn left or right for the new heading
        int left = (newHeading - currentTheta + 360) % 360;
        int right = (currentTheta - newHeading + 360) % 360;
        
        int thetaChange = 0;
        if (left < right) {
            thetaChange = Math.min(maxTurnTheta, left);
        }
        else {
            thetaChange = -Math.min(maxTurnTheta, right);
        }
        currentTheta = (currentTheta + thetaChange + 360) % 360;
    }
	
	public double getDistance(Bird otherBird) {
        float dX = otherBird.getX() - x;
        float dY = otherBird.getY() - y;
        
        return Math.sqrt( Math.pow( dX, 2 ) + Math.pow( dY, 2 ));
    }

	public Bird(Vector<Bird> vector, float x, float y, int theta, int rdB){
		this.List = vector;
		this.x = x;
		this.y = y;
		currentTheta = theta;
		bossCount = rdB;
	}
	
	public int getMaxTurnTheta() {
        return maxTurnTheta;
    }
	public void setMaxTurnTheta(int theta){maxTurnTheta = theta;}
	public int getTheta() {return currentTheta%360;}

	public int getX(){return (int)x;}
	public int getY(){return (int)y;}
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Bird: "+getAID().getName()+" terminating.");
	}

	public int getBossCount() {
		return bossCount;
	}

	public void setBossCount(int bossCount) {
		this.bossCount = bossCount;
	}
}