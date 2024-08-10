package oraksoft.codegen.main;

import oraksoft.codegen.modules.GocHomeWindowCont;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ozpasyazilim.utils.log.Loghelper;

/**
 * Goc -> Gui Orak Code Generator App
 * <p>
 * Moc -> Modal Ocg App
 * <p>
 * Roc -> Repo Ocg App
 */
public class AppCodeGen extends Application {

    public static void main(String[] args) {
        Loghelper.installLogger(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GocHomeWindowCont gocHomeWindowCont = new GocHomeWindowCont();
        gocHomeWindowCont.initCont();
        gocHomeWindowCont.setFxStage(primaryStage);

        Scene scene = new Scene(gocHomeWindowCont.getModView().getRootPane());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
