/**
 * InformationPanel.java
 **/

import org.fit.cssbox.swingbox.BrowserPane;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

import java.net.URL;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedReader;


/**
 * La classe InformationPanel permet d'importer d'un fichier .txt et d'afficher des informations
 * relatives soit à un gène donnée, soit à une réaction donnée.
 * Elle peut également afficher des messages d'erreurs si le fichier .txt est introuvable ou illisible.
 * Reaction InformationPanel contient spécifiquement un bouton "Image" qui permet d'afficher
 * le détails de la réaction donnée.
 *
 * @author Alfred Goumou, Karl-Stephan Baczkowski
 */
public class InformationPanel extends JPanel{
	
	//------------------------------------------------------------------//
	// Variables d'instance spécifiques de la classe InformationPanel	//
	//------------------------------------------------------------------//
	
	/** Bouton "Image" pour afficher le détail de la réaction dans le PathwayBrowser
	 * (Uniquement présent dans Reaction InformationPanel) **/
	protected JButton bouton;
	/** Fenetre déroulante de InformationPanel **/
	private JScrollPane scrollpane;
	/** Fenetre de texte contenant information sur gène/réaction ou message d'erreur **/
	private JTextArea textArea;
	
	
	//------------------------------------------------------------------//
	// Methodes (publiques) spécifiques de la classe InformationePanel	//
	//------------------------------------------------------------------//
	
	/** Adapter la taille du ScrollPane à la taille du InformationPanel 
	 * @param dim dimension du InvolvePanel
	 **/
	public void reSize(Dimension dim) {
		scrollpane.setPreferredSize(dim);
	}
	
	/** Mettre à jour information sur gene dans InformationPanel
	 * @param species identifiant KEGG de l'organisme
	 * @param ID identifiant KEGG du gène
	 * @throws IOException KEGGBrowser traite l'IOException si erreur de (télé)chargement du .txt
	 **/
	public void updateInfo(String species, String ID) throws IOException {
		
		String file_name = "./local/info/" + species + "_" + ID + ".txt";
		File file_txt = new File(file_name);
		
		// Check if file_name already exists in the "./local" repository
		// Otherwise, download file from rest.kegg.jp website
		if (!file_txt.exists()) {
			URL website = new URL("http://rest.kegg.jp/get/" + species + ":" + ID);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(file_name);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		}
		
		// Load new information text into InformationPanel
		BufferedReader reader = new BufferedReader(new FileReader(file_txt));
		textArea.read(reader, null);
		reader.close();
	}
	
	/** Erreur d'Affichage : fichier .txt ou .conf inexistant
	 * @param err message d'erreur à afficher selon la recherche (incorrecte) de l'utilisateur
	 **/
	public void errorInfo(String err) {
		textArea.setText(err);
	}
	
	/** Constructeur de InformationPanel 
	 * @param type : type d'InformationPanel "Gene information" ou "Reaction information"
	 * @throws IOException KEGGBrowser traite l'IOException si erreur dans MAJ de info.txt
	 **/
	public InformationPanel(String type) throws IOException {

		// Creation d'un Menu en haut du InformationPanel
		setLayout(new BorderLayout());
		JPanel Menu = new JPanel();
		//~ Menu.setBounds(0, 0, getWidth(), 35);
		Menu.setPreferredSize(new Dimension(getWidth(),35));
		add(Menu, BorderLayout.NORTH);
		
		// Affichage titre de Menu
		Menu.setLayout(new FlowLayout());
		JLabel title = new JLabel(type);
        title.setOpaque(true);
        title.setBackground(Color.CYAN);    
        Menu.add(title);
        
        // Ajout du bouton "Image" au Menu (dans Reaction InformationPanel uniquement)
        if (type == "Reaction information") {
			bouton = new JButton("Image");
			Menu.add(bouton);
        }
        
		// Définir une zone de texte où importer information.txt
		textArea = new JTextArea(300, 100);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		Font font = new Font("Courier",Font.PLAIN,12);
        textArea.setFont(font);
        textArea.setCaretPosition(0);
        
		scrollpane = new JScrollPane(textArea,
										JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar bar = scrollpane.getVerticalScrollBar();
		add(scrollpane);
		
	}
}
