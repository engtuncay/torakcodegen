package oraksoft.codegen.modal;

import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.datatypes.FiListKeyString;
import ozpasyazilim.utils.ficodegen.FiCodeGen;
import ozpasyazilim.utils.fidborm.FiQugen;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.table.FiColList;

import java.io.File;
import java.util.List;

public class McgExcel {

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

            return FiCodeGen.codeEntityClass(listHeader, listFields, className, fieldPrefix);
        }

        return "";
    }

    public static String actBtnCodeGenFiTableColFromExcel() {
        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();
        if (fileExcel != null) {
            List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
            //FiConsole.debugListObjectsToString(listHeader,getClass());
            return FiQugen.codeFiTableColsFromHeader(listHeader, "Excel");
        }
        return "";
    }

    public static FiListKeyString actExceldenTarihAlanlariniOkuForSqlTransfer() {

        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

        if (fileExcel != null) {

            //Loghelper.get(McgExcel.class).debug("Excel Dosyası Seçildi");

            FiColList fiCols = FiColList.bui()
                    .buiAdd("txDateField", "Tarih Alanı")
                    .buiAdd("txTable", "Tablo İsmi");

            FiListKeyString fiListKeyString = new FiExcel().readExcelFileAsMap(fileExcel, fiCols);
            return fiListKeyString;
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