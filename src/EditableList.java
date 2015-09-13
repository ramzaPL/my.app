

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class EditableList  extends Application{

	private Stage PRIMARY_STAGE;
	private Scene SCENE;
	
	private ScrollPane scrollPane;
	private VBox vbox;
	
	/*Control buttons*/
	private Button exit;
	private Button minimize;
	
	/*media files names*/
	//private ArrayList<String> playList;
	private List<File> playList;  //MOJE
	private VBox mediaFileList;
	
	/*Point where list window was, and where mouse clicked*/
	private Point2D previousLocation, anchor;
	
	
	public EditableList(List<File> playList, Stage stage) throws Exception {
		
		this.playList = playList;
		start(stage);
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		PRIMARY_STAGE = primaryStage;
		PRIMARY_STAGE.setResizable(false);
		PRIMARY_STAGE.initStyle(StageStyle.TRANSPARENT);
		
		vbox = new VBox(2);
		
		vbox.getChildren().add(createControlButtons());
		
		scrollPane = new ScrollPane();
		scrollPane.setPrefSize(300, 482);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		mediaFileList = new VBox(0);
		
		initPlayList();
		
		vbox.getChildren().add(scrollPane);
		VBox.setMargin(scrollPane, new Insets(0));
		
		SCENE = new Scene(vbox, 300, 500);
		SCENE.getStylesheets().add("my/res/stylesheets/list.css");
		
		initMovable();
		
		PRIMARY_STAGE.setScene(SCENE);
		PRIMARY_STAGE.show();
		
	}

	private void initPlayList() {
		
		Label[] mediaFileName = new Label[playList.size()];
		String prefix;
		
		for(int i = 0; i < mediaFileName.length; i++) {
			prefix = "  " + (i+1) + ". ";
			mediaFileName[i] = new Label(prefix + playList.get(i));
			mediaFileName[i].getStyleClass().add("media-name");
			mediaFileName[i].setWrapText(false);
		}
		mediaFileName[0].setStyle("-fx-border-width: 2 0 2 0");
		
		mediaFileList.getChildren().addAll(mediaFileName);
		scrollPane.setContent(mediaFileList);
	}

	private TilePane createControlButtons() {
		
		exit = new Button("X");
		exit.getStyleClass().add("control-buttons");
		exit.setPadding(new Insets(1));
		/*Adding behavior on different mouse actions*/
		exit.setOnMouseEntered(e -> {
			exit.setStyle("-fx-background-color: rgba(230, 0, 0, 130)");
		});
		
		exit.setOnMouseExited(e -> {
			exit.setStyle("fx-background-fills: transparent");
		});
		
		exit.setOnMouseClicked(e -> {
			PRIMARY_STAGE.hide();
		});
		
		minimize = new Button("---");
		minimize.getStyleClass().add("control-buttons");
		minimize.setPadding(new Insets(1));
		minimize.setOnMouseEntered(e -> {
			minimize.setStyle("-fx-background-color: rgba(0, 150, 220, 130)");
		});
		
		minimize .setOnMouseExited(e -> {
			minimize.setStyle("fx-background-fills: transparent");
		});
		
		minimize.setOnMouseClicked(e -> {
			PRIMARY_STAGE.setIconified(true);
		});
		
		TilePane controlButtonsTile = new TilePane();
		controlButtonsTile.setAlignment(Pos.TOP_RIGHT);
		controlButtonsTile.setHgap(0);
		controlButtonsTile.getChildren().addAll(minimize, exit);
		
		return controlButtonsTile;
	}

	private void initMovable() {
		
		/*Point where top left corner of stage was when showed up*/
		PRIMARY_STAGE.setOnShown(event -> {
			previousLocation = new Point2D(PRIMARY_STAGE.getX(), PRIMARY_STAGE.getY());
		});
		
		/*Point where user clicked on scene to drag the window*/
		SCENE.setOnMousePressed(mouseEvent -> {
			anchor = new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY());
		});
		
		SCENE.setOnMouseDragged(mouseEvent -> {
			if(anchor != null && previousLocation != null) {
				PRIMARY_STAGE.setX(previousLocation.getX() + mouseEvent.getScreenX() - anchor.getX());
				PRIMARY_STAGE.setY(previousLocation.getY() + mouseEvent.getScreenY() - anchor.getY());
			}
		});
		
		/*Remembering new location on screen*/
		SCENE.setOnMouseReleased(mouseEvent -> {
			previousLocation = new Point2D(PRIMARY_STAGE.getX(), PRIMARY_STAGE.getY());
		});
	}

}
