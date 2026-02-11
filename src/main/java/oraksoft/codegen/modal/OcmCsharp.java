package oraksoft.codegen.modal;

import oraksoft.codegen.modules.OccHomeCont;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.core.FiBool;
import ozpasyazilim.utils.core.FiCollection;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.core.FiTemplate;
import ozpasyazilim.utils.datatypes.FiKeybean;
import ozpasyazilim.utils.fidborm.FiQugen;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FiDialogMetaType;
import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.FicList;

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

        FicList fiCols = OcmFiColJava.getFiColListFromExcel();

        if (FiCollection.isEmpty(fiCols)) return;

        String templateFiColMethod = getTemplateFiColMethod();

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
            //fkbParamsFiColMethod.add("fieldMethodName", FiString.capitalizeFirstLetter(fieldName));
            fkbParamsFiColMethod.add("fieldMethodName", fieldName);
            fkbParamsFiColMethod.add("fieldName", fieldName);
            fkbParamsFiColMethod.add("fieldHeader", fiCol.getFcTxHeader());
            fkbParamsFiColMethod.add("fiColMethodBody", sbFiColMethodBody.toString());
            String txFiColMethod = FiString.substitutor(templateFiColMethod, fkbParamsFiColMethod);
            sbFiColMethodsBody.append(txFiColMethod).append("\n\n");

            if (!FiBool.isTrue(fiCol.getFcBoTransient())) {
                sbFieldColsAddition.append("\tfiColList.Add(").append(fieldName).append("());\n");
//                sbFieldColsAddition.append("\tfiColList.Add(").append(FiString.capitalizeFirstLetter(fieldName)).append("());\n");
            } else {
                sbFieldColsAdditionTrans.append("\tfiColList.Add(").append(fieldName).append("());\n");
//                sbFieldColsAdditionTrans.append("\tfiColList.Add(").append(FiString.capitalizeFirstLetter(fieldName)).append("());\n");
            }

            index++;
        }

        String tempGenTableCols = "public static FiColList GenTableCols() {\n\n" +
                "\tFiColList fiColList = new FiColList();\n\n" +
                "{{fiColsAddition}}\n" +
                "\treturn fiColList;\n" +
                "}";

        String txGenTableColsMethod = FiTemplate.replaceParams(tempGenTableCols, FiKeybean.bui().putKeyTos("fiColsAddition", sbFieldColsAddition.toString()));
        sbClassBody.append("\n").append(txGenTableColsMethod).append("\n");

        String tempGenTableColsTrans = "public static FiColList GenTableColsTrans() {\n\n" +
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

        getOccHomeCont().appendTextNewLine(txResult);

        //getGcgHome().appendTextNewLine(FiConsole.textFiCols(fiCols));
    }

    private static @NotNull String getTemplateFiColMethod() {
        return "public static FiCol {{fieldMethodName}}() {\n" +
                "\tFiCol fiCol = new FiCol(\"{{fieldName}}\", \"{{fieldHeader}}\");\n" +
                "{{fiColMethodBody}}\n" +
                "\treturn fiCol;\n" +
                "}";
    }

    private static StringBuilder genFiColMethodBodyDetailByFiCol(FiCol fiCol) {

        StringBuilder sbFiColMethodBody = new StringBuilder();

        //String fieldType = FiCodeGen.convertExcelTypeToOzColType(fiCol.getTosOrEmpty(FiColsMetaTable.fcTxFieldType()));

        if (fiCol.getColType() != null)
            sbFiColMethodBody.append(String.format("\tfiCol.fiColType = FiColType.%s;\n", fiCol.getColType().toString()));

        String fcTxIdType = fiCol.getFcTxIdType();
        //FiCodeGen.convertExcelIdentityTypeToFiColAttribute(fiCol.getTosOrEmpty(FiColsMetaTable.fcTxIdType()));

        if (!FiString.isEmpty(fcTxIdType)) {
            sbFiColMethodBody.append("\tfiCol.boKeyIdField = true;\n");
            sbFiColMethodBody.append(String.format("\tfiCol.fcTxIdType = FiIdGenerationType.%s.toString();\n", fcTxIdType));
        }

        if (FiBool.isTrue(fiCol.getFcBoTransient())) {
            sbFiColMethodBody.append("\tfiCol.oftBoTransient = true;\n");
        }

        if (fiCol.getFcLnLength() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.fcLnLength = %s;\n", fiCol.getFcLnLength().toString()));
        }

        if (FiBool.isTrue(fiCol.getBoNullable())) {
            sbFiColMethodBody.append("\tfiCol.fcBoNullable = true;\n");
        }

        if (fiCol.getFcLnPrecision() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.fcLnPrecision = %s;\n", fiCol.getFcLnPrecision().toString()));
        }

        if (fiCol.getFcLnScale() != null) {
            sbFiColMethodBody.append(String.format("\tfiCol.fcLnScale = %s;\n", fiCol.getFcLnScale().toString()));
        }

        if (FiBool.isTrue(fiCol.getFcBoUnique())) {
            sbFiColMethodBody.append("\tfiCol.fcBoUnique = true;\n");
        }

        if (FiBool.isTrue(fiCol.getFcBoUniqGro1())) {
            sbFiColMethodBody.append("\tfiCol.fcBoUniqGro1 = true;\n");
        }

        if (FiBool.isTrue(fiCol.getFcBoUtfSupport())) {
            sbFiColMethodBody.append("\tfiCol.fcBoUtfSupport = true;\n");
        }

        if (!FiString.isEmpty(fiCol.getFcTxDefValue())) {
            sbFiColMethodBody.append(String.format("\tfiCol.fcTxDefValue = \"%s\";\n", fiCol.getFcTxDefValue()));
        }

        if (FiBool.isTrue(fiCol.getBoFilterLike())) {
            sbFiColMethodBody.append("\tfiCol.fcBoFilterLike = true;\n");
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

        String templateMain =
                "public class {{classPref}}{{entityName}} : IFiTableMeta {\n" +
                        "\n" +
                        "\tpublic string GetITxTableName() {\n" +
                        "\t\treturn GetTxTableName();\n" +
                        "\t}\n\n" +
                        "\tpublic static string GetTxTableName() {\n" +
                        "\t\treturn \"{{entityName}}\";\n" +
                        "\t}\n" +
                        "\n" +
                        "public FiColList GenITableCols() {\n" +
                        "\treturn GenTableCols();\n" +
                        "}\n" +
                        "\n" +
                        "public FiColList GenITableColsTrans() {\n" +
                        "\treturn GenTableColsTrans();\n" +
                        "\t}\n" +
                        "\n" +
                        "{{classBody}}\n" +
                        "}";

        return templateMain;
    }
}
