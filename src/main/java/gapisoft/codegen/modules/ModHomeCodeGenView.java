package gapisoft.codegen.modules;

import javafx.scene.layout.Pane;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.mvc.IFxSimpleView;
import ozpasyazilim.utils.mvc.IFxTempView;

public class ModHomeCodeGenView implements IFxSimpleView , IFxTempView<ModHomeCodeGenCont> {

	private FxMigPane rootMigPane;

	private FxCheckBox chkDosyayaYazdir;
	private FxCheckBox chkVeritabandaOlustur;
	private FxComboBoxSimple cmbDbToCode;
	private FxComboBoxSimple cmbTableColGenerate;
	private FxComboBoxSimple cmbDbRead;
	private FxComboBoxSimple cmbQueryGenerator;
	private FxComboBoxSimple cmbTypeScriptOperations;
	private FxComboBoxSimple cmbExcelIslemler;
	private String txDosyaYolu;
	private FxButton btnClassSec;
	private FxButton btnServerConfig;
	private FxTextArea fxTextArea;

	// Kaldırılacak
	//private FxButton btnCodeGen;
	private FxButton btnCodeTypescript;
	private FxButton btnCodeEntityFieldFillerMethod;
	private FxButton btnCodeTableCol;
	//private FxButton btnCodeEntityFieldFillerMethodFromDb;
	private FxButton btnCodeGenFiTableColFromExcel;
	private FxButton btnCodeEntityFieldFillerMethodWithEnumFields;

	@Override
	public Pane getRootPane() {
		return getRootMigPane();
	}

	public void initGui() {

		//FxMpConfig.debugMode = true;

		rootMigPane = new FxMigPane(FxMpHelper.bui().lcStandardWithInset0Gap55().lcNoGrid().finalLc());

		FxMigPane header = new FxMigPane(FxMpHelper.bui().lcStandardWithInset0Gap55().finalLc());
		FxMigPane menu = new FxMigPane(FxMpHelper.bui().lcStandardWithInset3().finalLc());
		FxMigPane content = new FxMigPane(FxMpHelper.bui().lcStandardWithInset3().finalLc());

		rootMigPane.add(header, "growx,pushx,wrap");
		rootMigPane.add(menu,  FxMpHelper.bcc("grow,push").ccWidth("100px").finalCc());
		rootMigPane.add(content, "grow,push,span");

		//menu.add(new Label("a"), "span");
		//content.add(new Label("b"), "span");

		chkDosyayaYazdir = new FxCheckBox("Dosyaya Yazdır");
		chkVeritabandaOlustur = new FxCheckBox("Veritabanda Oluştur.(Create için)");
		btnServerConfig = new FxButton("Server Seç");

		btnClassSec = new FxButton("Class Seç");

		//btnCodeGen = new FxButton("Create Table Sorgusu");
		btnCodeTypescript = new FxButton("Typescript Entity");
		btnCodeEntityFieldFillerMethod = new FxButton("FiTableCol List With Field,Header By Class");
		btnCodeTableCol = new FxButton("Tablo Sütunları Listesi Tanımı");

		btnCodeGenFiTableColFromExcel = new FxButton("Excelden FiTableCol oluştur");
		btnCodeEntityFieldFillerMethodWithEnumFields = new FxButton("FiTableCol List With Enum Fields By Class");

		cmbDbToCode = new FxComboBoxSimple("Db To Code");
		cmbTableColGenerate = new FxComboBoxSimple("FiTableCol Generations");
		cmbDbRead = new FxComboBoxSimple("Db Table Read");
		cmbQueryGenerator = new FxComboBoxSimple("Query Generate");
		cmbTypeScriptOperations = new FxComboBoxSimple("TypeScript Operations");
		cmbExcelIslemler = new FxComboBoxSimple("Excel İşlemler");

		menu.add(chkDosyayaYazdir, "span");
		menu.add(chkVeritabandaOlustur, "span");
		menu.add(btnServerConfig, "span");
		menu.add(btnClassSec, "span");

		menu.add(new FxLabel("-----"), "span");
		menu.add(cmbDbToCode, "span");
		menu.add(cmbTableColGenerate, "span");
		menu.add(cmbDbRead, "span");
		menu.add(cmbQueryGenerator, "span");
		menu.add(cmbTypeScriptOperations, "span");
		menu.add(cmbExcelIslemler, "span");

		menu.add(new FxLabel("-----"), "span");
		//menu.add(btnCodeGen, "span");
		menu.add(btnCodeEntityFieldFillerMethod, "span");
		menu.add(btnCodeEntityFieldFillerMethodWithEnumFields, "wrap");
		menu.add(btnCodeTableCol, "span");
		menu.add(btnCodeTypescript, "span");
		menu.add(btnCodeGenFiTableColFromExcel, "wrap");


		fxTextArea = new FxTextArea();
		content.add(fxTextArea, "span,grow,push");

	}

	public FxMigPane getRootMigPane() {
		return rootMigPane;
	}

	public FxTextArea getFxTextArea() {
		return fxTextArea;
	}

	public FxButton getBtnCodeTypescript() {return btnCodeTypescript;}

	public FxButton getBtnCodeEntityFieldFillerMethod() {return btnCodeEntityFieldFillerMethod;}

	public FxButton getBtnCodeTableCol() {return btnCodeTableCol;}

	public String getTxDosyaYolu() {return txDosyaYolu;}

	public void setTxDosyaYolu(String txDosyaYolu) {this.txDosyaYolu = txDosyaYolu;}

	public FxCheckBox getChkDosyayaYazdir() {return chkDosyayaYazdir;}

	public FxCheckBox getChkVeritabandaOlustur() {return chkVeritabandaOlustur;}

	public FxButton getBtnClassSec() {return btnClassSec;}

	public FxButton getBtnServerConfig() {return btnServerConfig;}

	//public FxButton getBtnCodeEntityFieldFillerMethodFromDb() {return btnCodeEntityFieldFillerMethodFromDb;}

	public FxButton getBtnCodeGenFiTableColFromExcel() {return btnCodeGenFiTableColFromExcel;}

	public FxButton getBtnCodeEntityFieldFillerMethodWithEnumFields() {
		return btnCodeEntityFieldFillerMethodWithEnumFields;
	}

	public FxComboBoxSimple getCmbDbToCode() {
		return cmbDbToCode;
	}

	public FxComboBoxSimple getCmbTableColGenerate() {
		return cmbTableColGenerate;
	}

	public FxComboBoxSimple getCmbDbRead() {return cmbDbRead;}

	public FxComboBoxSimple getCmbQueryGenerator() {return cmbQueryGenerator;}

	public FxComboBoxSimple getCmbTypeScriptOperations() {return cmbTypeScriptOperations;}

	public FxComboBoxSimple getCmbExcelIslemler() {
		return cmbExcelIslemler;
	}
}