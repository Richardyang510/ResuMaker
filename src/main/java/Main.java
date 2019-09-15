import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application {

    private enum SelectorOption {
        EDUCATION,
        WORK,
        PROJECT,
        SKILL
    }

    private SelectorOption currentSelectorOption = SelectorOption.EDUCATION;

    private ListView<ResumeField> educationListView;
    private ObservableList<ResumeField> educationData;

    private BorderPane rootlayout;
    private BorderPane viewerBorderPane;
    private BorderPane itemChooserBorderPane;
    private StackPane htmlStackPane;
    private VBox sectionSelectorVBox;
    private HBox renderOptionsHBox;

    private Stage primaryStage;

    private ObjectMapper mapper = new ObjectMapper();

    public static String getFileContent(
            InputStream fis) throws IOException {
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(fis))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Scene scene = new Scene(createRootLayout(), 300, 275);

        stage.setTitle("Untitled Resume Builder");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private BorderPane createRootLayout() {
        rootlayout = new BorderPane();
        rootlayout.setRight(createViewerBorderPane());
        rootlayout.setLeft(createSectionSelectorVBox());
        rootlayout.setCenter(createItemChooserBorderPane());
        updateCurrentSelected();

        return rootlayout;
    }

    private BorderPane createViewerBorderPane() {
        viewerBorderPane = new BorderPane();
        viewerBorderPane.setRight(createHTMLScrollPane());

        renderOptionsHBox = new HBox();

        Button saveButton = new Button("Save");
        renderOptionsHBox.getChildren().addAll(saveButton);

        viewerBorderPane.setBottom(renderOptionsHBox);

        return viewerBorderPane;
    }

    private StackPane createHTMLScrollPane() {
        htmlStackPane = new StackPane();

        Platform.runLater(() -> {
            WebView webView = new WebView();
            try {
                webView.getEngine().loadContent(getFileContent(getClass().getResource("index.html").openStream())); // TODO find created HTML file
            } catch (IOException e) {
                e.printStackTrace();
            }
            webView.setMaxHeight(Double.MAX_VALUE);
            webView.setMaxWidth(Double.MAX_VALUE);
            htmlStackPane.getChildren().addAll(webView);
            htmlStackPane.setPrefWidth(1000D);
        });

        return htmlStackPane;
    }

    private VBox createSectionSelectorVBox() {
        sectionSelectorVBox = new VBox();
        Button eduButton = new Button("Education");
        Button workButton = new Button("Work Experience");
        Button projButton = new Button("Personal Project");
        Button skillButton = new Button("Skills");

        eduButton.setMaxWidth(Double.MAX_VALUE);
        workButton.setMaxWidth(Double.MAX_VALUE);
        projButton.setMaxWidth(Double.MAX_VALUE);
        skillButton.setMaxWidth(Double.MAX_VALUE);

        eduButton.setOnMouseClicked(e -> {
            currentSelectorOption = SelectorOption.EDUCATION;
            updateCurrentSelected();
        });

        workButton.setOnMouseClicked(e -> {
            System.out.println("work");
        });

        sectionSelectorVBox.getChildren().addAll(eduButton, workButton, projButton, skillButton);

        return sectionSelectorVBox;
    }

    private BorderPane createItemChooserBorderPane() {
        itemChooserBorderPane = new BorderPane();

        return itemChooserBorderPane;
    }

    private void updateCurrentSelected() {
        if (currentSelectorOption == SelectorOption.EDUCATION) {
            createEducationSelector();
        }
    }

    private void createEducationSelector() {
        itemChooserBorderPane.setTop(new Text("Education"));
        if (educationListView == null) {
            educationData = FXCollections.observableArrayList();

            InputStream is = Main.class.getResourceAsStream("data/education.json");

            try {
                ArrayList<ResumeField> myObjects = mapper.readValue(is, new TypeReference<ArrayList<ResumeField>>() {
                });
                educationData.addAll(myObjects);
            } catch (IOException e) {
                e.printStackTrace();
            }

            educationListView = new ListView<ResumeField>(educationData);
            educationListView.setCellFactory(CheckBoxListCell.forListView(new Callback<ResumeField, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(ResumeField field) {
                    BooleanProperty observable = new SimpleBooleanProperty();
                    observable.addListener((obs, wasSelected, isNowSelected) ->
                            System.out.println("Check box for " + field + " changed from " + wasSelected + " to " + isNowSelected)
                    );
                    return observable;
                }
            }));

            educationListView.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    ResumeField current = educationListView.getSelectionModel().getSelectedItem();
                    spawnEditor(current, educationListView);
                }
            });
        }
        itemChooserBorderPane.setCenter(educationListView);
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

}
