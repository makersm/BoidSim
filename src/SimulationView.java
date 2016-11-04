

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SimulationView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Bird> birdList;
	private SimPanel mainPanel;
	
	public SimulationView(AgentList v){
		birdList = v.getAgentList();
		mainPanel = new SimPanel(v);
		mainPanel.setBackground(Color.WHITE);
		this.add(mainPanel);
	}
	
	public void update(Graphics g){
		 System.out.println("PaintTest - update()");
		 super.update(g); 
	}
	 
	public void paint(Graphics g){
		 super.paint(g);
		 for (Bird a : birdList){
			 g.drawRect(a.getX(),a.getY(),5,5);
		 }
	}
	
	public SimPanel getSimPanel(){
		return mainPanel;
	}
}
