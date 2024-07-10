package oraksoft.codegen.modal;

import oraksoft.codegen.modules.GcgHomeCodeGenerator;
import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.ficodegen.FiCodeHelper;
import ozpasyazilim.utils.fidborm.FiFieldUtil;
import ozpasyazilim.utils.fidborm.FiField;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;

import java.io.File;
import java.util.List;

/**
 * Mcg : Model Code Generator
 * <p>
 * FiCol metod tanımları üreten metodlar
 */
public class McgFiCol {


    public static void actExcelToFiTableColViaMethods(GcgHomeCodeGenerator gcgHomeCodeGenerator) {

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

            gcgHomeCodeGenerator.appendTextNewLine(FiCodeHelper.codeFiColsMethodsFromHeaderAndFieldName(listHeader, "Excel", listFields, fieldPrefix));

        }

    }

    public static void actExcelToFiColsMethodWay1(GcgHomeCodeGenerator gcgHomeCodeGenerator) {

        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

        if (fileExcel != null) {

            List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
            List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
            //FiConsole.debugListObjectsToString(listHeader,getClass());
            gcgHomeCodeGenerator.appendTextNewLine(FiCodeHelper.codeFiColsMethodsFromHeaderAndFieldNameForExcel(listHeader, listFields));
        }

    }

    public static void actExcelToFiColsListByFiColsMikro(GcgHomeCodeGenerator gcgHomeCodeGenerator) {

        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

        if (fileExcel != null) {

            List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
            List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
            //FiConsole.debugListObjectsToString(listHeader,getClass());
            gcgHomeCodeGenerator.appendTextNewLine(FiCodeHelper.codeFiColListFromHeaderAndFieldNameByFiColsMikroWay(listHeader, listFields));
        }

    }

    public static void codeFiColsMethodsByClass1(GcgHomeCodeGenerator modHome) {

        if (!modHome.checkClassChoose()) return;

        Class entclazz = modHome.getClassSelected();

        List<FiField> listFields = FiFieldUtil.getListFieldsWoutStatic(entclazz, true);

        StringBuilder query = new StringBuilder();

        StringBuilder colList = new StringBuilder();

        int index = 0;
        for (FiField field : listFields) {

            String fieldName = field.getDbFieldName();

            String fieldGen = String.format("\npublic static FiCol %s() {" +
                    "\n\tFiCol fiCol = new FiCol(\"%s\", \"%s\");", fieldName, fieldName, FiString.orEmpty(field.getLabel()));

            String simpleType = field.getClassNameSimple();

            fieldGen = fieldGen + String.format("\n\tfiCol.buiColType(OzColType.%s);", simpleType);

            //field.getClassNameSimpleAsOzColType();

            fieldGen = fieldGen + "\n\treturn fiCol;\n\t}\n";

            query.append(fieldGen);

            colList.append(String.format("\tlistTableCols.add(FiTableColFactory.%s());\n", fieldName));

            index++;
        }


        String listColsTemplate = String.format("\n\npublic List<FiCol> genCols%s(){\n" +
                "\n" +
                "\tList<FiCol> listTableCols = new ArrayList<>();\n" +
                "\n" +
                "%s" +
                "\t\n" +
                "\treturn listTableCols;\n" +
                "\t\n" +
                "\t}", entclazz.getSimpleName(), colList.toString());


        modHome.appendTextNewLine(query.toString() + listColsTemplate);

        //return query.toString() + listColsTemplate;

    }

}
