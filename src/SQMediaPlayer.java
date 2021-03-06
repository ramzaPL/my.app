

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
 
public class SQMediaPlayer extends Application implements EventHandler<MouseEvent>{
    
	private Stage PRIMARY_STAGE;		//stage
	private Scene scene;				//container for all components
	private TilePane buttonTile;		//container for buttons
	private TilePane labelTile;		//container for labels
	private VBox vbox;				//container for components
	private Button play;				//starts to play music
	private Button previous;			//plays previous song
	private Button next;				//plays next song
	private Button add;				//allows to add songs to list
	private Button shuffle;			//allows to shuffle the songs list
	private Button randomPlay;			//play the songs list randomly
	private Button equalizer;			//opens equalizer
	private Button clearList;			//clears play list
	private Button exit;			//exits application
	private Button minimize;		//minimize application
	private Button mute;			//mutes media
	private Button list;			//shows editable list
	private ArrayList<Button> buttons;	//array of buttons
	private Label timeLabel;	//label
	private TextArea playListArea; 		//list of songs
	//static private ArrayList<URI> playList;		//paths to medias
	//private ArrayList<String> mediaName;	//names of medias
	private List<File> importedFiles;		//list of imported files
	private FileChooser chooser;			//allows to pick media
	private Integer mediaIterator = 0;		//iterator for play list
	private List<File> songList;			//list of songs
	private long shuffleTime = System.nanoTime(); //time for shuffle
	/*
	 * Tells if music is currently playing or not
	 * PAUSE value - music is NOT playing
	 * PLAY value - music is currently playing 
	 */
	private String playerState = "PAUSE";
	/*
	 * tells how to play media from play list
	 * NORMAL value - media is playing in natural order 1,2,3...
	 * RANDOM value - media is randomly using Random class
	 */
	private String playMode = "NORMAL";
	/*
	 * tells if player is muted or not
	 */
	private boolean isMuted = false;
	private Point2D anchorPt;
	private Point2D previousLocation;
	/*
	 * Object of MediaPlayer, provides base 
	 * functionality for player
	 */
	private MediaPlayer mediaPlayer;
	private Duration duration;	//duration of currently playing media
	
	public static void main(String[] args) {
      launch(args);
      
    }
	
	/*
	 * Returns URI of imported files
	 */
	public URI getMediaUri(int i)
	{	
		return importedFiles.get(i).toURI();
	}
	
    
    @Override
    public void start(Stage primaryStage) {
    	
    	PRIMARY_STAGE = primaryStage;
    	
    	PRIMARY_STAGE.initStyle(StageStyle.TRANSPARENT);		//disable ugly window
    	
    	initGUI();	//initialize components 
    	
    	/*
    	 * creates scene and adds new style sheet
    	 */
    	scene = new Scene(vbox, 450, 260);
    	scene.getStylesheets().add("my/res/stylesheets/style.css");
    	
    	initMovablePlayer();	//makes player movable
    	
    	play.requestFocus();	//requests focus for play button
    	
    	PRIMARY_STAGE.setScene(scene);		
    	PRIMARY_STAGE.setResizable(false);
    	PRIMARY_STAGE.show();
    	
    }

	/*
	 * Allows to move player on screen via mouse
	 * @param primaryStage
	 */
	private void initMovablePlayer() {
		//initialize previous location just after window is shown
		PRIMARY_STAGE.addEventHandler(WindowEvent.WINDOW_SHOWN, (WindowEvent e) -> {
    		previousLocation = new Point2D(PRIMARY_STAGE.getX(), PRIMARY_STAGE.getY());
    	});
    	
    	//starting point
    	scene.setOnMousePressed(mouseEvent -> anchorPt = new Point2D(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
    	
    	//dragging the entire stage
    	scene.setOnMouseDragged(mouseEvent -> {
    		if(anchorPt != null && previousLocation != null) {
    			PRIMARY_STAGE.setX(previousLocation.getX() + mouseEvent.getScreenX() - anchorPt.getX());
    			PRIMARY_STAGE.setY(previousLocation.getY() + mouseEvent.getScreenY() - anchorPt.getY());
    		}
    	});
    	
    	//new location
    	scene.setOnMouseReleased(mouseEvent -> previousLocation = new Point2D(PRIMARY_STAGE.getX(), PRIMARY_STAGE.getY()));
	}

	/*
	 * initialize graphic user interface
	 */
	private void initGUI() {
		vbox = new VBox(4);
    	vbox.setAlignment(Pos.CENTER_LEFT);
		
    	createControlButtons();
		
    	createTimeLabel();
    	
    	createSongsListsTextArea();
    	
    	createButtons();
	}

	/*
	 * Creates exit button and minimize button
	 * @param primaryStage
	 */
	private void createControlButtons() {
		/*
		 * exit button
		 */
		exit = new Button("X");
		exit.getStyleClass().add("controlButtons");
		exit.setOnMouseClicked(mouseEvent -> Platform.exit());
		exit.setOnMouseEntered(mouseEvent -> exit.setStyle("-fx-background-color: rgba(230, 0, 0, 130)"));
		exit.setOnMouseExited(mouseEvent -> exit.setStyle("-fx-background-color: rgba(0, 0, 0, 0)"));
		exit.setPadding(new Insets(0));
		
		/*
		 * minimize button
		 */
		minimize = new Button("---");
		minimize.getStyleClass().add("controlButtons");
		minimize.setOnMouseClicked(mouseEvent -> PRIMARY_STAGE.setIconified(true));
		minimize.setOnMouseEntered(mouseEvent -> minimize.setStyle("-fx-background-color: rgba(0, 150, 220, 130)"));
		minimize.setOnMouseExited(mouseEvent -> minimize.setStyle("-fx-background-color: rgba(0, 0, 0, 0)"));
		minimize.setPadding(new Insets(0));
		
		TilePane controlButtons = new TilePane();
		controlButtons.setAlignment(Pos.TOP_RIGHT);
		controlButtons.getChildren().addAll(minimize, exit);
		
		vbox.getChildren().add(controlButtons);
		VBox.setMargin(controlButtons, new Insets(2, 2, 0, 0));
	}

	/*
	 * Creates text area for songs list
	 */
	private void createSongsListsTextArea() {
		playListArea = new TextArea("Songs list");
    	playListArea.setEditable(false);
    	playListArea.setPrefHeight(90);
    	playListArea.setPrefWidth(400);
    	playListArea.setWrapText(true);
    	playListArea.getStyleClass().add("txArea");
    	
    	
    	playListArea.setOnDragOver(e ->	dragOver(e));
		
		playListArea.setOnDragExited(e -> playListArea.setStyle("-fx-border-width: 0px;"));
		
		playListArea.setOnDragDropped(e -> dragDropped(e));
    	
    	vbox.getChildren().add(playListArea);
    	VBox.setMargin(playListArea, new Insets(10, 10, 1, 10));
    	
	}

	/*
	 * Loads media from drag and drop gesture
	 */
	private void dragDropped(DragEvent e) {
		final Dragboard db = e.getDragboard();	
		boolean success = false;
		
		if(db.hasFiles()) {
			success = true;
			
			List<File> songList = db.getFiles();	//getting dragged files
			
			/*
			 * creating needed containers for paths and names 
			 * of dragged media
			 */
			if(importedFiles == null) {
				importedFiles = new ArrayList<File>();		
			}
			
			loadPlayList(songList);		//loading/refreshing play list
			
		}
		e.setDropCompleted(success);
		e.consume();
	}

	/*
	 * Checks if files extensions are acceptable
	 */
	private void dragOver(DragEvent e) {
		boolean isAccepted = false;
		
		/*
		 * checking if files extensions are .mp3
		 */
		for(int i = 0; i < e.getDragboard().getFiles().size(); i++) {
			isAccepted = e.getDragboard().getFiles().get(i).toString().toLowerCase().endsWith(".mp3"); 
		}
		
		/*
		 * if all files ends with .mp3 dragDrop has green light,
		 * if not: play list will flash with red light
		 */
		if(isAccepted) {
			playListArea.setStyle("-fx-border-color: #00ff00; -fx-border-width: 1.5px;"
					+ " -fx-border-radius: 0.5em; -fx-background-radius: 0.5em;");
			e.acceptTransferModes(TransferMode.LINK);
			
		} else {
			playListArea.setStyle("-fx-border-color: #ff0000; -fx-border-width: 1.5px;"
					+ " -fx-border-radius: 0.5em; -fx-background-radius: 0.5em;");
			e.consume();
			
		}
	}

	/*
	 * Creates time label
	 */
	private void createTimeLabel() {
		timeLabel = new Label("00:00/00:00");
    	
    	labelTile = new TilePane();
    	labelTile.setAlignment(Pos.TOP_RIGHT);
    	labelTile.setHgap(15);
    	labelTile.setVgap(10);
    	labelTile.getChildren().addAll(timeLabel);
    	
    	vbox.getChildren().add(labelTile);
    	VBox.setMargin(labelTile, new Insets(0, 11, 0, 0));
	}

	/*
	 * Creates default buttons
	 */
	private void createButtons() {
		/*
		 * Creates small buttons: mute, show list
		 */
		createSmallButtons();
		
		/*
		 * Creates large buttons: previous, play, next, add, group of buttons
		 */
		createLargeButtons();

	}

	/*
	 *  
	 */
	private void createSmallButtons() {
		Image icon;
		/*
    	 * creates small buttons
    	 */
    	
    	mute = new Button();
    	icon = new Image(getClass().getResourceAsStream("my/res/images/mute.png"));
    	mute.setGraphic(new ImageView(icon));
    	mute.getStyleClass().add("smallButton");
    	mute.setOnMouseEntered(e -> onHover(e));
    	mute.setOnMouseExited(e -> onExit(e));
    	mute.setOnMousePressed(e -> onPressed(e));
    	mute.setOnMouseReleased(e -> onHover(e));
    	mute.setOnMouseClicked(e -> {
    		
    		if(mediaPlayer != null && !isMuted) {
    			mediaPlayer.setMute(true);
    			mute.setStyle("-fx-border-color: #5555ff; -fx-border-width: 3px;");
    			mute.setOnMouseEntered(null);
    			mute.setOnMouseExited(null);
    			mute.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("my/res/images/unMute.png"))));
    			isMuted = true;
    			
    		} else if(mediaPlayer != null && isMuted) {
    			mediaPlayer.setMute(false);
    			mute.setOnMouseEntered(ev -> onHover(ev));
    	    	mute.setOnMouseExited(ev -> onExit(ev));
    	    	mute.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("my/res/images/mute.png"))));
    	    	isMuted = false;
    		}
    		
    	});
    	
    	list = new Button();
    	icon = new Image(getClass().getResourceAsStream("my/res/images/list.png"));
    	list.setGraphic(new ImageView(icon));
    	list.getStyleClass().add("smallButton");
    	list.setOnMouseEntered(e -> onHover(e));
    	list.setOnMouseExited(e -> onExit(e));
    	list.setOnMousePressed(e -> onPressed(e));
    	list.setOnMouseReleased(e -> onHover(e));
    	list.setOnMouseClicked(e -> {
    		if(importedFiles != null)
				try {
					new EditableList(importedFiles, new Stage());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
    	});
    	
    	TilePane smallButtonsTile = new TilePane();
    	smallButtonsTile.setAlignment(Pos.CENTER_LEFT);
    	smallButtonsTile.setHgap(10);
    	smallButtonsTile.getChildren().addAll(mute, list);
    	
    	vbox.getChildren().add(smallButtonsTile);
    	VBox.setMargin(smallButtonsTile, new Insets(1, 0, 1, 10));
	}

	/*
	 * creates large buttons
	 */
	private void createLargeButtons() {
		buttons = new ArrayList<Button>();
		
		/*
		 * play button 
		 */
    	play = new Button();
    	Image icon = new Image(getClass().getResourceAsStream("my/res/images/playButton.png"));
    	play.setGraphic(new ImageView(icon));		//adding image to button
    	play.setOnMouseEntered(e -> onHover(e));	//action when mouse cursor is above button
    	play.setOnMouseExited(e -> onExit(e));		//action when mouse cursor exits button
    	play.setOnMousePressed(e -> onPressed(e));	//action when mouse button is pressed
    	play.setOnMouseReleased(e -> onHover(e));	//action when mouse button releases
    	play.setOnMouseClicked(this);				//action when button is clicked
    	
    	
    	/*
    	 * previous button
    	 */
    	previous = new Button(); 
    	icon = new Image(getClass().getResourceAsStream("my/res/images/previousButton.png"));
    	previous.setGraphic(new ImageView(icon));
    	previous.setOnMouseEntered(e -> onHover(e));
    	previous.setOnMouseExited(e -> onExit(e));
    	previous.setOnMousePressed(e -> onPressed(e));
    	previous.setOnMouseReleased(e -> onHover(e));
    	previous.setOnMouseClicked(this);
    	
    	/*
    	 * next button
    	 */
    	next = new Button();
    	icon = new Image(getClass().getResourceAsStream("my/res/images/nextButton.png"));
    	next.setGraphic(new ImageView(icon));
    	next.setOnMouseEntered(e -> onHover(e));
    	next.setOnMouseExited(e -> onExit(e));
    	next.setOnMousePressed(e -> onPressed(e));
    	next.setOnMouseReleased(e -> onHover(e));
    	next.setOnMouseClicked(this);
    	
    	/*
    	 * add button
    	 */
    	add = new Button();
    	icon = new Image(getClass().getResourceAsStream("my/res/images/addButton.png"));
    	add.setGraphic(new ImageView(icon));
    	add.setOnMouseEntered(e -> onHover(e));
    	add.setOnMouseExited(e -> onExit(e));
    	add.setOnMousePressed(e -> onPressed(e));
    	add.setOnMouseReleased(e -> onHover(e));
    	add.setOnMouseClicked(e -> chooseMedia());
    	
    	/*
    	 * Array of buttons
    	 */
    	buttons.add(previous); buttons.add(play); buttons.add(next);
    	
    	/*
    	 * Creates tile of buttons
    	 */
    	VBox v = createButtonGroup();
    	
    	buttonTile = new TilePane();
    	buttonTile.setAlignment(Pos.CENTER);
    	buttonTile.setHgap(15);
    	buttonTile.setVgap(10);
    	buttonTile.getChildren().addAll(buttons);
    	buttonTile.getChildren().addAll(v, add);
    	
    	vbox.getChildren().add(buttonTile);
    	VBox.setMargin(buttonTile, new Insets(0, 10, 10, 10));
	}

	/*
	 * creates group of buttons
	 */
	private VBox createButtonGroup() {
		Image icon;
		/*
    	 * group of buttons 
    	 */
    	VBox v = new VBox(2);
    	v.setSpacing(0);
    	HBox h1 = new HBox();
    	h1.setSpacing(0);
    	
    	/*
    	 * shuffle button
    	 */
    	shuffle = new Button();
    	shuffle.getStyleClass().add("top_left");
    	icon = new Image(getClass().getResourceAsStream("my/res/images/shuffleButton.png"));
    	shuffle.setGraphic(new ImageView(icon));
    	shuffle.setPadding(new Insets(7, 0, 0, 5));
    	shuffle.setOnMouseEntered(e -> onHover(e));
    	shuffle.setOnMouseExited(e -> onExit(e));
    	shuffle.setOnMousePressed(e -> onPressed(e));
    	shuffle.setOnMouseReleased(e -> onHover(e));
    	shuffle.setOnMouseClicked(e -> { 	
    		if(importedFiles != null) {
    			shufflePlayList();  			
    		}
    		
    	});
    	
    	
    	/*
    	 * random play button
    	 */
    	randomPlay = new Button("R");
    	randomPlay.getStyleClass().add("top_right");
    	randomPlay.setPadding(new Insets(5, 8, 0, 0));
    	randomPlay.setOnMouseEntered(e -> onHover(e));
    	randomPlay.setOnMouseExited(e -> onExit(e));
    	randomPlay.setOnMousePressed(e -> onPressed(e));
    	randomPlay.setOnMouseReleased(e -> onHover(e));
    	randomPlay.setOnMouseClicked(e -> {
    		
    		if(playMode.equals("NORMAL")) { 
    			playMode = "RANDOM";
    			randomPlay.setStyle("-fx-border-color: #5555ff; -fx-border-width: 3px;");
    			randomPlay.setOnMouseExited(null);
    			randomPlay.setOnMouseEntered(null);
    		}
    		else {
    			playMode = "NORMAL";
    			randomPlay.setOnMouseExited(ev -> onExit(ev));
    			randomPlay.setOnMouseEntered(ev -> onHover(ev));
    		}
    		
    	});
    	
    	h1.getChildren().addAll(shuffle, randomPlay);
    	
    	HBox h2 = new HBox();
    	h2.setSpacing(0);
    	
    	/*
    	 * equalizer
    	 */
    	equalizer = new Button("EQ");
    	equalizer.getStyleClass().add("bottom_left");
    	equalizer.setPadding(new Insets(0, 0, 5, 5));
    	equalizer.setOnMouseEntered(e -> onHover(e));
    	equalizer.setOnMouseExited(e -> onExit(e));
    	equalizer.setOnMousePressed(e -> onPressed(e));
    	equalizer.setOnMouseReleased(e -> onHover(e));
    	equalizer.setOnMouseClicked(e -> {
    		if(importedFiles != null) {
    			for(File path : importedFiles){
    				System.out.println(path);
    			}
    		}
    	});
    	
    	/*
    	 * clear list button
    	 */
    	clearList = new Button("C");
    	clearList.getStyleClass().add("bottom_right");
    	clearList.setPadding(new Insets(0, 5, 5, 0));
    	clearList.setOnMouseEntered(e -> onHover(e));
    	clearList.setOnMouseExited(e -> onExit(e));
    	clearList.setOnMousePressed(e -> onPressed(e));
    	clearList.setOnMouseReleased(e -> onHover(e));
    	clearList.setOnMouseClicked(e -> clearList());
    	
    	h2.getChildren().addAll(equalizer, clearList);
    	
    	v.getChildren().addAll(h1, h2);
		return v;
	}

	/*
	 * clears play list
	 */
	private void clearList() {
		
		//clear lists
		if(importedFiles != null)	importedFiles.clear();
		if(playListArea != null)	playListArea.setText("");
		
		//stop and clear media player
		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer = null;
		}
		
		//resets media iterator
		mediaIterator = 0;
		
		//reset time
		timeLabel.setText("00:00/00:00");
		
		//change icon of play/pause button
		Image newIcon = new Image(getClass().getResourceAsStream("my/res/images/playButton.png"));
		play.setGraphic(new ImageView(newIcon));
		//change state to PAUSE
		playerState = "PAUSE";
	}

	/*
	 * allows to choose media you want to play
	 */
	private void chooseMedia() {
		
		/*
		 * Adds songs to list
		 */
		if(chooser == null) {		//initialize
			
			initFileChooser();	//setting file chooser's behavior
			
			songList = chooser.showOpenMultipleDialog(PRIMARY_STAGE);	//opening the chooser's window
			
			importedFiles = new ArrayList<File>();		//list of paths to media files
			
			loadPlayList(songList); 	//loading play list
			
		} else {
			List<File> songList = chooser.showOpenMultipleDialog(PRIMARY_STAGE);
			
			loadPlayList(songList); //refreshes play list
		}
	}

	/*
	 * loads/refreshes play list
	 * @param list
	 */
	private void loadPlayList(List<File> list) {
		if(list != null) {			
			for(File file : list) {
				importedFiles.add(file);
			}
			
			/*
			 * filling play list with names of medias
			 */
			playListArea.setText("");
			int index = 1;
			
			for(File media : importedFiles) {
				String fileName = ((File) media).getName();				
				playListArea.appendText(index + ".  " + fileName + "\n");
				index++;
			}
			
			/*
			 * deleting last whitespace
			 */
			playListArea.setEditable(true);
			playListArea.deletePreviousChar();
			playListArea.setEditable(false);
			
			/*
			 * setting caret at beginning
			 */
			setCaretPosition(playListArea, 0);			
		}
	}
	
	/*
	 * shuffle a playlist
	 */
	private void shufflePlayList() {
			playListArea.setText("");
			Collections.shuffle(importedFiles, new Random(shuffleTime));
			int index = 1;
			
			for(File media : importedFiles) {
				String fileName = ((File) media).getName();				
				playListArea.appendText(index + ".  " + fileName + "\n");
				index++;
			}
		}
		
	/*
	 * customize file chooser window and behavior 
	 */
	private void initFileChooser() {
		FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("MP3 music files", "*.mp3");
		chooser = new FileChooser();
		chooser.setTitle("Open Media");
		chooser.getExtensionFilters().add(fileExtension);
	}

	/*
	 * sets caret position to "pos" in text area
	 */
	private void setCaretPosition(TextArea ta, int pos) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ta.positionCaret(pos);
				
			}
			
		});
	}

	/*
	 * Changes border color when mouse cursor enters button
	 * @param e - button
	 */
	private void onHover(MouseEvent e) {
		
		Object source = e.getSource();
		((Button)source).setStyle("-fx-border-color: #aaaaff; -fx-border-width: 2");
		
	}
	
	/*
	 * Changes back border color when mouse cursor exits button
	 * @param e - button
	 */
	private void onExit(MouseEvent e) {
		
		Object source = e.getSource();
		((Button)source).setStyle("-fx-border-color: #cccccc; -fx-border-width: 1");
		
	}
	
	/*
	 * Changes border color and width when "pressing" the button
	 * @param e - pressed button
	 */
	private void onPressed(MouseEvent e) {
		
		Object source = e.getSource();
		((Button)source).setStyle("-fx-border-color: #5555ff; -fx-border-width: 3");
	}

	/*
	 * Handle actions taken on buttons
	 * (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(MouseEvent e) {
		Button source = (Button)e.getSource();
		
		/*
		 * Checks which button was clicked and takes a proper action
		 */
		
		//action for play button
		if(source == play && e.getButton().equals(MouseButton.PRIMARY)) {
			
			if(playerState.equals("PAUSE") && importedFiles != null && !importedFiles.isEmpty()) {
				/*
				 * plays music and changes image to pause symbol, and state to playing value
				 */
				Image newIcon = new Image(getClass().getResourceAsStream("my/res/images/pauseButton.png"));
				play.setGraphic(new ImageView(newIcon));
				playerState = "PLAY";
				
				playMedia(getMediaUri(mediaIterator));
				
			} else if(playerState.equals("PLAY")) {
				/*
				 * stops music and changes image to play symbol, and state to pause value
				 */
				Image newIcon = new Image(getClass().getResourceAsStream("my/res/images/playButton.png"));
				play.setGraphic(new ImageView(newIcon));
				playerState = "PAUSE";
				
				pauseMedia();
				
			} 
			
			/*
			 * Action for next button
			 */
		} else if(source == next && importedFiles != null && !importedFiles.isEmpty()) {		//additional checking if playList exists
			
			if(playerState.equals("PLAY")) {
				
				if( playMode.equals("NORMAL")) {
					/*
				 	* Plays next media on the list
				 	*/
					checkNextMedia();
				} else {
					Random r = new Random();
					mediaIterator = r.nextInt(importedFiles.size());
				}
				playNewMedia(getMediaUri(mediaIterator), true);
			
			} else if(playerState.equals("PAUSE")) {
				if( playMode.equals("NORMAL")) {
					checkNextMedia();
				} else {
					Random r = new Random();
					mediaIterator = r.nextInt(importedFiles.size());
				}
				playNewMedia(getMediaUri(mediaIterator), false);
			
			}
			/*
			 * Action for previous button
			 */
		} else if(source == previous && importedFiles != null && !importedFiles.isEmpty()) {
			
			/*
			 * Plays previous media on the list
			 * or random depends on play mode
			 */
			if(playerState.equals("PLAY")) {
				if( playMode.equals("NORMAL")) {
					checkPreviousMedia();
				} else {
					Random r = new Random();
					mediaIterator = r.nextInt(importedFiles.size());
				}
				playNewMedia(getMediaUri(mediaIterator), true);
			
			} else if(playerState.equals("PAUSE")) {
				if( playMode.equals("NORMAL")) {
					checkPreviousMedia();
				} else {
					Random r = new Random();
					mediaIterator = r.nextInt(importedFiles.size());
				}
				playNewMedia(getMediaUri(mediaIterator), false);
			}
			
		}
	}

	/*
	 * checks if there's a media file before current 
	 * and sets iterator at previous file, 
	 * or at the end if no previous file was found 
	 */
	private void checkPreviousMedia() {
		if(mediaIterator - 1 == -1) {
			mediaIterator = importedFiles.size() - 1;
		} else {
			mediaIterator -= 1;
		}
	}

	/*
	 * checks if there's a media file after current
	 * and sets iterator at next file, 
	 * or at the first if no next file was found 
	 */
	private void checkNextMedia() {
		if(mediaIterator + 1 == importedFiles.size()) {
			mediaIterator = 0;
		} else {
			mediaIterator += 1;
		}
	}

	/*
	 * Used when user wants to play next/previous media on list
	 */
	private void playNewMedia(URI path, boolean mediaPlaying) {
		
		if(mediaPlaying && mediaPlayer != null) {
			mediaPlayer.stop();
			Media currentMedia = new Media(path.toString());
			mediaPlayer = new MediaPlayer(currentMedia);
			mediaPlayer.play();
			initMediaPlayerListeners();
		
		} else if(!mediaPlaying && mediaPlayer != null) {
			Media currentMedia = new Media(path.toString());
			mediaPlayer = new MediaPlayer(currentMedia);
			initMediaPlayerListeners();
		}
	}

	/*
	 * Pauses playing media
	 */
	private void pauseMedia() {
		
		if(mediaPlayer != null) {
			mediaPlayer.pause();
		}
		
	}

	/*
	 * Loads and plays media
	 */
	private void playMedia(URI path) {
		
		if(mediaPlayer != null){
			
			mediaPlayer.play();

			
		} else {
			Media currentMedia = new Media(path.toString());
			mediaPlayer = new MediaPlayer(currentMedia);
			mediaPlayer.play();
			initMediaPlayerListeners();
		}
		
	}

	private void initMediaPlayerListeners() {
		/*
		 * Getting duration of just loaded media
		 */
		mediaPlayer.setOnReady(new Runnable() {

			@Override
			public void run() {
				//gets duration of loaded media
				duration = mediaPlayer.getMedia().getDuration();
				updateValues();
			}
			
		});
		
		mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
			
			/*
			 * Updates elapsed time
			 * (non-Javadoc)
			 * @see javafx.beans.InvalidationListener#invalidated(javafx.beans.Observable)
			 */
			@Override
			public void invalidated(Observable observable) {
				updateValues();
				
			}
		});
		
		/*
		 * Action taken when currently playing media reach its end
		 */
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			
			@Override
			public void run() {
				if( playMode.equals("NORMAL")) {
					checkNextMedia();
				} else {
					Random r = new Random();
					mediaIterator = r.nextInt(importedFiles.size());
				}
				playNewMedia(getMediaUri(mediaIterator), true);
			}
		});
	}
	
	/*
	 * calculates time
	 */
	private static String formatTime(Duration _elapsed, Duration _duration) {
		
		/*
		 * calculating elapsed time
		 */
		int elapsedTime =  (int) Math.floor(_elapsed.toSeconds());
		int elapsedMinutes = elapsedTime / 60;
		int elapsedSeconds = elapsedTime - (elapsedMinutes * 60);
		
		/*
		 * calculating duration of current media
		 */
		if(_duration.greaterThan(Duration.ZERO)) {
			int duration = (int) Math.floor(_duration.toSeconds());
			int durationMinutes = duration / 60;
			int durationSeconds = duration - (durationMinutes * 60);
			
			return String.format("%02d:%02d/%02d:%02d", 
					elapsedMinutes, elapsedSeconds, 
					durationMinutes, durationSeconds);
		} else {
			return String.format("%02d:%02d", 
					elapsedMinutes, elapsedSeconds);
		}
	}
	
	/*
	 * updates time label
	 */
	private void updateValues() {
		
		if(timeLabel != null) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					if(mediaPlayer != null) {
						Duration currentTime = mediaPlayer.getCurrentTime();
						timeLabel.setText(formatTime(currentTime, duration));
					}
				}
			});
		}
	}
}