package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModHomeCodeGenCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fidborm.FiQueryGenerator;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialog;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialogType;

public class ModalCsharp {

	public void actCsharpSinifOlusturma(ModHomeCodeGenCont modHome) {

		Jdbi activeServerJdbi = modHome.getAndSetupActiveServerJdbi();

		if (activeServerJdbi != null) {
			FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
			//System.out.println("Connected");
		} else {
			FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
			return;
		}

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextField, "Tablo Adını Giriniz:");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {

			FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
			FiQueryGenerator fiQueryGenerator = new FiQueryGenerator();

			System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
			String entityCode = FiQueryGenerator.codeEntityClassCsharp(fxSimpleDialog.getTxValue(), modHome.getAndSetupActiveServerJdbi());

			if (!FiString.isEmpty(entityCode)) {
				modHome.getCodeGenMainView().getFxTextArea().appendText(entityCode);
			} else {
				modHome.getCodeGenMainView().getFxTextArea().appendText("N/A");
			}

		}


	}
}
