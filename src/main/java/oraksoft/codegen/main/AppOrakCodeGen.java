package oraksoft.codegen.main;

import oraksoft.codegen.modules.OccHomeCont;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ozpasyazilim.utils.core.FiException;
import ozpasyazilim.utils.gui.components.OzFrame;
import ozpasyazilim.utils.log.Loghelper;

import javax.swing.*;
import java.util.Arrays;

/**
 * Ocg -> Orak Code Generator - Gui  (eski Goc ) -> Gui Orak Code Generator App
 * <p>
 * Occ : Orak Code Generator - Controller (eskiler : Moc)
 * <p>
 * Roc-Ocr -> Repo Ocg App
 * <p>
 * {@link OccHomeCont} Home Window Controller
 */
public class AppOrakCodeGen extends Application {

    public static void main(String[] args) {
        Loghelper.installLogger(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        OccHomeCont occHomeCont = new OccHomeCont();
        occHomeCont.initCont();
        occHomeCont.setFxStage(primaryStage);

        setupLookAndFeel();

        Scene scene = new Scene(occHomeCont.getModView().getRootPane());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void setupLookAndFeel() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Exception:" + Arrays.toString(ex.getStackTrace()));
            Loghelper.get(AppOrakCodeGen.class).debug(FiException.exToLog(ex));
        }

    }
}
