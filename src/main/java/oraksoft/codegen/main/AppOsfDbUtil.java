package oraksoft.codegen.main;

import oraksoft.codegen.modules.OduHomeCont;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ozpasyazilim.utils.core.FiException;
import ozpasyazilim.utils.log.Loghelper;

import javax.swing.*;
import java.util.Arrays;

/**
 * OsfDbUtil -> Orak Soft Database Util - Gui App
 * <p>
 * {@link OduHomeCont} Home Window Controller
 */
public class AppOsfDbUtil extends Application {

    public static void main(String[] args) {
        Loghelper.installLogger(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        OduHomeCont oduHomeCont = new OduHomeCont();
        oduHomeCont.initCont();
        oduHomeCont.setFxStage(primaryStage);

        setupLookAndFeel();

        Scene scene = new Scene(oduHomeCont.getModView().getRootPane());

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
            Loghelper.get(AppOsfDbUtil.class).debug(FiException.exToErrorLog(ex));
        }

    }
}
