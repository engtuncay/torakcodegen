package oraksoft.codegen.modules;

import javafx.scene.layout.Pane;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.mvc.IFxSimpleView;

public class ModHomeCodeGenView implements IFxSimpleView {

	private FxMigPane rootMigPane;

	private FxCheckBox chkDosyayaYazdir;
	private FxCheckBox chkVeritabandaOlustur;

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

	private FxButton btnServer1;
	private FxButton btnServer2;
	private FxButton btnClassSec;
	private FxButton btnClassSec2;
	private FxButton btnDosyaSec;

	private FxMenuButton csharpIslemler;
	private FxMigPane migHeader;
	private FxMigPane migMenu;
	private FxMigPane migContent;

	@Override
	public Pane getRootPane() {
		return getRootMigPane();
	}

	public void initGui() {

		//FxMpConfig.debugMode = true;

		rootMigPane = new FxMigPane(FxMigHp.bui().lcgInset0Gap55().lcgNoGrid().getLcg());

		migHeader = new FxMigPane(FxMigHp.bui().lcgInset0Gap55().getLcg());
		migMenu = new FxMigPane(FxMigHp.bui().lcgInset3Gap33().lcgNoGrid().getLcg());
		migContent = new FxMigPane(FxMigHp.bui().lcgInset3Gap33().getLcg());

//		rootMigPane.add(migHeader, "growx,pushx,wrap");
//		rootMigPane.add(migMenu,  FxMigHp.bcc("grow,push").ccWidth("100px").genCc());
//		rootMigPane.add(migContent, "grow,push,span");

		//rootMigPane.add(migHeader, "growx,pushx,wrap");
		rootMigPane.add(migMenu, "growx,pushx,wrap");
		rootMigPane.add(migContent, "grow,push,span");

		chkDosyayaYazdir = new FxCheckBox("Dosyaya Yazdır");
		chkVeritabandaOlustur = new FxCheckBox("Veritabanda Oluştur.(Create için)");
		btnServer1 = new FxButton("Server Seç");

		btnClassSec = new FxButton("Class Seç");
		btnServer2 = new FxButton("Server(2)");
		btnClassSec2 = new FxButton("Class(2) Seç");

		btnDosyaSec = new FxButton("Dosya Seç");

		cmbTableColGenerate = new FxComboBoxSimple("FiTableCol Generations");
		cmbTableColGenerate.setMaxWidth(200d);
		cmbDbRead = new FxComboBoxSimple("Db Table Read");
		cmbQueryGenerator = new FxComboBoxSimple("Query Generate");
		cmbTypeScriptOperations = new FxComboBoxSimple("TypeScript Operations");
		cmbExcelIslemler = new FxComboBoxSimple("Excel İşlemler");
		cmbXmlAraclar = new FxComboBoxSimple("Xml Araçlar");

		csharpIslemler = new FxMenuButton("Csharp İşlemler");

		migMenu.add(chkDosyayaYazdir, "");
		migMenu.add(chkVeritabandaOlustur, "wrap");
		migMenu.add(btnServer1, "");
		migMenu.add(btnClassSec, "");
		migMenu.add(btnDosyaSec, "");

		//migMenu.add(new FxLabel("-----"), "span");
		migMenu.add(btnServer2,"");
		migMenu.add(btnClassSec2,"wrap");
		//migMenu.add(new FxLabel("-----"), "span");
		migMenu.add(cmbTableColGenerate, "");
		migMenu.add(cmbDbRead, "");
		migMenu.add(cmbQueryGenerator, "");
		migMenu.add(cmbTypeScriptOperations, "");
		migMenu.add(cmbExcelIslemler, "wrap");
		migMenu.add(cmbXmlAraclar, "");
		migMenu.add(csharpIslemler, "wrap");

		//migMenu.add(new FxLabel("-----"), "span");

		fxTextArea = new FxTextArea();
		migContent.add(fxTextArea, "span,grow,push");

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

	public FxButton getBtnServer1() {return btnServer1;}

	//public FxButton getBtnCodeEntityFieldFillerMethodFromDb() {return btnCodeEntityFieldFillerMethodFromDb;}

	public FxButton getBtnCodeGenFiTableColFromExcel() {return btnCodeGenFiTableColFromExcel;}

	public FxButton getBtnCodeEntityFieldFillerMethodWithEnumFields() {
		return btnCodeEntityFieldFillerMethodWithEnumFields;
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

	public FxMigPane getMigHeader() {return migHeader;}

	public FxMigPane getMigMenu() {return migMenu;}

	public FxMigPane getMigContent() {return migContent;}

}
