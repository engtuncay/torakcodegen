package oraksoft.codegen.modules;

import javafx.stage.Stage;
import oraksoft.codegen.modal.*;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.configmisc.ServerConfig;
import oraksoft.codegen.entity.EntityClazz;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.core.*;
import ozpasyazilim.utils.datatypes.FiListKeyString;
import ozpasyazilim.utils.ficodegen.FiCodeGen;
import ozpasyazilim.utils.fidborm.*;
import ozpasyazilim.utils.gui.components.ComboItemText;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.mvc.AbsFiModBaseCont;
import ozpasyazilim.utils.mvc.IFiModCont;
import ozpasyazilim.utils.returntypes.Fdr;
import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.FiColList;
import ozpasyazilim.utils.fxwindow.FxSimpleContGen;
import ozpasyazilim.utils.fxwindow.FxSimpleDialog;
import ozpasyazilim.utils.fxwindow.FiDialogMetaType;


import java.io.File;
import java.util.*;

/**
 * Occ : Orak Code Generator - Controller
 * <p>
 * Ocm - Orak Code Generator Modals (Old Mcg,Moc)
 *
 */
public class OccHomeWindowCont extends AbsFiModBaseCont implements IFiModCont {

    OcgHomeWindow ocgHomeWindow;
    Class classSelected;
    Class classSelected2;

    Stage mainStage;
    File fileSelected;

    private McgSql mcgSql;
    private McgHome mcgHome;

    @Override
    public void initCont() {

        ocgHomeWindow = new OcgHomeWindow();
        ocgHomeWindow.initGui();

        ocgHomeWindow.getBtnClassSec().setOnAction(event -> actBtnSelectClass());
        ocgHomeWindow.getBtnServer1().setOnAction(event -> actBtnSelectServer1());

        ocgHomeWindow.getBtnClassSec2().setOnAction(event -> actBtnClassSec2());
        ocgHomeWindow.getBtnServer2().setOnAction(event -> actBtnSelectServer2());

        ocgHomeWindow.getBtnDosyaSec().setOnAction(event -> actBtnDosyaSec());

        // modal ayarlar
        getMcgHomeInit().setFxTextArea(getModView().getTxaMainOutput());
        getMcgHomeInit().setModalSql(getMcgSqlInit());
        getMcgSqlInit().setMcgHome(getMcgHomeInit());

        getModView().getChkVeritabandaOlustur().setOnChangeAction(aBoolean -> {
            getMcgSqlInit().setBoEnableDbOperation(aBoolean);
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
        setupDbToCode(mbDbToCode);

        // **** FiCol Generator Helpers
        setupMenuFiColHelpers2();
        setupMenuFiColHelper1();

        // **** Db Read Combos
        setupDbReadCombos();

        // **** Excel Islemler Combos
        setupMenuExcelIslemler();

        // enumComboItem.ExcelToEntity

        // ****** Query Helpers Combos
        setupMenuQueryGenerator();

        // Xml Combo
        setupMenuXml();

        setupMenuCsharpIslemler();

        // Combobox Listener Ayarları

        ocgHomeWindow.getCmbDbRead().activateSetNullAfterAction();
        ocgHomeWindow.getCmbFiColHelpers().activateSetNullAfterAction();
        ocgHomeWindow.getCmbFiColHelpers2().activateSetNullAfterAction();
        ocgHomeWindow.getCmbExcelIslemler().activateSetNullAfterAction();
        ocgHomeWindow.getCmbQueryGenerator().activateSetNullAfterAction();
        ocgHomeWindow.getCmbXmlAraclar().activateSetNullAfterAction();

        // Sql İşlemler
        FxMenuButton mbSqlTransfer = new FxMenuButton("Sql Transfer");
        setupMenuSqlTransfer(mbSqlTransfer);

        // Db Export
        FxMenuButton mbDbExport = new FxMenuButton("Db Export");
        setupMenuDbExport(mbDbExport);

        // Menu Layout
        getGcgHomeCodeGenView().getMigMenu().addSpan(mbDbToCode);
        getGcgHomeCodeGenView().getMigMenu().addSpan(mbSqlTransfer);
        getGcgHomeCodeGenView().getMigMenu().addSpan(mbDbExport);

    }

    private void setupMenuSqlTransfer(FxMenuButton mbSqlTransfer) {
        FxMenuItem miTransferTarih = new FxMenuItem("Sql Kopyalama:Tarih Belirle");
        miTransferTarih.setOnAction(event -> {
            FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buiTextFieldDialog("Tarih Giriniz (yyyymmdd)");
            //fxSimpleDialog.openAsDialogSync();
            if (fxSimpleDialog.isClosedWithOk()) {
                getMcgSqlInit().setTxSqlTransferDate(fxSimpleDialog.getTxValue());
                appendTextNewLine("Tarih Alanı Değeri Atandı:" + fxSimpleDialog.getTxValue());
            }
        });
        mbSqlTransfer.addItem(miTransferTarih);

        FxMenuItem miTransferTarihExcel = new FxMenuItem("Sql Kopyalama:Tarih Alanlarını Excelden Oku");
        miTransferTarihExcel.setOnAction(event -> {
            //Loghelper.get(getClass()).info("excel tarih start");
            FiListKeyString fiListKeyString = McgExcel.actExceldenTarihAlanlariniOkuForSqlTransfer();
            if (fiListKeyString != null) {
                fiListKeyString.clearRowsKeyIfEmpty("txDateField");
                appendTextNewLine("Tarih Alanları Okundu.");
            }
            getMcgSqlInit().setListMapDateField(fiListKeyString);
            FiConsole.debugListMap(fiListKeyString, McgExcel.class, true);
        });
        mbSqlTransfer.addItem(miTransferTarihExcel);

        FxMenuItem miTabloKopyalama = new FxMenuItem("Tablo Kopyalama (Kaynak:Db1->Hedef:Db2)");
        miTabloKopyalama.setOnAction(event -> actionResult(getMcgSqlInit().sqlTableCopySrv1ToSrv2(false)));
        mbSqlTransfer.addItem(miTabloKopyalama);

        FxMenuItem miTabloKopyalamaTarihli = new FxMenuItem("Tablo Kopyalama Tarihli (Kaynak:Db1->Hedef:Db2)");
        miTabloKopyalamaTarihli.setOnAction(event -> actionResult(getMcgSqlInit().sqlTableCopySrv1ToSrv2(true)));
        mbSqlTransfer.addItem(miTabloKopyalamaTarihli);

        FxMenuItem miTransferSqlExcelOto = new FxMenuItem("Sql Kopyalama Excelden Otomatik");
        miTransferSqlExcelOto.setOnAction(event -> {
            //Loghelper.get(getClass()).info("excel tarih start");
            FiListKeyString fiListKeyString = McgExcel.actExceldenTarihAlanlariniOkuForSqlTransfer();
            if (fiListKeyString != null) {
                //fiListMapStr.clearRowsKeyIfEmpty("txDateField");
                appendTextNewLine("Excel Tablosu Okundu.(Sql Kopyalama Oto için)");
            }
            // FiConsole.debugListMap(fiListMapStr,ModalExcel.class,true);
            getMcgSqlInit().setListMapDateField(fiListKeyString);
            getMcgSqlInit().sqlTableCopySrv1ToSrv2Auto();
        });
        mbSqlTransfer.addItem(miTransferSqlExcelOto);
    }

    private void setupMenuCsharpIslemler() {
        FxMenuItem cshEntitySinifOlusturma = new FxMenuItem("Tablodan sınıf oluştur");
        ocgHomeWindow.getCsharpIslemler().getItems().add(cshEntitySinifOlusturma);

        cshEntitySinifOlusturma.setOnAction(event -> new MlcgCsharp().actCsharpSinifOlusturma(this));
    }

    private void setupMenuXml() {
        ocgHomeWindow.getCmbXmlAraclar().addComboItem(
                ComboItemText.buildWitAction("Xml to Field List"
                        , this::actXmlToFiFieldList));
    }

    private void setupMenuDbExport(FxMenuButton mbDbExport) {
        FxMenuItem miDbExportForExportTable1 = new FxMenuItem("Export Table With Insert (Wout Pks) (1)"
                , (event) -> McgDbExport.actTableExport1(this, true));
        mbDbExport.addItem(miDbExportForExportTable1);

        FxMenuItem miDbExportForExportTable2 = new FxMenuItem("Export Table With Insert (With Pks) (2)"
                , (event) -> McgDbExport.actTableExport1(this, false));
        mbDbExport.addItem(miDbExportForExportTable2);
    }

    private void setupMenuQueryGenerator() {

        ocgHomeWindow.getCmbQueryGenerator().addComboItem(
                ComboItemText.buildWitAction("Create Query", () ->
                        actionResult(getMcgSqlInit().createQuery(getClassSelected()))));

        ocgHomeWindow.getCmbQueryGenerator().addComboItem(
                ComboItemText.buildWitAction("Create Query (By Ficol)", () ->
                        actionResult(getMcgSqlInit().createQueryByFiCol())));

        ocgHomeWindow.getCmbQueryGenerator().addComboItem(
                ComboItemText.buildWitAction("Alter Table Field(Add)"
                        , this::actAlterNewFields));

        ocgHomeWindow.getCmbQueryGenerator().addComboItem(
                ComboItemText.buildWitAction("Clone Table Data"
                        , this::actCloneTableData));

        ocgHomeWindow.getCmbQueryGenerator().addComboItem(
                ComboItemText.buildWitAction("Unique1 Fields", () ->
                        actionResult(getMcgSqlInit().queryUnique1Fields(getClassSelected()))));
    }

    private void setupMenuExcelIslemler() {
        ocgHomeWindow.getCmbExcelIslemler().addComboItem(
                ComboItemText.buildWitAction("Excel'den Entity Oluştur"
                        , () -> actionResult(McgExcel.actExcelToEntity())));
    }

    private void setupDbToCode(FxMenuButton mbDbToCode) {
        FxMenuItem tabloToEntity = new FxMenuItem("Tablodan Entity Oluştur");
        tabloToEntity.setOnAction(event -> actTableToEntity());
        mbDbToCode.addItem(tabloToEntity);

        FxMenuItem vtEntityDoldur = new FxMenuItem("Veritabanından Entity Alanlarını Doldurma Metodu");
        vtEntityDoldur.setOnAction(event -> actEntityFillerMethodFromDb());
        mbDbToCode.addItem(vtEntityDoldur);

        FxMenuItem vtAlter = new FxMenuItem("Veritabanına eklenecek alanların Alter Sorguları");
        vtAlter.setOnAction(event -> actAlterNewFields());
        mbDbToCode.addItem(vtAlter);
    }

    private void setupDbReadCombos() {
        ocgHomeWindow.getCmbDbRead().addComboItem(
                ComboItemText.buildWitAction("Kayıt Şablon By Id", this::actDbKayitSablonById));
//		actDbKayitSablonById(); //DbKayitSablonById

        ocgHomeWindow.getCmbDbRead().addComboItem(
                ComboItemText.buildWitAction("Kayıt Şablon By Cand Ids", this::actDbKayitSablonByCandIds));
        //actDbKayitSablonByCandIds();//DbKayitSablonByCandIds
    }

    private void setupMenuFiColHelper1() {
        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Sınıftan FiCol Generate Method oluştur."
                        , () -> MocFiColJava.codeFiColsMethodsByClass1(this)));

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Excelden FiCol List oluştur.(Excel Header As Header Name)"
                        , () -> appendTextNewLine(MocTableColGenerate.actExcelToFiColWithHeaderAsHeaderName())));

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Excelden FiCol List oluştur.(Excel Header As FieldName And Header)"
                        , () -> appendTextNewLine(MocTableColGenerate.actExcelToFiColWithHeaderAsFieldNameAndHeaderName())));

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Excelden FiColList Metodu oluştur"
                        , () -> MocFiColJava.actExcelToFiTableColViaMethods(this)));

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Excelden FiColList Metodu oluştur (FiColsMikro üzerinden)"
                        , () -> MocFiColJava.actGenFiColListByExcel(this)));

// codeGenMainView.getCmbTableColGenerate().addComboItemFi(enumComboItem.ExcelToFiTableColWithFieldName.toString()
// , "Excelden FiTableCol List oluştur.(Auto Field Name)");


        // actClassToFiTableColGenerate(); //ClassToFiTableColGenerator

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Sql Sorgusundan FiCol List oluştur."
                        , this::actSqlQueryToFiTableCol));

        //actSqlQueryToFiTableCol(); //SqlQueryToFiTableCol

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Sql Sorgusundan FiTableCol Generate Method oluştur."
                        , this::actSqlQueryToFiTableColGenerate));
        // actSqlQueryToFiTableColGenerate();//SqlQueryToFiTableColGenerator

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Alan Listesi", this::actAlanListesi));
        //actAlanListesi();//AlanListesi

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Alan Listesi By Id With Value"
                        , this::actAlanListesiByIdWithValue));
        //actAlanListesiByIdWithValue();//AlanListesiByIdWithValue

        ocgHomeWindow.getCmbFiColHelpers().addComboItem(
                ComboItemText.buildWitAction("Alan Listesi By Cand Id With Value"
                        , this::actAlanListesiByCandIdWithValue));
        //actAlanListesiByCandIdWithValue();//AlanListByCandIdWithValue
    }

    private void setupMenuFiColHelpers2() {

        ocgHomeWindow.getCmbFiColHelpers2().addComboItem(
                ComboItemText.buildWitAction("FiCol Alanları Sınıfı Oluşturma (Excel) (Detaylı Alanlar)"
                        , () -> MocFiColJava.bui(this).actGenFiColListByExcel()));

        ocgHomeWindow.getCmbFiColHelpers2().addComboItem(
                ComboItemText.buildWitAction("FiCol Alanları Sınıfı Oluşturma (Seçilen Sınıftan)"
                        , () -> MocFiColJava.bui(this).actFiColsClassByClass()));

        ocgHomeWindow.getCmbFiColHelpers2().addComboItem(
                ComboItemText.buildWitAction("FiCol Alanları Sınıf Oluşturma (Excelden) Row1:Header Row2:FieldName"
                        , () -> MocFiColJava.actFiColsClassJavaByExcelRowHeader(this)));
    }


    /**
     * Xml To Field List
     */
    private void actXmlToFiFieldList() {
        //Loghelper.get(getClass()).debug("Xml To Field List");
        String code = MocXml.actXmlToFiFieldList(getFileSelected());
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

        if (getMcgSqlInit().getJdbi1() == null) {
            actBtnSelectServer1();
        }

        return getMcgSqlInit().getJdbi1() != null;

    }

    public Boolean checkClassChoose() {

        if (getClassSelected() == null) {
            actBtnSelectClass();
        }

        if (getClassSelected() != null) {
            return true;
        } else {
            FxDialogShow.showPopError("Lütfen bir Sınıf Seçiniz.");
        }

        return false;
    }


    private void actSqlQueryToFiTableColGenerate() {

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextAreaString);
        fxSimpleDialog.setMessageHeader("Lütfen sorgu cümleciğini giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
            String sqlQuery = fxSimpleDialog.getTxValue();

            List<String> queryFields = FiQugen.getQueryFieldsAsString(sqlQuery);

            String result = FiCodeGen.codeFiTableColsGeneraterMethods(queryFields, "", queryFields);
            appendTextNewLine(result);

        } else {
            FxDialogShow.showPopWarn("Sorgu girilmedi.");
        }

    }


    private void actSqlQueryToFiTableCol() {

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextAreaString);
        fxSimpleDialog.setMessageHeader("Lütfen sorgu cümleciğini giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
            String sqlQuery = fxSimpleDialog.getTxValue();

            List<String> queryFields = FiQugen.getQueryFieldsAsString(sqlQuery);

            String result = FiCodeGen.codeFiColListFromHeadersAndFields(queryFields, "", queryFields);
            appendTextNewLine(result);

        } else {
            FxDialogShow.showPopWarn("Sorgu girilmedi.");
        }


    }


    private void actAlanListesi() {

        if (getClassSelected() == null) {
            actBtnSelectClass();
        }

        if (getClassSelected() != null) {
            appendTextNewLine(FiCodeGen.codeClassFieldList(getClassSelected()));
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
            actBtnSelectClass();
        }

        if (getClassSelected() != null) {

            FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextFieldInteger, "Id Değerini Giriniz");
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
            actBtnSelectClass();
        }

        if (getClassSelected() != null) {

            FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextField, "Id Değerini Giriniz");
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

    private void actBtnSelectClass() {
        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();
        EntityClazz selectedEntity = modEntityListCont.getEntitySelected();

        if (selectedEntity != null) {
            setClassSelected(selectedEntity.getClazz());
            getGcgHomeCodeGenView().getBtnClassSec().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
        } else {
            FxDialogShow.showPopWarn("Sınıf Seçilmedi !!!");
        }

    }

    private void actBtnClassSec2() {
        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();
        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {
            setClassSelected2(selectedEntity.getClazz());
            getGcgHomeCodeGenView().getBtnClassSec2().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
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

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextField, "Tablo Adını Giriniz:");
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {

            FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
            FiQugen fiqugen = new FiQugen();

            System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
            String entityCode = FiQugen.tableToEntityClass(fxSimpleDialog.getTxValue(), getAndSetupActiveServerJdbi());

            if (!FiString.isEmpty(entityCode)) {
                getGcgHomeCodeGenView().getTxaMainOutput().appendText(entityCode);
            } else {
                getGcgHomeCodeGenView().getTxaMainOutput().appendText("N/A");
            }

        }

    }


    private void actBtnFiTableColListWithEnumFields() {

        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();

        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {

            FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextField, "Enum Sınıfının Adını Giriniz :");
            fxSimpleDialog.openAsDialogSync();

            String fieldEnumClass = "EntegreField";

            if (fxSimpleDialog.isClosedWithOk()) {
                if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
                    fieldEnumClass = fxSimpleDialog.getTxValue();
                }
            }

            getGcgHomeCodeGenView().getTxaMainOutput().appendTextLnAsyn(
                    FiQugen.codeTableColsV2(selectedEntity.getClazz(), false, fieldEnumClass));

            getGcgHomeCodeGenView().getTxaMainOutput().appendNewLine();
            getGcgHomeCodeGenView().getTxaMainOutput().appendNewLine();

            getGcgHomeCodeGenView().getTxaMainOutput().appendTextLnAsyn(
                    FiQugen.codeColsEnum(selectedEntity.getClazz(), false));

        }

    }

    private void actBtnFiTableColListWithFieldHeader() {

        ModEntityListCont modEntityListCont = McgSharedDialogs.showDialogSelectEntityClass();

        EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

        if (selectedEntity != null) {

            getGcgHomeCodeGenView().getTxaMainOutput().appendTextLnAsyn(
                    FiQugen.codeTableColsSimple(selectedEntity.getClazz(), false));

            getGcgHomeCodeGenView().getTxaMainOutput().appendNewLine();
            getGcgHomeCodeGenView().getTxaMainOutput().appendNewLine();

            getGcgHomeCodeGenView().getTxaMainOutput().appendTextLnAsyn(
                    FiQugen.codeColsEnum(selectedEntity.getClazz(), false));

        }


    }

    private void actBtnSelectServer1() {

        ServerConfig serverConfig1 = actServerSelect();

        if (serverConfig1 != null) {

            Fdr<Jdbi> jdbiFdr = createJdbi(serverConfig1);

            if (jdbiFdr.isTrueBoResult()) {
                getMcgSqlInit().setServerConfig1(serverConfig1);
                getMcgSqlInit().setJdbi1(jdbiFdr.getValue());

                String txMessage = String.format("Server: %s Db: %s", serverConfig1.getServer(), serverConfig1.getServerDb());
                getGcgHomeCodeGenView().getBtnServer1().setText(txMessage);
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
                getMcgSqlInit().setServerConfig2(serverConfig2);
                getMcgSqlInit().setJdbi2(fdrConnection.getValue());
                getGcgHomeCodeGenView().getBtnServer2().setText("Server2:" + serverConfig2.getServer() + " / " + serverConfig2.getServerDb());
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
        appendTextNewLine(McgSql.entityFillerMethodFromDb(getAndSetupActiveServerJdbi(), getClassSelected()));
    }

    private void actAlterNewFields() {

        if (checkServer() && checkClassChoose()) {

            Fdr<List<String>> fdr = FiQugen.getAlterAddFieldQueries(getClassSelected(), getAndSetupActiveServerJdbi());

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
        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.TextFieldInteger, "Id Değerini Giriniz");
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {
            Integer idNo = (Integer) fxSimpleDialog.getObjectValue();
            return idNo;
        }

        return null;
    }

    private Object actDialogCandIdEntityForm() {

        FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FiDialogMetaType.FormAutoByCandIdFields);
        fxSimpleDialog.setEntityClass(getClassSelected());
        fxSimpleDialog.openAsDialogSync();

        if (fxSimpleDialog.isClosedWithOk()) {
            Object formValue = fxSimpleDialog.getFormValue();
            return formValue;
        }

        return null;
    }

    public Jdbi getAndSetupActiveServerJdbi() {
        if (getMcgSqlInit().getJdbi1() == null) {
            actBtnSelectServer1();
        }
        return getMcgSqlInit().getJdbi1();
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


        if (listServer.isEmpty()) {
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
        return getGcgHomeCodeGenView().getChkVeritabandaOlustur().isSelected();
    }

    public void appendTextNewLine(String txValue) {
        getGcgHomeCodeGenView().getTxaMainOutput().appendTextLnAsyn(txValue);
    }

    public void actionResult(String txResult) {
        getGcgHomeCodeGenView().getTxaMainOutput().appendTextLnAsyn(txResult);
    }

    @Override
    public OcgHomeWindow getModView() {
        return getGcgHomeCodeGenView();
    }

    public OcgHomeWindow getGcgHomeCodeGenView() {
        return ocgHomeWindow;
    }

    public String getPropPath() {
        return "appcodegen.properties";
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

    public McgSql getMcgSqlInit() {
        if (mcgSql == null) {
            mcgSql = new McgSql();
        }
        return mcgSql;
    }

    public McgHome getMcgHomeInit() {
        if (mcgHome == null) {
            mcgHome = new McgHome();
        }
        return mcgHome;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        setFxStage(mainStage);
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
