package blackjack.om;

public enum EtatBlackBot {
    MISE, // Init d'une partie : mises en cours
	JEU,  // Jeu en cours      : chaque joueur tire ou Passe
	GAIN  // Fin d'une partie  : Le croupier a tiré, les gains sont calculés
}
