package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModHomeCodeGenerator;
import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.ficodegen.FiCodeHelper;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;

import java.io.File;
import java.util.List;

public class MolFiColGenerate {


	public static void actExcelToFiTableColViaMethods(ModHomeCodeGenerator modHomeCodeGenerator) {

		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			String fieldPrefix = "";

			FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buiTextFieldDialog("Lütfen ön ek yazınız.(Eklenecekse)");

			if (fxSimpleDialog.isClosedWithOk()) {
				fieldPrefix = fxSimpleDialog.getTxValue();
			}

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());

			modHomeCodeGenerator.appendTextNewLine(FiCodeHelper.codeFiColsMethodsFromHeaderAndFieldName(listHeader, "Excel", listFields, fieldPrefix));

		}

	}

	public static void actExcelToFiColsMethodWay1(ModHomeCodeGenerator modHomeCodeGenerator) {

		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel,0);
			//FiConsole.debugListObjectsToString(listHeader,getClass());
			modHomeCodeGenerator.appendTextNewLine(FiCodeHelper.codeFiColsMethodsFromHeaderAndFieldNameForExcel(listHeader,listFields));
		}

	}

	public static void actExcelToFiColsListByFiColsMikro(ModHomeCodeGenerator modHomeCodeGenerator) {

		File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel,0);
			//FiConsole.debugListObjectsToString(listHeader,getClass());
			modHomeCodeGenerator.appendTextNewLine(FiCodeHelper.codeFiColListFromHeaderAndFieldNameByFiColsMikroWay(listHeader,listFields));
		}

	}

}
