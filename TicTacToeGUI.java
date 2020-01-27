//Ming-Xia Delvas 2019/04/11 Exercice 7 TicTacToe

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TicTacToeGUI extends Application {

	private Text status;
	private char joueur;

	public static void main(String[] args) {
		TicTacToeGUI.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Ouverture du Socket qui se connecte au serveur de TicTacToe
		Socket clientSocket = new Socket("127.0.0.1", 1337);

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		/**
		 * Notez qu'on peut envoyer des messages au serveur en utilisant :
		 * 
		 * writer.append("mon message" + "\n"); writer.flush();
		 */

		// CrÃ©ation de l'interface graphique
		VBox root = new VBox();
		Scene scene = new Scene(root, 300, 300);

		/**
		 * TODO : Ajouter un GridPane qui contient des boutons
		 *
		 * Lorsqu'on clique sur un des boutons, cela doit envoyer la coordonnÃ©e du
		 * bouton au serveur dans le format :
		 *
		 * colonne:ligne
		 */
		GridPane gridPane = new GridPane();
		/*
		 * Créer les boutons dans le gridPane et envoie au serveur les coordonnées
		 * lorsqu'on clique sur un des boutons
		 */
		for (int i = 1; i < 4; i++) {
			for (int j = 1; j < 4; j++) {
				Button button = new Button();
				button.setPrefSize(100, 100);
				gridPane.add(button, i, j);

				button.setOnAction((event) -> {
					int ligne = gridPane.getRowIndex(button);
					int colonne = gridPane.getColumnIndex(button);
					try {

						writer.append(colonne + ":" + ligne + "\n");
						writer.flush();
					} catch (IOException e) {
						e.getStackTrace();
					}
				});
			}
		}
		root.getChildren().add(gridPane);

		// Ajout de la barre de status
		status = new Text();
		root.getChildren().add(status);

		/**
		 * CrÃ©e un thread qui Ã©coute les messages envoyÃ©s par le serveur et qui met
		 * Ã  jour l'interface graphique en consÃ©quence
		 */

		Thread listener = new Thread(() -> {
			try {
				String line;
				
				while ((line = reader.readLine()) != null) {

					/**
					 * TODO : interprÃ©ter le message envoyÃ© par le serveur et agir en consÃ©quence
					 */
					switch (line) {
					case "start o":
						setJoueur('o');
						setStatus("Les X commencent");
						break;
					case "start x":
						setJoueur('x');
						setStatus("Commencez!");
						break;
					case "oWins":
						if (getJoueur() == line.charAt(0)) {
							setStatus("Vous avez gagné!");
						} else {
							setStatus("Vous avez perdu...");
						}
						break;
					case "xWins":
						if (getJoueur() == line.charAt(0)) {
							setStatus("Vous avez gagné!");
						} else {
							setStatus("Vous avez perdu...");
						}
						break;
					case "tie":
						setStatus("Match nul");
						break;
					default:
						//Mets dans un tableau les coordonnées x et y de la ligne 
						String[] coordonnees = line.substring(7).split(":");
						//Character représent soit un x ou o dépendamment du tour 
						String character = line.substring(5, 7);

						int x = Integer.parseInt(coordonnees[0]);
						int y = Integer.parseInt(coordonnees[1]);
						setStatus("");

						for (Node node : gridPane.getChildren()) {
							if (gridPane.getRowIndex(node) == y && gridPane.getColumnIndex(node) == x) {
								Platform.runLater(() -> {
									((Button)node).setText(character);
								});
							}
						}
						break;
					}
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		listener.start();

		primaryStage.setScene(scene);
		primaryStage.setTitle("Tic Tac Toe");
		// Fermer le programme (tous les threads) lorsqu'on ferme la fenÃªtre
		primaryStage.setOnCloseRequest((event) -> {
			Platform.exit();
			System.exit(0);
		});
		primaryStage.show();
	}

	public char getJoueur() {
		return joueur;
	}

	public void setJoueur(char joueur) {
		this.joueur = joueur;
	}

	private void setStatus(String str) {
		/**
		 * Important : toutes les modifications de l'interface graphique **doivent** se
		 * faire sur le Thread d'application de JavaFX
		 *
		 * Si un autre thread souhaite modifier l'interface, on doit passer par la
		 * mÃ©thode Platform.runLater(runnable);
		 */

		Platform.runLater(() -> {
			status.setText(str);
		});
	}
}
