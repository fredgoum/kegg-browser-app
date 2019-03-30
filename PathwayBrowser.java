/**
 * PathwayBrowser.java
 **/

import org.fit.cssbox.swingbox.BrowserPane;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;

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
 * La classe PathwayBrowser affiche en image le réseau de réactions impliquées dans une voie
 * métabolique donnée. Le contexte métabolique est mis à jour par l'utilisateur qui fournit
 * les identifiants de voie métabolique et clique sur le bouton "Search" ; les informations sur
 * une réaction féinie par défaut sont affichées dans le panneau droit correspondant.
 * PathwayBrowser gère le parsing des fichiers .conf pour construire une reactorList, qui contient 
 * chaque réaction présent dans la voie métabolique, ainsi que les gènes spécifiques d'un organisme
 * qui sont impliqués dans cette réaction.
 * PathwayBrowser peut également afficher le détail d'une réaction après un clic sur le bouton "Image". 
 *
 * @author Alfred Goumou, Karl-Stephan Baczkowski
 */
public class PathwayBrowser extends JPanel {
	
	//------------------------------------------------------------------//
	// Variables d'instance spécifiques de la classe PathwayBrowser		//
	//------------------------------------------------------------------//
	
	/** Champ de Texte pour identifiant de l'organisme **/
	private JTextField TextField1;
	/** Champ de Texte pour identifiant de la voie métabolique **/
	private JTextField TextField2;
	/** Bouton "Search" pour afficher voie métabolique, information sur la réaction par défaut
	 * et liste des gène(s) impliqué(s) dans cette réaction (si présents chez organisme) **/
	protected JButton bouton;
	/** Booléen indiquant si image affichée est une voie métabolique pour TRUE, une réaction pour FALSE **/
	private boolean isPathway;
	/** Image affichée dans le JPanel du PathwayBrowser **/
	protected Image image;
	/** Cadre affichant l'image dans le JPanel du PathwayBrowser **/
	protected JLabel imglabel;
	/** Liste de toutes les réactions impliquées dans la voie métabolique **/
	protected List<Reactor> reactorList;
	/** Réaction par défaut ou définie par l'utilisateur après sélection **/
	protected Reactor reactor_def;
	
	//------------------------------------------------------------------//
	// Methodes (publiques) spécifiques de la classe PathwayBrowser		//
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
	
	/** Inscrire identifiant de la voie métabolique dans le champ de Texte GeneID
	 * @param mapID identifiant de la voie métabolique
	 **/
	public void setFieldID(String mapID) {
		this.TextField2.setText(mapID);
	}
	
	/** Retourne identifiant de la voie métabolique dans le champ de Texte GeneID
	 * @return identifiant de la voie métabolique
	 **/
	public String getFieldID() {
		return this.TextField2.getText();
	}
	
	/** Mise à jour de l'image .png (pour une voie métabolique) dans la fenetre PathwayBrowser
	 * @param species identifiant d'organisme
	 * @param mapID identifiant de la voie métabolique
	 * @throws IOException KEGGBrowser gère l'IOException si impossible de (télé)charger l'image
	 **/
	public void updateImg(String species, String mapID) throws IOException {
		
		isPathway = true;
		String file_name = "./local/image/" + species + mapID + ".png";
		File file_image = new File(file_name);
		
		// Check if file_name already exists in the "./local" repository
		// Otherwise, download file from rest.kegg.jp website
		if (!file_image.exists()) {
			URL website = new URL("http://rest.kegg.jp/get/" + species + mapID +"/image");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(file_name);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		}
		
		// Load new updated image in PathwayPanel
		image = ImageIO.read(file_image);
		imglabel.setIcon(new ImageIcon(image));
		imglabel.repaint();
	}
	
	/** Mise à jour de l'image .gif (pour une réaction) dans la fenetre PathwayBrowser
	 * @param reacID identifiant de la réaction
	 * @throws IOException KEGGBrowser gère l'IOException si impossible de (télé)charger l'image
	 **/
	public void updateReac(String reacID) throws IOException {
		
		isPathway = false;
		String file_name = "./local/image/rn_" + reacID + ".gif";
		File file_image = new File(file_name);
		
		// Check if image_file already exists in "./local" repository
		// Otherwise, download file from rest.kegg.jp website
		if (!file_image.exists()) {
			URL website = new URL("http://rest.kegg.jp/get/rn:" + reacID + "/image");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(file_name);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		}
		
		// Load new updated image into PathwayPanel
		image = ImageIO.read(file_image);
		imglabel.setIcon(new ImageIcon(image));
		imglabel.repaint();
	}
	
	/** Parseur de fichier .conf et Recherche d'expressions régulière 
	 * MAJ de reactorList, la liste de réactions impliquées dans la voie métabolique donnée
	 * MAJ du reactor_def, la réaction choisie par défaut ou définié par l'utilisateur avec reacID
	 * @param species identifiant d'organisme
	 * @param mapID identifiant de la voie métabolique
	 * @param reacID identifiant de la réaction
	 **/
	public void Parsing(String species, String mapID, String reacID) {
		
		// Reactor by default is indexed at 0 if input reacID == ""
		int reactorIndex = 0;
		
		// Initialize a new reactorList : coordinates associated to a reaction rectangle and genes involved in this reaction
		this.reactorList = new ArrayList<Reactor>();
		
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
		Pattern coord1 = Pattern.compile("^rect \\((\\d*),(\\d*)\\) \\((\\d*),(\\d*)\\).*, R\\d{5}$");
		Pattern reac = Pattern.compile("\\+(R\\d{5})");
		try (BufferedReader reader1 = new BufferedReader(new FileReader(map_conf))) {
			String line;
			while ((line = reader1.readLine()) != null) {
				Matcher m1 = coord1.matcher(line);
				if (m1.matches()) {
					int x1 = Integer.parseInt(m1.group(1));
					int y1 = Integer.parseInt(m1.group(2));
					int x2 = Integer.parseInt(m1.group(3));
					int y2 = Integer.parseInt(m1.group(4));
					Matcher m2 = reac.matcher(line);
					while (m2.find()) {
						String reac_id = m2.group(1);
						// changes reactorIndex if reacID identified
						if (reac_id.equals(reacID)) {
							reactorIndex = reactorList.size();
						}
						// Add new Reactor to reactorList
						this.reactorList.add(new Reactor(x1, y1, x2, y2, reac_id));
					}
				}
			}
			reader1.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
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
		
		// Read the specific .conf file to parse coordinates x1, y1 and all genes involved in the reaction rectangle
		// Capture the Reactor in reactorList which share same coordinates x1, y1 then save the list of genes
		Pattern coord2 = Pattern.compile("^rect \\((\\d*),(\\d*)\\).*\\)$");
		Pattern bgene = Pattern.compile(species + ":(.{5,8})[\\t\\+]");
		try (BufferedReader reader2 = new BufferedReader(new FileReader(spec_conf))) {
			String line;
			while ((line = reader2.readLine()) != null) {
				Matcher m1 = coord2.matcher(line);
				if (m1.matches()) {
					int x1 = Integer.parseInt(m1.group(1));
					int y1 = Integer.parseInt(m1.group(2));
					Matcher m2 = bgene.matcher(line);
					while(m2.find()) {
						for (Reactor reactor : reactorList) {
							if ((reactor.X1 == x1) & (reactor.Y1 == y1)) {							
								reactor.geneList.addElement(m2.group(1));
							}
						}
					}				
				}
			}
			reader2.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		// Reactor by default is indexed at 0 if input reacID == "", Reactor corresponding to reacID otherwise
		reactor_def = reactorList.get(reactorIndex);
	}
	
	/** Classe interne qui contient un fichier .png or .gif et un rectangle rouge de sélection **/
	public class ImgLabel extends JLabel {
		/** Constructeur de la classe interne ImgLabel 
		 * @param imageicon une image adaptée au JLabel 
		 **/
		public ImgLabel(ImageIcon imageicon) {
			super(imageicon);
		}
		/** Réécriture de la méthode paintComponent pour afficher ou non le rectangle rouge de sélection 
		 * @param g Contexte graphique
		 **/
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (isPathway) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.red);
				g2d.setStroke(new BasicStroke(3.0f));
				g2d.drawRect(reactor_def.X1 + (imglabel.getWidth() - image.getWidth(null))/2,
							reactor_def.Y1 + (imglabel.getHeight() - image.getHeight(null))/2,
							reactor_def.X2 - reactor_def.X1,
							reactor_def.Y2 - reactor_def.Y1);
			}
		}
	}
	
	/** Constructeur de la classe PathwayBrowser
	 * @throws IOException KEGGBrowser gère l'IOException si impossible de (télé)charger l'image 
	 **/
	public PathwayBrowser() throws IOException {
		
		setLayout(new BorderLayout());
		
		// PathwayBrowser contains a menu bar on the top and a browser window at the bottom
		JPanel Menu = new JPanel();
		Menu.setBounds(0,0,getWidth(),35);
		Menu.setLayout(new FlowLayout());
		add(Menu, BorderLayout.NORTH);
		
		// Set and Display the browser title on the menu bar
		JLabel title = new JLabel("Pathway Browser");
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
        JLabel GeneID = new JLabel("Map ID");   
        Menu.add(GeneID);
        // Ajout de la zone de texte de Gene ID
        TextField2 = new JTextField("",7);
        Menu.add(TextField2); 
        // Ajout du button Search au Menu
        bouton = new JButton("Search");
        Menu.add(bouton, BorderLayout.NORTH);
        
        // Ajouter un JLabel dans la fenetre de la metabolic pathway
        imglabel = new ImgLabel(new ImageIcon());
        add(new JScrollPane(imglabel));
        
	}
	
}

