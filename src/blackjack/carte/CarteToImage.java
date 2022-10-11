package blackjack.carte;

import blackjack.om.Carte;

public class CarteToImage {

    public static String convertir(Carte carte) {
        int noImg = 0;
        if(carte.getHauteur() != 0) {
            noImg = (13-carte.getHauteur()) * 4;
        }
        noImg += convertirCouleur(carte.getCouleur());
        return noImg + ".png";
    }

    private static int convertirCouleur(int i) {
        switch (i) {
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 1;
            case 3:
                return 4;
            default:
                return 0;
        }
    }

}