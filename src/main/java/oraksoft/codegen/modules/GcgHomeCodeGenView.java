package oraksoft.codegen.modules;

import javafx.scene.layout.Pane;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.mvc.IFiModView;

public class GcgHomeCodeGenView implements IFiModView {

	// Layout Components
	private FxMigPane migRoot;

	private FxMigPane migHeader;
	private FxMigPane migMenu;
	private FxMigPane migContent;

	// Other Components

	private FxTextArea txaMainOutput;
	private FxButton btnServer1;
	private FxButton btnServer2;
	private FxButton btnClassSec;
	private FxButton btnClassSec2;
	private FxButton btnDosyaSec;

	private FxMenuButton csharpIslemler;
	private FxCheckBox chkDosyayaYazdir;
	private FxCheckBox chkVeritabandaOlustur;

	private FxComboBoxSimple cmbFiColHelpers;
	private FxComboBoxSimple cmbDbRead;
	private FxComboBoxSimple cmbQueryGenerator;
	private FxComboBoxSimple cmbTypeScriptOperations;
	private FxComboBoxSimple cmbExcelIslemler;
	private FxComboBoxSimple cmbXmlAraclar;
	private String txDosyaYolu;

	@Override
	public Pane getRootPane() {
		return getMigRoot();
	}

	public void initGui() {
		//FxMpConfig.debugMode = true;
		// Root Container
		migRoot = new FxMigPane(FxMigHp.bui().lcgInset0Gap55().lcgNoGrid().getLcg());
		// Layout Containers
		migHeader = new FxMigPane(FxMigHp.bui().lcgInset0Gap55().getLcg());
		migMenu = new FxMigPane(FxMigHp.bui().lcgInset3Gap33().lcgNoGrid().getLcg());
		migContent = new FxMigPane(FxMigHp.bui().lcgInset3Gap33().getLcg());

		//rootMigPane.add(migHeader, "growx,pushx,wrap");
		migRoot.add(migMenu, "growx,pushx,wrap");
		migRoot.add(migContent, "grow,push,span");

		// Other components
		chkDosyayaYazdir = new FxCheckBox("Dosyaya Yazdır");
		chkVeritabandaOlustur = new FxCheckBox("Veritabanda Oluştur.(Create için)");
		btnServer1 = new FxButton("Server Seç");

		btnClassSec = new FxButton("Class Seç");
		btnServer2 = new FxButton("Server(2)");
		btnClassSec2 = new FxButton("Class(2) Seç");

		btnDosyaSec = new FxButton("Dosya Seç");

		cmbFiColHelpers = new FxComboBoxSimple("FiTableCol Generations");
		cmbFiColHelpers.setMaxWidth(200d);
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
		migMenu.add(cmbFiColHelpers, "");
		migMenu.add(cmbDbRead, "");
		migMenu.add(cmbQueryGenerator, "");
		migMenu.add(cmbTypeScriptOperations, "");
		migMenu.add(cmbExcelIslemler, "wrap");
		migMenu.add(cmbXmlAraclar, "");
		migMenu.add(csharpIslemler, "wrap");

		//migMenu.add(new FxLabel("-----"), "span");

		txaMainOutput = new FxTextArea();
		migContent.add(txaMainOutput, "span,grow,push");

	}

	public FxMigPane getMigRoot() {
		return migRoot;
	}

	public FxTextArea getTxaMainOutput() {
		return txaMainOutput;
	}

	public String getTxDosyaYolu() {return txDosyaYolu;}

	public void setTxDosyaYolu(String txDosyaYolu) {this.txDosyaYolu = txDosyaYolu;}

	public FxCheckBox getChkVeritabandaOlustur() {return chkVeritabandaOlustur;}

	public FxButton getBtnClassSec() {return btnClassSec;}

	public FxButton getBtnServer1() {return btnServer1;}

	public FxComboBoxSimple getCmbFiColHelpers() {
		return cmbFiColHelpers;
	}

	public FxComboBoxSimple getCmbDbRead() {return cmbDbRead;}

	public FxComboBoxSimple getCmbQueryGenerator() {return cmbQueryGenerator;}

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
