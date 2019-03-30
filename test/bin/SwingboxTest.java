//Representation d'une fenetre graphique
import javax.swing.JFrame;

import java.awt.Container;
// Terminal: find
import org.fit.cssbox.swingbox.BrowserPane;
import java.net.URL;
import java.io.IOException;

public class SwingboxTest extends JFrame {
	
	//Les constructeurs
	public SwingboxTest() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Swingbox Test");
		setSize(800,600);
		
		// Documentation SwingBox
		// http://cssbox.sourceforge.net/swingbox/documentation.php
		
		// create the component 
		BrowserPane swingbox = new BrowserPane(); 
		// add the component to your GUI 
		Container myContainer = getContentPane();
		myContainer.add(swingbox);
		// display the page 
		swingbox.setPage(new URL("http://www.genome.jp/kegg-bin/show_genomemap?ORG=eco&ACCESSION=b0630"));
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
