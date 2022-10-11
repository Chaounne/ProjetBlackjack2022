package blackjack;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import blackjack.carte.CarteToImage;
import blackjack.om.BlackBot;
import blackjack.om.Carte;
import blackjack.om.EtatBlackBot;

public class BlackjackController implements Initializable{

	private Stage fenetrePrincipale;
	
	private BlackBot croupier = new BlackBot(2);
	
	int valArgentJ1;
	int valArgentJ2;
	int valMiseJ1 = 0;
	int valMiseJ2 = 0;
	int tour;
	boolean j1Mise = false;
	boolean j2Mise = false;
	boolean distrib = false;
	int valMainJ1 = 0;
	int valMainJ2 = 0;
	int valMainCroup = 0;
	
	public int getTour() {
		return tour;
	}


	public void setTour(int tour) {
		this.tour = tour;
	}

	@FXML
	Button butStart;
	
	@FXML
	Button butMiseJ1;
	
	@FXML
	Button butMiseJ2;
	
	@FXML
	Button butTireJ1;
	
	@FXML
	Button butTireJ2;
	
	@FXML 
	Button butPassJ1;
	
	@FXML
	Button butPassJ2;
	
	@FXML
	Label miseJ1;
	
	@FXML
	Label miseJ2;
	
	@FXML
	Label valeurJ1;
	
	@FXML
	Label valeurJ2;
	
	@FXML
	Label valeurCroupier;
	
	@FXML
	Label argentJ1;
	
	@FXML
	Label argentJ2;
	
	@FXML
	HBox carteJ1;
	
	@FXML
	HBox carteJ2;
	
	@FXML
	HBox carteCroup;
	
	@FXML
	Button butEndJ1;
	
	@FXML
	Button butEndJ2;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}

	@FXML	
	public void actionEnd() {
		if(this.croupier.getEtat() == EtatBlackBot.JEU) {
			if(this.tour == 1 && !this.croupier.getFinJoueurs(0)) {
				this.croupier.terminer(0);
				this.butEndJ1.setDisable(true);
			}
			if(this.tour == 2 && !this.croupier.getFinJoueurs(1)) {
				this.croupier.terminer(1);
				this.butEndJ2.setDisable(true);
			}
			
		}
		if(this.croupier.getMainJoueurs(0).getScore()>21 || this.croupier.getEtat() == EtatBlackBot.GAIN) {
			this.butTireJ1.setDisable(true);
		}
		if(this.croupier.getMainJoueurs(1).getScore()>21 || this.croupier.getEtat() == EtatBlackBot.GAIN) {
			this.butTireJ2.setDisable(true);
		}	
		
		this.carteCroup.getChildren().clear();
		for(int i= 0; i < this.croupier.getMainBanque().getNbCartes(); i = i + 1) {
			this.ajouterCarte(this.getCarteImage(this.croupier.getMainBanque().getCarte(i)), 3);
		}
		this.updateValMains();
		
		if(this.croupier.getFinJoueurs(0) || this.tour != 1) {
			this.butEndJ1.setDisable(true);
		}
		if(this.croupier.getFinJoueurs(1) || this.tour != 2) {
			this.butEndJ2.setDisable(true);
		}
		
		if(this.croupier.getFinJoueurs(1) && this.croupier.getFinJoueurs(0)) {
			this.butEndJ1.setDisable(true);
			this.butEndJ2.setDisable(true);
			this.finPartie();
		}
		this.actionTerminer();
		
	}
	
	private void finPartie() {
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle("Fin de partie");
		this.updateValMains();
		dialog.setHeaderText("Félicitation !");
		dialog.setContentText("");
		
		if(this.croupier.getGainJoueurs(0)>0) {
			this.valArgentJ1 = this.valArgentJ1 + this.croupier.getGainJoueurs(0);
			dialog.setContentText(dialog.getContentText() + "\nBravo Joueur 1 ! Vous avez gagné " + this.croupier.getGainJoueurs(0) + " jetons !");
			this.argentJ1.setText("Argent : " + String.valueOf(this.valArgentJ1));
			
		}
		if(this.croupier.getGainJoueurs(1)>0) {
			this.valArgentJ2 = this.valArgentJ2 + this.croupier.getGainJoueurs(1);
			dialog.setContentText(dialog.getContentText() + "\nBravo Joueur 2 ! Vous avez gagné " + this.croupier.getGainJoueurs(1) + " jetons !");
			this.argentJ2.setText("Argent : " + String.valueOf(this.valArgentJ2));
		}
		if(this.croupier.getGainJoueurs(1)<= 0 && this.croupier.getGainJoueurs(0) <= 0) {
			dialog.setContentText("Dommage personne n'a gagné... Recommencez pour voir !");	
		}
		dialog.showAndWait();
		this.butStart.setText("Recommencer");
		this.butStart.setDisable(false);
	}
	
	@FXML
	public void fenetreMiser() {
		TextInputDialog dialog = new TextInputDialog("0");
		dialog.setTitle("Mise");
		dialog.setHeaderText("Combien voulez-vous miser ?");
		dialog.setContentText("Mise : ");
		Optional<String> result = dialog.showAndWait();
		
		if(this.tour == 1) {
			result.ifPresent(val -> {
				try {
					this.valMiseJ1 = Integer.valueOf(val);
				} catch (Exception e) {
					this.valMiseJ1 = 0;
				}
			});
			this.valArgentJ1  = this.valArgentJ1 - this.valMiseJ1;
			if(this.valArgentJ1<0 || this.valMiseJ1 <= 0) {
				Alert attention = new Alert(Alert.AlertType.ERROR);
				attention.setTitle("Erreur");
				attention.setHeaderText("Erreur dans la mise.");
				attention.setContentText("Veuillez entrer une mise correcte.");
				attention.showAndWait();
				this.valArgentJ1 += this.valMiseJ1;
			}
			else {
				this.argentJ1.setText("Argent : " + String.valueOf(valArgentJ1));
				this.miseJ1.setText("Mise : " + String.valueOf(this.valMiseJ1));
				this.croupier.miser(0, this.valMiseJ1);
				this.j1Mise = true;
				this.butMiseJ1.setDisable(true);
				this.butPassJ1.setDisable(false);
				this.valMiseJ1 = 0;
			}
		}
		if(this.tour == 2) {
			result.ifPresent(val -> {
				try {
					this.valMiseJ2 = Integer.valueOf(val);
				} catch(Exception e ) {
					this.valMiseJ2 = 0;
				}
			});
				this.valArgentJ2 = valArgentJ2 - this.valMiseJ2;
				
				if(this.valArgentJ2<0 || this.valMiseJ2 <= 0) {
					Alert attention = new Alert(Alert.AlertType.ERROR);
					attention.setTitle("Erreur");
					attention.setHeaderText("Erreur dans la mise.");
					attention.setContentText("Veuillez entrer une mise correcte.");
					attention.showAndWait();
					this.valArgentJ2 += this.valMiseJ2;
				}
				else {
					this.argentJ2.setText("Argent : " + String.valueOf(valArgentJ2));
					this.miseJ2.setText("Mise : " + String.valueOf(this.valMiseJ2));
					this.croupier.miser(1, this.valMiseJ2);
					this.j2Mise = true;
					this.butMiseJ2.setDisable(true);
					this.butPassJ2.setDisable(false);
					this.valMiseJ2 = 0;
				}
		}
	}
	
	
	@FXML
	public void actionCommencer() {
		if(this.tour == 1 || this.tour == 2) {
			this.j1Mise = false;
			this.j2Mise = false;
			this.distrib = false;
			this.miseJ1.setText("Mise : 0");
			this.miseJ2.setText("Mise : 0");
			this.carteJ1.getChildren().clear();
			this.carteJ2.getChildren().clear();
			this.carteCroup.getChildren().clear();
			this.croupier.relancerPartie();
			this.updateValMains();
			this.butEndJ1.setDisable(true);
			this.butEndJ2.setDisable(true);
		}
		else {
			//argent joueur 1
			TextInputDialog dialog = new TextInputDialog("10");
			dialog.setTitle("Argent joueur 1");
			dialog.setHeaderText("Combien d'argent voulez-vous utiliser Joueur 1 ?");
			dialog.setContentText("Argent : ");
			Optional<String> result = dialog.showAndWait();
			
				result.ifPresent(val -> {
					try {
						this.valArgentJ1 = Integer.valueOf(val);
					} catch (Exception e) {
						this.valArgentJ1 = 0;
					}
				});
				if(this.valArgentJ1<=0) {
					Alert attention = new Alert(Alert.AlertType.ERROR);
					attention.setTitle("Erreur");
					attention.setHeaderText("Erreur dans la somme d'argent.");
					attention.setContentText("Veuillez entrer une somme d'argent correcte.");
					attention.showAndWait();
				}
				else {
					this.argentJ1.setText("Argent : " + String.valueOf(valArgentJ1));
				}		
				
			//argent joueur 2
			TextInputDialog dialogJ2 = new TextInputDialog("10");
			dialogJ2.setTitle("Argent joueur 2");
			dialogJ2.setHeaderText("Combien d'argent voulez-vous utiliser Joueur 2 ?");
			dialogJ2.setContentText("Argent : ");
			Optional<String> resultJ2 = dialogJ2.showAndWait();
				
					resultJ2.ifPresent(val -> {
						try {
							this.valArgentJ2 = Integer.valueOf(val);
						} catch (Exception e) {
							this.valArgentJ2 = 0;
						}
					});
					if(this.valArgentJ2<=0) {
						Alert attention = new Alert(Alert.AlertType.ERROR);
						attention.setTitle("Erreur");
						attention.setHeaderText("Erreur dans la somme d'argent.");
						attention.setContentText("Veuillez entrer une somme d'argent correcte.");
						attention.showAndWait();
					}
					else {
						this.argentJ2.setText("Argent : " + String.valueOf(valArgentJ2));
					}
		}
		this.tour = 1;
		this.butMiseJ1.setDisable(false);
		this.butStart.setDisable(true);
		
	}
	
	@FXML
	public void actionTerminer() {
		if(this.tour == 1) {
			this.tour = 2;
			if(!this.j2Mise) {
				this.butMiseJ2.setDisable(false);
				this.butTireJ2.setDisable(true);
			}
			else {
				this.butMiseJ2.setDisable(true);
				if(this.valMainJ2 <= 21) {
					this.butTireJ2.setDisable(false);
				}
				this.butEndJ2.setDisable(false);
			}
			if(this.j1Mise) {
				this.butMiseJ1.setDisable(true);
			}
			
			if(!this.butPassJ1.isDisable()) {
				this.butPassJ1.setDisable(true);
			}
			this.butTireJ1.setDisable(true);
			this.butEndJ1.setDisable(true);
		}
		else {
			this.tour = 1;
			if(!this.j1Mise) {
				this.butMiseJ1.setDisable(false);
				this.butTireJ1.setDisable(false);
			}
			else {
				this.butMiseJ1.setDisable(true);
				if(this.valMainJ1<=21) {
					this.butTireJ1.setDisable(false);
				}
				if (!this.croupier.getFinJoueurs(0)) {
					this.butEndJ1.setDisable(false);
				}
			}
			if(this.j2Mise) {
				this.butMiseJ2.setDisable(true);
			}
			if(!this.butPassJ2.isDisable()) {
				this.butPassJ2.setDisable(true);
			}	
			if(this.croupier.getFinJoueurs(0)) {
				this.butTireJ1.setDisable(true);
			}
			else {
				this.butTireJ1.setDisable(false);
			}
			this.butTireJ2.setDisable(true);
			this.butEndJ2.setDisable(true);
		}
		if(this.j1Mise && this.j2Mise && !this.distrib) {
			this.distrib = true;
			this.butPassJ1.setDisable(true);
			this.butPassJ2.setDisable(true);
			this.croupier.distribuer();
			for(int i = 0; i < this.croupier.getMainJoueurs(0).getCartes().size();i = i + 1) {
				ajouterCarte(this.getCarteImage(this.croupier.getMainJoueurs(0).getCartes().get(i)),1);				
			}
			for(int i = 0; i < this.croupier.getMainJoueurs(1).getCartes().size();i = i + 1) {
				ajouterCarte(this.getCarteImage(this.croupier.getMainJoueurs(1).getCartes().get(i)),2);
			}
			for(int i = 0; i < this.croupier.getMainBanque().getNbCartes(); i = i +1 ) {
				ajouterCarte(this.getCarteImage(this.croupier.getMainBanque().getCarte(i)),3);
			}
			this.updateValMains();
		}
	}
	
	@FXML
	public void actionTirer() {
		if(this.tour==1 && this.croupier.getMainJoueurs(0).getScore() <=21 && this.croupier.getEtat() == EtatBlackBot.JEU) {
			this.croupier.tirer(0);
			ImageView c = this.getCarteImage(this.croupier.getMainJoueurs(0).getCartes().get(this.croupier.getMainJoueurs(0).getCartes().size()-1));
			ajouterCarte(c, 1);
			this.valMainJ1 += this.croupier.getMainJoueurs(0).getCartes().get(this.croupier.getMainJoueurs(0).getCartes().size()-1).getValeur();
			this.updateValMains();
		}
		if(this.tour==2 && this.croupier.getMainJoueurs(1).getScore() <= 21 && this.croupier.getEtat() == EtatBlackBot.JEU) {
			this.croupier.tirer(1);
			ImageView c = this.getCarteImage(this.croupier.getMainJoueurs(1).getCartes().get(this.croupier.getMainJoueurs(1).getCartes().size()-1));
			ajouterCarte(c, 2);
			this.valMainJ2 += this.croupier.getMainJoueurs(1).getCartes().get(this.croupier.getMainJoueurs(1).getCartes().size()-1).getValeur();
			this.updateValMains();
		}
		if(this.croupier.getMainJoueurs(0).getScore()>21) {
			this.butTireJ1	.setDisable(true);
		}
		if(this.croupier.getMainJoueurs(1).getScore()>21) {
			this.butTireJ2.setDisable(true);
		}
		
	}
	
	private ImageView getCarteImage(Carte carte) {
		String nomCarte = CarteToImage.convertir(carte);
		String ficURL = Blackjack.class.getResource("carte/images/" + nomCarte).toString();
		ImageView c = new ImageView(ficURL);
		StackPane.setMargin(c, new Insets(0, 47, 0, 0));
		return c;
	}
	
	private void ajouterCarte(ImageView c, int joueur) {
		if(joueur == 1) {
			this.carteJ1.getChildren().add(c);
			this.carteJ1.setMargin(c, new Insets(0,-60,0,0));
		}
		if(joueur == 2) {
			this.carteJ2.getChildren().add(c);	
			this.carteJ2.setMargin(c, new Insets(0,-60,0,0));
		}
		if(joueur == 3) {
			this.carteCroup.getChildren().add(c);	
			this.carteCroup.setMargin(c, new Insets(0,-60,0,0));
		}
	}
	
	private void updateValMains() {
		this.valMainJ1 = this.croupier.getMainJoueurs(0).getScore();
		this.valMainJ2 = this.croupier.getMainJoueurs(1).getScore();
		this.valMainCroup = this.croupier.getMainBanque().getScore();
		this.valeurJ1.setText("Valeur main : " + String.valueOf(this.valMainJ1));
		this.valeurJ2.setText("Valeur main : " + String.valueOf(this.valMainJ2));
		this.valeurCroupier.setText("Valeur main : " + String.valueOf(this.valMainCroup));
	}
	
	@FXML
	public void actionQuitter(Event e) {
		if(this.butStart.isDisable()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Fermeture de l'application");
			alert.setHeaderText("Une partie est en cours.");
			alert.setContentText("Voulez-vous réellement quitter ?");

			ButtonType yes = new ButtonType("Oui");
			ButtonType no = new ButtonType("Non");

			alert.getButtonTypes().clear();
			alert.getButtonTypes().addAll(yes, no);

			Optional<ButtonType> option = alert.showAndWait();

			if (option.get() == yes) {
				this.fenetrePrincipale.close();
			}
			else {
				e.consume();
			}
		}
		
	}
	
	public void setFenetrePrincipale(Stage fenetre) {
		this.fenetrePrincipale = fenetre;
		this.fenetrePrincipale.setOnCloseRequest(event -> actionQuitter(event));
	}

}
