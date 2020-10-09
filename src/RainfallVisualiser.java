import javafx.application.Application;
import javafx.event.ActionEvent;
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
import rainfall.Station;

public class RainfallVisualiser extends Application {

    // TODO: add your UI control instance variables here
    private Label directoryNameInputLabel;
    private TextField directoryNameInput;
    private Label stationNameInputLabel;
    private TextField stationNameInput;
    private Button openButton;
    private HBox dataSelectionBar;
    private Canvas chartCanvas;
    private GraphicsContext chartGraphicsContext;
    private TextArea recordDisplay;
    private HBox viewerRow;
    private Label statusLabel;
    private HBox statusRow;

    private Station station;

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
    }

    private void generateNodes() {
        // Data selection Bar
        Insets labelInset = new Insets(3, 0, 0, 0);
        directoryNameInputLabel = new Label("Directory Name:");
        directoryNameInputLabel.setPadding(labelInset); // Center label vertically with TextField
        directoryNameInput = new TextField();

        stationNameInputLabel = new Label("Directory Name: ");
        stationNameInputLabel.setPadding(labelInset); // Center label vertically with TextField
        stationNameInput = new TextField();

        openButton = new Button("Open");
        openButton.setPrefWidth(125);
        openButton.setDefaultButton(true);
        openButton.setOnAction(this::handleOpen);

        dataSelectionBar = new HBox(directoryNameInputLabel, directoryNameInput, stationNameInputLabel, stationNameInput, openButton);
        dataSelectionBar.setMaxHeight(25);
        dataSelectionBar.setSpacing(5);
        HBox.setHgrow(directoryNameInput, Priority.ALWAYS);
        HBox.setHgrow(stationNameInput, Priority.ALWAYS);
        HBox.setHgrow(openButton, Priority.ALWAYS);

        // Graph and data viewer
        int canvasWidth = 1000;
        int canvasHeight = 500;
        chartCanvas = new Canvas(canvasWidth, canvasHeight);

        // Fill canvas area
        chartGraphicsContext = chartCanvas.getGraphicsContext2D();
        chartGraphicsContext.setFill(Color.WHITE);
        chartGraphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);

        recordDisplay = new TextArea();
        recordDisplay.setPrefHeight(canvasHeight);
        recordDisplay.setPrefWidth(canvasWidth / 2.5);

        viewerRow = new HBox(chartCanvas, recordDisplay);
        viewerRow.setSpacing(5);

        // Status Row
        statusLabel = new Label("Status: ");
        statusRow = new HBox(statusLabel);
        statusLabel.setMaxHeight(8);
    }

    private void handleOpen(ActionEvent actionEvent) {
        System.out.println("Open Pressed :)");
    }

    public static void main(String[] args) {
        launch();
    }
}
