package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ChatBot extends JFrame implements KeyListener {

	private JPanel contentPane;
	private static JTextArea dialog;
	private JTextArea input;
	private JScrollPane scroll;
	
	private static Socket s;
	private static ServerSocket ss;
	private static BufferedReader br;
	private static InputStreamReader isr;
	
	private static Synthesizer synthesizer;

	
	
	
	static String[][] chatBot = {
		// Standard greetings
		{"hi","hello","hola","ola","howdy","hey"},
		{"hi","hello","hey","oui","yes"},
		
		// Question greetings
		{"how are you","how aa you","how r you","how aa u","how are u","how r u","whatsup","wassup"},
		{"good","great","fine","well","doing good","feeling fine","doing well","I'm great","better that yesterday","I guess I'm fine"},
		
		// Default
		{"noob","Sorry, I didn't get you", "come again","Sorry, I can't help you with this."}
	};
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatBot frame = new ChatBot();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		

		
		try {
			while(true){
				String message;
				ss = new ServerSocket(5000);
				s = ss.accept();
				isr = new InputStreamReader(s.getInputStream()); //Receive Data....
				
				br = new BufferedReader(isr);
				message = br.readLine();
				
				addNewMessage(message);
				
				br.close();
				isr.close();
				s.close();
				ss.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	

	
	

	/**
	 * Create the frame.
	 */
	public ChatBot() {
		super("HaSAY");
		setSize(600,400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		dialog = new JTextArea(20,50);
		input =  new JTextArea(1,50);
		
		scroll = new JScrollPane(
				dialog,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		dialog.setEditable(false);
		input.addKeyListener(this);
		
		contentPane.add(scroll);
		contentPane.add(input);
		contentPane.setBackground(new Color(255, 200, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		

        // Register Engine 
        try {

            // set property as Kevin Dictionary 
    		System.setProperty("freetts.voices", 
        			"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");  
    		
    		
			Central.registerEngineCentral 
			("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

	        // Create a Synthesizer 
	          synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
	            
	        
	        // Allocate synthesizer 
	        synthesizer.allocate();
			
	        // Resume Synthesizer 
	        synthesizer.resume();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		
		setContentPane(contentPane);
		
        
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			input.setEditable(false);
			
			//-------------- grab quote from the input-------//
				String quote = input.getText();
				input.setText("");
				
				addNewMessage(quote);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			input.setEditable(true);
		}		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
			


	public static void addNewMessage(String quote) {
		
		addTextToDialog("--> You: \t"+quote +"\n");	
		
		quote = quote.trim();
		
		//Remove punctuation
		while(quote.charAt(quote.length()-1) == '!' ||
				quote.charAt(quote.length()-1) == '.' ||
				quote.charAt(quote.length()-1) == '?' ) { 
			
			quote = quote.substring(0,quote.length()-1);
			
		}
		quote = quote.trim();
		
		
		byte response = 0;
		/*
		 * 0: we are searching through chatBot[][] for matches
		 * 1: we didn't find anything in chatBot[][]
		 * 2: we did find something in the chatBot[][]
		 */
			 
	//-------------- Check for matches  in the double string array-------//
		int j = 0; // Which group are we to check in the chatBot[][]
		while(response == 0) {
			
			if(inArray(quote.toLowerCase(),chatBot[j*2])) {
				response = 2;
				
				int arrayPosition = (j*2)+1;
				int randomPosition = (int) Math.floor(Math.random() * chatBot[arrayPosition].length);
				chatBotReply(arrayPosition, randomPosition);
			}
			j++;
			
			//Check if j reaches the last array then default response is needed...
			if(j*2 == chatBot.length - 1 && response == 0) {
				response = 1;
			}
			
		}				
		
		
	//-------------- If no match come out with the default-------//
		if(response == 1) {
			int arrayPosition = chatBot.length-1;
			int randomPosition = (int) Math.floor(Math.random() * chatBot[arrayPosition].length);
			chatBotReply(arrayPosition, randomPosition);
		}
	}
		
	private static void addTextToDialog(String str) {
		dialog.setText(dialog.getText() + str);	
	}

	private static boolean inArray(String in, String[] strs) {
		boolean match = false;
		for(int i = 0; i<strs.length; i++) {
			if(strs[i].equals(in)) {
				match = true;
			}
		}
		
		return match;
	}

	private static void chatBotReply(int arrayPosition, int randomPosition) {
		String message = chatBot[arrayPosition][randomPosition];
		addTextToDialog("-->HaSAY:\t" +message + "\n");
		
		new Thread (speakText(message));
		
		new Thread(returnToAndroid(message));
	
	}
	
	private static Runnable speakText(String textMessage) {
		try { 
            // speaks the given text until queue is empty. 
			synthesizer.speakPlainText(textMessage, null);
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
			
            // Deallocate the Synthesizer.
//			synthesizer.deallocate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Runnable returnToAndroid(String message) {
		try {
			Socket s = new Socket("192.168.43.2",5000);
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			pw.write(message);
			pw.flush();
			pw.close();
			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return null;
		
	}

}
