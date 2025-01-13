package oraksoft.codegen.modal;

import oraksoft.codegen.modules.OccHomeCont;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.core.FiBool;
import ozpasyazilim.utils.core.FiCollection;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.core.FiTemplate;
import ozpasyazilim.utils.datatypes.FiKeyBean;
import ozpasyazilim.utils.fidborm.FiQugen;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FiDialogMetaType;
import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.FiColList;

public class OcmCsharp {

    OccHomeCont occHomeCont;

    public OcmCsharp(OccHomeCont occHomeCont) {
        this.occHomeCont = occHomeCont;
    }

    public OcmCsharp() {
    }

    public static OcmCsharp bui(OccHomeCont occHomeCont) {
        OcmCsharp ocmCsharp = new OcmCsharp(occHomeCont);
        return ocmCsharp;
    }

    public void actCsharpSinifOlusturma(OccHomeCont modHome) {

        Jdbi activeServerJdbi = modHome.getAndSetupActiveServerJdbi();

        if (activeServerJdbi != null) {
            FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
            //System.out.println("Connected");
        } else {
            FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
            return;
        }

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextField, "Tablo Adını Giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {

            FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
            FiQugen fiqugen = new FiQugen();

            System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
            String entityCode = FiQugen.codeEntityClassCsharp(fxSimpleDialog.getTxValue(), modHome.getAndSetupActiveServerJdbi());

            if (!FiString.isEmpty(entityCode)) {
                modHome.getTxaMainOutput().appendText(entityCode);
            } else {
                modHome.getTxaMainOutput().appendText("N/A");
            }

        }


    }

    public void actGenFiColListByExcel() {

        FiColList fiCols = OcmFiColJava.getFiColListFromExcel();

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

            FiKeyBean fkbParamsFiColMethod = new FiKeyBean();
            String fieldName = fiCol.getOfcTxFieldName();
            fkbParamsFiColMethod.add("fieldName", fieldName);
            fkbParamsFiColMethod.add("fieldHeader", fiCol.getOfcTxHeader());
            fkbParamsFiColMethod.add("fiColMethodBody", sbFiColMethodBody.toString());
            String txFiColMethod = FiString.substitutor(templateFiColMethod, fkbParamsFiColMethod);
            sbFiColMethodsBody.append(txFiColMethod).append("\n\n");

            if (!FiBool.isTrue(fiCol.getOftBoTransient())) {
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

        String txGenTableColsMethod = FiTemplate.replaceParams(tempGenTableCols, FiKeyBean.bui().putKeyTos("fiColsAddition", sbFieldColsAddition.toString()));
        sbClassBody.append("\n").append(txGenTableColsMethod).append("\n");

        String tempGenTableColsTrans = "public static FiColList genTableColsTrans() {\n\n" +
                "\tFiColList fiColList = new FiColList();\n\n" +
                "{{fiColsAddition}}\n" +
                "\treturn fiColList;\n" +
                "}";

        String txGenTableColsMethodTrans = FiTemplate.replaceParams(tempGenTableColsTrans
                , FiKeyBean.bui().putKeyTos("fiColsAddition", sbFieldColsAdditionTrans.toString()));
        sbClassBody.append("\n").append(txGenTableColsMethodTrans).append("\n");


        sbClassBody.append("\n").append(sbFiColMethodsBody.toString()).append("\n");

        String classPref = "FiCols";
        //FIXME entity name çekilecek
        String txEntityName = fiCols.get(0).getOfcTxEntityName(); //fikeysExcelFiCols.get(0).getTosOrEmpty(FiColsMetaTable.ofcTxEntityName());

        FiKeyBean fkbParamsMain = new FiKeyBean();
        fkbParamsMain.add("classPref", classPref);
        fkbParamsMain.add("entityName", txEntityName);
        fkbParamsMain.add("classBody", sbClassBody.toString());

        String templateMain = getTemplateFiColsClassWithInterface();
        String txResult = FiTemplate.replaceParams(templateMain, fkbParamsMain);

        getOccHomeCont().appendTextNewLine(txResult);

        //getGcgHome().appendTextNewLine(FiConsole.textFiCols(fiCols));
    }

    private static StringBuilder genFiColMethodBodyDetailByFiCol(FiCol fiCol) {

        StringBuilder sbFiColMethodBody = new StringBuilder();

        //String fieldType = FiCodeGen.convertExcelTypeToOzColType(fiCol.getTosOrEmpty(FiColsMetaTable.ofcTxFieldType()));

        if (fiCol.getColType() != null)
            sbFiColMethodBody.append(String.format("\tfiCol.buiColType(OzColType.%s);\n", fiCol.getColType().toString()));

        String ofiTxIdType = fiCol.getOfiTxIdType();
        //FiCodeGen.convertExcelIdentityTypeToFiColAttribute(fiCol.getTosOrEmpty(FiColsMetaTable.ofiTxIdType()));

        if (!FiString.isEmpty(ofiTxIdType)) {
            sbFiColMethodBody.append("\tfiCol.setBoKeyIdField(true);\n");
            sbFiColMethodBody.append(String.format("\tfiCol.setOfiTxIdType(FiIdGenerationType.%s.toString());\n", ofiTxIdType));
        }

        if (FiBool.isTrue(fiCol.getOftBoTransient())) {
            sbFiColMethodBody.append("\tfiCol.setOftBoTransient(true);\n");
        }

        if (fiCol.getOfcLnLength() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.setOfcLnLength(%s);\n", fiCol.getOfcLnLength().toString()));
        }

        if (FiBool.isTrue(fiCol.getBoNullable())) {
            sbFiColMethodBody.append("\tfiCol.setOfcBoNullable(true);\n");
        }

        if (fiCol.getOfcLnPrecision() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.setOfcLnPrecision(%s);\n", fiCol.getOfcLnPrecision().toString()));
        }

        if (fiCol.getOfcLnScale() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.setOfcLnScale(%s);\n", fiCol.getOfcLnScale().toString()));
        }

        if (FiBool.isTrue(fiCol.getOfcBoUnique())) {
            sbFiColMethodBody.append("\tfiCol.setOfcBoUnique(true);\n");
        }

        if (FiBool.isTrue(fiCol.getOfcBoUniqGro1())) {
            sbFiColMethodBody.append("\tfiCol.setOfcBoUniqGro1(true);\n");
        }

        if (FiBool.isTrue(fiCol.getOfcBoUtfSupport())) {
            sbFiColMethodBody.append("\tfiCol.setOfcBoUtfSupport(true);\n");
        }

        if (!FiString.isEmpty(fiCol.getOfcTxDefValue())) {
            sbFiColMethodBody.append(String.format("\tfiCol.setOfcTxDefValue(\"%s\");\n", fiCol.getOfcTxDefValue()));
        }

        if (FiBool.isTrue(fiCol.getBoFilterLike())) {
            sbFiColMethodBody.append("\tfiCol.setOfcBoFilterLike(true);\n");
        }

        // ofcTxCollation	ofcTxTypeName

        return sbFiColMethodBody;
    }

    public OccHomeCont getOccHomeCont() {
        return occHomeCont;
    }

    public void setOccHomeCont(OccHomeCont occHomeCont) {
        this.occHomeCont = occHomeCont;
    }

    public static @NotNull String getTemplateFiColsClassWithInterface() {

        String templateMain = "import ozpasyazilim.utils.table.FiCol;\n" +
                "import ozpasyazilim.utils.table.OzColType;\n" +
                "import ozpasyazilim.utils.table.FiColList;\n" +
                "import ozpasyazilim.utils.fidbanno.FiIdGenerationType;\n" +
                "import ozpasyazilim.utils.fidborm.IFiTableMeta;\n" +
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
}
