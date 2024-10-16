package oraksoft.codegen.main;

import oraksoft.codegen.modules.CocHomeWindowCont;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ozpasyazilim.utils.log.Loghelper;

/**
 * Goc -> Gui Orak Code Generator App
 * <p>
 * Coc : Controller of Orak Code Generator   (eskiler : Moc)
 * <p>
 * Roc -> Repo Ocg App
 * <p>
 * {@link oraksoft.codegen.modules.CocHomeWindowCont} Home Window Controller
 */
public class AppCodeGen extends Application {

    public static void main(String[] args) {
        Loghelper.installLogger(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        CocHomeWindowCont cocHomeWindowCont = new CocHomeWindowCont();
        cocHomeWindowCont.initCont();
        cocHomeWindowCont.setFxStage(primaryStage);

        Scene scene = new Scene(cocHomeWindowCont.getModView().getRootPane());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
