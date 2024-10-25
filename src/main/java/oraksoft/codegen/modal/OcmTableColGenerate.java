package oraksoft.codegen.modal;

import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.ficodegen.FiCodeGen;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;

import java.io.File;
import java.util.List;

public class OcmTableColGenerate {

	public static String actExcelToFiColWithHeaderAsHeaderName() {

		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());
			return FiCodeGen.codeFiColListFromHeadersAndFields(listHeader, "Excel", listFields);

		}

		return "Excel File is not selected.";
	}

	public static String actExcelToFiColWithHeaderAsFieldNameAndHeaderName() {

		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {
			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			//List<String> listRow1 = new FiExcel().readExcelRowIndex(fileExcel, 1);
			//FiConsole.debugListObjectsToString(listHeader,getClass());
			return FiCodeGen.codeFiColListFromHeadersAndFields(listHeader, "Excel", listHeader);
		}

		return "Excel File is not selected.";
	}
}
