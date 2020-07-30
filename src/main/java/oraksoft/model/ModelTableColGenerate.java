package oraksoft.model;

import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.fidborm.FiCodeHelper;
import ozpasyazilim.utils.gui.fxcomponents.FiFileHelper;

import java.io.File;
import java.util.List;

public class ModelTableColGenerate {

	public static String actExcelToFiTableColWithFieldName() {

		File fileExcel = new FiFileHelper().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());
			return FiCodeHelper.codeFiTableColsFromHeaderAndFieldName(listHeader, "Excel", listFields);

		}

		return "Excel File is not selected.";
	}
}
