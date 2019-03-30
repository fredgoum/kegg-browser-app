import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.*;
import javax.swing.*;
import java.awt.Container;
import org.fit.cssbox.swingbox.BrowserPane;
import java.net.URL;
import java.io.IOException;

public class SwingboxTest extends JFrame {
	
	//~ private Panneau panneau; 
	
	//Les constructeurs
	public SwingboxTest() throws IOException {	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Swingbox Test");
		setSize(900,500);
		
		//~ setLayout(new GridLayout(2,2));
		//~ getContentPane().add(new JButton("1"));
		
		// create the component 
		BrowserPane swingbox = new BrowserPane(); 
		// add the component to your GUI 
		Container myContainer = getContentPane();
		myContainer.add(new JScrollPane(swingbox));
		// display the page 
		swingbox.setPage(new URL("https://www.google.fr/?gws_rd=ssl"));
		
		//~ // create the component 
		//~ BrowserPane swingbox2 = new BrowserPane(); 
		//~ // add the component to your GUI 
		//~ Container myContainer2 = getContentPane();
		//~ myContainer2.add(new JScrollPane(swingbox2));
		//~ // display the page 
		//~ swingbox2.setPage(new URL("https://www.google.fr/"));
		
		//~ panneau = new Panneau();
        //~ myContainer.add (panneau);
        
        //~ this.setContentPane(new Panneau());
     
	}
	
	//La fonction main
	public static void main(String[] args) throws IOException {
		
		SwingboxTest browser = new SwingboxTest();
		browser.setVisible(true);
		
	}
}


/**
 * compilation
 * javac -cp swingbox-1.1-bin.jar SwingboxTest.java
 * Execution
 * java -cp swingbox-1.1-bin.jar:. SwingboxTest
 */
