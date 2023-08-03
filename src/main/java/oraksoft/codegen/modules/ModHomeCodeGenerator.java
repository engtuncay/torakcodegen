package oraksoft.codegen.modules;

import javafx.stage.Stage;
import oraksoft.codegen.modal.*;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.configmisc.ServerConfig;
import oraksoft.codegen.entity.EntityClazz;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.core.*;
import ozpasyazilim.utils.datatypes.FiListKeyString;
import ozpasyazilim.utils.ficodegen.FiCodeHelper;
import ozpasyazilim.utils.fidborm.*;
import ozpasyazilim.utils.gui.components.ComboItemText;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.mvc.AbsFxSimpleCont;
import ozpasyazilim.utils.mvc.IFxSimpleCont;
import ozpasyazilim.utils.returntypes.Fdr;
import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.FiColList;
import ozpasyazilim.utils.fxwindow.FxSimpleContGen;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FxSimpleDialogMetaType;


import java.io.File;
import java.util.*;

/**
 * Code Gen Home
 */
public class ModHomeCodeGenerator extends AbsFxSimpleCont implements IFxSimpleCont {
    ModHomeCodeGenView codeGenMainView;
    Class classSelected;
    Class classSelected2;

    Stage mainStage;

    File fileSelected;
    String propPath = "appcodegen.properties";

    private MlcgSql mlcgSql;
    private MlcgHome mlcgHome;

    @Override
    public void initCont() {

        codeGenMainView = new ModHomeCodeGenView();
        codeGenMainView.initGui();

        codeGenMainView.getBtnClassSec().setOnAction(event -> actBtnClassSec());
        codeGenMainView.getBtnServer1().setOnAction(event -> actBtnSelectServer1());

        codeGenMainView.getBtnClassSec2().setOnAction(event -> actBtnClassSec2());
        codeGenMainView.getBtnServer2().setOnAction(event -> actBtnSelectServer2());

        codeGenMainView.getBtnDosyaSec().setOnAction(event -> actBtnDosyaSec());

        // modal ayarlar
        getModalHome().setFxTextArea(getModView().getTxaMainOutput());
        getModalHome().setModalSql(getModalSqlInit());
        getModalSqlInit().setModalHome(getModalHome());

        getModView().getChkVeritabandaOlustur().setOnChangeAction(aBoolean -> {
            getModalSqlInit().setEnableDbOperation(aBoolean);
            //System.out.println("isEnabDbOper:"+ aBoolean);
        });

        setupCombos();

    }

    private void actBtnDosyaSec() {

        File fileSelected = FiFile.selectFileDialogSwing("Dosya Seçiniz", null);
        setFileSelected(fileSelected);

        if (fileSelected != null) {
            getModView().getBtnDosyaSec().setText("Dosya:" + fileSelected.getName());
        }

    }

    public void setupCombos() {

        //codeGenMainView.getBtnCreateQuery().setOnAction(event -> actQueryCreate());

        // ***** Db To Code Combos
        FxMenuButton mbDbToCode = new FxMenuButton("Db To Code");

        FxMenuItem tabloToEntity = new FxMenuItem("Tablodan Entity Oluştur");
        tabloToEntity.setOnAction(event -> actTableToEntity());
        mbDbToCode.addItem(tabloToEntity);

        FxMenuItem vtEntityDoldur = new FxMenuItem("Veritabanından Entity Alanlarını Doldurma Metodu");
        vtEntityDoldur.setOnAction(event -> actEntityFillerMethodFromDb());
        mbDbToCode.addItem(vtEntityDoldur);

        FxMenuItem vtAlter = new FxMenuItem("Veritabanına eklenecek alanların Alter Sorguları");
        vtAlter.setOnAction(event -> actAlterNewFields());
        mbDbToCode.addItem(vtAlter);

        // **** Table Col Generate Combos

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Excelden FiCol List oluştur.(Excel Header As Header Name)", () -> {
            appendTextNewLine(MlcgTableColGenerate.actExcelToFiColWithHeaderAsHeaderName());
        }));

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Excelden FiCol List oluştur.(Excel Header As FieldName And Header)", () -> {
            appendTextNewLine(MlcgTableColGenerate.actExcelToFiColWithHeaderAsFieldNameAndHeaderName());
        }));

        //this::actExcelToFiTableColWithFieldName

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Excelden FiCol Üreten Metodları oluştur(1)R1:Header R2:FieldName", () -> MlcgFiCol.actExcelToFiColsMethodWay1(this)));

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Excelden FiColList Metodu oluştur", () -> MlcgFiCol.actExcelToFiTableColViaMethods(this)));

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Excelden FiColList Metodu oluştur (3)", () -> MlcgFiCol.actExcelToFiColsMethodWay1(this)));

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Excelden FiColList Metodu oluştur (FiColsMikro üzerinden)", () -> MlcgFiCol.actExcelToFiColsListByFiColsMikro(this)));

// codeGenMainView.getCmbTableColGenerate().addComboItemFi(enumComboItem.ExcelToFiTableColWithFieldName.toString()
// , "Excelden FiTableCol List oluştur.(Auto Field Name)");

        codeGenMainView.getCmbTableColGenerate().addComboItem(
                ComboItemText.buildWitAction("Sınftan FiCol Generate Method oluştur."
                        ,()-> MlcgFiCol.codeFiColsMethodsByClass1(this)));

        //actClassToFiTableColGenerate(); //ClassToFiTableColGenerator

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Sql Sorgusundan FiCol List oluştur.", this::actSqlQueryToFiTableCol));

        //actSqlQueryToFiTableCol(); //SqlQueryToFiTableCol

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Sql Sorgusundan FiTableCol Generate Method oluştur.", this::actSqlQueryToFiTableColGenerate));
        // actSqlQueryToFiTableColGenerate();//SqlQueryToFiTableColGenerator

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Alan Listesi", this::actAlanListesi));
        //actAlanListesi();//AlanListesi

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Alan Listesi By Id With Value", this::actAlanListesiByIdWithValue));
        //actAlanListesiByIdWithValue();//AlanListesiByIdWithValue

        codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItemText.buildWitAction("Alan Listesi By Cand Id With Value", this::actAlanListesiByCandIdWithValue));
        //actAlanListesiByCandIdWithValue();//AlanListByCandIdWithValue


        // **** Db Read Combos

        codeGenMainView.getCmbDbRead().addComboItem(ComboItemText.buildWitAction("Kayıt Şablon By Id", this::actDbKayitSablonById));
//		actDbKayitSablonById(); //DbKayitSablonById

        codeGenMainView.getCmbDbRead().addComboItem(ComboItemText.buildWitAction("Kayıt Şablon By Cand Ids", this::actDbKayitSablonByCandIds));
        //actDbKayitSablonByCandIds();//DbKayitSablonByCandIds


        // **** Excel Islemler Combos

        codeGenMainView.getCmbExcelIslemler().addComboItem(ComboItemText.buildWitAction("Excel'den Entity Oluştur", () -> actionResult(MlcgExcel.actExcelToEntity())));

        // enumComboItem.ExcelToEntity

        // ****** Query Generator Combos

        codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItemText.buildWitAction("Create Query", () ->
                actionResult(getModalSqlInit().createQuery(getClassSelected()))));

        codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItemText.buildWitAction("Alter Table Field(Add)", this::actAlterNewFields));

        codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItemText.buildWitAction("Clone Table Data", this::actCloneTableData));

        codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItemText.buildWitAction("Unique1 Fields", () ->
                actionResult(getModalSqlInit().queryUnique1Fields(getClassSelected()))));

        // Xml Combo

        codeGenMainView.getCmbXmlAraclar().addComboItem(ComboItemText.buildWitAction("Xml to Field List", this::actXmlToFiFieldList));

        FxMenuItem cshEntitySinifOlusturma = new FxMenuItem("Tablodan sınıf oluştur");
        codeGenMainView.getCsharpIslemler().getItems().add(cshEntitySinifOlusturma);
        cshEntitySinifOlusturma.setOnAction(event -> new MlcgCsharp().actCsharpSinifOlusturma(this));

        // Combobox Listener Ayarları

        codeGenMainView.getCmbDbRead().activateSetNullAfterAction();
        codeGenMainView.getCmbTableColGenerate().activateSetNullAfterAction();
        codeGenMainView.getCmbExcelIslemler().activateSetNullAfterAction();
        codeGenMainView.getCmbQueryGenerator().activateSetNullAfterAction();
        codeGenMainView.getCmbXmlAraclar().activateSetNullAfterAction();

        // Sql İşlemler
        FxMenuButton mbSqlTransfer = new FxMenuButton("Sql Transfer");


        FxMenuItem miTransferTarih = new FxMenuItem("Sql Kopyalama:Tarih Belirle");
        miTransferTarih.setOnAction(event -> {
            FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buiTextFieldDialog("Tarih Giriniz (yyyymmdd)");
            //fxSimpleDialog.openAsDialogSync();
            if (fxSimpleDialog.isClosedWithOk()) {
                getModalSqlInit().setTxSqlTransferDate(fxSimpleDialog.getTxValue());
                appendTextNewLine("Tarih Alanı Değeri Atandı:" + fxSimpleDialog.getTxValue());
            }
        });
        mbSqlTransfer.addItem(miTransferTarih);

        FxMenuItem miTransferTarihExcel = new FxMenuItem("Sql Kopyalama:Tarih Alanlarını Excelden Oku");
        miTransferTarihExcel.setOnAction(event -> {
            //Loghelper.get(getClass()).info("excel tarih start");
            FiListKeyString fiListKeyString = MlcgExcel.actExceldenTarihAlanlariniOkuForSqlTransfer();
            if (fiListKeyString != null) {
                fiListKeyString.clearRowsKeyIfEmpty("txDateField");
                appendTextNewLine("Tarih Alanları Okundu.");
            }
            getModalSqlInit().setListMapDateField(fiListKeyString);
            FiConsole.debugListMap(fiListKeyString, MlcgExcel.class, true);
        });
        mbSqlTransfer.addItem(miTransferTarihExcel);

        FxMenuItem miTabloKopyalama = new FxMenuItem("Tablo Kopyalama (Kaynak:Db1->Hedef:Db2)");
        miTabloKopyalama.setOnAction(event -> actionResult(getModalSqlInit().sqlTableCopySrv1ToSrv2(false)));
        mbSqlTransfer.addItem(miTabloKopyalama);

        FxMenuItem miTabloKopyalamaTarihli = new FxMenuItem("Tablo Kopyalama Tarihli (Kaynak:Db1->Hedef:Db2)");
        miTabloKopyalamaTarihli.setOnAction(event -> actionResult(getModalSqlInit().sqlTableCopySrv1ToSrv2(true)));
        mbSqlTransfer.addItem(miTabloKopyalamaTarihli);

        FxMenuItem miTransferSqlExcelOto = new FxMenuItem("Sql Kopyalama Excelden Otomatik");
        miTransferSqlExcelOto.setOnAction(event -> {
            //Loghelper.get(getClass()).info("excel tarih start");
            FiListKeyString fiListKeyString = MlcgExcel.actExceldenTarihAlanlariniOkuForSqlTransfer();
            if (fiListKeyString != null) {
                //fiListMapStr.clearRowsKeyIfEmpty("txDateField");
                appendTextNewLine("Excel Tablosu Okundu.(Sql Kopyalama Oto için)");
            }
            // FiConsole.debugListMap(fiListMapStr,ModalExcel.class,true);
            getModalSqlInit().setListMapDateField(fiListKeyString);
            getModalSqlInit().sqlTableCopySrv1ToSrv2Auto();
        });
        mbSqlTransfer.addItem(miTransferSqlExcelOto);

        // Db Export
        FxMenuButton mbDbExport = new FxMenuButton("Db Export");

        FxMenuItem miDbExportForExportTable1 = new FxMenuItem("Export Table With Insert (Wout Pks) (1)"
                , (event) -> MlcgDbExport.actTableExport1(this,true));
        mbDbExport.addItem(miDbExportForExportTable1);

        FxMenuItem miDbExportForExportTable2 = new FxMenuItem("Export Table With Insert (With Pks) (2)"
                , (event) -> MlcgDbExport.actTableExport1(this,false));
        mbDbExport.addItem(miDbExportForExportTable2);

        // Menu Layout
        getCodeGenMainView().getMigMenu().addSpan(mbDbToCode);
        getCodeGenMainView().getMigMenu().addSpan(mbSqlTransfer);
        getCodeGenMainView().getMigMenu().addSpan(mbDbExport);


    }


    /**
     * Xml To Field List
     */
    private void actXmlToFiFieldList() {
//		Loghelper.get(getClass()).debug("Xml To Field List");
        String code = MlcgXml.actXmlToFiFieldList(getFileSelected());
        appendTextNewLine(code);
    }


    private void actCloneTableData() {


    }

    // **** İşlemler

    private void actDbKayitSablonByCandIds() {
        Object form = actDialogCandIdEntityForm();

        //Loghelper.getInstance(getClass()).debug("Id:" + idNo);

        if (form != null) {

            Fdr<List> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getClassSelected()).jdSelectListByCandIds(form);

            if (fdr.getValue().size() > 0) {

                //Loghelper.getInstance(getClass()).debug("Size:"+fiDbResult.getResValue().size());

                FiExcel.saveSablonExcelByClass(this, fdr.getValue(), getClassSelected(), "ozpasentegre");

            } else {
                System.out.println("Db den Veri Okunamadı");
            }

        } else {
            FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
        }
    }

    private void actDbKayitSablonById() {
        Integer idNo = actDialogIdSelection();

        Loghelper.get(getClass()).debug("Id:" + idNo);

        if (idNo != null) {

            Fdr<List> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getClassSelected()).jdSelectListById(idNo);

            if (fdr.getValue().size() > 0) {

                FiExcel.saveSablonExcelByClass(this, fdr.getValue(), getClassSelected(), "ozpasentegre");

            } else {
                System.out.println("Db den Veri Okunamadı");
            }

        } else {
            FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
        }
    }

//	private void actCmbDbReadByCandId(ComboItem newValue) {
//
//		if (newValue == null || newValue.getValue() == null) return;
//
//		if (newValue.getValue().equals(enumComboItem.DbKayitSablonById.toString()) && checkServer() && checkClassChoose()) {
//
//			actDbKayitSablonById();
//
//		}
//
//
//		codeGenMainView.getCmbDbRead().clearSelectionFi();
//	}

    public Boolean checkServer() {

        if (getModalSqlInit().getJdbi1() == null) {
            actBtnSelectServer1();
        }

        return getModalSqlInit().getJdbi1() != null;

    }

    public Boolean checkClassChoose() {

        if (getClassSelected() == null) {
            actBtnClassSec();
        }

        if (getClassSelected() != null) {
            return true;
        } else {
            FxDialogShow.showPopError("Lütfen bir Sınıf Seçiniz.");
        }

        return false;
    }


    private void actSqlQueryToFiTableColGenerate() {

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextAreaString);
        fxSimpleDialog.setMessageHeader("Lütfen sorgu cümleciğini giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
            String sqlQuery = fxSimpleDialog.getTxValue();

            List<String> queryFields = FiQueryGenerator.getQueryFieldsAsString(sqlQuery);

            String result = FiCodeHelper.codeFiTableColsGeneraterMethods(queryFields, "", queryFields);
            appendTextNewLine(result);

        } else {
            FxDialogShow.showPopWarn("Sorgu girilmedi.");
        }

    }



    private void actSqlQueryToFiTableCol() {

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextAreaString);
        fxSimpleDialog.setMessageHeader("Lütfen sorgu cümleciğini giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
            String sqlQuery = fxSimpleDialog.getTxValue();

            List<String> queryFields = FiQueryGenerator.getQueryFieldsAsString(sqlQuery);

            String result = FiCodeHelper.codeFiColListFromHeadersAndFields(queryFields, "", queryFields);
            appendTextNewLine(result);

        } else {
            FxDialogShow.showPopWarn("Sorgu girilmedi.");
        }


    }


    private void actAlanListesi() {

        if (getClassSelected() == null) {
            actBtnClassSec();
        }

        if (getClassSelected() != null) {
            appendTextNewLine(FiCodeHelper.codeClassFieldList(getClassSelected()));
        } else {
            FxDialogShow.showPopError("Lütfen bir Sınıf Seçiniz.");
        }

    }

    private void actAlanListesiByIdWithValue() {

        Jdbi activeServerJdbi = getAndSetupActiveServerJdbi();

        if (activeServerJdbi != null) {
            FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
            //System.out.println("Connected");
        } else {
            FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
            return;
        }

        if (getClassSelected() == null) {
            actBtnClassSec();
        }

        if (getClassSelected() != null) {

            FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextFieldInteger, "Id Değerini Giriniz");
            fxSimpleDialog.openAsDialogSync();

            if (fxSimpleDialog.isClosedWithOk()) {

                Integer idNo = (Integer) fxSimpleDialog.getObjectValue();

                Loghelper.get(getClass()).debug("Id:" + idNo);

                if (idNo != null) {

                    Fdr<Optional<Object>> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getClassSelected()).jdSelectEntityOptById(idNo);

                    if (fdr.getValue().isPresent()) {
                        //FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
                        String result = FiCodeGeneratorTest.codeEntityFieldsWithValue(getClassSelected(), fdr.getValue());
                        appendTextNewLine(result);
                    } else {
                        System.out.println("Db den Veri Okunamadı");
                    }

                } else {
                    FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
                }

            }
        } else {
            FxDialogShow.showPopError("Lütfen bir sınıf seçin");
        }

        //ModEntityListCont modEntityListCont = showDialogSelectEntityClass();
        //EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();
        //if (selectedEntity != null) {
        //}

    }

    private void actAlanListesiByCandIdWithValue() {

        Jdbi activeServerJdbi = getAndSetupActiveServerJdbi();

        if (activeServerJdbi != null) {
            FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
            //System.out.println("Connected");
        } else {
            FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
            return;
        }

        if (getClassSelected() == null) {
            actBtnClassSec();
        }

        if (getClassSelected() != null) {

            FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextField, "Id Değerini Giriniz");
            fxSimpleDialog.openAsDialogSync();

            if (fxSimpleDialog.isClosedWithOk()) {

                String candIdValue = fxSimpleDialog.getTxValue();

                Loghelper.get(getClass()).debug("Id:" + candIdValue);

                if (candIdValue != null) {

                    Fdr<Optional<Object>> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getClassSelected()).jdSelectEntityOptByStringCandId1(candIdValue);

                    if (fdr.getValue().isPresent()) {
                        //FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
                        String result = FiCodeGeneratorTest.codeEntityFieldsWithValue(getClassSelected(), fdr.getValue());
                        appendTextNewLine(result);
                    } else {
                        System.out.println("Db den Veri Okunamadı");
                    }

                } else {
                    FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
                }

            }
        } else {
            FxDialogShow.showPopError("Lütfen bir sınıf seçin");
        }

        //ModEntityListCont modEntityListCont = showDialogSelectEntityClass();
        //EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();
        //if (selectedEntity != null) {
        //}

    }

    private void actBtnClassSec() {
        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();
        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {
            setClassSelected(selectedEntity.getClazz());
            getCodeGenMainView().getBtnClassSec().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
        }

    }

    private void actBtnClassSec2() {
        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();
        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {
            setClassSelected2(selectedEntity.getClazz());
            getCodeGenMainView().getBtnClassSec2().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
        }

    }

    private void actTableToEntity() {

        Jdbi activeServerJdbi = getAndSetupActiveServerJdbi();

        if (activeServerJdbi != null) {
            FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
            //System.out.println("Connected");
        } else {
            FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
            return;
        }

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextField, "Tablo Adını Giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {

            FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
            FiQueryGenerator fiQueryGenerator = new FiQueryGenerator();

            System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
            String entityCode = FiQueryGenerator.tableToEntityClass(fxSimpleDialog.getTxValue(), getAndSetupActiveServerJdbi());

            if (!FiString.isEmpty(entityCode)) {
                getCodeGenMainView().getTxaMainOutput().appendText(entityCode);
            } else {
                getCodeGenMainView().getTxaMainOutput().appendText("N/A");
            }

        }

    }


    private void actBtnFiTableColListWithEnumFields() {

        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();

        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {

            FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextField, "Enum Sınıfının Adını Giriniz :");
            fxSimpleDialog.openAsDialogSync();

            String fieldEnumClass = "EntegreField";

            if (fxSimpleDialog.isClosedWithOk()) {
                if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
                    fieldEnumClass = fxSimpleDialog.getTxValue();
                }
            }

            getCodeGenMainView().getTxaMainOutput().appendTextLnAsyn(
                    FiQueryGenerator.codeTableColsV2(selectedEntity.getClazz(), false, fieldEnumClass));

            getCodeGenMainView().getTxaMainOutput().appendNewLine();
            getCodeGenMainView().getTxaMainOutput().appendNewLine();

            getCodeGenMainView().getTxaMainOutput().appendTextLnAsyn(
                    FiQueryGenerator.codeColsEnum(selectedEntity.getClazz(), false));

        }

    }

    private void actBtnFiTableColListWithFieldHeader() {

        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();

        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {

            getCodeGenMainView().getTxaMainOutput().appendTextLnAsyn(
                    FiQueryGenerator.codeTableColsSimple(selectedEntity.getClazz(), false));

            getCodeGenMainView().getTxaMainOutput().appendNewLine();
            getCodeGenMainView().getTxaMainOutput().appendNewLine();

            getCodeGenMainView().getTxaMainOutput().appendTextLnAsyn(
                    FiQueryGenerator.codeColsEnum(selectedEntity.getClazz(), false));

        }


    }

    private void actBtnSelectServer1() {

        ServerConfig serverConfig1 = actServerSelect();

        if (serverConfig1 != null) {

            Fdr<Jdbi> jdbiFdr = createJdbi(serverConfig1);

            if (jdbiFdr.isTrueBoResult()) {
                getModalSqlInit().setServerConfig1(serverConfig1);
                getModalSqlInit().setJdbi1(jdbiFdr.getValue());

                String txMessage = String.format("Server: %s Db: %s", serverConfig1.getServer(), serverConfig1.getServerDb());
                getCodeGenMainView().getBtnServer1().setText(txMessage);
                FxDialogShow.showPopInfo("Server Bağlantı Başarılı ***");
            } else {
                FxDialogShow.showPopError("Server Bağlantı Başarısız !!!");
            }
        } else {
            //FxDialogShow.showPopWarn("Lütfen Server Seçiniz...");
        }
    }

    private void actBtnSelectServer2() {
        ServerConfig serverConfig2 = actServerSelect();

        if (serverConfig2 != null) {

            Fdr<Jdbi> fdrConnection = createJdbi(serverConfig2);
            if (fdrConnection.getValue() != null) {
                getModalSqlInit().setServerConfig2(serverConfig2);
                getModalSqlInit().setJdbi2(fdrConnection.getValue());
                getCodeGenMainView().getBtnServer2().setText("Server2:" + serverConfig2.getServer() + " / " + serverConfig2.getServerDb());
                FxDialogShow.showPopInfo("Server Bağlantı Başarılı **");
            } else {
                FxDialogShow.showPopError("Server Bağlantı Başarısız !!!\n" + fdrConnection.getMessage());
            }

        } else {
            //FxDialogShow.showPopWarn("Lütfen Server Seçiniz...");
        }
    }

    /**
     * Örnek bir kayıda göre not null kontrollü , alanları set eden kodları ortaya çıkarır.
     */
    private void actEntityFillerMethodFromDb() {

        if (checkServer() && checkClassChoose()) {

            entityFillerMethodFromDb();


        } else {
            FxDialogShow.showPopError("Lütfen bir sınıf seçin");
        }

        //ModEntityListCont modEntityListCont = showDialogSelectEntityClass();
        //EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();
        //if (selectedEntity != null) {
        //}

    }

    private void entityFillerMethodFromDb() {
        appendTextNewLine(MlcgSql.entityFillerMethodFromDb(getAndSetupActiveServerJdbi(), getClassSelected()));
    }

    private void actAlterNewFields() {

        if (checkServer() && checkClassChoose()) {

            Fdr<List<String>> fdr = FiQueryGenerator.getAlterAddFieldQueries(getClassSelected(), getAndSetupActiveServerJdbi());

            FxDialogShow.showDbResult(fdr);

            String sqltum = "";
            if (fdr.getBoResultInit()) {
                if (!FiCollection.isEmpty(fdr.getValue())) {
                    appendTextNewLine("");
                    for (String s : fdr.getValue()) {
                        appendTextNewLine(s);
                        sqltum += s + "\n";
                    }
                    appendTextNewLine("");
                }
            }

            if (isEnableDbOperation() && getAndSetupActiveServerJdbi() != null && !FiString.isEmpty(sqltum)) {

                Fdr fdrAlter = new RepoJdbiString(getAndSetupActiveServerJdbi()).jdUpdateBindMapViaAtTire(sqltum, null);

                if (fdr.getBoResultInit()) {
                    fdr.setMessage("Değişiklikler başarıyla uygulandı.");
                }
                FxDialogShow.showDbResult(fdr);
            }
        } else {
            FxDialogShow.showPopError("Server veya Sınıfı seçiminizi kontrol ediniz.");
        }

    }

    public Integer actDialogIdSelection() {
        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.TextFieldInteger, "Id Değerini Giriniz");
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {
            Integer idNo = (Integer) fxSimpleDialog.getObjectValue();
            return idNo;
        }

        return null;
    }

    private Object actDialogCandIdEntityForm() {

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogMetaType.FormAutoByCandIdFields);
        fxSimpleDialog.setEntityClass(getClassSelected());
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {
            Object formValue = fxSimpleDialog.getFormValue();
            return formValue;
        }

        return null;
    }

    public Jdbi getAndSetupActiveServerJdbi() {
        if (getModalSqlInit().getJdbi1() == null) {
            actBtnSelectServer1();
        }
        return getModalSqlInit().getJdbi1();
    }

    private ServerConfig actServerSelect() {

        Properties properties = FiPropertyFile.readProperties(getPropPath());
        ServerConfig selectedServer = null;

        List<ServerConfig> listServer = new ArrayList<>();

        for (int index = 0; index < 20; index++) {

            String txName = properties.getProperty("server-" + index + "-name", "");
            String txDbName = properties.getProperty("server-" + index + "-db", "");
            String txDefNo = properties.getProperty("server-" + index + "-def-no", "");

            // Server-user-pass bilgiler
            String server = properties.getProperty("server-def-" + txDefNo, "");
            String user = properties.getProperty("server-def-" + txDefNo + "-user", "");
            String pass = properties.getProperty("server-def-" + txDefNo + "-key", "");

            if (FiString.isEmpty(txDefNo) | FiString.isEmpty(server)) {
                continue;
            }

            ServerConfig serverConfig = new ServerConfig();
            serverConfig.setServer(server);
            serverConfig.setServerDb(txDbName);
            serverConfig.setServerUser(user);
            serverConfig.setServerKey(pass);
            serverConfig.setName(txName);

            listServer.add(serverConfig);

        }


        if (listServer.size() == 0) {
            FxDialogShow.showPopWarn("Server Ayarları Girilmemiş");

        } else {

            FxSimpleContGen<ServerConfig> fxSimpleContGen = new FxSimpleContGen<>(true);
            FxTableView2Ng tblServerList = new FxTableView2Ng();

            fxSimpleContGen.getModView().addGrowPushSpan(tblServerList);

//			List<FiCol> listCols = ListFiTableColBuilder.build().addFields("name", "server").getList();

            FiColList fiCols = new FiColList();
            fiCols.add(FiCol.build("İsim", "name"));
            fiCols.add(FiCol.build("Sunucu", "server"));

            tblServerList.setEnableLocalFilterEditor(true);
            tblServerList.addAllFiColsAuto(fiCols);
            tblServerList.setItemsAsFilteredList(listServer);
            tblServerList.activateExtensionFxTableSelectAndClose(fxSimpleContGen);
            fxSimpleContGen.openAsDialogSync(null, null);

            selectedServer = fxSimpleContGen.getEntitySelected();

            if (selectedServer != null) {
                FxDialogShow.showPopInfo(selectedServer.getServer() + " server seçildi.");
            }
            //FiConsole.debug(selectedServer);
        }

        return selectedServer;
    }

    private Fdr<Jdbi> createJdbi(ServerConfig serverConfig) {

        Fdr<Jdbi> fdr = new Fdr<>();

        if (serverConfig == null) {
            fdr.setMessage("Server Ayarları tanımlanmamış.");
            return fdr;
        }

        try {
            Jdbi jdbi = FiJdbiFactory.createJdbi(serverConfig);
            fdr.setValue(jdbi);
            fdr.setBoResult(true);
            return fdr;
        } catch (Exception ex) {
            //ex.printStackTrace();
            Loghelper.get(getClass()).debug(FiException.exceptionIfToString(ex));
            fdr.setMessage("Bağlantı kurulurken hata oluştu. Bağlantı bilgilerini kontrol ediniz.");
            fdr.setBoResult(false);
            return fdr;
        }

    }

    private boolean isEnableDbOperation() {
        return getCodeGenMainView().getChkVeritabandaOlustur().isSelected();
    }

    public void appendTextNewLine(String txValue) {
        getCodeGenMainView().getTxaMainOutput().appendTextLnAsyn(txValue);
    }

    public void actionResult(String txResult) {
        getCodeGenMainView().getTxaMainOutput().appendTextLnAsyn(txResult);
    }


    @Override
    public ModHomeCodeGenView getModView() {
        return getCodeGenMainView();
    }

    public ModHomeCodeGenView getCodeGenMainView() {
        return codeGenMainView;
    }

    public String getPropPath() {
        return propPath;
    }

    public void setPropPath(String propPath) {
        this.propPath = propPath;
    }

    public Class getClassSelected() {
        return classSelected;
    }

    public void setClassSelected(Class classSelected) {
        this.classSelected = classSelected;
    }

    public Class getClassSelected2() {
        return classSelected2;
    }

    public void setClassSelected2(Class classSelected2) {
        this.classSelected2 = classSelected2;
    }

    public File getFileSelected() {
        return fileSelected;
    }

    public void setFileSelected(File fileSelected) {
        this.fileSelected = fileSelected;
    }

    public MlcgSql getModalSqlInit() {
        if (mlcgSql == null) {
            mlcgSql = new MlcgSql();
        }
        return mlcgSql;
    }

    public MlcgHome getModalHome() {
        if (mlcgHome == null) {
            mlcgHome = new MlcgHome();
        }
        return mlcgHome;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }


}


//	private void actCmbQueryGenerators(ComboItem newValue) {
//
//		if (newValue == null || newValue.getValue() == null) return;
//
//
//		if (newValue.getValue().equals(enumComboItem.CreateQuery.toString())) {
//			actQueryCreate();
//		}
//
//		if (newValue.getValue().equals(enumComboItem.QueryAlterAdd.toString())) {
//			actAlterNewFields();
//		}
//
//		getModView().getCmbQueryGenerator().clearSelectionFi();
//
//
//	}

// *** Combobox seçim hareketleri

//	private void actCmbExcelIslemler(ComboItem newValue) {
//
//		if (newValue == null || newValue.getValue() == null) return;
//
//		if (newValue.getValue().equals(enumComboItem.ExcelToEntity.toString())) {
//			actExcelToEntity();
//		}
//
//		getModView().getCmbExcelIslemler().clearSelectionFi();
//	}

//	private void actCmbDbRead(ComboItem newValue) {
//
//		if (newValue == null || newValue.getValue() == null) return;
//
//		if (newValue.getValue().equals(enumComboItem.DbKayitSablonById.toString()) && checkServer() && checkClassChoose()) {
//			actDbKayitSablonById(); //DbKayitSablonById
//		}
//
//		if (newValue.getValue().equals(enumComboItem.DbKayitSablonByCandIds.toString()) && checkServer() && checkClassChoose()) {
//			actDbKayitSablonByCandIds();//DbKayitSablonByCandIds
//		}
//
//
//		codeGenMainView.getCmbDbRead().clearSelectionFi();
//	}

//	private void actCmbFiTableColGenerate(ComboItem newValue) {
//
//		if (newValue == null || newValue.getValue() == null) return;
//
////		if (newValue.getValue().equals(enumComboItem.ExcelToFiTableColWithFieldName.toString())) {
////			actExcelToFiTableColWithFieldName();
////		}
//
//
//		if (newValue.getValue().equals(enumComboItem.ExcelToFiTableColViaMethods.toString())) {
//			actExcelToFiTableColViaMethods();// ExcelToFiTableColViaMethods
//		}
//
//		if (newValue.getValue().equals(enumComboItem.ClassToFiTableColGenerator.toString())) {
//			actClassToFiTableColGenerate(); //ClassToFiTableColGenerator
//		}
//
//		if (newValue.getValue().equals(enumComboItem.AlanListesi.toString())) {
//			actAlanListesi();//AlanListesi
//		}
//
//		if (newValue.getValue().equals(enumComboItem.AlanListesiByIdWithValue.toString())) {
//			actAlanListesiByIdWithValue();//AlanListesiByIdWithValue
//		}
//
//		if (newValue.getValue().equals(enumComboItem.AlanListByCandIdWithValue.toString())) {
//			actAlanListesiByCandIdWithValue();//AlanListByCandIdWithValue
//		}
//
//		if (newValue.getValue().equals(enumComboItem.SqlQueryToFiTableCol.toString())) {
//			actSqlQueryToFiTableCol(); //SqlQueryToFiTableCol
//		}
//
//		if (newValue.getValue().equals(enumComboItem.SqlQueryToFiTableColGenerator.toString())) {
//			actSqlQueryToFiTableColGenerate();//SqlQueryToFiTableColGenerator
//		}
//
//		codeGenMainView.getCmbTableColGenerate().clearSelectionFi();
//
//	}


// combobox lara eklenecek
//btnCodeGenFiTableColFromExcel = new FxButton("Excelden FiTableCol oluştur");
//btnCodeEntityFieldFillerMethodWithEnumFields = new FxButton("FiTableCol List With Enum Fields By Class");
//btnCodeTypescript = new FxButton("Typescript Entity");
//btnCodeEntityFieldFillerMethod = new FxButton("FiTableCol List With Field,Header By Class");
//btnCodeTableCol = new FxButton("Tablo Sütunları Listesi Tanımı");

//codeGenMainView.getBtnCodeGenFiTableColFromExcel().setOnAction(event -> actBtnCodeGenFiTableColFromExcel());
//codeGenMainView.getBtnCodeTypescript().setOnAction(event -> actBtnTypescriptEntity());
//codeGenMainView.getBtnCodeEntityFieldFillerMethod().setOnAction(event -> actBtnFiTableColListWithFieldHeader());
//codeGenMainView.getBtnCodeEntityFieldFillerMethodWithEnumFields().setOnAction(event -> actBtnFiTableColListWithEnumFields());
//codeGenMainView.getBtnCodeGenFiTableColFromExcel().setOnAction(event -> actBtnCodeGenFiTableColFromExcel());
