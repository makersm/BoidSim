

import jade.wrapper.StaleProxyException;


public class Main {
	
	public static void main(String[] args) throws StaleProxyException {
		
		JADEXLauncher launcher = new JADEXLauncher();	
		
		launcher.start(args);
	}

}
