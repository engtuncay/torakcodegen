package gapisoft.codegen.modules;

import oraksoft.model.ModelTableColGenerate;
import ozpasyazilim.mikro.util.codegen.FiCodeGeneratorTest;
import ozpasyazilim.utils.configmisc.ServerConfig;
import gapisoft.codegen.entity.EntityClazz;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.core.FiCollection;
import ozpasyazilim.utils.core.FiExcel;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.ficodegen.FiTypescriptHelper;
import ozpasyazilim.utils.fidborm.*;
import ozpasyazilim.utils.gui.components.ComboItem;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.mvc.AbsFxSimpleCont;
import ozpasyazilim.utils.mvc.IFxSimpleCont;
import ozpasyazilim.utils.core.FiPropertyFile;
import ozpasyazilim.utils.returntypes.FdrResult;
import ozpasyazilim.utils.table.FiTableCol;
import ozpasyazilim.utils.table.ListFiTableColBuilder;

import java.io.File;
import java.util.*;

/**
 * Entity List Class : ModEntityListCont
 */
public class ModHomeCodeGenCont extends AbsFxSimpleCont implements IFxSimpleCont {

	ModHomeCodeGenView codeGenMainView;
	Jdbi activeServerJdbi;
	Class selectedClass;

	String propPath = "appcodegen.properties";

	private ServerConfig serverConfig;

//	private enum enumComboItem {
//		ExcelToFiTableColWithFieldName, ExcelToFiTableCol, AlanListesi, AlanListesiByIdWithValue, AlanListByCandIdWithValue, TableToEntity, TableToFillEntity, DbKayitSablonById, DbKayitSablonByCandIds, AlterNewFields, SqlQueryToFiTableCol, SqlQueryToFiTableColGenerator, ExcelToEntity, ClassToFiTableColGenerator, ExcelToFiTableColViaMethods, QueryAlterAdd, CreateQuery;
//	}

	@Override
	public void initCont() {

		codeGenMainView = new ModHomeCodeGenView();
		codeGenMainView.initGui();

		//codeGenMainView.getBtnCreateQuery().setOnAction(event -> actQueryCreate());

		codeGenMainView.getCmbDbToCode().addComboItem(ComboItem.buildWitAction("Tablodan Entity Oluştur", this::actTableToEntity));

		codeGenMainView.getCmbDbToCode().addComboItem(ComboItem.buildWitAction("Veritabanından Entity Alanlarını Doldurma Metodu", this::actEntityFillerMethodFromDb));

		codeGenMainView.getCmbDbToCode().addComboItem(ComboItem.buildWitAction("Veritabanına eklenecek alanların Alter Sorguları", this::actAlterNewFields));

		// **** Table Col Generate Combobox

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction("Excelden FiTableCol List oluştur.(Alan isimli)",() -> {
			appendTextNewLine(ModelTableColGenerate.actExcelToFiTableColWithFieldName());
		} ));

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

		codeGenMainView.getCmbTableColGenerate().addComboItem(ComboItem.buildWitAction( "Alan Listesi By Cand Id With Value",this::actAlanListesiByCandIdWithValue));
		//actAlanListesiByCandIdWithValue();//AlanListByCandIdWithValue

		// **** Db Read Combobox

		codeGenMainView.getCmbDbRead().addComboItem(ComboItem.buildWitAction( "Kayıt Şablon By Id",this::actDbKayitSablonById));
//		actDbKayitSablonById(); //DbKayitSablonById

		codeGenMainView.getCmbDbRead().addComboItem(ComboItem.buildWitAction( "Kayıt Şablon By Cand Ids",this::actDbKayitSablonByCandIds));
		//actDbKayitSablonByCandIds();//DbKayitSablonByCandIds

		// **** Db To Code Combobox

		codeGenMainView.getCmbExcelIslemler().addComboItem(ComboItem.buildWitAction("Excel'den Entity Oluştur",this::actExcelToEntity));
		// enumComboItem.ExcelToEntity

		codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItem.buildWitAction("Create Query",this::actQueryCreate));
		//actQueryCreate();//CreateQuery

		codeGenMainView.getCmbQueryGenerator().addComboItem(ComboItem.buildWitAction( "Alter Table Field(Add)",this::actAlterNewFields));
		//actAlterNewFields();//QueryAlterAdd

		// Combobox Listener Ayarları

		codeGenMainView.getCmbDbToCode().activateComboItemOnAction();
		codeGenMainView.getCmbDbRead().activateComboItemOnAction();
		codeGenMainView.getCmbTableColGenerate().activateComboItemOnAction();
		codeGenMainView.getCmbExcelIslemler().activateComboItemOnAction();
		codeGenMainView.getCmbQueryGenerator().activateComboItemOnAction();

		// -------- combobox sonu

		//codeGenMainView.getBtnCodeTableCol().setOnAction(event -> actBtnCreateQuery());

		codeGenMainView.getBtnClassSec().setOnAction(event -> actBtnClassSec());

		codeGenMainView.getBtnServerConfig().setOnAction(event -> actBtnServerConfig());

		codeGenMainView.getBtnCodeGenFiTableColFromExcel().setOnAction(event -> actBtnCodeGenFiTableColFromExcel());

		codeGenMainView.getBtnCodeTypescript().setOnAction(event -> actBtnTypescriptEntity());

		codeGenMainView.getBtnCodeEntityFieldFillerMethod().setOnAction(event -> actBtnFiTableColListWithFieldHeader());

		codeGenMainView.getBtnCodeEntityFieldFillerMethodWithEnumFields().setOnAction(event -> actBtnFiTableColListWithEnumFields());

		codeGenMainView.getBtnCodeGenFiTableColFromExcel().setOnAction(event -> actBtnCodeGenFiTableColFromExcel());


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

		File fileExcel = new FiFileHelper().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			String fieldPrefix = "";

			FxSimpleDialog fxSimpleDialog2 = FxSimpleDialog.buildTextFieldDialog("Lütfen ön ek yazınız.(Eklenecekse)");

			if (fxSimpleDialog2.isClosedWithOk()) {
				fieldPrefix = fxSimpleDialog2.getTxValue();
			}

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());

			appendTextNewLine(FiCodeHelper.codeFiTableColsFromHeaderAndFieldNameWithMethodsForExcel(listHeader, "Excel", listFields, fieldPrefix));

		}


	}


	// **** İşlemler

	private void actExcelToEntity() {

		File fileExcel = new FiFileHelper().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);
			List<String> listFields = new FiExcel().readExcelRowIndex(fileExcel, 1);

			//FiConsole.debugListObjectsToString(listHeader,getClass());

			String className = "EntityName";
			FxSimpleDialog fxSimpleDialog = FxSimpleDialog.buildTextFieldDialog("Lütfen sınıf ismini yazınız");

			if (fxSimpleDialog.isClosedWithOk()) {
				className = fxSimpleDialog.getTxValue();
			}

			String fieldPrefix = "";

			FxSimpleDialog fxSimpleDialog2 = FxSimpleDialog.buildTextFieldDialog("Lütfen ön ek yazınız.(Eklenecekse)");

			if (fxSimpleDialog2.isClosedWithOk()) {
				fieldPrefix = fxSimpleDialog2.getTxValue();
			}

			appendTextNewLine(FiCodeHelper.codeEntityClass(listHeader, listFields, className, fieldPrefix));

		}

	}

	private void actDbKayitSablonByCandIds() {
		Object form = actDialogCandIdEntityForm();

		//Loghelper.getInstance(getClass()).debug("Id:" + idNo);

		if (form != null) {

			FdrResult<List> fdrResult = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectListByCandIds(form);

			if (fdrResult.getValue().size() > 0) {

				//Loghelper.getInstance(getClass()).debug("Size:"+fiDbResult.getResValue().size());

				FiExcel.saveSablonExcelByClass(this, fdrResult.getValue(), getSelectedClass(), "ozpasentegre");

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

			FdrResult<List> fdrResult = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectListById(idNo);

			if (fdrResult.getValue().size() > 0) {

				FiExcel.saveSablonExcelByClass(this, fdrResult.getValue(), getSelectedClass(), "ozpasentegre");

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

	private Boolean checkServer() {

		if (getActiveServerJdbi() == null) {
			actBtnServerConfig();
		}

		return getActiveServerJdbi() != null;

	}

	private Boolean checkClassChoose() {

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

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextAreaString);
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

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextAreaString);
		fxSimpleDialog.setMessageHeader("Lütfen sorgu cümleciğini giriniz:");
		fxSimpleDialog.openAsDialogSync();

		if (!FiString.isEmpty(fxSimpleDialog.getTxValue())) {
			String sqlQuery = fxSimpleDialog.getTxValue();

			List<String> queryFields = FiQueryGenerator.getQueryFieldsAsString(sqlQuery);

			String result = FiCodeHelper.codeFiTableColsFromHeaderAndFieldName(queryFields, "", queryFields);
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

			FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextFieldInteger, "Id Değerini Giriniz");
			fxSimpleDialog.openAsDialogSync();

			if (fxSimpleDialog.isClosedWithOk()) {

				Integer idNo = (Integer) fxSimpleDialog.getObjectValue();

				Loghelper.get(getClass()).debug("Id:" + idNo);

				if (idNo != null) {

					FdrResult<Optional<Object>> fdrResult = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectEntityById(idNo);

					if (fdrResult.getValue().isPresent()) {
						//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
						String result = FiCodeGeneratorTest.codeEntityFieldsWithValue(getSelectedClass(), fdrResult.getValue());
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

			FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextFieldString, "Id Değerini Giriniz");
			fxSimpleDialog.openAsDialogSync();

			if (fxSimpleDialog.isClosedWithOk()) {

				String candIdValue = fxSimpleDialog.getTxValue();

				Loghelper.get(getClass()).debug("Id:" + candIdValue);

				if (candIdValue != null) {

					FdrResult<Optional<Object>> fdrResult = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectEntityByStringCandId1(candIdValue);

					if (fdrResult.getValue().isPresent()) {
						//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
						String result = FiCodeGeneratorTest.codeEntityFieldsWithValue(getSelectedClass(), fdrResult.getValue());
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

		ModEntityListCont modEntityListCont = showDialogSelectEntityClass();

		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {
			setSelectedClass(selectedEntity.getClazz());
			getCodeGenMainView().getBtnClassSec().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
		}

	}

//	private void actCmbDbToCodeChanged(ComboItem comboItem) {
//		if (comboItem == null || comboItem.getValue() == null) return;
//
//		if (comboItem.getValue().equals(enumComboItem.TableToEntity.toString())) {
//			Loghelper.get(getClass()).info("Table To Entity");
//			//System.out.println("m");
//			actTableToEntity(); //TableToEntity
//		}
//
//		if (comboItem.getValue().equals(enumComboItem.TableToFillEntity.toString())) {
//			//Loghelper.getInstance(getClass()).info("Table To Entity");
//			//System.out.println("m");
//			actEntityFillerMethodFromDb(); // TableToFillEntity
//		}
//
//		if (comboItem.getValue().equals(enumComboItem.AlterNewFields.toString())) {
//			actAlterNewFields(); //AlterNewFields
//		}
//
//		getCodeGenMainView().getCmbDbToCode().clearSelectionFi();
//
//	}

	private void actTableToEntity() {

		Jdbi activeServerJdbi = getAndSetupActiveServerJdbi();

		if (activeServerJdbi != null) {
			FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
			//System.out.println("Connected");
		} else {
			FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
			return;
		}

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextFieldString, "Tablo Adını Giriniz:");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {

			FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
			FiQueryGenerator fiQueryGenerator = new FiQueryGenerator();

			System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
			String entityCode = FiQueryGenerator.codeEntityClass(fxSimpleDialog.getTxValue(), getAndSetupActiveServerJdbi());

			if (!FiString.isEmpty(entityCode)) {
				getCodeGenMainView().getFxTextArea().appendText(entityCode);
			} else {
				getCodeGenMainView().getFxTextArea().appendText("N/A");
			}

		}

	}


	private void actBtnFiTableColListWithEnumFields() {

		ModEntityListCont modEntityListCont = showDialogSelectEntityClass();

		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {

			FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextFieldString, "Enum Sınıfının Adını Giriniz :");
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

		ModEntityListCont modEntityListCont = showDialogSelectEntityClass();

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

	private void actBtnServerConfig() {

		if (actSelectServer() != null) {

			if (setupActiveServerJdbi() != null) {
				getCodeGenMainView().getBtnServerConfig().setText("Server:" + getServerConfig().getServer() + " / " + getServerConfig().getServerDb());
				FxDialogShow.showPopInfo("Server Bağlantı Başarılı **");
			} else {
				FxDialogShow.showPopError("Server Bağlantı Başarısız !!!");
			}

		} else {
			FxDialogShow.showPopWarn("Lütfen Server Seçiniz...");
		}
	}

	private void actBtnCodeGenFiTableColFromExcel() {

		File fileExcel = new FiFileHelper().actFileChooserForExcelXlsxFromDesktop();

		if (fileExcel != null) {

			List<String> listHeader = new FiExcel().readExcelRowIndex(fileExcel, 0);

			//FiConsole.debugListObjectsToString(listHeader,getClass());

			appendTextNewLine(FiQueryGenerator.codeFiTableColsFromHeader(listHeader, "Excel"));

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

	/**
	 * Örnek bir kayıda göre not null kontrollü , alanları set eden kodları ortaya çıkarır.
	 */
	private void actEntityFillerMethodFromDb() {

		if (checkServer() && checkClassChoose()) {

			Integer idNo = actDialogIdSelection();

			Loghelper.get(getClass()).debug("Id:" + idNo);

			if (idNo != null) {

				FdrResult<Optional<Object>> fdrResult = new RepoJdbiCustom(getAndSetupActiveServerJdbi(), getSelectedClass()).jdSelectEntityById(idNo);

				if (fdrResult.getValue().isPresent()) {
					//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
					String result = FiQueryGenerator.codeEntityFieldsInitMethod(getAndSetupActiveServerJdbi(), getSelectedClass(), fdrResult.getValue());
					appendTextNewLine(result);
				} else {
					System.out.println("Db den Veri Okunamadı");
				}

			} else {
				FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
			}


		} else {
			FxDialogShow.showPopError("Lütfen bir sınıf seçin");
		}

		//ModEntityListCont modEntityListCont = showDialogSelectEntityClass();
		//EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();
		//if (selectedEntity != null) {
		//}

	}

	private void actAlterNewFields() {

		if (checkServer() && checkClassChoose()) {

			FdrResult<List<String>> fdrResult = FiQueryGenerator.getAlterAddFieldQueries(getSelectedClass(), getAndSetupActiveServerJdbi());

			FxDialogShow.showDbResult(fdrResult);

			String sqltum = "";
			if (fdrResult.getBoResultNotNull()) {
				if (!FiCollection.isEmpty(fdrResult.getValue())) {
					appendTextNewLine("");
					for (String s : fdrResult.getValue()) {
						appendTextNewLine(s);
						sqltum += s + "\n";
					}
					appendTextNewLine("");
				}
			}

			if (isEnableDbOperation() && getAndSetupActiveServerJdbi() != null && !FiString.isEmpty(sqltum)) {

				FdrResult fdrAlter = new RepoJdbiString(getAndSetupActiveServerJdbi()).jdUpdateBindMapViaAtTire(sqltum, null);

				if (fdrResult.getBoResultNotNull()) {
					fdrResult.setMessage("Değişiklikler başarıyla uygulandı.");
				}

				FxDialogShow.showDbResult(fdrResult);

			}


		} else {
			FxDialogShow.showPopError("Server veya Sınıfı seçiminizi kontrol ediniz.");
		}

	}

	private Integer actDialogIdSelection() {
		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.TextFieldInteger, "Id Değerini Giriniz");
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {
			Integer idNo = (Integer) fxSimpleDialog.getObjectValue();
			return idNo;
		}

		return null;
	}

	private Object actDialogCandIdEntityForm() {

		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialog.SimpleDialogType.FormByCandId);
		fxSimpleDialog.setEntityClass(getSelectedClass());
		fxSimpleDialog.openAsDialogSync();

		if (fxSimpleDialog.isClosedWithOk()) {
			Object formValue = fxSimpleDialog.getFormValue();
			return formValue;
		}

		return null;
	}

	private Jdbi getAndSetupActiveServerJdbi() {

		if (activeServerJdbi == null) {

			if (getServerConfig() == null) {
				actSelectServer();
			}

//			if(getServerConfig()!=null){
//				setupActiveServerJdbi();
//			}
		}

		return activeServerJdbi;
	}

	private ServerConfig actSelectServer() {

		Properties properties = FiPropertyFile.readProperties(getPropPath());
		ServerConfig entityDefault = null;

		List<ServerConfig> listServer = new ArrayList<>();

		for (int i = 0; i < 20; i++) {

			String server = properties.getProperty("server-" + i, "");

			if (FiString.isEmpty(server)) {
				continue;
			}

			String db = properties.getProperty("server-" + i + "-db", "");
			String user = properties.getProperty("server-" + i + "-user", "");
			String pass = properties.getProperty("server-" + i + "-key", "");
			String name = properties.getProperty("server-" + i + "-name", "");

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

			FxSimpleCont<ServerConfig> fxSimpleCont = new FxSimpleCont<>(true);

			FxTableView2 fxTableView2 = new FxTableView2();

			fxSimpleCont.getModView().add(fxTableView2, "grow,push");

			List<FiTableCol> listCols = ListFiTableColBuilder.build().addFields("name", "server").getList();
			fxTableView2.addAllFiTableColsAuto(listCols);
			fxTableView2.setItemsAsFilteredList(listServer);
			fxTableView2.activateExtensionFxTableSelectAndClose(fxSimpleCont);

			fxSimpleCont.openAsDialogSync(null, null);

			entityDefault = fxSimpleCont.getEntitySelected();
			setServerConfig(entityDefault);

			if (entityDefault != null) {
				FxDialogShow.showPopInfo(entityDefault.getServer() + " server seçildi.");
			}
			//FiConsole.debug(entityDefault);
		}

		return entityDefault;
	}

	private Jdbi setupActiveServerJdbi() {

		if (getServerConfig() == null) {
			FxDialogShow.showPopWarn("Lütfen Server seçimi yapınız.");
			return null;
		}

		try {
			Jdbi jdbi = FiJdbiFactory.createJdbi(getServerConfig().getServer(), getServerConfig().getServerDb()
					, getServerConfig().getServerUser(), getServerConfig().getServerKey());
			setActiveServerJdbi(jdbi);
			return jdbi;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private void actQueryCreate() {

		if (checkServer() && checkClassChoose()) {

			String sqlCreate = null;

			if (getSelectedClass() != null) {
				sqlCreate = FiQueryGenerator.createQuery20(getSelectedClass());
				getCodeGenMainView().getFxTextArea().appendTextLnAsyn(sqlCreate);
			}

			if (isEnableDbOperation() && getAndSetupActiveServerJdbi() != null) {

				FdrResult fdrResult = new RepoJdbiString(getAndSetupActiveServerJdbi()).jdUpdateBindMapViaAtTire(sqlCreate, null);

				if (fdrResult.getBoResultNotNull()) {
					fdrResult.setMessage("Veritabanı Oluşturuldu.");
				}

				FxDialogShow.showDbResult(fdrResult);

//			if (lnRowsAffected > 0) {
//				FxDialogShow.showPopInfo("Veritabanı oluşturuldu.");
//			} else {
//				FxDialogShow.showPopError("Hata oluştu.");
//			}


			} else {
				FxDialogShow.showPopError("Server veya Sınıfı seçiminizi kontrol ediniz.");
			}

		}


	}

	private boolean isEnableDbOperation() {
		return getCodeGenMainView().getChkVeritabandaOlustur().isSelected();
	}

	private void appendTextNewLine(String txValue) {
		getCodeGenMainView().getFxTextArea().appendTextLnAsyn(txValue);
	}

	private void actBtnTypescriptEntity() {

		ModEntityListCont modEntityListCont = showDialogSelectEntityClass();

		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();

		if (selectedEntity != null) {
			getCodeGenMainView().getFxTextArea().appendTextLnAsyn(
					FiTypescriptHelper.tsEntity(selectedEntity.getClazz()));
		}

	}

	private ModEntityListCont showDialogSelectEntityClass() {

		ModEntityListCont modEntityListCont = new ModEntityListCont();
		modEntityListCont.initCont();
		FxDialogShow.build().nodeModalByIFxMod(null, modEntityListCont, null, null, null);
		return modEntityListCont;

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

	public void setActiveServerJdbi(Jdbi activeServerJdbi) {
		this.activeServerJdbi = activeServerJdbi;
	}

	public void setCodeGenMainView(ModHomeCodeGenView codeGenMainView) {
		this.codeGenMainView = codeGenMainView;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public Class getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(Class selectedClass) {
		this.selectedClass = selectedClass;
	}

	public Jdbi getActiveServerJdbi() {
		return activeServerJdbi;
	}
}