package oraksoft.codegen.modal;

import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.datatypes.FiListMapStr;
import ozpasyazilim.utils.fidborm.FiCodeHelper;
import ozpasyazilim.utils.fidborm.FiQueryGenerator;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialog;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.table.FiColList;

import java.io.File;
import java.util.List;

public class ModalExcel {

	public static String actExcelToEntity() {

		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());

			String className = "EntityName";
			FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buiTextFieldDialog("Lütfen sınıf ismini yazınız");

			if (fxSimpleDialog.isClosedWithOk()) {
				className = fxSimpleDialog.getTxValue();
			}

			String fieldPrefix = "";

			FxSimpleDialog fxSimpleDialog2 = FxSimpleDialog.buiTextFieldDialog("Lütfen ön ek yazınız.(Eklenecekse)");

			if (fxSimpleDialog2.isClosedWithOk()) {
				fieldPrefix = fxSimpleDialog2.getTxValue();
			}

			return FiCodeHelper.codeEntityClass(listHeader, listFields, className, fieldPrefix);
		}

		return "";
	}

	public static String actBtnCodeGenFiTableColFromExcel() {
		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();
		if (fileExcel != null) {
			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			//FiConsole.debugListObjectsToString(listHeader,getClass());
			return FiQueryGenerator.codeFiTableColsFromHeader(listHeader, "Excel");
		}
		return "";
	}

	public static FiListMapStr actExceldenTarihAlanlariniOkuForSqlTransfer() {
		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {
			Loghelper.get(ModalExcel.class).debug("Excel Dosyası Seçildi");
			FiColList fiCols = FiColList.build().buildAdd("txDateField", "Tarih Alanı").buildAdd("txTable", "Tablo İsmi");
			FiListMapStr fiListMapStr = new FiExcel().readExcelFileAsMap(fileExcel, fiCols);
			return fiListMapStr;
		}
		return null;
	}
}

//	private void actExcelToFiTableColWithFieldName() {
//
//		File fileExcel = new FiFileHelper().actFileChooserForExcelXlsxFromDesktop();
//
//		if (fileExcel != null) {
//
//			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
//			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
//
//
//			//FiConsole.debugListObjectsToString(listHeader,getClass());
//
//			appendTextNewLine(FiCodeHelper.codeFiTableColsFromHeaderAndFieldName(listHeader, "Excel", listFields));
//
//		}
//
//	}