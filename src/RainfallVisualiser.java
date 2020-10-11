import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import rainfall.Loader;
import rainfall.Record;
import rainfall.Station;

/**
 * Class to represent a RainfallVisualiser GUI.
 */
public class RainfallVisualiser extends Application {

    private TextField directoryNameInput;
    private TextField stationNameInput;

    private GraphicsContext chartGraphicsContext;
    // Text area to display Station Record values.
    private Label statusLabel;
    private TextArea recordDisplay;

    // HBoxes for 3 rows of GUI
    private HBox dataSelectionBar;
    private HBox viewerRow;
    private HBox statusRow;

    /**
     * Method to setup and display RainfallVisualiser GUI.
     */
    @Override
    public void start(Stage stage) {
        generateNodes();

        // Position Nodes
        BorderPane root = new BorderPane();
        root.setTop(dataSelectionBar);
        root.setCenter(viewerRow);
        root.setBottom(statusRow);

        // Style pane
        Insets rootInset = new Insets(5, 5, 5, 5);
        BorderPane.setMargin(dataSelectionBar, rootInset);
        BorderPane.setMargin(viewerRow, rootInset);
        BorderPane.setMargin(statusRow, rootInset);

        root.setStyle("-fx-background-color: Tan");

        // Setup window
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rainfall Visualiser 2.0");
        stage.setResizable(false);
        stage.show();
    } // end start

    /**
     * Helper method to initialise and style nodes to be displayed in BorderPane.
     */
    private void generateNodes() {
        // Data selection Bar
        Insets labelInset = new Insets(3, 0, 0, 0);
        // TODO: add your UI control instance variables here
        Label directoryNameInputLabel = new Label("Directory Name:");
        directoryNameInputLabel.setPadding(labelInset); // Center label vertically with TextField
        // directoryNameInput = new TextField("resources"); // Testing
        directoryNameInput = new TextField();

        Label stationNameInputLabel = new Label("Directory Name: ");
        stationNameInputLabel.setPadding(labelInset); // Center label vertically with TextField
        // stationNameInput = new TextField("TinarooFallsStation"); // Testing
        stationNameInput = new TextField();

        Button openButton = new Button("Open");
        openButton.setPrefWidth(125);
        openButton.setDefaultButton(true);
        openButton.setOnAction(e -> handleOpen());

        dataSelectionBar = new HBox(directoryNameInputLabel, directoryNameInput, stationNameInputLabel, stationNameInput, openButton);
        dataSelectionBar.setMaxHeight(25);
        dataSelectionBar.setSpacing(5);
        HBox.setHgrow(directoryNameInput, Priority.ALWAYS);
        HBox.setHgrow(stationNameInput, Priority.ALWAYS);
        HBox.setHgrow(openButton, Priority.ALWAYS);

        // Graph and data viewer
        int canvasWidth = 1000;
        int canvasHeight = 500;
        Canvas chartCanvas = new Canvas(canvasWidth, canvasHeight);

        // Fill canvas area
        chartGraphicsContext = chartCanvas.getGraphicsContext2D();
        chartGraphicsContext.setFill(Color.WHITE);
        chartGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);

        recordDisplay = new TextArea();
        recordDisplay.setPrefHeight(canvasHeight);
        recordDisplay.setPrefWidth(canvasWidth / 3.0);

        viewerRow = new HBox(chartCanvas, recordDisplay);
        viewerRow.setSpacing(5);

        // Status Row
        statusLabel = new Label("Status: ");
        statusRow = new HBox(statusLabel);
        statusLabel.setMaxHeight(8);
    } // end generateNodes

    /**
     * Method to handle open button being pressed.
     */
    private void handleOpen() {
        String directoryName = directoryNameInput.getText().strip();
        String stationName = stationNameInput.getText().strip();
        try {
            // Station object to hold Records.
            Station station = Loader.load(directoryName, stationName);
            for (int i = 0; i < station.numberOfRecords(); i++) {
                String currentDisplay = recordDisplay.getText();

                // First record just display to avoid blank line
                recordDisplay.setText(i == 0 ? formatRecord(station.getRecord(i)) :
                        currentDisplay + formatRecord(station.getRecord(i)));

                // Display blank line in between years
                if (station.getRecord(i).getMonth() == 12) recordDisplay.setText(recordDisplay.getText() + "\n");

                // TODO - Draw graph
            }
            statusLabel.setText("Status: Loaded");
        } catch (
                Loader.LoaderException e) { // Display error to Status Bar
            statusLabel.setText("Status: " + e.getMessage());
        }
    } // end handleOpen

    /**
     * Helper method to format a Record object ready to be displayed in recordDisplay.
     *
     * @param record The Record object being formatted.
     * @return String readable representation of Record object.
     */
    private String formatRecord(Record record) {
        return String.format("%s/%s - Total: %1.2f, Min: %1.2f, Max: %1.2f\n", record.getYear(), record.getMonth(),
                record.getTotal(), record.getMin(), record.getMax());
    } // end formatRecord

    /**
     * Helper method to launch the JavaFX application.
     *
     * @param args Arguments passed by command line.
     */
    public static void main(String[] args) {
        launch();
    } // end main
}
