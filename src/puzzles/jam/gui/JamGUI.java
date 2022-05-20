package puzzles.jam.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.Car;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;


public class JamGUI extends Application  implements Observer<JamModel>  {
    /** jam model */
    private JamModel jam;

    /** current file */
    private String file;

    /** true if car is current selected */
    private boolean selected;

    /** current car selected */
    private Car selectedCar;

    /** current row selected */
    private int selectedRow;

    /** current column selected */
    private int selectedCol;


    /** map of car colors */
    private HashMap<String, String> colors;


    /** 2D array of grid buttons */
    private Button[][] buttons = null;

    /** load button */
    private Button load;

    /** reset button */
    private Button reset;

    /** hint button */
    private Button hint;


    /** jam message */
    private Label message;

    /** BUTTON_FONT_SIZE */
    private final static int BUTTON_FONT_SIZE = 20;

    /** ICON_SIZE */
    private final static int ICON_SIZE = 75;

    /**
     * loads car colors into the colors HashMap
     * 
     */
    private void loadColors() {
        this.colors = new HashMap<String, String>();
        this.colors.put("A", "#81F781");
        this.colors.put("B", "#FE642E");
        this.colors.put("C", "#0101DF");
        this.colors.put("D", "#FF00FF");
        this.colors.put("E", "#AC58FA");
        this.colors.put("F", "#0B610B");
        this.colors.put("G", "#A4A4A4");
        this.colors.put("H", "#F5D0A9");
        this.colors.put("I", "#F3F781");
        this.colors.put("J", "#8A4B08");
        this.colors.put("K", "#0B6121");
        this.colors.put("L", "#FFFFFF");
        this.colors.put("O", "#FFFF00");
        this.colors.put("P", "#DA81F5");
        this.colors.put("Q", "#58ACFA");
        this.colors.put("R", "#088A08");
        this.colors.put("S", "#000000");
        this.colors.put("X", "#DF0101");
        this.colors.put("EMPTY", "#C2C9CA");
    }

    /**  
     * initializes JamGUI
     * 
     * @throws FileNotFoundException if file not found
     */
    public void init() throws FileNotFoundException {
        this.file = getParameters().getRaw().get(0);
        this.jam = new JamModel(file);
        this.buttons = new Button[this.jam.board.length][this.jam.board[0].length];
        this.selected = false;
        loadColors();
        this.jam.addObserver(this);
    }

    /**
     * Sets button as empty
     *
     * @param button button to set empty
     * @param row row of button
     * @param col col of button
     */
    private void setEmptyButton(Button button, int row, int col){
        button.setText("");

        button.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                "-fx-background-color: " + colors.get("EMPTY") + ";" +
                "-fx-font-weight: bold;" + "-fx-border-color:black;");

        button.setOnAction(actionEvent -> {
            // if a car is selected
            if (selected) {
                if(this.jam.isValidMove(selectedCar, row, col)){
                    this.jam.makeMove(selectedCar, row, col);
                    this.message.setText("Moved from (" + this.selectedRow + ", " + this.selectedCol + ") to (" + row + ", " + col + ")");
                }
                else{
                    this.message.setText("Cannot move from (" + this.selectedRow + ", " + this.selectedCol + ") to (" + row + ", " + col + ")");
                }
                this.selected = false;
            }
            // if car is not selected
            else {
                this.message.setText("No car at (" + row + ", " + col + ")");
            }
        });
    }

    /**
     * Sets button as car
     *
     * @param button button to set as car
     * @param row row of button
     * @param col col of button
     */
    private void setCarButton(Button button, int row, int col){
        Car car = this.jam.getCar(row, col);

        button.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                "-fx-background-color: " + colors.get(car.name) + ";" +
                "-fx-font-weight: bold;");
        button.setText(car.name);

        button.setOnAction(actionEvent -> {
            // if car is selected
            if(selected){
                this.message.setText("Cannot move from (" + this.selectedRow + ", " + this.selectedCol + ") to (" + row + ", " + col + ")");
                this.selected = false;
            }
            // select car if one is not selected
            else {
                this.selectedCar = this.jam.getCar(row, col);
                this.selectedRow = row;
                this.selectedCol = col;
                this.message.setText("Selected (" + row + ", " + col + ")");
                this.selected = true;
            }
        });
    }

    /**
     * Construct the layout for the game.
     *
     * @param stage container (window) in which to render the GUI
     * @throws Exception if there is a problem
     */
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();

        // creates a gridPane of buttons
        GridPane gridPane = new GridPane();

        // initialize message
        HBox topMessage = new HBox();
        this.message = new Label("Loaded: " + file);
        this.message.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE);
        topMessage.getChildren().addAll(message);
        topMessage.setAlignment(Pos.CENTER);
        borderPane.setTop(topMessage);

        // HBox of buttons at bottom
        HBox statusBar = new HBox();

        // initialize load button
        this.load = new Button();
        load.setText("Load");
        load.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE);
        load.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();

            // my path to "/data/jam"; feel free to change
            File dataDir = new File(System.getProperty("user.home"), "IntelliJProjects/Project 2-2/data/jam");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            fileChooser.setInitialDirectory(dataDir);
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(stage);
            if(file!=null){
               this.file = "data/jam/" + file.getName();
            }

            this.message.setText("Loaded: " + this.file);

            // create new model with new file
            try {
                this.jam = new JamModel(this.file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // create new gridPane with new buttons
            gridPane.getChildren().clear();
            this.buttons = new Button[this.jam.board.length][this.jam.board[0].length];
            for (int row=0; row < this.jam.board.length; ++row) {
                for (int col=0; col<this.jam.board[row].length; ++col) {
                    Button button = new Button();
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    if(this.jam.isEmpty(row, col)) {
                        this.setEmptyButton(button, row, col);
                    }
                    else{
                        this.setCarButton(button, row, col);
                    }
                    gridPane.add(button, col, row);
                    buttons[row][col] = button;
                }
            }
            borderPane.setCenter(gridPane);
            stage.sizeToScene();
            hint.setDisable(false);

            // adds new model as observer
            this.jam.addObserver(this);
        });

        // initialize reset button
        this.reset = new Button();
        reset.setText("Reset");
        reset.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE);
        reset.setOnAction(actionEvent -> {
            // create new model with same file
            try {
                this.jam = new JamModel(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // enable all buttons
            for (int row=0; row < this.jam.board.length; ++row) {
                for (int col = 0; col < this.jam.board[row].length; ++col) {
                    buttons[row][col].setDisable(false);
                }
            }
            this.hint.setDisable(false);

            // adds new model as observer
            this.jam.addObserver(this);

            //updates GUI
            this.update(this.jam);
        });

        // initialize hint button
        this.hint = new Button();
        hint.setText("Hint");
        hint.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE);
        hint.setOnAction(actionEvent -> {
            this.message.setText("Next Step!");

            // turns model into config
            JamConfig config = this.jam.makeConfig();
            Solver solver = new Solver();
            List<Configuration> steps = (List<Configuration>) solver.solve(config);

            // if no solution
            if(steps.isEmpty())
                this.message.setText("No Solution");
            else {
                // finds next step in puzzle
                JamConfig nextConfig = (JamConfig) steps.get(1);

                // turns config into model
                this.jam = nextConfig.makeModel();

                // adds new model as observer
                this.jam.addObserver(this);

                // updates GUI
                update(this.jam);
            }
        });

        // finish statusBar
        statusBar.getChildren().addAll(load, reset, hint);
        statusBar.setAlignment(Pos.CENTER);
        borderPane.setBottom(statusBar);


        // create gridPane of buttons
        for (int row=0; row < this.jam.board.length; ++row) {
            for (int col=0; col<this.jam.board[row].length; ++col) {
                Button button = new Button();
                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);
                if(this.jam.isEmpty(row, col))
                    this.setEmptyButton(button, row, col);
                else
                    this.setCarButton(button, row, col);

                gridPane.add(button, col, row);
                buttons[row][col] = button;
            }
        }
        borderPane.setCenter(gridPane);

        // creates the stage
        Scene scene = new Scene(borderPane);
        stage.setTitle("JamGUI");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * updates the Model and GUI
     *
     * @param jamModel the jam model
     */
    @Override
    public void update(JamModel jamModel) {
        // updates buttons
        for (int row=0; row < this.jam.board.length; ++row) {
            for (int col = 0; col < this.jam.board[row].length; ++col) {
                // disable buttons if game is won
                if(this.jam.isWon()){
                    buttons[row][col].setDisable(true);
                }
                // set button as empty if empty
                if(this.jam.isEmpty(row, col)){
                    this.setEmptyButton(buttons[row][col], row, col);
                }
                // set button as car
                else{
                    this.setCarButton(buttons[row][col], row, col);
                }
            }
        }

        // if game is won
        if(this.jam.isWon()){
            this.message.setText("Solved!");
            this.hint.setDisable(true);
        }

    }

    /**
     * The main method expects the host and port.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        }
        else{
            Application.launch(args);
        }
    }
}