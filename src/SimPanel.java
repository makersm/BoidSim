import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

public class SimPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Vector<Bird> list;
	public SimPanel(AgentList v){
		list = v.getAgentList();
	}
	@Override
	public void paint(Graphics g) {
//		System.out.println("PaintTest - update()");
//		super.update(g); 
		super.paint(g);
		 for (Bird a : list){
			 g.drawRect(a.getX(),a.getY(),5,5);
		 }
	}
	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
//		System.out.println("PaintTest - update()");
//		super.update(g); 
	}
	
}
