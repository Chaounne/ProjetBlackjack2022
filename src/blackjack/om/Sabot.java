package blackjack.om;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Un sabot contient des jeux de 52 cartes mélangés
 * Il peut les tirer une par une et se réinitialise automatiquement si il est vite
 */
public class Sabot {

	private List<Carte> cartesSabot;
	private List<Carte> cartesTirees;

	/**
	 * Création d'un sabot de 6 jeux de 52 cartes mélangées
	 * @param nbJeux nombre de jeux complets dans le sabot
	 */
	public Sabot() {
		this(6);
	}
	/**
	 * Création d'un sabot de "nbJeux" jeux de 52 cartes mélangées
	 * @param nbJeux nombre de jeux complets dans le sabot
	 */
	public Sabot(int nbJeux) {
		this.cartesSabot  = new LinkedList<>();
		this.cartesTirees = new LinkedList<>();
		// creation d'un sabot contenant nbJeux complets
		for (int iJeux = 0; iJeux < nbJeux; iJeux++) {
			for (int iCoul = 0; iCoul < 4; iCoul++) {
				for (int iVal = 0; iVal < 13; iVal++) {
					this.cartesSabot.add( new Carte(iCoul,iVal) );
				}
			}
		}
		this.raz();
	}
	/**
	 * Remettre toutes les cartes tirées dans le sabot et mélanger le Sabot
	 */
	public void raz() {
		this.cartesTirees.forEach( carte -> this.cartesSabot.add(carte) );
		this.cartesTirees.clear();
		Collections.shuffle(this.cartesSabot);
	}
	/**
	 *  Tirage d'une carte (elle passe du sabot à l'historique des cartes tirée)
	 *  @returns la carte que l'on vient de retirer du sabot
	 */
	public Carte tirage () {
		if (this.cartesSabot.size()==0) {
			this.raz();
		}
		Carte carte = this.cartesSabot.remove(0);
		this.cartesTirees.add(0, carte);
		return carte;

	}
	/**
	 * redonne la dernière carte tirée
	 * @returns la carte
	 */
	public Carte dernierTirage() {
		return this.cartesTirees.get(0);
	}
	/**
	 * Nombre de cartes restant a tirer dans le sabot
	 * @returns le nombre de carte dans le sabot
	 */
	public int nbCartesRestantes() {
		return this.cartesSabot.size();
	}


}
