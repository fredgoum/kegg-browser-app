/**
 * GenomeBrowser.java
 **/

import org.fit.cssbox.swingbox.BrowserPane;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;

import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.List;
import java.util.ArrayList;


/**
 * La classe GenomeBrowser est une fenetre web qui affiche le contexte génomique d'un gène donné.
 * L'utlisateur peut changer de contexte génomique en inscrivant les nouveaux identifiants d'un gène
 * et en cliquant sur le bouton "Search". Si le gène existe, les informations sur le gène est mis à jour
 * dans le panneau droit correspondant. Sinon, un message d'erreur est affiché dans le GeneInformationPanel.
 * GenomeBrowser s'occupe ainsi du parsing des fichier .conf pour déterminer la liste des réactions
 * associées à un gène donné. L'information est ensuite remontée dans la frame KEGGBrowser.
 *
 * @author Alfred Goumou, Karl-Stephan Baczkowski
 */
public class GenomeBrowser extends JPanel {
	
	//------------------------------------------------------------------//
	// Variables d'instance spécifiques de la classe GenomeBrowser		//
	//------------------------------------------------------------------//
	
	/** Champ de Texte pour identifiant de l'organisme **/
	private JTextField TextField1;
	/** Champ de Texte pour identifiant du gène **/
	private JTextField TextField2;
	/** Bouton "Search" pour afficher contexte génomique, information sur gène et réaction(s) éventuelle(s) associée(s) **/
	protected JButton bouton;
	/** Fenetre page web de contexte génomique du gène **/
	private BrowserPane swingbox;
	/** Liste de(s) réaction(s) éventuelle(s) catalysée(s) par le gène **/
	protected DefaultListModel<String> reacList;
	
	
	//------------------------------------------------------------------//
	// Methodes (publiques) spécifiques de la classe GenomeBrowser		//
	//------------------------------------------------------------------//
	
	/** Inscrire identifiant d'organisme dans le champ de Texte Species
	 * @param species identifiant d'organisme
	 **/
	public void setFieldSp(String species) {
		this.TextField1.setText(species);
	}
	
	/** Retourne identifiant d'organisme dans le champ de Texte Species
	 * @return identifiant d'organisme
	 **/
	public String getFieldSp() {
		return this.TextField1.getText();
	}
	
	/** Inscrire identifiant de gène dans le champ de Texte GeneID
	 * @param geneID identifiant de gène
	 **/
	public void setFieldID(String geneID) {
		this.TextField2.setText(geneID);
	}
	
	/** Retourne identifiant de gène dans le champ de Texte GeneID
	 * @return identifiant de gène
	 **/
	public String getFieldID() {
		return this.TextField2.getText();
	}
	
	/** Mise à jour de la page web sur contexte génomique dans la fenetre BrowserPane
	 * @param species identifiant d'organisme
	 * @param geneID identifiant de gène
	 **/
	public void updatePage(String species, String geneID) {
		try {
			this.swingbox.setPage(new URL("http://www.genome.jp/kegg-bin/show_genomemap?ORG=" + species + "&ACCESSION=" + geneID));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/** Parseur de fichier .txt à la recherche de metabolic pathways .conf à parser à leur tour
	 * MAJ de reaclList, la liste de réaction(s) éventuelle(s) associée(s) au gène donné
	 * @param species identifiant d'organisme
	 * @param geneID identifiant de gène
	 **/
	public void Parsing(String species, String geneID) {
		
		// Initialize reacList : list of reactions in which geneID is involved
		reacList = new DefaultListModel<String>();
		
		// Part 1.
		
		String file_name = "./local/info/" + species + "_" + geneID + ".txt";
		File file_txt = new File(file_name);
		
		// Check if file_name already exists in the "./local" repository
		// Otherwise, download file from rest.kegg.jp website
		if (!file_txt.exists()) {
			try {
				URL website = new URL("http://rest.kegg.jp/get/" + species + ":" + geneID);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream(file_name);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
				rbc.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		// Initialize a new pathwayList : all pathways in which the input gene catalyses a reaction
		List<String> pathwayList = new ArrayList<String>();
		
		// Load new information text into BufferedReader to parse metabolic pathways
		Pattern path = Pattern.compile(" {15}(\\d{5}) ");
		try (BufferedReader reader1 = new BufferedReader(new FileReader(file_txt))) {
			String line;
			while ((line = reader1.readLine()) != null) {
				Matcher m = path.matcher(line);
				while (m.find()) {
					pathwayList.add(m.group(1));
				}
			}
			reader1.close();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
		
		
		// For each pathway identified previously, indentify in which reaction input geneID is involved
		for (String mapID : pathwayList) {
			
			// Initialize a new locatorList : coordinates associated to a reaction catalysed by input species:geneID
			List<Point> locatorList = new ArrayList<Point>();
			
			// Part 2.
			
			String spec_name = "local/conf/" + species + mapID + ".conf";
			File spec_conf = new File(spec_name);
			
			// Check if specific .conf file already exists in "./local" repository
			if (!spec_conf.exists()) {
				try {
					URL website = new URL("http://rest.kegg.jp/get/" + species + mapID + "/conf");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(spec_name);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					rbc.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			// Read the specific .conf file to parse coordinates x1, y1 and any gene involved in the reaction rectangle
			// Capture and save coordinates in locatorList if one gene matches to input geneID
			Pattern coord1 = Pattern.compile("^rect \\((\\d*),(\\d*)\\).*\\)$");
			Pattern bgene = Pattern.compile(species + ":(.{5,8})[\\t\\+]");
			try (BufferedReader reader2 = new BufferedReader(new FileReader(spec_conf))) {
				String line;
				while ((line = reader2.readLine()) != null) {
					Matcher m1 = coord1.matcher(line);
					if (m1.matches()) {
						Matcher m2 = bgene.matcher(line);
						while(m2.find()) {
							if (m2.group(1).equals(geneID)) {
								int x1 = Integer.parseInt(m1.group(1));
								int y1 = Integer.parseInt(m1.group(2));
								locatorList.add(new Point(x1, y1));
							}
						}
					}
				}
				reader2.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			// Part 3.
			
			String map_name = "local/conf/map" + mapID + ".conf";
			File map_conf = new File(map_name);
			
			// Check if generic .conf file already exists in "./local" repository
			if (!map_conf.exists()) {
				try {
					URL website = new URL("http://rest.kegg.jp/get/map" + mapID + "/conf");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(map_name);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					rbc.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			// Read the generic .conf file to parse coordinates x1, y1, x2, y2 and reaction ID and save them into a new Reactor
			Pattern coord2 = Pattern.compile("^rect \\((\\d*),(\\d*)\\).*, R\\d{5}$");
			Pattern reac = Pattern.compile("\\+(R\\d{5})");
			try (BufferedReader reader3 = new BufferedReader(new FileReader(map_conf))) {
				String line;
				while ((line = reader3.readLine()) != null) {
					Matcher m1 = coord2.matcher(line);
					if (m1.matches()) {
						int x1 = Integer.parseInt(m1.group(1));
						int y1 = Integer.parseInt(m1.group(2));
						if (locatorList.contains(new Point(x1,y1))) {
							Matcher m2 = reac.matcher(line);
							while (m2.find()) {
								reacList.addElement(m2.group(1) + " @ " + species + mapID);
							}
						}
					}
				}
				reader3.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		
		}
		
	}
	
	/** Constructeur de la classe GenomeBrowser
	 * @throws IOException KEGGBrowser gère l'IOException si adresse url incorrecte
	 **/
	public GenomeBrowser() throws IOException {
		
		setLayout(new BorderLayout());
		
		// GenomeBrowser contains a menu bar on the top and a browser window at the bottom
		JPanel Menu = new JPanel();
		Menu.setBounds(0,0,getWidth(),35);
		Menu.setLayout(new FlowLayout());
		add(Menu, BorderLayout.NORTH);
		
		// Set and Display the browser title on the menu bar
		JLabel title = new JLabel("Genome Browser");
        title.setOpaque(true);
        title.setBackground(Color.CYAN);    
        Menu.add(title);
        // Ajout du champ Species
        JLabel Species = new JLabel("Species");    
        Menu.add(Species);
        // Ajout de la zone de texte de Species
        TextField1 = new JTextField("",7);
        Menu.add(TextField1); 
        // Ajout du champ de Gene ID
        JLabel GeneID = new JLabel("Gene ID");   
        Menu.add(GeneID);
        // Ajout de la zone de texte de Gene ID
        TextField2 = new JTextField("",7);
        Menu.add(TextField2); 
        // Ajout du button Search au Menu
        bouton = new JButton("Search");
        Menu.add(bouton, BorderLayout.NORTH);
        
        // Add URL or local image to GenomeBrowser window
        swingbox = new BrowserPane();
        add(new JScrollPane(swingbox));
        
	}
	
}

