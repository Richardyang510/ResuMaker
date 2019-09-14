import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application {

    private class ResumeField {
        String title;
        String position;
        String location;
        String startDate;
        String toDate;
        ArrayList<String> points;

        ResumeField(String title, String position, String location, String startDate, String toDate) {
            this.title = title;
            this.position = position;
            this.location = location;
            this.startDate = startDate;
            this.toDate = toDate;
            this.points = new ArrayList<>();
        }

        public void addPoint(String point) {
            points.add(point);
        }

        @Override
        public String toString() {
            return "[" + title + "] " + position + " at " + location + " from " + startDate + "-" + toDate;
        }
    }

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

        sectionSelectorVBox.getChildren().addAll(eduButton, workButton, projButton, skillButton);

        return sectionSelectorVBox;
    }

    private BorderPane createItemChooserBorderPane() {
        itemChooserBorderPane = new BorderPane();

        return itemChooserBorderPane;
    }

    private void updateCurrentSelected() {
        if(currentSelectorOption == SelectorOption.EDUCATION) {
            createEducationSelector();
        }
    }

    private void createEducationSelector() {
        itemChooserBorderPane.setTop(new Text("Education"));
        if(educationListView == null) {
            educationData = FXCollections.observableArrayList();
            educationData.add(new ResumeField("University of Waterloo", "Software Developer", "Waterloo, ON", "2014", "Present"));
            educationListView = new ListView<ResumeField>(educationData);
            educationListView.setCellFactory(CheckBoxListCell.forListView(new Callback<ResumeField, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(ResumeField field) {
                    BooleanProperty observable = new SimpleBooleanProperty();
                    observable.addListener((obs, wasSelected, isNowSelected) ->
                            System.out.println("Check box for "+field+" changed from "+wasSelected+" to "+isNowSelected)
                    );
                    return observable ;
                }
            }));
        }
        itemChooserBorderPane.setCenter(educationListView);
    }

}
