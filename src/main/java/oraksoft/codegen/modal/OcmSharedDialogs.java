package oraksoft.codegen.modal;

import ozpasyazilim.utils.entitysql.EntSqlTable;
import oraksoft.codegen.modules.OccEntityListCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.metadata.metaMikro.FiColsEntegre;
import ozpasyazilim.utils.core.FiBool;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fxwindow.FxSimpleTableWindowCont;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FiDialogMetaType;
import ozpasyazilim.utils.gui.fxcomponents.FxWindow;

import java.util.List;

/**
 * Mcg : Modal of Code Generator
 */
public class OcmSharedDialogs {

	public static Integer actDialogIdSelection() {

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextFieldInteger, "Id Değerini Giriniz");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {
			Integer idNo = (Integer) fxSimpleDialog.getObjectValue();
			return idNo;
		}

		return null;
	}

	public static OccEntityListCont showDialogSelectEntityClass() {
		OccEntityListCont occEntityListCont = new OccEntityListCont();
		occEntityListCont.initCont();
		FxWindow.nodeWindow(null, occEntityListCont, null, null, null);
		return occEntityListCont;
	}

	public static String showDialogSelectTable(Jdbi jdbi, String txMessage) {

		FxSimpleTableWindowCont<EntSqlTable> fxSimpleTableWindowCont = new FxSimpleTableWindowCont();
		fxSimpleTableWindowCont.initCont();
		fxSimpleTableWindowCont.getFxTableView().setEnableLocalFilterEditor(true);
		fxSimpleTableWindowCont.activateSelectButton();
		if (!FiString.isEmpty(txMessage)) {
			fxSimpleTableWindowCont.addNoteLine(txMessage);
		}

		List<EntSqlTable> entSqlTableList = new McgSql().getSqlTableList(jdbi);
		fxSimpleTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.sqlTableName());
		fxSimpleTableWindowCont.setTableContent(entSqlTableList);
		fxSimpleTableWindowCont.openAsModalMain(null);

		if (fxSimpleTableWindowCont.checkClosedWithDone()) {
			return fxSimpleTableWindowCont.getEntitySelected().getTABLE_NAME();
		}

		return null;
	}

	public static List<EntSqlTable> showDialogSelectTable(Jdbi jdbi, String txMessage, Boolean boShowCount, Boolean boMulti) {

		FxSimpleTableWindowCont<EntSqlTable> fxSimpleTableWindowCont = new FxSimpleTableWindowCont();
		fxSimpleTableWindowCont.initCont();
		fxSimpleTableWindowCont.getFxTableView().setEnableLocalFilterEditor(true);

		// çoklu seçim olacak mı
		if(FiBool.isTrue(boMulti)){
			fxSimpleTableWindowCont.activateSelectButtonForMulti();
		}else{
			fxSimpleTableWindowCont.activateSelectButton();
		}

		if (!FiString.isEmpty(txMessage)) {
			fxSimpleTableWindowCont.addNoteLine(txMessage);
		}

		fxSimpleTableWindowCont.getFxTableView().addFiColSelection();
		fxSimpleTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.sqlTableName());

		List<EntSqlTable> entSqlTableList;

		if (FiBool.isTrue(boShowCount)) {
			entSqlTableList = new McgSql().getSqlTableListWithCount(jdbi);
			fxSimpleTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.sqlBoCount());
		} else {
			entSqlTableList = new McgSql().getSqlTableList(jdbi);
		}

		fxSimpleTableWindowCont.setTableContent(entSqlTableList);
		fxSimpleTableWindowCont.openAsModalMain(null);

		if (fxSimpleTableWindowCont.checkClosedWithDone()) {
			return fxSimpleTableWindowCont.getFxTableView().getItemsFiCheckedAsNewListInAllElements();
		}

		return null;
	}
}
