import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Taquin extends Application {

    @FXML
    TextField taille;

    @FXML
    TextField pourcentage;

    private static Label[][] grid;

    private static List<Agent> agents;

    private static String[][] grille;

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            Taquin.primaryStage = primaryStage;
            primaryStage.setTitle("IA TP1 - Taquin");

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Taquin.class.getResource("Saisie.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void go() {
        try {
            int tailleGrille = Integer.parseInt(taille.getText());
            int pourcentageAgent = Integer.parseInt(pourcentage.getText());

            if (pourcentageAgent > 0 && pourcentageAgent <= 100) {
                primaryStage.hide();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Taquin.class.getResource("Grille.fxml"));
                GridPane rootGrid = loader.load();
                rootGrid.getChildren().clear();
                rootGrid.setGridLinesVisible(true);

                Agent.getGrille().create(tailleGrille, pourcentageAgent);
                grille = Agent.getGrille().get();
                grid = new Label[grille.length][grille[0].length];

                for (int rowIndex = 0; rowIndex < grille.length; rowIndex++) {
                    RowConstraints rc = new RowConstraints();
                    rc.setVgrow(Priority.ALWAYS);
                    rc.setFillHeight(true);
                    rootGrid.getRowConstraints().add(rc);
                }
                for (int colIndex = 0; colIndex < grille[0].length; colIndex++) {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setHgrow(Priority.ALWAYS);
                    cc.setFillWidth(true);
                    cc.setHalignment(HPos.CENTER);
                    rootGrid.getColumnConstraints().add(cc);
                }

                for (int i = 0; i < grille.length; i++) {
                    for (int j = 0; j < grille[0].length; j++) {
                        grid[i][j] = new Label(grille[i][j]);
                        grid[i][j].setFont(new Font(30));
                        grid[i][j].setAlignment(Pos.CENTER);
                        grid[i][j].setPrefSize(50, 50);
                        rootGrid.add(grid[i][j], j, i);
                    }
                }

                Scene scene = new Scene(rootGrid);
                primaryStage.setScene(scene);
                primaryStage.show();

                // Lancer les agents
                agents(grille);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void agents(String[][] grille) {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    agents = new ArrayList<>();
                    for (int i = 0; i < grille.length; i++) {
                        for (int j = 0; j < grille[0].length; j++) {
                            if (!grille[i][j].equals("")) {
                                Agent agent = new Agent(grille[i][j], new Position(i, j), Agent.getGrille().findTerminale(grille[i][j]));
                                agents.add(agent);
                                Agent.communication.put(agent.getNom(), new ArrayList<>());
                            }
                        }
                    }

                    // Run agents
                    Thread.sleep(1000);
                    agents.stream().forEach(agent -> {
                        agent.start();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();

    }

    public static void update(Position prev, Position next, String nom) {
        Platform.runLater(() -> {
            grid[prev.getX()][prev.getY()].setText("");
            grid[next.getX()][next.getY()].setText(nom);
        });
    }

    public static Agent findAgent(String agentDestinataire) {
        for (Agent agent : agents) {
            if (agent.getName().equals(agentDestinataire)) {
                return agent;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static List<Agent> getAgents() {
        return agents;
    }
}
