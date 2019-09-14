package com.test.resume;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    BorderPane rootlayout;
    BorderPane viewerBorderPane;
    ScrollPane htmlScrollPane;
    VBox sectionSelectorVBox;

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

        return rootlayout;
    }

    private BorderPane createViewerBorderPane() {
        viewerBorderPane = new BorderPane();
        viewerBorderPane.setRight(createHTMLScrollPane());

        viewerBorderPane.setLeft(createSectionSelectorVBox());

        return viewerBorderPane;
    }

    private ScrollPane createHTMLScrollPane() {
        htmlScrollPane = new ScrollPane();

        Platform.runLater(() -> {
            WebView webView = new WebView();
            try {
                webView.getEngine().loadContent(getFileContent(getClass().getResource("test.html").openStream())); // TODO find created HTML file
            } catch (IOException e) {
                e.printStackTrace();
            }
            htmlScrollPane.setContent(webView);
        });

        return htmlScrollPane;
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

        sectionSelectorVBox.getChildren().addAll(eduButton, workButton, projButton, skillButton);

        return sectionSelectorVBox;
    }

}
