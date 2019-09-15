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

    private SelectorOption currentSelectorOption = SelectorOption.SECTIONS;

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
    private HTMLGenerator htmlGenerator;
    private String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> <html lang=\"en\"> <head> <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></html>";
    private CheckBox liveUpdateCheckBox;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        htmlGenerator = new HTMLGenerator();

        Scene scene = new Scene(createRootLayout(), 300, 275);

        stage.setTitle("Untitled Resume Builder"); // TODO name
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
        updateLiveIfChecked();

        return rootlayout;
    }

    private BorderPane createViewerBorderPane() {
        viewerBorderPane = new BorderPane();
        viewerBorderPane.setRight(createHTMLScrollPane());

        renderOptionsHBox = new HBox();

        Button saveButton = new Button("Save");
        saveButton.setOnMouseClicked(e -> {
            html = htmlGenerator.generateHTMLString(sectionObservableList);
            viewerBorderPane.setRight(createHTMLScrollPane());
        });
        liveUpdateCheckBox = new CheckBox("Live Update");
        liveUpdateCheckBox.setSelected(true);

        renderOptionsHBox.getChildren().addAll(saveButton, liveUpdateCheckBox);

        viewerBorderPane.setBottom(renderOptionsHBox);

        return viewerBorderPane;
    }

    private StackPane createHTMLScrollPane() {
        if(htmlStackPane == null) {
            htmlStackPane = new StackPane();
        }
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().loadContent(html);
            webView.setMaxHeight(Double.MAX_VALUE);
            webView.setMaxWidth(Double.MAX_VALUE);
            htmlStackPane.getChildren().removeAll();
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

        blurb = new Section("blurb", "Career Objective", this);
        blurb.initData();
        education = new Section("education", "Education", this);
        education.initUseResumeField(primaryStage);
        work = new Section("work", "Work Experience", this);
        work.initUseResumeField(primaryStage);
        proj = new Section("proj", "Projects", this);
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
                BooleanProperty observable = new SimpleBooleanProperty(item.isEnabled());
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                        item.setEnabled(isNowSelected);
                        updateLiveIfChecked();
                        System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
                });
                return observable;
            }
        }));

        return sectionListView;
    }

    public void updateLiveIfChecked() {
        System.out.println(liveUpdateCheckBox.isSelected());
        if(liveUpdateCheckBox.isSelected()) {
            html = htmlGenerator.generateHTMLString(sectionObservableList);
            viewerBorderPane.setRight(createHTMLScrollPane());
        }
    }
}
