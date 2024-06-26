package oraksoft.codegen.modal;

import ozpasyazilim.utils.entitysql.EntSqlTable;
import oraksoft.codegen.modules.ModEntityListCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.metadata.metaMikro.FiColsEntegre;
import ozpasyazilim.utils.core.FiBool;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fxwindow.FxSimpleTableWindowCont;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FxSimpleDialogMetaType;
import ozpasyazilim.utils.gui.fxcomponents.FxWindow;

import java.util.List;

/**
 * Mcg : Modal of Code Generator
 */
public class McgSharedDialogs {

	public static Integer actDialogIdSelection() {
		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextFieldInteger, "Id Değerini Giriniz");
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
		FxWindow.nodeWindow(null, modEntityListCont, null, null, null);
		return modEntityListCont;
	}

	public static String showDialogSelectTable(Jdbi jdbi, String txMessage) {

		FxSimpleTableWindowCont<EntSqlTable> fxSimpleTableWindowCont = new FxSimpleTableWindowCont();
		fxSimpleTableWindowCont.initCont();
		fxSimpleTableWindowCont.getFxTableView().setEnableLocalFilterEditor(true);
		fxSimpleTableWindowCont.activateSelectButton();
		if (!FiString.isEmpty(txMessage)) {
			fxSimpleTableWindowCont.addNoteLine(txMessage);
		}

		List<EntSqlTable> entSqlTableList = new MlcgSql().getSqlTableList(jdbi);
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
			entSqlTableList = new MlcgSql().getSqlTableListWithCount(jdbi);
			fxSimpleTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.sqlBoCount());
		} else {
			entSqlTableList = new MlcgSql().getSqlTableList(jdbi);
		}

		fxSimpleTableWindowCont.setTableContent(entSqlTableList);
		fxSimpleTableWindowCont.openAsModalMain(null);

		if (fxSimpleTableWindowCont.checkClosedWithDone()) {
			return fxSimpleTableWindowCont.getFxTableView().getItemsFiCheckedAsNewListInAllElements();
		}

		return null;
	}
}
