package oraksoft.codegen.main;

import oraksoft.codegen.modules.OccHomeWindowCont;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ozpasyazilim.utils.log.Loghelper;

/**
 * Ocg -> Orak Code Generator - Gui  (eski Goc ) -> Gui Orak Code Generator App
 * <p>
 * Occ : Orak Code Generator - Controller (eskiler : Moc)
 * <p>
 * Roc -> Repo Ocg App
 * <p>
 * {@link OccHomeWindowCont} Home Window Controller
 */
public class AppOrakCodeGen extends Application {

    public static void main(String[] args) {
        Loghelper.installLogger(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        OccHomeWindowCont occHomeWindowCont = new OccHomeWindowCont();
        occHomeWindowCont.initCont();
        occHomeWindowCont.setFxStage(primaryStage);

        Scene scene = new Scene(occHomeWindowCont.getModView().getRootPane());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
