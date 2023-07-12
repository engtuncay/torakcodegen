package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModHomeCodeGenerator;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fidborm.FiQueryGenerator;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FxSimpleDialogMetaType;

public class MolcdgCsharp {

	public void actCsharpSinifOlusturma(ModHomeCodeGenerator modHome) {

		Jdbi activeServerJdbi = modHome.getAndSetupActiveServerJdbi();

		if (activeServerJdbi != null) {
			FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
			//System.out.println("Connected");
		} else {
			FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
			return;
		}

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextField, "Tablo Adını Giriniz:");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {

			FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
			FiQueryGenerator fiQueryGenerator = new FiQueryGenerator();

			System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
			String entityCode = FiQueryGenerator.codeEntityClassCsharp(fxSimpleDialog.getTxValue(), modHome.getAndSetupActiveServerJdbi());

			if (!FiString.isEmpty(entityCode)) {
				modHome.getCodeGenMainView().getTxaMainOutput().appendText(entityCode);
			} else {
				modHome.getCodeGenMainView().getTxaMainOutput().appendText("N/A");
			}

		}


	}
}
