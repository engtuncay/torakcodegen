package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModEntityListCont;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialog;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialogType;

public class ModalDialog {

	public static Integer actDialogIdSelection() {
		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextFieldInteger, "Id Değerini Giriniz");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {
			Integer idNo = (Integer) fxSimpleDialog.getObjectValue();
			return idNo;
		}

		return null;
	}

	public static ModEntityListCont showDialogSelectEntityClass() {
		ModEntityListCont modEntityListCont = new ModEntityListCont();
		modEntityListCont.initCont();
		FxDialogShow.build().nodeModalByIFxMod(null, modEntityListCont, null, null, null);
		return modEntityListCont;
	}
}
