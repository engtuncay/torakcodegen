package oraksoft.codegen.modal;

import ozpasyazilim.utils.entitysql.SqlTable;
import oraksoft.codegen.modules.ModEntityListCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.metadata.metaMikro.FiColsEntegre;
import ozpasyazilim.utils.core.FiBoolean;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fxwindow.FxTableWindowCont;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialog;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialogType;

import java.util.List;

public class ModalCodeGenDialog {

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
		FxDialogShow.build().nodeModalByIFxSimpleCont(null, modEntityListCont, null, null, null);
		return modEntityListCont;
	}

	public static String showDialogSelectTable(Jdbi jdbi, String txMessage) {
		FxTableWindowCont<SqlTable> fxTableWindowCont = new FxTableWindowCont();
		fxTableWindowCont.initCont();
		fxTableWindowCont.getFxTableView().setEnableLocalFilterEditor(true);
		fxTableWindowCont.activateSelectButton();
		if (!FiString.isEmpty(txMessage)) {
			fxTableWindowCont.addNoteLine(txMessage);
		}

		List<SqlTable> sqlTableList = new ModalSql().getSqlTableList(jdbi);
		fxTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.bui().sqlTableName());
		fxTableWindowCont.setTableContent(sqlTableList);
		fxTableWindowCont.openAsModalMain(null);

		if (fxTableWindowCont.checkClosedWithDone()) {
			return fxTableWindowCont.getEntitySelected().getTABLE_NAME();
		}

		return null;
	}

	public static List<SqlTable> showDialogSelectTableMulti(Jdbi jdbi, String txMessage, Boolean boShowCount) {
		FxTableWindowCont<SqlTable> fxTableWindowCont = new FxTableWindowCont();
		fxTableWindowCont.initCont();
		fxTableWindowCont.getFxTableView().setEnableLocalFilterEditor(true);
		fxTableWindowCont.activateSelectButtonForMulti();
		if (!FiString.isEmpty(txMessage)) {
			fxTableWindowCont.addNoteLine(txMessage);
		}

		fxTableWindowCont.getFxTableView().addFiColSelection();
		fxTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.bui().sqlTableName());

		List<SqlTable> sqlTableList;

		if (FiBoolean.isTrue(boShowCount)) {
			sqlTableList = new ModalSql().getSqlTableListWithCount(jdbi);
			fxTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.bui().sqlBoCount());
		} else {
			sqlTableList = new ModalSql().getSqlTableList(jdbi);
		}

		fxTableWindowCont.setTableContent(sqlTableList);
		fxTableWindowCont.openAsModalMain(null);

		if (fxTableWindowCont.checkClosedWithDone()) {
			return fxTableWindowCont.getFxTableView().getItemsCheckedByBoSelectAsListInAllElements();
		}

		return null;
	}
}
