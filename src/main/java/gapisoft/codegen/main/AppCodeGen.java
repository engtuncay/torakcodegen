package gapisoft.codegen.main;

import gapisoft.codegen.modules.ModHomeCodeGenCont;
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

		ModHomeCodeGenCont modHomeCodeGenCont = new ModHomeCodeGenCont();
		modHomeCodeGenCont.initCont();

		Scene scene = new Scene(modHomeCodeGenCont.getModView().getRootPane());

		primaryStage.setScene(scene);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(500);
		primaryStage.show();


	}
}
