package oraksoft.codegen.modal;

import oraksoft.codegen.modules.OccHomeCont;
import org.jetbrains.annotations.NotNull;
import ozpasyazilim.utils.core.*;
import ozpasyazilim.utils.datatypes.FiKeybean;
import ozpasyazilim.utils.datatypes.FiKeytext;
import ozpasyazilim.utils.datatypes.FiListKeyString;
import ozpasyazilim.utils.ficodegen.FiCodeGen;
import ozpasyazilim.utils.fidbanno.FiIdGenerationType;
import ozpasyazilim.utils.fidborm.FiColsMetaTable;
import ozpasyazilim.utils.fidborm.FiReflectClass;
import ozpasyazilim.utils.fidborm.FiField;
import ozpasyazilim.utils.gui.fxcomponents.FiFileGui;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.FicList;
import ozpasyazilim.utils.table.OzColType;

import java.io.File;
import java.util.List;

/**
 * Mcg : Modal of Code Generator Project
 * <p>
 * FiCol metod tanımları üreten metodlar
 */
public class OcmFiColJava {

    OccHomeCont gcgHome;

    public OcmFiColJava(OccHomeCont gcgHome) {
        this.gcgHome = gcgHome;
    }

    public static OcmFiColJava bui(OccHomeCont occHomeCont) {
        return new OcmFiColJava(occHomeCont);
    }

    public OccHomeCont getGcgHome() {
        return gcgHome;
    }

    public void setGcgHome(OccHomeCont gcgHome) {
        this.gcgHome = gcgHome;
    }

    // Metodlar

    public static void actExcelToFiTableColViaMethods(OccHomeCont occHomeCont) {

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

            occHomeCont.appendTextNewLine(FiCodeGen.codeFiColsMethodsFromHeaderAndFieldName(listHeader, "Excel", listFields, fieldPrefix));

        }

    }

    public static void actFiColsClassJavaByExcelRowHeader(OccHomeCont occHomeCont) {

        //Loghelper.get(MocFiCol.class).debug("actExcelToFiColsMethodWay1");

        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

        if (fileExcel == null) return;

        List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
        List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
        //FiConsole.debugListObjectsToString(listHeader,getClass());
        //Class entclazz = getGcgHome().getClassSelected();

        String className = "";

        FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buiTextFieldDialog("Lütfen sınıf ismini yazınız");

        if (fxSimpleDialog.isClosedWithOk()) {
            className = fxSimpleDialog.getTxValue();
        }

        //List<FiField> listFields = FiFieldUtil.getListFieldsWoutStatic(entclazz, true);

        String templateMain = getTemplateFiColsClass();

        FiKeybean fkbMain = new FiKeybean();
        fkbMain.add("className",  className); // "FiCols" +

        String templateFiColMethod = "\tpublic static FiCol {{fieldName}}() {\n" +
                "       FiCol fiCol = new FiCol(\"{{fieldName}}\", \"{{fieldHeader}}\");\n" +
                "{{fieldTypeComment}} fiCol.buiColType(OzColType.{{fieldType}});\n" +
                "       return fiCol;\n" +
                "   }";

        StringBuilder sbClassBody = new StringBuilder();
        StringBuilder sbFiColMethods = new StringBuilder();
        StringBuilder sbFieldColsAddition = new StringBuilder();

        for (int index = 0; index < listFields.size(); index++) {

            String fieldName = listFields.get(index);
            String headerName = listHeader.get(index);

            if(FiString.isEmpty(fieldName))continue;

            FiKeybean fkbFiColMethod = new FiKeybean();
            fkbFiColMethod.add("fieldName", fieldName);
            fkbFiColMethod.add("fieldType", ""); //field.getClassNameSimple()
            fkbFiColMethod.add("fieldTypeComment", "//"); //field.getClassNameSimple()
            fkbFiColMethod.add("fieldHeader", headerName);

            String txFiColMethod = FiString.substitutor(templateFiColMethod, fkbFiColMethod);
            sbFiColMethods.append(txFiColMethod).append("\n\n");

            sbFieldColsAddition.append("fiColList.add(").append(fieldName).append("());\n");
        }

        String tempGenTableCols = "public static FiColList genTableCols() {\n" +
                "\n" +
                "        FiColList fiColList = new FiColList();\n" +
                "\n" +
                "        {{fiColsAddition}}\n" +
                "\n" +
                "        return fiColList;\n" +
                "\n" +
                "    }";

        String txGenTableColsMethod = FiTemplate.replaceParams(tempGenTableCols, FiKeybean.bui().putKeyTos("fiColsAddition", sbFieldColsAddition.toString()));
        sbClassBody.append("\n").append(txGenTableColsMethod);

        sbClassBody.append("\n\n");
        sbClassBody.append(sbFiColMethods.toString());

        fkbMain.add("classBody", sbClassBody.toString());
        String txResult = FiString.substitutor(templateMain, fkbMain);

        occHomeCont.appendTextNewLine(txResult);

        //gocHomeWindowCont.appendTextNewLine(FiCodeHelper.codeFiColsMethodsFromHeaderAndFieldNameForExcel(listHeader, listFields));
    }

    public static @NotNull String getTemplateFiColsClass() {

        String templateMain = "import ozpasyazilim.utils.table.FiCol;\n" +
                "import ozpasyazilim.utils.table.OzColType;\n" +
                "import ozpasyazilim.utils.table.FiColList;\n" +
                "import ozpasyazilim.utils.fidbanno.FiIdGenerationType;\n" +
                "\n" +
                "public class {{className}} {\n" +
                "\n" +
                "{{classBody}}\n" +
                "}";
        return templateMain;

    }

    public static @NotNull String getTemplateFiColsClassWithInterface() {

        String templateMain = "import ozpasyazilim.utils.table.FiCol;\n" +
                "import ozpasyazilim.utils.table.OzColType;\n" +
                "import ozpasyazilim.utils.table.FiColList;\n" +
                "import ozpasyazilim.utils.fidbanno.FiIdGenerationType;\nimport ozpasyazilim.utils.fidborm.IFiTableMeta;\n" +
                "\n" +
                "public class {{classPref}}{{entityName}} implements IFiTableMeta {\n" +
                "\n" +
                "\tpublic String getITxTableName() {\n" +
                "\t\treturn getTxTableName();\n" +
                "\t}\n\n" +
                "\tpublic static String getTxTableName() {\n" +
                "\t\treturn \"{{entityName}}\";\n" +
                "\t}\n" +
                "\n" +
                "public FiColList genITableCols() {\n" +
                "\treturn genTableCols();\n" +
                "}\n" +
                "\n" +
                "public FiColList genITableColsTrans() {\n" +
                "\treturn genTableColsTrans();\n" +
                "\t}\n" +
                "\n" +
                "{{classBody}}\n" +
                "}";

        return templateMain;
    }

    public static void actGenFiColListByExcel(OccHomeCont occHomeCont) {

        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

        if (fileExcel != null) {

            List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);
            List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
            //FiConsole.debugListObjectsToString(listHeader,getClass());
            occHomeCont.appendTextNewLine(FiCodeGen.codeFiColListFromHeaderAndFieldNameByFiColsMikroWay(listHeader, listFields));
        }

    }

    public static void codeFiColsMethodsByClass1(OccHomeCont modHome) {

        if (!modHome.checkClassChoose()) return;

        Class entclazz = modHome.getClassSelected1();

        List<FiField> listFields = FiReflectClass.getListFieldsWoutStatic(entclazz, true);

        StringBuilder query = new StringBuilder();

        StringBuilder colList = new StringBuilder();

        int index = 0;
        for (FiField field : listFields) {

            String fieldName = field.getFcTxDbField();

            String fieldGen = String.format("\npublic static FiCol %s() {" +
                    "\n\tFiCol fiCol = new FiCol(\"%s\", \"%s\");", fieldName, fieldName, FiString.orEmpty(field.getFcTxHeader()));

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
    public void actFiColsClassByClass() {

        if (!getGcgHome().checkClassChoose()) return;

        Class entclazz = getGcgHome().getClassSelected1();
        List<FiField> listFields = FiReflectClass.getListFieldsWoutStatic(entclazz, true);

        String templateMain = getTemplateFiColsClass();

        FiKeybean fkbParamsMain = new FiKeybean();
        fkbParamsMain.add("className", "FiCols" + entclazz.getSimpleName());

        String templateFiColMethod = "\tpublic static FiCol {{fieldName}}() {\n" +
                "       FiCol fiCol = new FiCol(\"{{fieldName}}\", \"{{fieldHeader}}\");\n" +
                "       fiCol.buiColType(OzColType.{{fieldType}});\n" +
                "       return fiCol;\n" +
                "   }";

        StringBuilder sbClassBody = new StringBuilder();
        int index = 0;
        for (FiField field : listFields) {

            String fieldName = field.getFcTxDbField();

            FiKeybean fkbParamsFiColMethod = new FiKeybean();
            fkbParamsFiColMethod.add("fieldName", fieldName);
            fkbParamsFiColMethod.add("fieldType", field.getClassNameSimple());
            fkbParamsFiColMethod.add("fieldHeader", FiString.orEmpty(field.getFcTxHeader()));

            String txFiColMethod = FiString.substitutor(templateFiColMethod, fkbParamsFiColMethod);
            sbClassBody.append(txFiColMethod).append("\n\n");

            index++;
        }

        fkbParamsMain.add("classBody", sbClassBody.toString());
        String txResult = FiString.substitutor(templateMain, fkbParamsMain);

        getGcgHome().appendTextNewLine(txResult);
    }

    /**
     * Excel Detaylı Alan Listesi FiCol Üretme Sınıfına Dönüştürülür
     */
    public void actGenFiColListByExcel() {

        FicList fiCols = getFiColListFromExcel();

        if (FiCollection.isEmpty(fiCols)) return;

        String templateFiColMethod = "public static FiCol {{fieldName}}() {\n" +
                "\tFiCol fiCol = new FiCol(\"{{fieldName}}\", \"{{fieldHeader}}\");\n" +
                "{{fiColMethodBody}}\n" +
                "\treturn fiCol;\n" +
                "}";

        //fiCol.buiColType(OzColType.{{fieldType}});
        StringBuilder sbClassBody = new StringBuilder();
        StringBuilder sbFiColMethodsBody = new StringBuilder();
        int index = 0;

        StringBuilder sbFieldColsAddition = new StringBuilder();
        StringBuilder sbFieldColsAdditionTrans = new StringBuilder();

        for (FiCol fiCol : fiCols) {

            StringBuilder sbFiColMethodBody = genFiColMethodBodyDetailByFiCol(fiCol);

            FiKeybean fkbParamsFiColMethod = new FiKeybean();
            String fieldName = fiCol.getFcTxFieldName();
            fkbParamsFiColMethod.add("fieldName", fieldName);
            fkbParamsFiColMethod.add("fieldHeader", fiCol.getFcTxHeader());
            fkbParamsFiColMethod.add("fiColMethodBody", sbFiColMethodBody.toString());
            String txFiColMethod = FiString.substitutor(templateFiColMethod, fkbParamsFiColMethod);
            sbFiColMethodsBody.append(txFiColMethod).append("\n\n");

            if (!FiBool.isTrue(fiCol.getFcBoTransient())) {
                sbFieldColsAddition.append("\tfiColList.add(").append(fieldName).append("());\n");
            } else {
                sbFieldColsAdditionTrans.append("\tfiColList.add(").append(fieldName).append("());\n");
            }

            index++;
        }

        String tempGenTableCols = "public static FiColList genTableCols() {\n\n" +
                "\tFiColList fiColList = new FiColList();\n\n" +
                "{{fiColsAddition}}\n" +
                "\treturn fiColList;\n" +
                "}";

        String txGenTableColsMethod = FiTemplate.replaceParams(tempGenTableCols, FiKeybean.bui().putKeyTos("fiColsAddition", sbFieldColsAddition.toString()));
        sbClassBody.append("\n").append(txGenTableColsMethod).append("\n");

        String tempGenTableColsTrans = "public static FiColList genTableColsTrans() {\n\n" +
                "\tFiColList fiColList = new FiColList();\n\n" +
                "{{fiColsAddition}}\n" +
                "\treturn fiColList;\n" +
                "}";

        String txGenTableColsMethodTrans = FiTemplate.replaceParams(tempGenTableColsTrans
                , FiKeybean.bui().putKeyTos("fiColsAddition", sbFieldColsAdditionTrans.toString()));
        sbClassBody.append("\n").append(txGenTableColsMethodTrans).append("\n");


        sbClassBody.append("\n").append(sbFiColMethodsBody.toString()).append("\n");

        String classPref = "FiCols";
        //FIXME entity name çekilecek
        String txEntityName = fiCols.get(0).getFcTxEntityName(); //fikeysExcelFiCols.get(0).getTosOrEmpty(FiColsMetaTable.fcTxEntityName());

        FiKeybean fkbParamsMain = new FiKeybean();
        fkbParamsMain.add("classPref", classPref);
        fkbParamsMain.add("entityName", txEntityName);
        fkbParamsMain.add("classBody", sbClassBody.toString());

        String templateMain = getTemplateFiColsClassWithInterface();
        String txResult = FiTemplate.replaceParams(templateMain, fkbParamsMain);

        getGcgHome().appendTextNewLine(txResult);

        //getGcgHome().appendTextNewLine(FiConsole.textFiCols(fiCols));
    }

    public static FicList getFiColListFromExcel() {

        File fileExcel = FiFileGui.actFileChooserForExcelXlsxFromDesktop();

        if (fileExcel == null) return null;

        //Loghelper.get(McgExcel.class).debug("Excel Dosyası Seçildi");

        FiListKeyString fikeysExcelFiCols = genFksFiColDetailsFromExcel(fileExcel);

        //FiConsole.logFiListKeyString(fikeysExcelFiCols);

        //Class entclazz = getGcgHome().getClassSelected();
        //List<FiField> listFields = FiFieldUtil.getListFieldsWoutStatic(entclazz, true);
        FicList fiCols = new FicList();

        for (FiKeytext fikField : fikeysExcelFiCols) {
            fiCols.add(genFiColFromExcelFks(fikField));
        }

        return fiCols;
    }

    private static StringBuilder genFiColMethodBodyDetailByFiCol(FiCol fiCol) {

        StringBuilder sbFiColMethodBody = new StringBuilder();

        //String fieldType = FiCodeGen.convertExcelTypeToOzColType(fiCol.getTosOrEmpty(FiColsMetaTable.fcTxFieldType()));

        if (fiCol.getColType() != null)
            sbFiColMethodBody.append(String.format("\tfiCol.buiColType(OzColType.%s);\n", fiCol.getColType().toString()));

        String ofiTxIdType = fiCol.getFcTxIdType();
        //FiCodeGen.convertExcelIdentityTypeToFiColAttribute(fiCol.getTosOrEmpty(FiColsMetaTable.ofiTxIdType()));

        if (!FiString.isEmpty(ofiTxIdType)) {
            sbFiColMethodBody.append("\tfiCol.setBoKeyIdField(true);\n");
            sbFiColMethodBody.append(String.format("\tfiCol.setOfiTxIdType(FiIdGenerationType.%s.toString());\n", ofiTxIdType));
        }

        if (FiBool.isTrue(fiCol.getFcBoTransient())) {
            sbFiColMethodBody.append("\tfiCol.setOftBoTransient(true);\n");
        }

        if (fiCol.getFcLnLength() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.setFcLnLength(%s);\n", fiCol.getFcLnLength().toString()));
        }

        if (FiBool.isTrue(fiCol.getBoNullable())) {
            sbFiColMethodBody.append("\tfiCol.setFcBoNullable(true);\n");
        }

        if (fiCol.getFcLnPrecision() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.setFcLnPrecision(%s);\n", fiCol.getFcLnPrecision().toString()));
        }

        if (fiCol.getFcLnScale() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.setFcLnScale(%s);\n", fiCol.getFcLnScale().toString()));
        }

        if (FiBool.isTrue(fiCol.getFcBoUnique())) {
            sbFiColMethodBody.append("\tfiCol.setFcBoUnique(true);\n");
        }

        if (FiBool.isTrue(fiCol.getFcBoUniqGro1())) {
            sbFiColMethodBody.append("\tfiCol.setFcBoUniqGro1(true);\n");
        }

        if (FiBool.isTrue(fiCol.getFcBoUtfSupport())) {
            sbFiColMethodBody.append("\tfiCol.setFcBoUtfSupport(true);\n");
        }

        if (!FiString.isEmpty(fiCol.getFcTxDefValue())) {
            sbFiColMethodBody.append(String.format("\tfiCol.setFcTxDefValue(\"%s\");\n", fiCol.getFcTxDefValue()));
        }

        if (FiBool.isTrue(fiCol.getBoFilterLike())) {
            sbFiColMethodBody.append("\tfiCol.setFcBoFilterLike(true);\n");
        }

        // ofcTxCollation	ofcTxTypeName

        return sbFiColMethodBody;
    }

    private static FiCol genFiColFromExcelFks(FiKeytext fikField) {

        //String fieldType = FiCodeGen.convertExcelTypeToOzColType(fikField.getTosOrEmpty(FiColsMetaTable.fcTxFieldType()));

        FiCol fiCol = new FiCol();

        fiCol.setFcTxFieldName(fikField.getTosOrEmpty(FiColsMetaTable.fcTxFieldName()));
        fiCol.setFcTxHeader(fikField.getTosOrEmpty(FiColsMetaTable.fcTxHeader()));
        fiCol.setFcTxEntityName(fikField.getTosOrEmpty(FiColsMetaTable.fcTxEntityName()));

        String txFieldType = fikField.getTosOrEmpty(FiColsMetaTable.fcTxFieldType());

        if (!FiString.isEmpty(txFieldType)) {

            if (txFieldType.equalsIgnoreCase("int")) {
                fiCol.setColType(OzColType.Integer);
            } else if (txFieldType.equalsIgnoreCase("string")) {
                fiCol.setColType(OzColType.String);
            } else if (txFieldType.equalsIgnoreCase("bool")) {
                fiCol.setColType(OzColType.Boolean);
            } else if (txFieldType.equalsIgnoreCase("tint")) {
                fiCol.setColType(OzColType.Integer);
                //FIXME tint olduğunu belirten tanımlama
            } else if (txFieldType.equalsIgnoreCase("double")) {
                fiCol.setColType(OzColType.Double);
            }

        }


        String ofiTxIdType = FiCodeGen.convertExcelIdentityTypeToFiColAttribute(fikField.getTosOrEmpty(FiColsMetaTable.ofiTxIdType()));

        if (!FiString.isEmpty(ofiTxIdType)) {
            fiCol.setBoKeyIdField(true);

            if (ofiTxIdType.equals(FiIdGenerationType.identity.toString())) {
                fiCol.setFcTxIdType(FiIdGenerationType.identity.toString());
            }

        }

        Boolean fcBoTransient = FiBool.convertExcelTxBool(fikField.getTosOrEmpty(FiColsMetaTable.fcBoTransient()));

        if (FiBool.isTrue(fcBoTransient)) {
            fiCol.setFcBoTransient(true);
        }

        Integer lnLength = FiNumber.convertStringToInteger(fikField.getTosOrEmpty(FiColsMetaTable.fcLnLength()));

        if (lnLength != null) {
            fiCol.setFcLnLength(lnLength);
        }

        Boolean prmOfcBoNullable = FiBool.convertExcelTxBool(fikField.getTosOrEmpty(FiColsMetaTable.fcBoNullable()));

        if (FiBool.isTrue(prmOfcBoNullable)) {
            fiCol.setFcBoNullable(true);
        }

        Integer lnPrecision = FiNumber.convertStringToInteger(fikField.getTosOrEmpty(FiColsMetaTable.fcLnPrecision()));

        if (lnPrecision != null) {
            fiCol.setFcLnPrecision(lnPrecision);
        }

        Integer lnScale = FiNumber.convertStringToInteger(fikField.getTosOrEmpty(FiColsMetaTable.fcLnScale()));

        if (lnScale != null) {
            fiCol.setFcLnScale(lnScale);
        }

        Boolean boUnique = FiBool.convertExcelTxBool(fikField.getTosOrEmpty(FiColsMetaTable.fcBoUnique()));

        if (FiBool.isTrue(boUnique)) {
            fiCol.setFcBoUnique(true);
        }

        Boolean boUnique1 = FiBool.convertExcelTxBool(fikField.getTosOrEmpty(FiColsMetaTable.fcBoUniqGro1()));

        if (FiBool.isTrue(boUnique1)) {
            fiCol.setFcBoUniqGro1(true);
        }

        Boolean boUtfSupport = FiBool.convertExcelTxBool(fikField.getTosOrEmpty(FiColsMetaTable.fcBoUtfSupport()));

        if (FiBool.isTrue(boUtfSupport)) {
            fiCol.setFcBoUtfSupport(true);
        }

        String txDefValue = fikField.getTosOrEmpty(FiColsMetaTable.fcTxDefValue());

        if (!FiString.isEmpty(txDefValue)) {
            fiCol.setFcTxDefValue(txDefValue);
        }

        Boolean boFilterLike = FiBool.convertExcelTxBool(fikField.getTosOrEmpty(FiColsMetaTable.fcBoFilterLike()));

        if (FiBool.isTrue(boFilterLike)) {
            fiCol.setBoFilterLike(true);
        }

        // ofcTxCollation	ofcTxTypeName

        return fiCol;
    }

    public static FiListKeyString genFksFiColDetailsFromExcel(File fileExcel) {

        FicList fiCols = getFiColsFields();

        FiListKeyString fiKeyStrings = new FiExcel().readExcelFileAsMap(fileExcel, fiCols);

        return fiKeyStrings;
    }

    public static FicList getFiColsFields() {
        FicList fiCols = FicList.bui()
                .buiAdd(FiColsMetaTable.fcTxEntityName().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxFieldName().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxFieldType().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxHeader().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.ofiTxIdType().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcBoTransient().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcLnLength().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcBoNullable().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcLnPrecision().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcLnScale().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcBoUnique().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcBoUniqGro1().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxDefValue().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxDefValue().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxCollation().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcTxTypeName().buiSynFieldToHeader())
                .buiAdd(FiColsMetaTable.fcBoFilterLike().buiSynFieldToHeader());
        return fiCols;
    }

}

