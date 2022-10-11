package blackjack.om;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Une main de black-jack prend des cartes une par une
 * Elle indique en continu :
 *  - getNbCartes() : combien elle a de cartes
 *  - getValeur()   : la valeur totale de la main
 *  - isBlackJack() : si elle contient un blackJack (oui ssi il y a 1 carte valant 10 et un AS)
 *  - isPerdante()  : si elle est perdante (oui ssi le total est supérieur à 21)
 * NB: une main peut toujours prendre des cartes même si elle est perdante
 *     mais ça n'a pas de sens dans une partie de blackjack
 */
public class MainBlackjack {

	/**
	 * Attributs d'un objet MainBlackjack :
	 * 
	 * cartes : liste dynamique des cartes contenues dans la main
	 * score  : valeur de toutes les cartes (peut aussi être obtenu par .getValeur() )
	 */
	private List<Carte> cartes;
	private int			score;

	public MainBlackjack() {
		this.cartes = new LinkedList<>();
		this.score  = 0;
	}
	/**
	 * Action sur la main : Ajouter une carte
	 * @param carte la carte à ajouter dans la main
	 */
	public void prendreCarte(Carte carte) {
		this.cartes.add(carte);
		this.score = this.calculerValeur();
	}
	/**
	 * Action sur la main : Jeter toutes les cartes (réinitialiser la main)
	 */
	public void viderMain() {
		this.cartes.clear();
		this.score = 0;
	}
	/**
	 * Nombre de cartes contenues dans la main
	 * @returns nombre de cartes
	 */
	public int getNbCartes() {
		return this.cartes.size();
	}
	/**
	 * Une carte de la main
	 * @returns nombre de cartes
	 */
	public Carte getCarte(int num) {
		return this.cartes.get(num);
	}
	/**
	 * la liste des cartes de la main
	 * @returns la liste
	 */
	public List<Carte> getCartes() {
		return new ArrayList<Carte>(cartes);
	}
	/**
	 * Obtenir le "score" de la main (somme des valeurs des cartes avec un comptage optimisé des as)
	 * @returns la valeur de la main complète
	 */
	public int getScore() {
		return this.score;
	}
	/**
	 * Indique si la main contient (ou non) un BlackJack (21 points avec 2 cartes seulement)
	 * @returns vrai si la main représente un "BlackJack"
	 */
	public boolean isBlackJack() {
		return this.cartes.size()==2 && this.getScore()==21 ;
	}
	/**
	 * Indique si la main est perdante (score de plus de 21 points)
	 * @returns vrai si le score est supérieur à 21
	 */
	public boolean isPerdante() {
		return this.getScore()>21;
	}
	/**
	 * Méthode interne utilisée pour actualiser le "score" de la main
	 * @returns le score actuel (somme des valeurs des cartes)
	 */
	private int calculerValeur() {
		int total = 0;
		int nbAs  = 0;
		for (Carte carte : this.cartes) {
			if (carte.getValeur()!=0) {
				total += carte.getValeur();
			} else {
				nbAs ++;
			}
		}
		while (nbAs>0) {
			if ( total+nbAs-1 <=10 ) {
				total += 11;
			} else {
				total +=1;
			}
			nbAs--;
		}
		return total;
	}

	@Override
	public String toString() {
		String descr = "cartes :\n";
	    for (Carte carte : this.cartes) {
			descr += carte.getValeur()+ " : " +carte.getNomComptet()+"\n";
		}
		descr += this.getNbCartes()+" cartes\n";
		descr += this.getScore()   +" points\n";
		if (this.isBlackJack()) {
			descr += "xxxx BLACK-JACK xxxx\n";
		} else if ( this.isPerdante() ) {
			descr += "####   PERDU    ####\n";
		}
		return descr;
	}


}
