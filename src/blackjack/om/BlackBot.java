package blackjack.om;

/**
 * Robot de jeu de Blackjack
 * 
 * D�roulement d'une partie :
 *  - en �tat "MISE"
 *    -- chaque "joueur" peut d�poser sa mise
 *    -- un joueur sans mise ne participera pas � la prochaine partie
 *    -- il faut au moins une mise pour pouvoir lancer la partie
 *    -- la m�thode "distribuer()" termine la phase "MISE" et passe en �tat "JEU"
 *  - en �tat "JEU"
 *    -- chaque joueur peut "tirer(j)" ou "terminer(j)" tant qu'il n'a pas perdu ou d�j� termin�
 *    -- tirer(j) ajoute une carte dans la main d'un joueur (et peut �ventuellement le faire perdre)
 *    -- terminer(j) indique qu'il ne prendra plus de carte
 *    -- quand tous les joueurs ont perdu ou termin� :
 *         --- le croupier tire ses cartes et calcule les gains de chacun
 *         --- les gains sont plac�s dans miseJoueurs[j] (à la place des mises)
 *         --- le jeu passe alors en �tat "GAIN"
 * - en �tat "GAIN"
 *    -- on peut encore consulter les mains et les gains
 *    -- la seule action disponible est "relancerPartie()"
 *    -- relancerPartie() va vider les mains et les gains et basculer dans l'�tat "MISE"
 * 
 * 
 * Attributs utilisables sur un objet BlackBot :
 * 
 * sabot       : le sabot des cartes (mais dans une partie normale on n'a pas à y toucher)
 * etat        : l'�tat de la partie MISE, JEU ou GAIN
 * mainBanque  : la main du croupier
 * mainJoueurs : tableau des mains des joueurs (même ceux qui ne participent pas à un tour)
 * miseJoueurs : la mise initiale (en etat MISE et JEU)
 * gainJoueurs : le gain (en etat GAIN)
 * finJoueurs  : indique si un joueur peut encore tirer ou non (false si il a termin�, perdu ou n'a pas jou�)
 */
public class BlackBot {

	private Sabot           sabot;
	private EtatBlackBot    etat;
	private MainBlackjack   mainBanque;
	private MainBlackjack[] mainJoueurs;
	private int[]           miseJoueurs;
	private int[]           gainJoueurs;
	private boolean[]       finJoueurs;

	/**
	 * Construction d'une table de blackJack pour nbJoueurs participants.
	 */
	public BlackBot( int nbJoueurs ) {
		this.sabot = new Sabot(6);
		this.etat  = EtatBlackBot.MISE;
		this.mainBanque  = new MainBlackjack();
		this.mainJoueurs = new MainBlackjack[nbJoueurs];
		for (int i = 0; i < mainJoueurs.length; i++) {
			mainJoueurs[i] = new MainBlackjack();
		}
		this.miseJoueurs = new int[nbJoueurs];
		this.gainJoueurs = new int[nbJoueurs];
		this.finJoueurs  = new boolean[nbJoueurs];
	}
	/**
	 * Relancer une partie
	 * Utilisable uniquement en �tat GAIN
	 * 
	 */
	public void relancerPartie() {
		if (this.etat==EtatBlackBot.GAIN) {
			this.etat = EtatBlackBot.MISE;
			this.mainBanque.viderMain();
			for (int i = 0; i < mainJoueurs.length; i++) {	
				this.mainJoueurs[i].viderMain();
				this.miseJoueurs[i] = 0;
				this.gainJoueurs[i] = 0;
				this.finJoueurs[i]  = false;
			}
		} else {
			throw new BlackjackException("[BlackBot] : Impossible d'encaisser et de relancer une partie en dehors de l'�tat GAIN");
		}
	}
	/**
	 * Enregistrer la mise d'un joueur
	 * Utilisable uniquement en �tat MISE
	 * 
	 * @param   joueur   num�ro du joueur [0..nbJoueur-1]
	 * @param   montant  mise totale pour ce joueur
	 */
	public void miser(int joueur, int montant) {
		if ( this.etat==EtatBlackBot.MISE ) {
			this.miseJoueurs[joueur] = montant;
		} else {
			throw new BlackjackException("[BlackBot] : Mise impossible en dehors de l'�tat MISE");
		}
	}
	/**
	 * Tirer une nouvelle carte pour un joueur
	 * Utilisable uniquement en �tat MISE si le joueur n'a pas d�j� "perdu" ou "termin�"
	 * 
	 * @param   joueur   num�ro du joueur [0..nbJoueur-1]
	 */
	public void tirer(int joueur) {
		if (this.etat==EtatBlackBot.JEU) {
			if (this.finJoueurs[joueur]==false) {
				this.mainJoueurs[joueur].prendreCarte( this.sabot.tirage() );
				if (this.mainJoueurs[joueur].isPerdante()) {
					// si un joueur perd en tirant on �value la fin de partie
					this.finJoueurs[joueur] = true;
					this.checkFinPartie();
				}
			} else {
				throw new BlackjackException("[BlackBot] : Tirage impossible quand le joueur a termin� de jouer");
			}
		} else {
			throw new BlackjackException("[BlackBot] : Tirage impossible en dehors de l'�tat JEU");
		}
	}
	/**
	 * Terminer le tour d'un joueur
	 * Utilisable uniquement en �tat MISE si le joueur n'a pas d�jà "perdu" ou "termin�"
	 * 
	 * @param   joueur   num�ro du joueur [0..nbJoueur-1]
	 */
	public void terminer(int joueur) {
		if ( this.etat==EtatBlackBot.JEU ) {
			if (this.finJoueurs[joueur]==false) {
				// quand un joueur termine de jouer on �value la fin de partie
				this.finJoueurs[joueur] = true;
				this.checkFinPartie();
			} else {
				throw new BlackjackException("[BlackBot] : Impossible de terminer sa main quand elle est d�jà termin�e");
			}
		} else {
			throw new BlackjackException("[BlackBot] : Impossible de terminer sa main en dehors de l'�tat JEU");
		}
	}
	/**
	 * Lancer une nouvelle partie et distribuer les premières cartes
	 * Utilisable uniquement en etat MISE si au moins un joueur a mis�
	 * 
	 */
	public void distribuer() {
		// Il faut être en �tat MISE
		if (this.etat!=EtatBlackBot.MISE) {
			throw new BlackjackException("[BlackBot] : Distribution impossible en dehors de l'�tat MISE");
		}
		// On distribue :
		//  - Une carte � chaque joueur ayant mis� (Exception si aucune mise)
		//  - Puis une carte au croupier
		//  - Puis une seconde carte � chaque joueur
		this.mainBanque.viderMain();
		int nbMises = 0;
		for (int i = 0; i < miseJoueurs.length; i++) {
			if (miseJoueurs[i]>0) {
				nbMises++;
				this.finJoueurs[i] = false;
				this.mainJoueurs[i].viderMain();
				this.mainJoueurs[i].prendreCarte( this.sabot.tirage() );
			} else {
				this.finJoueurs[i] = true;
			}
		}
		if (nbMises==0) {
			throw new BlackjackException("[BlackBot] : Distribution impossible si aucun joueur n'a mis�");
		}
		this.mainBanque.prendreCarte( this.sabot.tirage() );
		for (int i = 0; i < miseJoueurs.length; i++) {
			if (miseJoueurs[i]>0) {
				this.mainJoueurs[i].prendreCarte( this.sabot.tirage() );
			}
		}
		// Le bot passe alors en �tat  JEU
		this.etat = EtatBlackBot.JEU;
	}
	/**
	 * M�thode interne (lanc�e automatiquement après chaque tirage ou demande de fin de tour)
	 * Elle v�rifie si il reste des joueurs "actifs" (ni perdu ni termin�)
	 * Si il n'y a plus de joueurs actifs :
	 *   -- le croupier tire ses cartes
	 *   -- il calcul les gains (en prend les mises perdues)
	 *   -- le jeu bascule en etat "GAIN"
	 */
	private void checkFinPartie() {
		// la fin de partie est lanc�e ssi :
		// tous les joueurs ayant une mise ont perdu ou sont en mode "fin"
		int nbJoueursActifs = 0;
		for (int i = 0; i < miseJoueurs.length; i++) {
			if (miseJoueurs[i]>0) {
				if ( ! this.finJoueurs[i] ) {
					if (this.mainJoueurs[i].isPerdante()) {
						this.finJoueurs[i] = true;
					} else {
						nbJoueursActifs++;
					}
				}
			}
		}
		if (nbJoueursActifs==0) {
			// fin de partie : le croupier fait son tirage et calcule les gains.
			this.etat = EtatBlackBot.GAIN;
			// on tire à 16 passe à 17
			while (this.mainBanque.getScore()<=16) {
				this.mainBanque.prendreCarte(this.sabot.tirage());
			}            
			// on calcule les gains... 
			for (int i = 0; i < miseJoueurs.length; i++) {
				if (miseJoueurs[i]>0) {
					if (this.mainJoueurs[i].isPerdante()) {
						// main perdante : on perd la mise
						this.gainJoueurs[i] = 0;
					} else if (this.mainJoueurs[i].isBlackJack()) {
						if ( ! this.mainBanque.isBlackJack() ) {
							// gain par BlackJack : payement 3 pour 2
							this.gainJoueurs[i] = (int)(this.miseJoueurs[i] * 2.5);
						} else {
							// �galit� de blackJack :-(  : pas de gain / pas de perte
							this.gainJoueurs[i] = this.miseJoueurs[i];
						}
					} else if (this.mainBanque.isPerdante()) {
						// main croupier perdante : on double la mise
						this.gainJoueurs[i] = this.miseJoueurs[i] * 2;
					} else if ( this.mainJoueurs[i].getScore() > this.mainBanque.getScore() ) {
						// main sup�rieure : on double la mise
						this.gainJoueurs[i] = this.miseJoueurs[i] * 2;
					} else if ( this.mainJoueurs[i].getScore() < this.mainBanque.getScore() ) {
						// main inf�rieure : on perd la mise
						this.gainJoueurs[i] = 0;
					} else {
						// main �gale : pas de gain / pas de perte
						this.gainJoueurs[i] = this.miseJoueurs[i];
					}
					this.miseJoueurs[i] = 0;
				}
			}
		}
	}

	// ACCESSEURS donn�es blackbot
	public Sabot getSabot() {
		return sabot;
	}
	public EtatBlackBot getEtat() {
		return etat;
	}
	public MainBlackjack getMainBanque() {
		return mainBanque;
	}
	public MainBlackjack getMainJoueurs(int joueur) {
		return mainJoueurs[joueur];
	}
	public int getMiseJoueurs(int joueur) {
		return miseJoueurs[joueur];
	}
	public int getGainJoueurs(int joueur) {
		return gainJoueurs[joueur];
	}
	public boolean getFinJoueurs(int joueur) {
		return finJoueurs[joueur];
	}
	
	
}
