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
	private FxComboBoxSimple cmbXmlAraclar;
	private String txDosyaYolu;


	private FxTextArea fxTextArea;

	// Kaldırılacak
	//private FxButton btnCodeGen;
	private FxButton btnCodeTypescript;
	private FxButton btnCodeEntityFieldFillerMethod;
	private FxButton btnCodeTableCol;
	//private FxButton btnCodeEntityFieldFillerMethodFromDb;
	private FxButton btnCodeGenFiTableColFromExcel;
	private FxButton btnCodeEntityFieldFillerMethodWithEnumFields;

	private FxButton btnServerConfig;
	private FxButton btnServer2;
	private FxButton btnClassSec;
	private FxButton btnClassSec2;
	private FxButton btnDosyaSec;

	private FxMenuButton csharpIslemler;

	@Override
	public Pane getRootPane() {
		return getRootMigPane();
	}

	public void initGui() {

		//FxMpConfig.debugMode = true;

		rootMigPane = new FxMigPane(FxMigHelper.bui().lcStInset0Gap55().lcNoGrid().genLc());

		FxMigPane header = new FxMigPane(FxMigHelper.bui().lcStInset0Gap55().genLc());
		FxMigPane menu = new FxMigPane(FxMigHelper.bui().lcStInset3().genLc());
		FxMigPane content = new FxMigPane(FxMigHelper.bui().lcStInset3().genLc());

		rootMigPane.add(header, "growx,pushx,wrap");
		rootMigPane.add(menu,  FxMigHelper.bcc("grow,push").ccWidth("100px").genCc());
		rootMigPane.add(content, "grow,push,span");

		chkDosyayaYazdir = new FxCheckBox("Dosyaya Yazdır");
		chkVeritabandaOlustur = new FxCheckBox("Veritabanda Oluştur.(Create için)");
		btnServerConfig = new FxButton("Server Seç");

		btnClassSec = new FxButton("Class Seç");
		btnServer2 = new FxButton("Server(2)");
		btnClassSec2 = new FxButton("Class(2) Seç");

		btnDosyaSec = new FxButton("Dosya Seç");

		cmbDbToCode = new FxComboBoxSimple("Db To Code");
		cmbTableColGenerate = new FxComboBoxSimple("FiTableCol Generations");
		cmbDbRead = new FxComboBoxSimple("Db Table Read");
		cmbQueryGenerator = new FxComboBoxSimple("Query Generate");
		cmbTypeScriptOperations = new FxComboBoxSimple("TypeScript Operations");
		cmbExcelIslemler = new FxComboBoxSimple("Excel İşlemler");
		cmbXmlAraclar = new FxComboBoxSimple("Xml Araçlar");

		csharpIslemler = new FxMenuButton("Csharp İşlemler");

		menu.add(chkDosyayaYazdir, "span");
		menu.add(chkVeritabandaOlustur, "span");
		menu.add(btnServerConfig, "span");
		menu.add(btnClassSec, "span");
		menu.add(btnDosyaSec, "span");

		menu.add(new FxLabel("-----"), "span");
		menu.add(btnServer2,"span");
		menu.add(btnClassSec2,"span");
		menu.add(new FxLabel("-----"), "span");
		menu.add(cmbDbToCode, "span");
		menu.add(cmbTableColGenerate, "span");
		menu.add(cmbDbRead, "span");
		menu.add(cmbQueryGenerator, "span");
		menu.add(cmbTypeScriptOperations, "span");
		menu.add(cmbExcelIslemler, "span");
		menu.add(cmbXmlAraclar, "span");
		menu.add(csharpIslemler, "span");

		menu.add(new FxLabel("-----"), "span");

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

	public FxButton getBtnServer2() {return btnServer2;}

	public FxButton getBtnClassSec2() {return btnClassSec2;}

	public FxButton getBtnDosyaSec() {
		return btnDosyaSec;
	}

	public FxComboBoxSimple getCmbXmlAraclar() {
		return cmbXmlAraclar;
	}

	public FxMenuButton getCsharpIslemler() {
		return csharpIslemler;
	}
}
