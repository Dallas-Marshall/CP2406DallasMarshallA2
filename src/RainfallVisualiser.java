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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    private final int canvasWidth = 1250;
    private final int canvasHeight = 500;
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

        // Set blank display
        resetDisplays();

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
         directoryNameInput = new TextField();

        Label stationNameInputLabel = new Label("Directory Name: ");
        stationNameInputLabel.setPadding(labelInset); // Center label vertically with TextField
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
        Canvas chartCanvas = new Canvas(canvasWidth, canvasHeight);

        // Fill canvas area
        chartGraphicsContext = chartCanvas.getGraphicsContext2D();
        chartGraphicsContext.setFill(Color.WHITE);
        chartGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        chartGraphicsContext.setFont(Font.font("Calibri", 10));

        recordDisplay = new TextArea();
        recordDisplay.setPrefHeight(canvasHeight);
        recordDisplay.setPrefWidth(canvasWidth / 3.25);
        recordDisplay.setStyle("-fx-font-family: monospace;");

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
        resetDisplays();
        String directoryName = directoryNameInput.getText().strip();
        String stationName = stationNameInput.getText().strip();

        // Label station
        try {
            // Station object to hold Records.
            Station station = Loader.load(directoryName, stationName);
            for (int i = 0; i < station.getNumberOfRecords(); i++) {
                String currentDisplay = recordDisplay.getText();

                // First record just display to avoid blank line
                recordDisplay.setText(i == 0 ? formatRecord(station.getRecord(i)) :
                        currentDisplay + formatRecord(station.getRecord(i)));

                // Display blank line in between years
                if (station.getRecord(i).getMonth() == 12) recordDisplay.setText(recordDisplay.getText() + "\n");
            }

            // Display bar graph
            draw(station);
            statusLabel.setText("Status: Loaded");
        } catch (
                Loader.LoaderException e) { // Display error to Status Bar
            statusLabel.setText("Status: " + e.getMessage());
        }
    } // end handleOpen

    /**
     * Method to draw the bar graph of Station object rainfall Totals,
     * Auto Scales data to fit to axes.
     *
     * @param station Station object to be graphed.
     */
    private void draw(Station station) {
        // Calculate graphing Variables
        double HIGHEST_RAINFALL_VALUE = station.getMaxRainfallValue();

        int X_AXIS_Y_VALUE = canvasHeight - 40; // Distance from bottom of chartCanvas
        int Y_AXIS_HEIGHT = X_AXIS_Y_VALUE - 25; // Height from x-axis to top of y-axis
        int X_AXIS_LABEL_Y_VALUE = X_AXIS_Y_VALUE + 10; // Height of x-axis labels

        int Y_AXIS_LABEL_SPACING = Y_AXIS_HEIGHT / 10; // Value to space each y-axis label
        int Y_AXIS_LABEL_VALUE_INTERVAL = (int) HIGHEST_RAINFALL_VALUE / 10; // Value to increment each y-axis label

        double SCALE_FACTOR = Y_AXIS_HEIGHT / HIGHEST_RAINFALL_VALUE; // Factor to scale down monthlyRainfall to fit to graph
        int STARTING_X_VALUE = 100; // The x value that the chart axes starts from.

        // Draw Graph
        graphStationRecords(station, X_AXIS_Y_VALUE, X_AXIS_LABEL_Y_VALUE, SCALE_FACTOR, STARTING_X_VALUE);

        // Label Graph
        labelYAxis(X_AXIS_Y_VALUE, Y_AXIS_LABEL_SPACING, Y_AXIS_LABEL_VALUE_INTERVAL, STARTING_X_VALUE);
    } // end draw

    /**
     * Helper method to label y-axis of graph from graphStationRecords.
     *
     * @param x_AXIS_Y_VALUE              The y-value of x-axis.
     * @param y_AXIS_LABEL_SPACING        The value to space each y-axis label
     * @param y_AXIS_LABEL_VALUE_INTERVAL The value to space each y-axis label
     * @param STARTING_X_VALUE            The x value that the chart axes starts from.
     */
    private void labelYAxis(int x_AXIS_Y_VALUE, int y_AXIS_LABEL_SPACING, int y_AXIS_LABEL_VALUE_INTERVAL, int STARTING_X_VALUE) {
        for (int i = 0; i <= 10; i++) {
            // Calculate Label details
            int rainfallLabelValue = y_AXIS_LABEL_VALUE_INTERVAL * i;
            int rainfallLabelYValue = y_AXIS_LABEL_SPACING * i;
            // Create and measure label width
            Text rainfallLabel = new Text(String.format("%4d -", rainfallLabelValue));
            rainfallLabel.setFont(rainfallLabel.getFont());
            double textWidth = rainfallLabel.getBoundsInLocal().getWidth();
            // Adjust label start value
            double rainfallLabelXValue = STARTING_X_VALUE - textWidth;
            chartGraphicsContext.strokeText(String.format("%4d -", rainfallLabelValue), rainfallLabelXValue, x_AXIS_Y_VALUE - rainfallLabelYValue, textWidth);
        }
    }

    /**
     * Helper method to graph Record objects from Station object.
     *
     * @param station              The Station object containing Record's to be graphed.
     * @param x_AXIS_Y_VALUE       The y-value of x-axis.
     * @param x_AXIS_LABEL_Y_VALUE The y-value of x-axis Labels.
     * @param SCALE_FACTOR         The factor to scale down monthlyRainfall to fit to graph.
     * @param STARTING_X_VALUE     The x value that the chart axes starts from.
     */
    private void graphStationRecords(Station station, int x_AXIS_Y_VALUE, int x_AXIS_LABEL_Y_VALUE, double SCALE_FACTOR, int STARTING_X_VALUE) {
        int NUMBER_OF_RECORDS = station.getNumberOfRecords();
        double X_AXIS_WIDTH = canvasWidth - 30 - STARTING_X_VALUE;
        double COLUMN_WIDTH = (X_AXIS_WIDTH / NUMBER_OF_RECORDS);

        double currentValueX = STARTING_X_VALUE; // start graphing inside axis
        int lastYearLabelled = station.getRecord(0).getYear(); // Last year labelled on x-axis

        for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
            Record record = station.getRecord(i);
            double scaledMonthlyRainfall = record.getTotal() * SCALE_FACTOR; // Scale rainfallTotal to fit on axes

            // alternate bar colours per year
            if (record.getYear() % 2 == 0) {
                chartGraphicsContext.setFill(Color.TAN);
            } else {
                chartGraphicsContext.setFill(Color.BLUE);
            }

            // Label x-axis
            if (record.getMonth() == 1) {
                if (record.getYear() - lastYearLabelled == 5 || i == 0) { // Label first and then every 5 years
                    chartGraphicsContext.strokeText("| " + record.getYear(), currentValueX, x_AXIS_LABEL_Y_VALUE, 50);
                    lastYearLabelled = record.getYear();
                }
            }

            // Draw column
            double adjustedYValue = x_AXIS_Y_VALUE - scaledMonthlyRainfall; // Find top coord of column
            chartGraphicsContext.fillRect(currentValueX, adjustedYValue, COLUMN_WIDTH, scaledMonthlyRainfall);

            currentValueX += COLUMN_WIDTH; // Move x across for new column
        }
    } // end graphRainfall

    /**
     * Helper method to draw and label x and y axes as well as setting the chart title.
     *
     * @param STARTING_X_VALUE The x value that the chart axes starts from.
     * @param X_AXIS_Y_VALUE   The y value that the x axis sits at.
     */
    private void drawAxis(int STARTING_X_VALUE, int X_AXIS_Y_VALUE) {
        chartGraphicsContext.setFont(Font.font("Calibri", 10));
        chartGraphicsContext.setStroke(Color.BLACK);

        chartGraphicsContext.strokeLine(STARTING_X_VALUE, X_AXIS_Y_VALUE, canvasWidth - 25, X_AXIS_Y_VALUE);
        chartGraphicsContext.strokeLine(STARTING_X_VALUE, X_AXIS_Y_VALUE, STARTING_X_VALUE, 25);

        chartGraphicsContext.strokeText("Year", (canvasWidth / 2.0), canvasHeight - 10, 50);
        chartGraphicsContext.strokeText("Rainfall (mm)", 10, (canvasHeight / 2.0), 75);

        chartGraphicsContext.setFont(Font.font("Calibri", 12));
        chartGraphicsContext.strokeText("Monthly Rainfall Totals per Year", (canvasWidth / 2.0 - 30), 10, 200);
    }

    /**
     * Helper method to reset chartCanvas and recordDisplay to initial blank state.
     */
    private void resetDisplays() {
        int X_AXIS_Y_VALUE = canvasHeight - 40;
        int STARTING_X_VALUE = 100;

        chartGraphicsContext.setFill(Color.WHITE);
        chartGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        recordDisplay.setText("");
        // draw and label x and y axis
        drawAxis(STARTING_X_VALUE, X_AXIS_Y_VALUE);
    } // end resetDisplays

    /**
     * Helper method to format a Record object ready to be displayed in recordDisplay.
     *
     * @param record The Record object being formatted.
     * @return String readable representation of Record object.
     */
    private String formatRecord(Record record) {
        return String.format("%s/%-2s - Total: %-7.2f Min: %-5.2f Max: %-6.2f\n", record.getYear(), record.getMonth(),
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
