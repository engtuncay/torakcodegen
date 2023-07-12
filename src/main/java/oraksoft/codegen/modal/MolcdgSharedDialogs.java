package oraksoft.codegen.modal;

import ozpasyazilim.utils.entitysql.EntSqlTable;
import oraksoft.codegen.modules.ModEntityListCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.mikro.metadata.metaMikro.FiColsEntegre;
import ozpasyazilim.utils.core.FiBoolean;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.fxwindow.FxSimpleTableWindowCont;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FxSimpleDialogMetaType;

import java.util.List;

public class MolcdgSharedDialogs {

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
		FxDialogShow.build().nodeModalByIFxSimpleCont(null, modEntityListCont, null, null, null);
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

		List<EntSqlTable> entSqlTableList = new MolcdgSql().getSqlTableList(jdbi);
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
		if(FiBoolean.isTrue(boMulti)){
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

		if (FiBoolean.isTrue(boShowCount)) {
			entSqlTableList = new MolcdgSql().getSqlTableListWithCount(jdbi);
			fxSimpleTableWindowCont.getFxTableView().addFiColsAuto(FiColsEntegre.sqlBoCount());
		} else {
			entSqlTableList = new MolcdgSql().getSqlTableList(jdbi);
		}

		fxSimpleTableWindowCont.setTableContent(entSqlTableList);
		fxSimpleTableWindowCont.openAsModalMain(null);

		if (fxSimpleTableWindowCont.checkClosedWithDone()) {
			return fxSimpleTableWindowCont.getFxTableView().getItemsCheckedByBoSelectAsListInAllElements();
		}

		return null;
	}
}
