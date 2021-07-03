package oraksoft.codegen.model;

import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.fidborm.FiCodeHelper;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;

import java.io.File;
import java.util.List;

public class ModelTableColGenerate {

	public static String actExcelToFiColWithHeaderAsHeaderName() {

		File fileExcel = new FiFileGui().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());
			return FiCodeHelper.codeFiColListFromHeadersAndFields(listHeader, "Excel", listFields);

		}

		return "Excel File is not selected.";
	}

	public static String actExcelToFiColWithHeaderAsFieldNameAndHeaderName() {

		File fileExcel = new FiFileGui().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
//			List<String> listRow1 = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());
			return FiCodeHelper.codeFiColListFromHeadersAndFields(listHeader, "Excel", listHeader);

		}

		return "Excel File is not selected.";
	}
}
