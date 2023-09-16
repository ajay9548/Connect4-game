
package com.example.connect4;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private  static  final  int COLUMNS = 7;
	private  static  final  int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private  static  final String discColor1 = "#24303E";
	private  static  final String discColor2 = "#4CAA88";

	private  static final String PLAYER_ONE = "PLAYER ONE";
	private  static final String PLAYER_TWO = "PLAYER TWO";
	public Label playerNameLabel;
	public Region playerLabel;


	private  boolean isPlayerOneTurn = true;

	private final Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS]; // for structural change , for developers

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playernameLabel;

	private  boolean isAllowedToInsert = true;

	public void createPlayground(){
		Shape rectangleWidthHoles = createGameStructure();
		rootGridPane.add(rectangleWidthHoles,0,1);
		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle: rectangleList) {
			rootGridPane.add(rectangle,0,1);
		}
	}

	private Shape createGameStructure(){
		Shape rectangleWidthHoles = new Rectangle((COLUMNS +1)* CIRCLE_DIAMETER, (ROWS+1)*CIRCLE_DIAMETER);
		for(int row = 0; row<ROWS ; row++){
			for(int col = 0; col<COLUMNS ; col++){
				Circle circle = new Circle();
				circle.setRadius((double) CIRCLE_DIAMETER / 2);
				circle.setCenterX((double) CIRCLE_DIAMETER / 2);
				circle.setCenterY((double) CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col* (CIRCLE_DIAMETER+5) + (double) CIRCLE_DIAMETER /4);
				circle.setTranslateY(row* (CIRCLE_DIAMETER+5) + (double) CIRCLE_DIAMETER /4);
				rectangleWidthHoles = Shape.subtract(rectangleWidthHoles,circle);
			}
		}
		rectangleWidthHoles.setFill(Color.WHITE);
		return rectangleWidthHoles;
	}

	private List<Rectangle>  createClickableColumns(){
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col =0; col<COLUMNS;col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col* (CIRCLE_DIAMETER+5) + (double) CIRCLE_DIAMETER / 4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert){
					isAllowedToInsert = false;

					insertDisc(new Disc(isPlayerOneTurn),column);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList ;
	}

	private void  insertDisc(Disc disc, int column){
		int row = ROWS-1;
		while (row>=0){
			if(getDiscIfPresent(row,column)==null)
				break;
			row--;
		}
		if(row<0)  // row is full, we can not insert further
			return;
		insertedDiscsArray[row][column]= disc; // for structural changes , for developers
		insertedDiscsPane.getChildren().add(disc);
		disc.setTranslateX(column* (CIRCLE_DIAMETER+5) + (double) CIRCLE_DIAMETER / 4);
		TranslateTransition translateTransition = getTranslateTransition(disc, column, row);
		translateTransition.play();
	}

	private TranslateTransition getTranslateTransition(Disc disc, int column, int row) {
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER+5) + (double) CIRCLE_DIAMETER / 4);
		translateTransition.setOnFinished(event -> {

			isAllowedToInsert = true;
			if(gameEnded(row, column)){
				gameOver();
				return;
			}
			isPlayerOneTurn= !isPlayerOneTurn;
			if (isPlayerOneTurn) {
				playerNameLabel.setText(PLAYER_ONE);
			} else {
				playerNameLabel.setText(PLAYER_TWO);
			}

		});
		return translateTransition;
	}

	private boolean gameEnded(int row,int column){
		//vertical-points
		List<Point2D> verticalPoints= IntStream.rangeClosed(row-3,row+3)
				.mapToObj(r -> new Point2D(r,column))
				.collect(Collectors.toList());
		//horizontal points
		List<Point2D> horizontalPoints= IntStream.rangeClosed(column-3,column+3)
				.mapToObj(c -> new Point2D(row,c))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3,column+3);

		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3,column-3);

		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint2.add(i,i))
				.collect(Collectors.toList());

		return checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain =0;

		for (Point2D point: points) {

			int rowIndexForArray = (int) point.getX();
			int colIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray,colIndexForArray);
			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn) { //if the lst inserted discs belong to first player

				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain =0;
			}
		}
		return false;

	}


	private  Disc getDiscIfPresent(int row, int column){
		if (row>=ROWS || row<0|| column>=COLUMNS || column<0)

			return null;
		else
			return  insertedDiscsArray[row][column];
	}

	private  void gameOver(){
		String winner =isPlayerOneTurn? PLAYER_ONE:PLAYER_TWO;
		System.out.println("winner is:" + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("CONNECT FOUR GAME");
		alert.setHeaderText("THE WINNER IS:" + winner);
		alert.setContentText("Do you want to play again?");

		ButtonType yes = new ButtonType("YES");
		ButtonType no = new ButtonType("NO");
		alert.getButtonTypes().setAll(yes, no);

		Platform.runLater(()-> {

			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.isPresent()&& buttonClicked.get()==yes)
			{
				resetGame();

			} else {
				Platform.exit();
				System.exit(0);
			}

		});

	}

	public void resetGame() {

		insertedDiscsPane.getChildren().clear();
		for (Disc[] discs : insertedDiscsArray) {
			Arrays.fill(discs, null);
		}

		isPlayerOneTurn =true;
		playernameLabel.setText(PLAYER_ONE);
		createPlayground();




	}

	private static class Disc extends Circle{
		private  final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove=isPlayerOneMove;
			setRadius((double) CIRCLE_DIAMETER /2);
			setCenterX((double) CIRCLE_DIAMETER /2);
			setCenterY((double) CIRCLE_DIAMETER /2);
			setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}
