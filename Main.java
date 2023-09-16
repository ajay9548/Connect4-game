package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	private  Controller controller;

	@Override
	public void start(Stage myStage) throws Exception{


		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();

		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar =createMenu();
		menuBar.prefWidthProperty().bind(myStage.widthProperty());
		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);

		myStage.setScene(scene);
		myStage.setTitle("CONNECT FOUR");
		myStage.setResizable(false);
		myStage.show();

	}


	private MenuBar createMenu(){

		Menu fileMenu = new Menu("FILE");

		MenuItem newGame = new MenuItem("NEW GAME");
		newGame.setOnAction(event -> controller.resetGame());

		MenuItem restartGame = new MenuItem("RESTART GAME");
		restartGame.setOnAction(event -> controller.resetGame());

		SeparatorMenuItem smi = new SeparatorMenuItem();

		MenuItem quitGame = new MenuItem("QUIT GAME");
		quitGame.setOnAction(event -> exitGame());

		fileMenu.getItems().addAll(newGame,restartGame,smi,quitGame);

		Menu helpMenu = new Menu("HELP");

		MenuItem howToPlay = new MenuItem("HOW TO PLAY");
		howToPlay.setOnAction(event -> HTPlay());

		SeparatorMenuItem smi2 = new SeparatorMenuItem();

		MenuItem about_us = new MenuItem("About Us");
		about_us.setOnAction(event -> aboutUs());
		helpMenu.getItems().addAll(howToPlay,smi2,about_us);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		return menuBar;

	}

	private void aboutUs() {


		//TODO

		Alert allert = new Alert(Alert.AlertType.INFORMATION);
		allert.setTitle("ABOUT THE DEVELOPER");
		allert.setHeaderText("Ajay Negi");
		allert.setContentText("this Game is created by  Ajay Negi ,it is my first ever project in java .i love to developed awesome cool game for family and friends ");
		allert.show();

	}

	private void HTPlay() {

		//TODO

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("CONNECT FOUR GAME");
		alert.setContentText("Connect Four is a two-player connection game " +
				"in which the players first choose a color and then take turns dropping " +
				"one colored disc from the top into a seven-column, six-row vertically " +
				"suspended grid. The pieces fall straight down, occupying the next " +
				"available space within the column. The objective of the game is " +
				"to be the first to form a horizontal, vertical, or diagonal " +
				"line of four of one's own discs.The first player" +
				" can always win by playing the right moves. ");
		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);

	}


	public static void main(String[] args) {
		launch(args);
	}
}

