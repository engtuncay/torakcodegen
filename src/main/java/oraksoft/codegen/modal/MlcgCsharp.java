package oraksoft.codegen.modal;

import oraksoft.codegen.modules.OccHomeCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fidborm.FiQugen;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FiDialogMetaType;

public class MlcgCsharp {

	public void actCsharpSinifOlusturma(OccHomeCont modHome) {

		Jdbi activeServerJdbi = modHome.getAndSetupActiveServerJdbi();

		if (activeServerJdbi != null) {
			FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
			//System.out.println("Connected");
		} else {
			FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
			return;
		}

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextField, "Tablo Adını Giriniz:");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {

			FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
			FiQugen fiqugen = new FiQugen();

			System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
			String entityCode = FiQugen.codeEntityClassCsharp(fxSimpleDialog.getTxValue(), modHome.getAndSetupActiveServerJdbi());

			if (!FiString.isEmpty(entityCode)) {
				modHome.getTxaMainOutput().appendText(entityCode);
			} else {
				modHome.getTxaMainOutput().appendText("N/A");
			}

		}


	}
}
