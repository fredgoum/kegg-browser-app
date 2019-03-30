/**
 * Reactor.java
 **/

import javax.swing.DefaultListModel;

/**
 * La classe Reactor fait correspondre à chaque rectangle (défini par deux coordonnées) présent
 * dans les fichier .conf d'une voie métabolique, à un identifiant de réaction et à une liste
 * de gènes impliquées dans cette réaction pour un organisme donné. Cette classe permet de vérifier
 * si les coordonnées du curseur de la souris (après un clic) sont dans ce rectangle.
 *
 * @author Alfred Goumou, Karl-Stephan Baczkowski
 */
public class Reactor {
	
	//------------------------------------------------------------------//
	// Variables d'instance spécifiques de la classe Reactor 			//
	//------------------------------------------------------------------//
	
	/** Abscisse X1 du coin supérieur gauche du rectangle **/
	protected int X1;
	/** Ordonnée Y1 du coin supérieur gauche du rectangle **/
	protected int Y1;
	/** Abscisse X2 du coin inférieur droit du rectangle **/
	protected int X2;
	/** Ordonnée Y2 du coin inférieur droit du rectangle **/
	protected int Y2;
	/** Identifiant de la réaction associé au rectangle **/
	protected String reacID;
	/** Liste des gènes impliqués dans la réaction associée **/
	protected DefaultListModel<String> geneList;
	
	
	//------------------------------------------------------------------//
	// Methodes (publiques) spécifiques de la classe Reactor			//
	//------------------------------------------------------------------//
	
	/** Vérifie si les coordonnées du Point fournies en argument sont inclues dans le rectangle du Reactor
	 * @param x abscisse x du Point 
	 * @param y ordonnée y du Point
	 * @return booléen indiquant si le Point est contenu dans le rectangle du Reactor
	 **/
	public boolean contains(int x, int y) {
		return ((X1 <= x) & (x <= X2) & (Y1 <= y) & (y <= Y2));
	}
	
	/** Constructeur de la classe Reactor
	 * @param X1 Abscisse X1 du coin supérieur gauche du rectangle
	 * @param Y1 Ordonnée Y1 du coin supérieur gauche du rectangle
	 * @param X2 Abscisse X2 du coin inférieur droit du rectangle
	 * @param Y2 Ordonnée Y2 du coin inférieur droit du rectangle
	 * @param reacID Identifiant de la réaction associé au rectangle
	 **/
	public Reactor(int X1, int Y1, int X2, int Y2, String reacID) {
		this.X1 = X1;
		this.Y1 = Y1;
		this.X2 = X2;
		this.Y2 = Y2;
		this.reacID = reacID;
		this.geneList = new DefaultListModel<String>();
	}
}

