package oraksoft.codegen.modal;

import oraksoft.codegen.modules.GcgHomeCodeGenerator;
import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.datatypes.FiKeyBean;
import ozpasyazilim.utils.ficodegen.FiCodeHelper;
import ozpasyazilim.utils.fidborm.FiFieldUtil;
import ozpasyazilim.utils.fidborm.FiField;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;

import java.io.File;
import java.util.List;

/**
 * Mcg : Modal of Code Generator Project
 * <p>
 * FiCol metod tanımları üreten metodlar
 */
public class McgFiCol {

    GcgHomeCodeGenerator gcgHome;

    public McgFiCol(GcgHomeCodeGenerator gcgHome) {
        this.gcgHome = gcgHome;
    }

    public GcgHomeCodeGenerator getGcgHome() {
        return gcgHome;
    }

    public void setGcgHome(GcgHomeCodeGenerator gcgHome) {
        this.gcgHome = gcgHome;
    }

    // Metodlar

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


    /**
     * FiColsMikro gibi sınıfı oluşturuk, statik FiCol oluşturan metodlar barındırır.
     */
    public void codeFiColsClass() {

        if (!getGcgHome().checkClassChoose()) return;

        Class entclazz = getGcgHome().getClassSelected();
        List<FiField> listFields = FiFieldUtil.getListFieldsWoutStatic(entclazz, true);

        String templateMain = "import ozpasyazilim.utils.table.FiCol;\n" +
                "import ozpasyazilim.utils.table.OzColType;\n" +
                "\n" +
                "public class {{className}} {\n" +
                "\n" +
                "{{classBody}}\n" +
                "}";

        FiKeyBean fkbParamsMain = new FiKeyBean();
        fkbParamsMain.add("className", "FiCols" + entclazz.getSimpleName());

        String templateFiColMethod = "\tpublic static FiCol {{fieldName}}() {\n" +
                "       FiCol fiCol = new FiCol(\"{{fieldName}}\", \"{{fieldHeader}}\");\n" +
                "       fiCol.buiColType(OzColType.{{fieldType}});\n" +
                "       return fiCol;\n" +
                "   }";

        StringBuilder sbClassBody = new StringBuilder();
        int index = 0;
        for (FiField field : listFields) {

            String fieldName = field.getDbFieldName();

            FiKeyBean fkbParamsFiColMethod = new FiKeyBean();
            fkbParamsFiColMethod.add("fieldName", fieldName);
            fkbParamsFiColMethod.add("fieldType", field.getClassNameSimple());
            fkbParamsFiColMethod.add("fieldHeader", FiString.orEmpty(field.getLabel()));

            String txFiColMethod = FiString.substitutor(templateFiColMethod, fkbParamsFiColMethod);
            sbClassBody.append(txFiColMethod).append("\n\n");

            index++;
        }

        fkbParamsMain.add("classBody", sbClassBody.toString());
        String txResult = FiString.substitutor(templateMain, fkbParamsMain);

        getGcgHome().appendTextNewLine(txResult);
    }

}
