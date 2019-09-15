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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
        Scene scene = new Scene(grid, 300, 275);
        Stage editorStage = new Stage();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        grid.add(scenetitle, 0, 0, 2, 1);

        Label title = new Label("Title:");
        grid.add(title, 0, 1);

        TextField titleTextField = new TextField(field.getTitle());
        grid.add(titleTextField, 1, 1);

        Label position = new Label("Position:");
        grid.add(position, 0, 2);

        TextField positionTextField = new TextField(field.getPosition());
        grid.add(positionTextField, 1, 2);

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnMouseClicked(e -> {
            listView.getSelectionModel().clearSelection();
            field.setTitle(titleTextField.getText());
            field.setPosition(positionTextField.getText());
            listView.refresh();
            editorStage.close();
        });

        cancelButton.setOnMouseClicked(e -> {
            editorStage.close();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(saveButton);
        hbBtn.getChildren().add(cancelButton);
        grid.add(hbBtn, 1, 4);

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
