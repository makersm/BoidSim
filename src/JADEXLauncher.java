import java.util.Random;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JADEXLauncher {

	public static final String DEFAULT_FILENAME = "leap.properties";
	private static Logger logger = Logger.getMyLogger("jade.Boot");

	private ContainerController cc;
	
	public void start(String[] args) throws StaleProxyException {
		if (args.length == 0){
			args = new String[] { "-gui" ,"-port", "9000" };
		}
		try {
			// Create the Profile
			ProfileImpl p = null;
			if (args.length > 0) {
				if (args[0].startsWith("-")) {
					// Settings specified as command line arguments
					Properties pp = parseCmdLineArgs(args);
					if (pp != null) {
						p = new ProfileImpl(pp);
					} else {
						// One of the "exit-immediately" options was specified!
						return;
					}
				} else {
					// Settings specified in a property file
					p = new ProfileImpl(args[0]);
				}
			} else {
				// Settings specified in the default property file
				p = new ProfileImpl(DEFAULT_FILENAME);
			}

			// Start a new JADE runtime system
			Runtime.instance().setCloseVM(true);
			// #PJAVA_EXCLUDE_BEGIN
			// Check whether this is the Main Container or a peripheral
			// container
			if (p.getBooleanProperty(Profile.MAIN, true)) {
				cc = Runtime.instance().createMainContainer(p);
				
				AgentList viewer = new AgentList();
				AgentController v = cc.acceptNewAgent("view", viewer);
				Random a = new Random();
				int randomBird=0;
				randomBird = a.nextInt(50)+1;
				for(int i =1; i<=50; i++){
					setBird(viewer,i, randomBird);
				}
				
				v.start();
				
			
		/*		// Show Game View
				LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
				new LwjglApplication(new SimulationView(viewer), config);*/
				

			} else {
				cc = Runtime.instance().createAgentContainer(p);
//				// TODO is right?
//				AgentList viewer = new AgentList();
//				AgentController v = cc.acceptNewAgent("view", viewer);
//				Random a = new Random();
//				int randomBird=0;
//				randomBird = a.nextInt(50)+1;
//				for(int i =1; i<=50; i++){
//					setBird(viewer,i, randomBird);
//				}
//				
//				v.start();
				
			}

		} catch (ProfileException pe) {
			System.err.println("Error creating the Profile [" + pe.getMessage()
					+ "]");
			pe.printStackTrace();
			printUsage();
			// System.err.println("Usage: java jade.Boot <filename>");
			System.exit(-1);
		} catch (IllegalArgumentException iae) {
			System.err.println("Command line arguments format error. "
					+ iae.getMessage());
			iae.printStackTrace();
			printUsage();
			// System.err.println("Usage: java jade.Boot <filename>");
			System.exit(-1);
		}
	}
	
	public void setBird(AgentList viewer, int idx, int rdB ) throws StaleProxyException{
		Random random = new Random();
		int bossCount = random.nextInt(1000);
		if(bossCount >= 0 && bossCount <= 9){
			Bird bird = new Bird(viewer.getAgentList(), random.nextInt(600), random.nextInt(600), random.nextInt(180)*2, 2);
			AgentController b = cc.acceptNewAgent("bird"+idx, bird);
			viewer.addBird(bird);
			b.start();
		} else if (bossCount >= 10 && bossCount <= 19){
			Bird bird = new Bird(viewer.getAgentList(), random.nextInt(600), random.nextInt(600), random.nextInt(180)*2, 1);
			AgentController b = cc.acceptNewAgent("bird"+idx, bird);
			viewer.addBird(bird);
			b.start();
		} else{
			Bird bird = new Bird(viewer.getAgentList(), random.nextInt(600), random.nextInt(600), random.nextInt(180)*2, 0);
			AgentController b = cc.acceptNewAgent("bird"+idx, bird);
			viewer.addBird(bird);
			b.start();
		}
	}
	
	public static Properties parseCmdLineArgs(String[] args)
			throws IllegalArgumentException {
		Properties props = new ExtendedProperties();

		int i = 0;
		while (i < args.length) {
			if (args[i].startsWith("-")) {
				// Parse next option

				// Switch options require special handling
				if (args[i].equalsIgnoreCase("-version")) {
					logger.log(
							Logger.INFO,
							"----------------------------------\n"
									+ Runtime.getCopyrightNotice()
									+ "----------------------------------------");
					return null;
				}
				if (args[i].equalsIgnoreCase("-help")) {
					printUsage();
					return null;
				}
				if (args[i].equalsIgnoreCase("-container")) {
					props.setProperty(Profile.MAIN, "false");
				} else if (args[i].equalsIgnoreCase("-"
						+ Profile.LOCAL_SERVICE_MANAGER)) {
					props.setProperty(Profile.LOCAL_SERVICE_MANAGER, "true");
				} else if (args[i].equalsIgnoreCase("-" + Profile.GUI)) {
					props.setProperty(Profile.GUI, "true");
				} else if (args[i].equalsIgnoreCase("-" + Profile.NO_MTP)) {
					props.setProperty(Profile.NO_MTP, "true");
				}
				// Options that can be specified in different ways require
				// special handling
				else if (args[i].equalsIgnoreCase("-name")) {
					if (++i < args.length) {
						props.setProperty(Profile.PLATFORM_ID, args[i]);
					} else {
						throw new IllegalArgumentException(
								"No platform name specified after \"-name\" option");
					}
				} else if (args[i].equalsIgnoreCase("-mtp")) {
					if (++i < args.length) {
						props.setProperty(Profile.MTPS, args[i]);
					} else {
						throw new IllegalArgumentException(
								"No mtps specified after \"-mtp\" option");
					}
				}
				// The -conf option requires special handling
				else if (args[i].equalsIgnoreCase("-conf")) {
					if (++i < args.length) {
						// Some parameters are specified in a properties file
						try {
							props.load(args[i]);
						} catch (Exception e) {
							if (logger.isLoggable(Logger.SEVERE))
								logger.log(Logger.SEVERE,
										"WARNING: error loading properties from file "
												+ args[i] + ". " + e);
						}
					} else {
						throw new IllegalArgumentException(
								"No configuration file name specified after \"-conf\" option");
					}
				}
				// Default handling for all other properties
				else {
					String name = args[i].substring(1);
					if (++i < args.length) {
						props.setProperty(name, args[i]);
					} else {
						throw new IllegalArgumentException(
								"No value specified for property \"" + name
										+ "\"");
					}
				}
				++i;
			} else {
				// Get agents at the end of command line
				if (props.getProperty(Profile.AGENTS) != null) {
					if (logger.isLoggable(Logger.WARNING))
						logger.log(Logger.WARNING,
								"WARNING: overriding agents specification set with the \"-agents\" option");
				}
				String agents = args[i];
				props.setProperty(Profile.AGENTS, args[i]);
				if (++i < args.length) {
					if (logger.isLoggable(Logger.WARNING))
						logger.log(
								Logger.WARNING,
								"WARNING: ignoring command line argument "
										+ args[i]
										+ " occurring after agents specification");
					if (agents != null && agents.indexOf('(') != -1
							&& !agents.endsWith(")")) {
						if (logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,
									"Note that agent arguments specifications must not contain spaces");
					}
					if (args[i].indexOf(':') != -1) {
						if (logger.isLoggable(Logger.WARNING))
							logger.log(
									Logger.WARNING,
									"Note that agent specifications must be separated by a semicolon character \";\" without spaces");
					}
				}
				break;
			}
		}

		// Consistency check
		if ("true".equals(props.getProperty(Profile.NO_MTP))
				&& props.getProperty(Profile.MTPS) != null) {
			if (logger.isLoggable(Logger.WARNING))
				logger.log(
						Logger.WARNING,
						"WARNING: both \"-mtps\" and \"-nomtp\" options specified. The latter will be ignored");
			props.remove(Profile.NO_MTP);
		}

		return props;
	}

	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("java -cp <classpath> jade.Boot [options] [agents]");
		System.out.println("Main options:");
		System.out.println("    -container");
		System.out.println("    -gui");
		System.out.println("    -name <platform name>");
		System.out.println("    -host <main container host>");
		System.out.println("    -port <main container port>");
		System.out
				.println("    -local-host <host where to bind the local server socket on>");
		System.out
				.println("    -local-port <port where to bind the local server socket on>");
		System.out
				.println("    -conf <property file to load configuration properties from>");
		System.out
				.println("    -services <semicolon separated list of service classes>");
		System.out
				.println("    -mtps <semicolon separated list of mtp-specifiers>");
		System.out
				.println("     where mtp-specifier = [in-address:]<mtp-class>[(comma-separated args)]");
		System.out.println("    -<property-name> <property-value>");
		System.out
				.println("Agents: [-agents] <semicolon separated list of agent-specifiers>");
		System.out
				.println("     where agent-specifier = <agent-name>:<agent-class>[(comma separated args)]");
		System.out.println();
		System.out
				.println("Look at the JADE Administrator's Guide for more details");
	}
}