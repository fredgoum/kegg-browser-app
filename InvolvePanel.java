/**
 * InvolvePanel.java
 **/

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;


/**
 * La classe InvolvePanel correspond à la fenetre de sélection qui contient 
 * soit la liste des gènes impliqués dans une réaction donnée, 
 * soit la liste des réactions catalysées par un gène donné.
 * Cette JListe est mise à jour par intervention de l'utilisateur ailleur dans KEGGBrowser.
 * Elle supporte un ListSelectionListener qui à, son déclenchement, fait le lien
 * entre un gène affiché et une réaction associée, ou vice versa.
 *
 * @author Alfred Goumou, Karl-Stephan Baczkowski
 */
public class InvolvePanel extends JPanel {

	//------------------------------------------------------------------//
	// Variables d'instance spécifiques de la classe InvolvePanel		//
	//------------------------------------------------------------------//
	
	/** Fenetre déroulante de InvolvePanel **/
	private JScrollPane scrollpane;
	/** Fenetre à sélection contenant une liste d'items impliqués (gènes/réactions) **/
	protected JList<String> jlist;
	
	
	//------------------------------------------------------------------//
	// Methodes (publiques) spécifiques de la classe InvolvePanel		//
	//------------------------------------------------------------------//
	
	/** Adapter la taille du ScrollPane à la taille du InvolvePanel 
	 * @param dim dimension du InvolvePanel
	 **/
	public void reSize(Dimension dim) {
		scrollpane.setPreferredSize(dim);
	}
	
	/** Mise à jour de la JList
	 * @param list liste des gènes/réactions à afficher
	 **/
	public void updateJList(DefaultListModel<String> list) {
		jlist.setModel(list);
	}
	
	/** Constructeur de la classe InvolvePanel
	 * @param type type d'InvolvePanel "Involved in reaction(s)" ou "Involves gene(s)"
	 **/
	public InvolvePanel(String type) {
		
		setLayout(new BorderLayout());
		
		// Création d'un Menu en haut du InvolvePanel 
		JPanel Menu = new JPanel();
		Menu.setBounds(0,0,getWidth(),35);
		setLayout(new FlowLayout());
		add(Menu, BorderLayout.NORTH);
		
		// Set and Display the browser title on the menu bar
		JLabel title = new JLabel(type);
		Menu.add(title);
		
		// Add a list of genes/reactions with JList
		String[] data = new String[0];
		jlist = new JList<String>(data); //data has type String[]
		
		jlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jlist.setLayoutOrientation(JList.VERTICAL);
		jlist.setVisibleRowCount(0);
		
		scrollpane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setViewportView(jlist);
        JScrollBar bar = scrollpane.getVerticalScrollBar();
        scrollpane.setPreferredSize(new Dimension(280,100));
        add(scrollpane);
		
	}
	
}

