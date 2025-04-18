package oraksoft.codegen.modal;

import ozpasyazilim.utils.gui.fxcomponents.FxTextArea;

/**
 * Mcg : Orak Code Gen. Modal (Home Module)
 */
public class OcmHome {

	FxTextArea fxTextArea;
	OcmSql ocmSql;

	public OcmHome() {
	}

	public OcmHome(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}

	public void appendTextNewLine(String txValue) {
		getFxTextArea().appendTextLnAsyn(txValue);
	}

	public FxTextArea getFxTextArea() {
		return fxTextArea;
	}

	public OcmSql getModalSql() {
		return ocmSql;
	}

	public void setModalSql(OcmSql ocmSql) {
		this.ocmSql = ocmSql;
	}

	public void setFxTextArea(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}
}
