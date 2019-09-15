import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Section {

    private String type; // e.g. education, work
    private String name; // e.g. Education, Work Experience

    private boolean usesResumeField = false; // would be only a string if false
    private boolean enabled = false;

    private String notResumeFieldData;

    private String dataLocation;
    private ObservableList<ResumeField> data;
    private ListView<ResumeField> listView;
    private Stage primaryStage;

    private BorderPane pointsBorderPane;
    private HBox pointsButtonHBox;
    private Button newPointButton;
    private Button remPointButton;
    private ObservableList<String> pointsData;
    private ListView<String> pointsListView;

    private Text title;

    private ObjectMapper mapper = new ObjectMapper();

    public Section(String type, String name) {
        this.type = type;
        this.title = new Text(name);
        this.name = name;
        title.setFont(Font.font(14));
        this.dataLocation = "data/" + type + ".json";
    }

    public String getName() {
        return name;
    }

    public boolean isUsesResumeField() {
        return usesResumeField;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getNotResumeFieldData() {
        return notResumeFieldData;
    }

    public ObservableList<ResumeField> getData() {
        return data;
    }

    public void initTextData(String blockData) {
        notResumeFieldData = blockData;
    }

    public void initUseResumeField(Stage primaryStage) {
        this.usesResumeField = true;
        this.primaryStage = primaryStage;
        this.data = FXCollections.observableArrayList();

        InputStream is = getClass().getResourceAsStream(dataLocation);
        try {
            ArrayList<ResumeField> myObjects = mapper.readValue(is, new TypeReference<ArrayList<ResumeField>>() {});
            data.addAll(myObjects);
        } catch (IOException e) {
            e.printStackTrace();
        }


        listView = new ListView<>(data);
        listView.setCellFactory(CheckBoxListCell.forListView(field -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                field.setEnabled(isNowSelected);
                System.out.println("Check box for " + field + " changed from " + wasSelected + " to " + isNowSelected);
            });
            return observable;
        }));

        listView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                ResumeField current = listView.getSelectionModel().getSelectedItem();
                spawnEditor(current, listView);
            }
        });
    }

    private Stage spawnEditor(ResumeField field, ListView listView) {
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 600, 450);
        Stage editorStage = new Stage();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        grid.add(scenetitle, 0, 0, 2, 1);

        // Title
        Label title = new Label("Title:");
        grid.add(title, 0, 1);

        TextField titleTextField = new TextField(field.getTitle());
        grid.add(titleTextField, 1, 1);

        // Position
        Label position = new Label("Position:");
        grid.add(position, 0, 2);

        TextField positionTextField = new TextField(field.getPosition());
        grid.add(positionTextField, 1, 2);

        // Location
        Label location = new Label("Location:");
        grid.add(location, 0, 3);

        TextField locationTextField = new TextField(field.getLocation());
        grid.add(locationTextField, 1, 3);

        // Start
        Label startDate = new Label("Start Date:");
        grid.add(startDate, 0, 4);

        TextField startDateTextField = new TextField(field.getStartDate());
        grid.add(startDateTextField, 1, 4);

        // End
        Label toDate = new Label("End Date:");
        grid.add(toDate, 0, 5);

        TextField toDateTextField = new TextField(field.getToDate());
        grid.add(toDateTextField, 1, 5);

        // Points
        pointsData = FXCollections.observableArrayList();
        pointsData.addAll(field.getPoints());
        pointsListView = new ListView<>(pointsData);
        pointsListView.setEditable(true);
        pointsListView.setCellFactory(TextFieldListCell.forListView());

        pointsListView.setOnEditCommit(t -> {
            pointsData.set(t.getIndex(), t.getNewValue());
        });

        Label points = new Label("Points:");
        grid.add(points, 0, 6);

        pointsBorderPane = new BorderPane();
        pointsButtonHBox = new HBox();
        pointsButtonHBox.setMaxWidth(Double.MAX_VALUE);
        newPointButton = new Button("+");
        remPointButton = new Button("-");
        pointsButtonHBox.getChildren().addAll(newPointButton, remPointButton);
        pointsBorderPane.setBottom(pointsButtonHBox);
        pointsBorderPane.setCenter(pointsListView);

        newPointButton.setOnMouseClicked(e -> pointsData.add("null"));

        remPointButton.setOnMouseClicked(e -> {
            pointsData.remove(pointsListView.getSelectionModel().getSelectedItem());
        });

        grid.add(pointsBorderPane, 1, 6);

        // Confirm/Cancel
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnMouseClicked(e -> {
            listView.getSelectionModel().clearSelection();
            field.setTitle(titleTextField.getText());
            field.setPosition(positionTextField.getText());
            field.setLocation(locationTextField.getText());
            field.setStartDate(startDateTextField.getText());
            field.setToDate(toDateTextField.getText());
            field.setPoints(new ArrayList<>(pointsData));
            listView.refresh();
            editorStage.close();
        });

        cancelButton.setOnMouseClicked(e -> {
            editorStage.close();
        });

        grid.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                editorStage.close();
            }
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(saveButton);
        hbBtn.getChildren().add(cancelButton);
        grid.add(hbBtn, 1, 7);

        editorStage.setTitle("Editing " + field);
        editorStage.setScene(scene);

        editorStage.initOwner(primaryStage);
        editorStage.initModality(Modality.APPLICATION_MODAL);

        editorStage.showAndWait();
        return editorStage;
    }

    public void display(BorderPane display) {
        display.setTop(title);
        display.setCenter(listView);
    }

    @Override
    public String toString() {
        return name;
    }
}
