package oraksoft.codegen.main;

import oraksoft.codegen.modules.ModHomeCodeGenerator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ozpasyazilim.utils.log.Loghelper;

public class AppCodeGen extends Application {
    public static void main(String[] args) {
        Loghelper.installLogger(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ModHomeCodeGenerator modHomeCodeGenerator = new ModHomeCodeGenerator();
        modHomeCodeGenerator.initCont();
        modHomeCodeGenerator.setMainStage(primaryStage);

        Scene scene = new Scene(modHomeCodeGenerator.getModView().getRootPane());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
