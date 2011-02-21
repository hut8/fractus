package fractus.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import fractus.main.Fractus;
import fractus.main.UserInterface;

public class CommandLineInterface
implements UserInterface {
	private BufferedReader inputReader;
	private static Logger log;
	static {
		log = Logger.getLogger(CommandLineInterface.class.getName());
	}
	
	public CommandLineInterface() {
		inputReader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void initialize() {
		printTitle();
		repl();
	}
	
	public void printTitle() {
		
	}
	
	private void repl() {
		while (true) {
			
		}
	}
	
	public String prompt(String query) {
		return prompt(query, null);
	}
	
	public String prompt(String query, String default_) {
		// Print "query and [default]:"
		System.out.print(query);
		if (default_ != null) {
			System.out.print(" [" + default_ + "]");
		}
		System.out.println(" :");
		
		String line = null;
		
		try {
			line = inputReader.readLine();
		} catch (IOException e) {
			log.error("IO Error getting input", e);
			System.exit(-1);
		}
		
		if (line == null || line.equals("")) {
			return default_;
		}
		
		return line;
	}

}
