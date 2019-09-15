import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;

public class Main extends Application {

    private enum SelectorOption {
        SECTIONS,
        BLURB,
        EDUCATION,
        WORK,
        PROJECT,
        SKILL
    }

    private SelectorOption currentSelectorOption = SelectorOption.EDUCATION;

    private ObservableList<Section> sectionObservableList;
    private ListView<Section> sectionListView;
    private Section blurb;
    private Section education;
    private Section work;
    private Section proj;

    private BorderPane rootlayout;
    private BorderPane viewerBorderPane;
    private BorderPane itemChooserBorderPane;
    private StackPane htmlStackPane;
    private VBox sectionSelectorVBox;
    private HBox renderOptionsHBox;

    private Stage primaryStage;

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
        Button secButton = new Button("Sections");
        Button blurbButton = new Button("Career Objective");
        Button eduButton = new Button("Education");
        Button workButton = new Button("Work Experience");
        Button projButton = new Button("Personal Project");
        Button skillButton = new Button("Skills");

        secButton.setMaxWidth(Double.MAX_VALUE);
        blurbButton.setMaxWidth(Double.MAX_VALUE);
        eduButton.setMaxWidth(Double.MAX_VALUE);
        workButton.setMaxWidth(Double.MAX_VALUE);
        projButton.setMaxWidth(Double.MAX_VALUE);
        skillButton.setMaxWidth(Double.MAX_VALUE);

        secButton.setOnMouseClicked(e -> {
            currentSelectorOption = SelectorOption.SECTIONS;
            updateCurrentSelected();
        });

        blurbButton.setOnMouseClicked(e -> {
            currentSelectorOption = SelectorOption.BLURB;
            updateCurrentSelected();
        });

        eduButton.setOnMouseClicked(e -> {
            currentSelectorOption = SelectorOption.EDUCATION;
            updateCurrentSelected();
        });

        workButton.setOnMouseClicked(e -> {
            currentSelectorOption = SelectorOption.WORK;
            updateCurrentSelected();
        });

        projButton.setOnMouseClicked(e -> {
            currentSelectorOption = SelectorOption.PROJECT;
            updateCurrentSelected();
        });

        sectionSelectorVBox.getChildren().addAll(secButton, blurbButton, eduButton, workButton, projButton, skillButton);

        return sectionSelectorVBox;
    }

    private BorderPane createItemChooserBorderPane() {
        sectionObservableList = FXCollections.observableArrayList();

        blurb = new Section("blurb", "Career Objective");
        education = new Section("education", "Education");
        education.initUseResumeField(primaryStage);
        work = new Section("work", "Work Experience");
        work.initUseResumeField(primaryStage);
        proj = new Section("proj", "Projects");
        proj.initUseResumeField(primaryStage);

        sectionObservableList.addAll(blurb, education, work, proj);

        itemChooserBorderPane = new BorderPane();
        return itemChooserBorderPane;
    }

    private Text sectionsText = new Text("Sections");


    private void updateCurrentSelected() {
        if (currentSelectorOption == SelectorOption.SECTIONS) {
            sectionsText.setFont(Font.font(14));
            itemChooserBorderPane.setCenter(createSectionsSelector());
            itemChooserBorderPane.setTop(sectionsText);
        } else if (currentSelectorOption == SelectorOption.BLURB) {
            blurb.display(itemChooserBorderPane);
        } else if (currentSelectorOption == SelectorOption.EDUCATION) {
            education.display(itemChooserBorderPane);
        } else if (currentSelectorOption == SelectorOption.WORK) {
            work.display(itemChooserBorderPane);
        } else if (currentSelectorOption == SelectorOption.PROJECT) {
            proj.display(itemChooserBorderPane);
        }
    }

    private ListView createSectionsSelector() {
        if (sectionListView != null) return sectionListView;

        sectionListView = new ListView<>(sectionObservableList);
        sectionListView.setCellFactory(CheckBoxListCell.forListView(new Callback<Section, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Section item) {
                System.out.println("asdf");
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) ->
                        // TODO section.setEnabled(true);
                        System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected)
                );
                return observable;
            }
        }));

        return sectionListView;
    }
}
