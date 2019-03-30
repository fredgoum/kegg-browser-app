/**
 * KEGGBrowser.java
 * 
 * # Compiler KEGGBrowser.java
 * javac -cp lib/swingbox-1.1-bin.jar Reactor.java GenomeBrowser.java PathwayBrowser.java InformationPanel.java InvolvePanel.java KEGGBrowser.java
 * 
 * # Lancer l'appli KEGGBrowser
 * java -cp lib/swingbox-1.1-bin.jar:. KEGGBrowser
 * 
**/

import org.fit.cssbox.swingbox.BrowserPane;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.io.IOException;


/**
 * La classe KEGGBrowser est une application intéractive JAVA développée à l'intention des Biologistes
 * et BioInformaticiens. Cet outil fait le lien entre des données génomiques et des données métaboliques
 * téléchargées automatiquement depuis la plateforme en ligne KEGG.
 * Le panneau supérieur intègre des données génomiques pour un gène donné en affichant informations et
 * contexte génomique. Le panneau inférieur intègre des données pour une voie métabolique données ;
 * L'utilisateur peut intéragir avec le réseau réactionnel affiché pour définir une réaction particulière
 * et connaitre les gènes éventuels qui catalysent cette réaction, pour un organisme donné.
 * Les menus déroulants placés sous les fenetres d'information permettent de faire le lien entre le 
 * GenomeBrowser et le PathwayBrowser à l'initiative de l'utilisateur.
 *
 * @author Alfred Goumou, Karl-Stephan Baczkowski
 */
public class KEGGBrowser extends JFrame implements ActionListener, MouseListener, ListSelectionListener {
	
	//------------------------------------------------------------------//
	// Variables d'instance spécifiques de la classe KEGGBrowser		//
	//------------------------------------------------------------------//
	
	/** leftPanel contenant genome browser et pathway browser **/
	private JPanel leftPanel;
	/** fenetre d'affichage du contexte génomique, genome browser **/
	private GenomeBrowser genomebrowser;
	/** fenetre d'affichage de la voie métabolique, pathway browser **/
	private PathwayBrowser pathwaybrowser;
	
	/** rightPanel contenant Gene Information et Reaction Information **/
	private JPanel rightPanel;
	/** Gene InformationPanel **/
	private InformationPanel gene_info;
	/** Reaction InformationPanel **/
	private InformationPanel reaction_info;
	/** Involved in reaction(s) Panel **/
	private InvolvePanel inv_reaction;
	/** Involves gene(s) Panel **/
	private InvolvePanel inv_gene;
	/** JSplitPane divise la frame en deux JPanels droit et gauche **/
	private JSplitPane splitPane;
	
	/** Identifiant de l'organisme pour le gène **/
	private String GeneSp = "eco";
	/** Identifiant du gène **/
	private String GeneID = "b0628";
	/** Identifiant de l'organisme pour la voie métabolique **/
	private String MapSp = "eco";
	/** Identifiant de la voie métabolique **/
	private String MapID = "00785";
	/** Réaction affichée par défaut ou définie par l'utilisateur **/
	private Reactor reactor_def;
	
	
	/** Redimensionnage de la fenetre et des InvolvePanels **/
	public void MAJ_redimensioning() {
		splitPane.setDividerLocation((int) (getWidth()*0.7));
		//~ splitPane.setDividerLocation(getWidth()-300);
		inv_reaction.reSize(new Dimension((int) rightPanel.getWidth(), (int) (rightPanel.getHeight()/4 - 35)));
		inv_gene.reSize(new Dimension((int) rightPanel.getWidth(), (int) (rightPanel.getHeight()/4 - 35)));	
	}
	
	/** Implementer la methode actionPerformed(ActionEvent e) pour l'interface ActionListener permet :
	 * 1. si bouton du GenomeBrowser pressé 
	 * - Mettre à jour la page web dans la fenetre genomebrowser
	 * - Importer les informations sur le gene
	 * - Afficher dans quelle(s) réaction(s) est impliqué ce gène
	 * - OU afficher un message d'erreur si info.txt introuvable
	 * 2. si bouton du PathwayBrowser pressé
	 * - Mettre à jour l'image affichée dans pathwaybrowser
	 * - Importer les information sur un réaction par défaut
	 * - Afficher les gène(s) qui catalyse(nt) cette réaction
	 * - OU Afficher un message d'erreur si image.png introuvable
	 * 3. si bouton Image du GeneInformationPanel pressé
	 * - Afficher détails de la réaction dans pathwaybrowser
	 * @param e ActionEvent particulier déclenché dans la frame KEGGBrowser
	 **/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == genomebrowser.bouton) {
			String gene_sp = genomebrowser.getFieldSp();
			String gene_id = genomebrowser.getFieldID();
			genomebrowser.updatePage(gene_sp, gene_id);
			try {
				gene_info.updateInfo(gene_sp, gene_id);
				genomebrowser.Parsing(gene_sp, gene_id);
				inv_reaction.updateJList(genomebrowser.reacList);
				this.GeneSp = gene_sp;
				this.GeneID = gene_id;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				gene_info.errorInfo("ERROR: incorrect gene entry");
				inv_reaction.updateJList(new DefaultListModel<String>());
			}
		}
		if (e.getSource() == pathwaybrowser.bouton) {
			String map_sp = pathwaybrowser.getFieldSp();
			String map_id = pathwaybrowser.getFieldID();
			try {
				pathwaybrowser.updateImg(map_sp, map_id);
				pathwaybrowser.Parsing(map_sp, map_id, "");
				reactor_def = pathwaybrowser.reactor_def;
				reaction_info.updateInfo("rn", reactor_def.reacID);
				inv_gene.updateJList(reactor_def.geneList);
				this.MapSp = map_sp;
				this.MapID = map_id;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				reaction_info.errorInfo("ERROR: incorrect pathway entry");
				inv_gene.updateJList(new DefaultListModel<String>());
			}
		}
		if (e.getSource() == reaction_info.bouton) {
			try {
				pathwaybrowser.updateReac(reactor_def.reacID);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	/** Redéfinir la méthode MouseEntered(MouseEvent me)
	 * @param me MouseEvent déclenché uniquement sur l'imagelabel dans pathwaybrowser
	 **/
	public void mouseEntered(MouseEvent me) {}
	/** Redéfinir la méthode MouseExited(MouseEvent me)
	 * @param me MouseEvent déclenché uniquement sur l'imagelabel dans pathwaybrowser
	 **/
	public void mouseExited(MouseEvent me) {}
	/** Redéfinir la méthode MousePressed(MouseEvent me)
	 * @param me MouseEvent déclenché uniquement sur l'imagelabel dans pathwaybrowser
	 **/
	public void mousePressed(MouseEvent me) {}
	/** Redéfinir la méthode MouseReleased(MouseEvent me)
	 * @param me MouseEvent déclenché uniquement sur l'imagelabel dans pathwaybrowser
	 **/
	public void mouseReleased(MouseEvent me) {}
	/** Redéfinir la méthode MouseClicked(MouseEvent me)
	 * Elle permet de changer de reaction_def et de redéssiner le rectangle rouge associé
	 * @param me MouseEvent déclenché uniquement sur l'imagelabel dans pathwaybrowser
	 **/
	public void mouseClicked(MouseEvent me) {
		if (me.getSource() == pathwaybrowser.imglabel) {
			Point p = me.getPoint();
			int xM = (int) (p.getX() - (pathwaybrowser.imglabel.getWidth() - pathwaybrowser.image.getWidth(null)) / 2);
			int yM = (int) (p.getY() - (pathwaybrowser.imglabel.getHeight() - pathwaybrowser.image.getHeight(null)) / 2);
			for (Reactor reactor : pathwaybrowser.reactorList) {
				if (reactor.contains(xM, yM)) {
					reactor_def = pathwaybrowser.reactor_def = reactor;
					try {
						reaction_info.updateInfo("rn", reactor_def.reacID);
						inv_gene.updateJList(reactor_def.geneList);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		}
	}
	
	/** Implementer la methode valueChanged(ListSelectionEvent e) pour l'interface ListSelectionListener permet :
	 * 1. si l'évenement vient du InvolvesGenes Panel 
	 * - Inscrit les identifiants d'organisme et de gène dans les champs de texte de GenomeBrowser
	 * - Mettre à jour la page web dans la fenetre genomebrowser
	 * - Importer les informations sur le gene
	 * - Afficher dans quelle(s) réaction(s) est impliqué ce gène
	 * 2. si l'évenement vient du InvolvedInReactions Panel 
	 * - Inscrit les identifiants d'organisme et de voie métabolique dans les champs de texte de PathwayBrowser
	 * - Mettre à jour l'image affichée dans pathwaybrowser
	 * - Importer les information sur un réaction par défaut
	 * - Afficher les gène(s) qui catalyse(nt) cette réaction
	 * @param e ListSelectionEvent déclenché dans la JList d'un des InvolvePanels
	 **/
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == inv_gene.jlist) {
			if (!inv_gene.jlist.isSelectionEmpty() && (e.getValueIsAdjusting() == false)) {
				// Get gene species et gene ID and rewrite textfields in Genome Browser Menu
				this.GeneSp = this.MapSp;
				this.GeneID = inv_gene.jlist.getSelectedValue();
				genomebrowser.setFieldSp(GeneSp);
				genomebrowser.setFieldID(GeneID);
				
				// Update Genome Browser, de Gene Information et de InvolvedInReactions Panels
				genomebrowser.updatePage(GeneSp,GeneID);
				try {
					gene_info.updateInfo(GeneSp, GeneID);
					genomebrowser.Parsing(GeneSp, GeneID);
					inv_reaction.updateJList(genomebrowser.reacList);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		if (e.getSource() == inv_reaction.jlist) {
			if (!inv_reaction.jlist.isSelectionEmpty() && (e.getValueIsAdjusting() == false)) {
				// Get map species et map ID and rewrite textfields in Pathway Browser Menu
				this.MapSp = this.GeneSp;
				this.MapID = inv_reaction.jlist.getSelectedValue().substring(12);
				String reacID = inv_reaction.jlist.getSelectedValue().substring(0,6);
				pathwaybrowser.setFieldSp(MapSp);
				pathwaybrowser.setFieldID(MapID);
				
				// Update Pathway Browser, de Reaction Information et de InvolvesGenes Panels
				try {
					pathwaybrowser.updateImg(MapSp, MapID);
					pathwaybrowser.Parsing(MapSp, MapID, reacID);
					reactor_def = pathwaybrowser.reactor_def;
					reaction_info.updateInfo("rn", reactor_def.reacID);
					inv_gene.updateJList(reactor_def.geneList);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	/** Constructeur de la classe KEGGBrowser
	 * @throws IOException KEGGBrowser laisse l'intérpréteur gérer l'IOException
	 **/
	public KEGGBrowser() throws IOException {
		
		setTitle("KEGG Browser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		
		
		// Creation of leftPanel containg both genome browser and pathway browser
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(2,1));
        
        // Creation des fenetres de recherche génomique et MAJ de la reacList
        genomebrowser = new GenomeBrowser();
        genomebrowser.bouton.addActionListener(this);
        genomebrowser.setFieldSp(GeneSp);
        genomebrowser.setFieldID(GeneID);
        genomebrowser.Parsing(GeneSp, GeneID);
        genomebrowser.updatePage(GeneSp, GeneID);
        
        // Creation des fenetres de recherche métabolique et MAJ de la reactorList
		pathwaybrowser = new PathwayBrowser();
        pathwaybrowser.bouton.addActionListener(this);
        pathwaybrowser.setFieldSp(MapSp);
        pathwaybrowser.setFieldID(MapID);
        pathwaybrowser.imglabel.addMouseListener(this);
        try {
			pathwaybrowser.updateImg(MapSp, MapID);
			pathwaybrowser.Parsing(MapSp, MapID, "");
			reactor_def = pathwaybrowser.reactor_def;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
        
		// Repartition de JPanel leftPanel
		leftPanel.add(genomebrowser);
        leftPanel.add(pathwaybrowser);
		
		
		// Creation of rightPanel containing gene and reaction information
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(4,1));
		
		// Creation des JPanel d'information sur gènes et réaction
        gene_info = new InformationPanel("Gene information");
        try {
			gene_info.updateInfo(GeneSp, GeneID);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
        reaction_info = new InformationPanel("Reaction information");
        reaction_info.bouton.addActionListener(this);
        try {
			reaction_info.updateInfo("rn", reactor_def.reacID);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
        
        // Creation de JPanel pour les champs Involde in reaction(s) et Involves gene(s)
        inv_reaction = new InvolvePanel("Involved in reaction(s)");
        inv_reaction.jlist.addListSelectionListener(this);
        inv_reaction.updateJList(genomebrowser.reacList);
        inv_gene = new InvolvePanel("Involves gene(s)");
        inv_gene.jlist.addListSelectionListener(this);
        inv_gene.updateJList(reactor_def.geneList);
        
		// Repartition du JPanel rightPanel
        rightPanel.add(gene_info);
        rightPanel.add(inv_reaction);
        rightPanel.add(reaction_info);
        rightPanel.add(inv_gene);
        
        
        // JSplitPane devides the window in two components (here: left and right)
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);  // split the window verticaly					
        splitPane.setDividerLocation((int) (getWidth()*0.7));		// relative position of splitPane separator relative to frame width
        //~ splitPane.setDividerLocation(getWidth()-300);			// the initial position of the divider is 600 (our window is 900 pixels high)
        getContentPane().setLayout(new GridLayout());
        getContentPane().add(splitPane);
		
	}
	
	/** Méthode main qui crée la frame KEGGBrowser et redimensionne à tout moment :
	 * - la frame de l'application KEGGBrowser
	 * - la JList des fenetres InvolvePanels
	 * @param args laissé vide par l'utilisateur
	 * @throws IOException laisse l'interpréteur gérer l'IOException
	 **/
	public static void main(String[] args) throws IOException {
		
		KEGGBrowser keggbrowser = new KEGGBrowser();
		keggbrowser.setVisible(true);
		
		while (true) {
			keggbrowser.MAJ_redimensioning();
		}
	}
	
}

