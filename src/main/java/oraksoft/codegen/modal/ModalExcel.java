package oraksoft.codegen.modal;

import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.fidborm.FiCodeHelper;
import ozpasyazilim.utils.fidborm.FiQueryGenerator;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialog;

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
			FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buildTextFieldDialog("Lütfen sınıf ismini yazınız");

			if (fxSimpleDialog.isClosedWithOk()) {
				className = fxSimpleDialog.getTxValue();
			}

			String fieldPrefix = "";

			FxSimpleDialog fxSimpleDialog2 = FxSimpleDialog.buildTextFieldDialog("Lütfen ön ek yazınız.(Eklenecekse)");

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