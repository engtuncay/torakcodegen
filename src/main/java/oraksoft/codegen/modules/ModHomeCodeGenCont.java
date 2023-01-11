package oraksoft.codegen.modules;

import oraksoft.codegen.modal.*;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.configmisc.ServerConfig;
import oraksoft.codegen.entity.EntityClazz;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.core.*;
import ozpasyazilim.utils.datatypes.FiListKeyString;
import ozpasyazilim.utils.ficodegen.FiCodeHelper;
import ozpasyazilim.utils.fidborm.*;
import ozpasyazilim.utils.gui.components.ComboItem;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.mvc.AbsFxSimpleCont;
import ozpasyazilim.utils.mvc.IFxSimpleCont;
import ozpasyazilim.utils.returntypes.Fdr;
import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.FiColList;


import java.io.File;
import java.util.*;

/**
 *
 */
public class ModHomeCodeGenCont extends AbsFxSimpleCont implements IFxSimpleCont {
	ModHomeCodeGenView codeGenMainView;
	Class selectedClass;
	Class selectedClass2;

	File selectedFile;
	String propPath = "appcodegen.properties";

	private ModalSql modalSql;
	private ModalHome modalHome;

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
		getModalHome().setFxTextArea(getModView().getFxTextArea());
		getModalHome().setModalSql(getModalSql());
		getModalSql().setModalHome(getModalHome());

		getModView().getChkVeritabandaOlustur().setOnChangeAction(aBoolean -> {
			getModalSql().setEnableDbOperation(aBoolean);
			//System.out.println("isEnabDbOper:"+ aBoolean);
		});

		setupCombos();

		// combobox lara eklenecek
		//btnCodeGenFiTableColFromExcel = new FxButton("Excelden FiTableCol oluştur");
		//btnCodeEntityFieldFillerMethodWithEnumFields = new FxButton("FiTableCol List With Enum Fields By Class");
		//btnCodeTypescript = new FxButton("Typescript Entity");
		//btnCodeEntityFieldFillerMethod = new FxButton("FiTableCol List With Field,Header By Class");
		//btnCodeTableCol = new FxButton("Tablo Sütunları Listesi Tanımı");

		//codeGenMainView.getBtnCodeGenFiTableColFromExcel().setOnAction(event -> actBtnCodeGenFiTableColFromExcel());
		//codeGenMainView.getBtnCodeTypescript().setOnAction(event -> actBtnTypescriptEntity());
		//codeGenMainView.getBtnCodeEntityFieldFillerMethod().setOnAction(event -> actBtnFiTableColListWithFieldHeader());
//		codeGenMainView.getBtnCodeEntityFieldFillerMethodWithEnumFields().setOnAction(event -> actBtnFiTableColListWithEnumFields());
//		codeGenMainView.getBtnCodeGenFiTableColFromExcel().setOnAction(event -> actBtnCodeGenFiTableColFromExcel());


	}

	private void actBtnDosyaSec() {

		File fileSelected = FiFile.selectFileDialogSwing("Dosya Seçiniz", null);
		setSelectedFile(fileSelected);

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

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Excelden FiCol List oluştur.(Excel Header As Header Name)", () -> {
			appendTextNewLine(ModalTableColGenerate.actExcelToFiColWithHeaderAsHeaderName());
		}));

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Excelden FiCol List oluştur.(Excel Header As FieldName And Header)", () -> {
			appendTextNewLine(ModalTableColGenerate.actExcelToFiColWithHeaderAsFieldNameAndHeaderName());
		}));

		//this::actExcelToFiTableColWithFieldName

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Excelden FiTableCol List oluştur.(Metodlarla)", this::actExcelToFiTableColViaMethods));

//		codeGenMainView.getCmbTableColGenerate().addComboItemFi(enumComboItem.ExcelToFiTableColWithFieldName.toString()
//				, "Excelden FiTableCol List oluştur.(Auto Field Name)");

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Sınftan FiTableCol Generate Method oluştur.", this::actClassToFiTableColGenerate));

		//actClassToFiTableColGenerate(); //ClassToFiTableColGenerator

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Sql Sorgusundan FiTableCol List oluştur.", this::actSqlQueryToFiTableCol));
		//actSqlQueryToFiTableCol(); //SqlQueryToFiTableCol

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Sql Sorgusundan FiTableCol Generate Method oluştur.", this::actSqlQueryToFiTableColGenerate));
		// actSqlQueryToFiTableColGenerate();//SqlQueryToFiTableColGenerator

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Alan Listesi", this::actAlanListesi));
		//actAlanListesi();//AlanListesi

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Alan Listesi By Id With Value", this::actAlanListesiByIdWithValue));
		//actAlanListesiByIdWithValue();//AlanListesiByIdWithValue

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Alan Listesi By Cand Id With Value", this::actAlanListesiByCandIdWithValue));
		//actAlanListesiByCandIdWithValue();//AlanListByCandIdWithValue


		// **** Db Read Combos

		codeGenMainView.getCmbDbRead().addComboItem(ComboItem.buildWitAction("Kayıt Şablon By Id", this::actDbKayitSablonById));
//		actDbKayitSablonById(); //DbKayitSablonById

		codeGenMainView.getCmbDbRead().addComboItem(ComboItem.buildWitAction("Kayıt Şablon By Cand Ids", this::actDbKayitSablonByCandIds));
		//actDbKayitSablonByCandIds();//DbKayitSablonByCandIds


		// **** Excel Islemler Combos

		codeGenMainView.getCmbExcelIslemler().addComboItem(ComboItem.buildWitAction("Excel'den Entity Oluştur", () -> actionResult(ModalExcel.actExcelToEntity())));

		// enumComboItem.ExcelToEntity

		// ****** Query Generator Combos

		codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItem.buildWitAction("Create Query", () ->
				actionResult(getModalSql().createQuery(getSelectedClass()))));

		codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItem.buildWitAction("Alter Table Field(Add)", this::actAlterNewFields));

		codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItem.buildWitAction("Clone Table Data", this::actCloneTableData));

		codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItem.buildWitAction("Unique1 Fields", () ->
				actionResult(getModalSql().queryUnique1Fields(getSelectedClass()))));

		// Xml Combo

		codeGenMainView.getCmbXmlAraclar().addComboItem(ComboItem.buildWitAction("Xml to Field List", this::actXmlToFiFieldList));

		FxMenuItem cshEntitySinifOlusturma = new FxMenuItem("Tablodan sınıf oluştur");
		codeGenMainView.getCsharpIslemler().getItems().add(cshEntitySinifOlusturma);
		cshEntitySinifOlusturma.setOnAction(event -> new ModalCsharp().actCsharpSinifOlusturma(this));

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
				getModalSql().setTxSqlTransferDate(fxSimpleDialog.getTxValue());
				appendTextNewLine("Tarih Alanı Değeri Atandı:" + fxSimpleDialog.getTxValue());
			}
		});
		mbSqlTransfer.addItem(miTransferTarih);

		FxMenuItem miTransferTarihExcel = new FxMenuItem("Sql Kopyalama:Tarih Alanlarını Excelden Oku");
		miTransferTarihExcel.setOnAction(event -> {
			//Loghelper.get(getClass()).info("excel tarih start");
			FiListKeyString fiListKeyString = ModalExcel.actExceldenTarihAlanlariniOkuForSqlTransfer();
			if (fiListKeyString != null) {
				fiListKeyString.clearRowsKeyIfEmpty("txDateField");
				appendTextNewLine("Tarih Alanları Okundu.");
			}
			getModalSql().setListMapDateField(fiListKeyString);
			FiConsole.debugListMap(fiListKeyString, ModalExcel.class, true);
		});
		mbSqlTransfer.addItem(miTransferTarihExcel);

		FxMenuItem miTabloKopyalama = new FxMenuItem("Tablo Kopyalama (Kaynak:Db1->Hedef:Db2)");
		miTabloKopyalama.setOnAction(event -> actionResult(getModalSql().sqlTableCopySrv1ToSrv2(false)));
		mbSqlTransfer.addItem(miTabloKopyalama);

		FxMenuItem miTabloKopyalamaTarihli = new FxMenuItem("Tablo Kopyalama Tarihli (Kaynak:Db1->Hedef:Db2)");
		miTabloKopyalamaTarihli.setOnAction(event -> actionResult(getModalSql().sqlTableCopySrv1ToSrv2(true)));
		mbSqlTransfer.addItem(miTabloKopyalamaTarihli);

		FxMenuItem miTransferSqlExcelOto = new FxMenuItem("Sql Kopyalama Excelden Otomatik");
		miTransferSqlExcelOto.setOnAction(event -> {
			//Loghelper.get(getClass()).info("excel tarih start");
			FiListKeyString fiListKeyString = ModalExcel.actExceldenTarihAlanlariniOkuForSqlTransfer();
			if (fiListKeyString != null) {
				//fiListMapStr.clearRowsKeyIfEmpty("txDateField");
				appendTextNewLine("Excel Tablosu Okundu.(Sql Kopyalama Oto için)");
			}
			// FiConsole.debugListMap(fiListMapStr,ModalExcel.class,true);
			getModalSql().setListMapDateField(fiListKeyString);
			getModalSql().sqlTableCopySrv1ToSrv2Auto();
		});
		mbSqlTransfer.addItem(miTransferSqlExcelOto);

		// Menu Layout
		getCodeGenMainView().getMigMenu().addSpan(mbDbToCode);
		getCodeGenMainView().getMigMenu().addSpan(mbSqlTransfer);

	}


	/**
	 * Xml To Field List
	 */
	private void actXmlToFiFieldList() {
//		Loghelper.get(getClass()).debug("Xml To Field List");
		String code = ModalXml.actXmlToFiFieldList(getSelectedFile());
		appendTextNewLine(code);
	}


	private void actCloneTableData() {


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

	private void actExcelToFiTableColViaMethods() {

		File fileExcel = new FiFileGui().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			String fieldPrefix = "";

			FxSimpleDialog fxSimpleDialog2 = FxSimpleDialog.buiTextFieldDialog("Lütfen ön ek yazınız.(Eklenecekse)");

			if (fxSimpleDialog2.isClosedWithOk()) {
				fieldPrefix = fxSimpleDialog2.getTxValue();
			}

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());

			appendTextNewLine(FiCodeHelper.codeFiTableColsMethodsFromHeaderAndFieldName(listHeader, "Excel", listFields, fieldPrefix));

		}


	}


	// **** İşlemler

	private void actDbKayitSablonByCandIds() {
		Object form = actDialogCandIdEntityForm();

		//Loghelper.getInstance(getClass()).debug("Id:" + idNo);

		if (form != null) {

			Fdr<List> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectListByCandIds(form);

			if (fdr.getValue().size() > 0) {

				//Loghelper.getInstance(getClass()).debug("Size:"+fiDbResult.getResValue().size());

				FiExcel.saveSablonExcelByClass(this, fdr.getValue(), getSelectedClass(), "ozpasentegre");

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

			Fdr<List> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectListById(idNo);

			if (fdr.getValue().size() > 0) {

				FiExcel.saveSablonExcelByClass(this, fdr.getValue(), getSelectedClass(), "ozpasentegre");

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

		if (getModalSql().getJdbi1() == null) {
			actBtnSelectServer1();
		}

		return getModalSql().getJdbi1() != null;

	}

	public Boolean checkClassChoose() {

		if (getSelectedClass() == null) {
			actBtnClassSec();
		}

		if (getSelectedClass() != null) {
			return true;
		} else {
			FxDialogShow.showPopError("Lütfen bir Sınıf Seçiniz.");
		}

		return false;
	}


	private void actSqlQueryToFiTableColGenerate() {

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextAreaString);
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

	private void actClassToFiTableColGenerate() {

		if (!checkClassChoose()) return;

		String result = FiCodeHelper.codeFiTableColsGeneraterMethodsByFiFields(getSelectedClass());
		appendTextNewLine(result);
	}

	private void actSqlQueryToFiTableCol() {

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextAreaString);
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

		if (getSelectedClass() == null) {
			actBtnClassSec();
		}

		if (getSelectedClass() != null) {
			appendTextNewLine(FiCodeHelper.codeClassFieldList(getSelectedClass()));
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

		if (getSelectedClass() == null) {
			actBtnClassSec();
		}

		if (getSelectedClass() != null) {

			FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextFieldInteger, "Id Değerini Giriniz");
			fxSimpleDialog.openAsDialogSync();

			if (fxSimpleDialog.isClosedWithOk()) {

				Integer idNo = (Integer) fxSimpleDialog.getObjectValue();

				Loghelper.get(getClass()).debug("Id:" + idNo);

				if (idNo != null) {

					Fdr<Optional<Object>> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectEntityById(idNo);

					if (fdr.getValue().isPresent()) {
						//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
						String result = FiCodeGeneratorTest.codeEntityFieldsWithValue(getSelectedClass(), fdr.getValue());
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

		if (getSelectedClass() == null) {
			actBtnClassSec();
		}

		if (getSelectedClass() != null) {

			FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextField, "Id Değerini Giriniz");
			fxSimpleDialog.openAsDialogSync();

			if (fxSimpleDialog.isClosedWithOk()) {

				String candIdValue = fxSimpleDialog.getTxValue();

				Loghelper.get(getClass()).debug("Id:" + candIdValue);

				if (candIdValue != null) {

					Fdr<Optional<Object>> fdr = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectEntityOptByStringCandId1(candIdValue);

					if (fdr.getValue().isPresent()) {
						//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
						String result = FiCodeGeneratorTest.codeEntityFieldsWithValue(getSelectedClass(), fdr.getValue());
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
		ModEntityListCont modEntityListCont = ModalCodeGenDialog.showDialogSelectEntityClass();
		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {
			setSelectedClass(selectedEntity.getClazz());
			getCodeGenMainView().getBtnClassSec().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
		}

	}

	private void actBtnClassSec2() {
		ModEntityListCont modEntityListCont = ModalCodeGenDialog.showDialogSelectEntityClass();
		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {
			setSelectedClass2(selectedEntity.getClazz());
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

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextField, "Tablo Adını Giriniz:");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {

			FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
			FiQueryGenerator fiQueryGenerator = new FiQueryGenerator();

			System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
			String entityCode = FiQueryGenerator.tableToEntityClass(fxSimpleDialog.getTxValue(), getAndSetupActiveServerJdbi());

			if (!FiString.isEmpty(entityCode)) {
				getCodeGenMainView().getFxTextArea().appendText(entityCode);
			} else {
				getCodeGenMainView().getFxTextArea().appendText("N/A");
			}

		}

	}


	private void actBtnFiTableColListWithEnumFields() {

		ModEntityListCont modEntityListCont = ModalCodeGenDialog.showDialogSelectEntityClass();

		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {

			FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextField, "Enum Sınıfının Adını Giriniz :");
			fxSimpleDialog.openAsDialogSync();

			String fieldEnumClass = "EntegreField";

			if (fxSimpleDialog.isClosedWithOk()) {
				if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
					fieldEnumClass = fxSimpleDialog.getTxValue();
				}
			}

			getCodeGenMainView().getFxTextArea().appendTextLnAsyn(
					FiQueryGenerator.codeTableColsV2(selectedEntity.getClazz(), false, fieldEnumClass));

			getCodeGenMainView().getFxTextArea().appendNewLine();
			getCodeGenMainView().getFxTextArea().appendNewLine();

			getCodeGenMainView().getFxTextArea().appendTextLnAsyn(
					FiQueryGenerator.codeColsEnum(selectedEntity.getClazz(), false));

		}

	}

	private void actBtnFiTableColListWithFieldHeader() {

		ModEntityListCont modEntityListCont = ModalCodeGenDialog.showDialogSelectEntityClass();

		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {

			getCodeGenMainView().getFxTextArea().appendTextLnAsyn(
					FiQueryGenerator.codeTableColsSimple(selectedEntity.getClazz(), false));

			getCodeGenMainView().getFxTextArea().appendNewLine();
			getCodeGenMainView().getFxTextArea().appendNewLine();

			getCodeGenMainView().getFxTextArea().appendTextLnAsyn(
					FiQueryGenerator.codeColsEnum(selectedEntity.getClazz(), false));

		}


	}

	private void actBtnSelectServer1() {

		ServerConfig serverConfig1 = actServerSelect();

		if (serverConfig1 != null) {

			Fdr<Jdbi> jdbiFdr = createJdbi(serverConfig1);
			if (jdbiFdr.isTrueBoResult()) {
				getModalSql().setServerConfig1(serverConfig1);
				getModalSql().setJdbi1(jdbiFdr.getValue());

				String txMessage = String.format("Server: %s Db: %s", serverConfig1.getServer(), serverConfig1.getServerDb());
				getCodeGenMainView().getBtnServer1().setText(txMessage);
				FxDialogShow.showPopInfo("Server Bağlantı Başarılı **");
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
				getModalSql().setServerConfig2(serverConfig2);
				getModalSql().setJdbi2(fdrConnection.getValue());
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
		appendTextNewLine(ModalSql.entityFillerMethodFromDb(getAndSetupActiveServerJdbi(), getSelectedClass()));
	}

	private void actAlterNewFields() {

		if (checkServer() && checkClassChoose()) {

			Fdr<List<String>> fdr = FiQueryGenerator.getAlterAddFieldQueries(getSelectedClass(), getAndSetupActiveServerJdbi());

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
		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextFieldInteger, "Id Değerini Giriniz");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {
			Integer idNo = (Integer) fxSimpleDialog.getObjectValue();
			return idNo;
		}

		return null;
	}

	private Object actDialogCandIdEntityForm() {

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.FormAutoByCandIdFields);
		fxSimpleDialog.setEntityClass(getSelectedClass());
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {
			Object formValue = fxSimpleDialog.getFormValue();
			return formValue;
		}

		return null;
	}

	public Jdbi getAndSetupActiveServerJdbi() {
		if (getModalSql().getJdbi1() == null) {
			if (getModalSql().getServerConfig1() == null) {
				actServerSelect();
			}
		}
		return getModalSql().getJdbi1();
	}

	private ServerConfig actServerSelect() {

		Properties properties = FiPropertyFile.readProperties(getPropPath());
		ServerConfig selectedServer = null;

		List<ServerConfig> listServer = new ArrayList<>();

		for (int i = 0; i < 20; i++) {

			String name = properties.getProperty("server-" + i + "-name", "");
			String db = properties.getProperty("server-" + i + "-db", "");
			String defNo = properties.getProperty("server-" + i + "-def-no", "");

			// Server-user-pass bilgiler
			String server = properties.getProperty("server-def-" + defNo, "");
			String user = properties.getProperty("server-def-" + defNo + "-user", "");
			String pass = properties.getProperty("server-def-" + defNo + "-key", "");

			if (FiString.isEmpty(defNo) | FiString.isEmpty(server)) {
				continue;
			}

			ServerConfig serverConfig = new ServerConfig();
			serverConfig.setServer(server);
			serverConfig.setServerDb(db);
			serverConfig.setServerUser(user);
			serverConfig.setServerKey(pass);
			serverConfig.setName(name);

			listServer.add(serverConfig);

		}


		if (listServer.size() == 0) {
			FxDialogShow.showPopWarn("Server Ayarları Girilmemiş");

		} else {

			FxSimpleContGen<ServerConfig> fxSimpleContGen = new FxSimpleContGen<>(true);
			FxTableView2 fxTableView2 = new FxTableView2();

			fxSimpleContGen.getModView().addGrowPushSpan(fxTableView2);

//			List<FiCol> listCols = ListFiTableColBuilder.build().addFields("name", "server").getList();

			FiColList fiCols = new FiColList();
			fiCols.add(FiCol.build("İsim", "name"));
			fiCols.add(FiCol.build("Sunucu", "server"));

			fxTableView2.setEnableLocalFilterEditor(true);
			fxTableView2.addAllFiColsAuto(fiCols);
			fxTableView2.setItemsAsFilteredList(listServer);
			fxTableView2.activateExtensionFxTableSelectAndClose(fxSimpleContGen);
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
			Jdbi jdbi = FiJdbiFactory.createJdbi(serverConfig.getServer(), serverConfig.getServerDb()
					, serverConfig.getServerUser(), serverConfig.getServerKey());
			fdr.setValue(jdbi);
			fdr.setBoResult(true);
			return fdr;
		} catch (Exception ex) {
			ex.printStackTrace();
			fdr.setMessage("Bağlantı kurulurken hata oluştu. Bağlantı bilgilerini kontrol ediniz.");
			fdr.setBoResult(false);
			return fdr;
		}

	}

	private boolean isEnableDbOperation() {
		return getCodeGenMainView().getChkVeritabandaOlustur().isSelected();
	}

	public void appendTextNewLine(String txValue) {
		getCodeGenMainView().getFxTextArea().appendTextLnAsyn(txValue);
	}

	public void actionResult(String txResult) {
		getCodeGenMainView().getFxTextArea().appendTextLnAsyn(txResult);
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

//	public void setJdbi1(Jdbi jdbi1) {
//		this.jdbi1 = jdbi1;
//	}

	public void setCodeGenMainView(ModHomeCodeGenView codeGenMainView) {
		this.codeGenMainView = codeGenMainView;
	}

	public Class getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(Class selectedClass) {
		this.selectedClass = selectedClass;
	}

//	public Jdbi getJdbi1() {
//		return jdbi1;
//	}

	public Class getSelectedClass2() {
		return selectedClass2;
	}

	public void setSelectedClass2(Class selectedClass2) {
		this.selectedClass2 = selectedClass2;
	}

//	public Jdbi getJdbi2() {
//		return jdbi2;
//	}

//	public void setJdbi2(Jdbi jdbi2) {
//		this.jdbi2 = jdbi2;
//	}

	public File getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
	}

	public ModalSql getModalSql() {
		if (modalSql == null) {
			modalSql = new ModalSql();
		}
		return modalSql;
	}

	public void setModalSql(ModalSql modalSql) {
		this.modalSql = modalSql;
	}

	public ModalHome getModalHome() {
		if (modalHome == null) {
			modalHome = new ModalHome();
		}
		return modalHome;
	}

	public void setModalHome(ModalHome modalHome) {
		this.modalHome = modalHome;
	}
}
