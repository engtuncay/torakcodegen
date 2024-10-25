package oraksoft.codegen.modal;

import ozpasyazilim.utils.gui.fxcomponents.FxTextArea;

/**
 * Mcg : Orak Code Gen. Modal (Home Module)
 */
public class OcmHome {

	FxTextArea fxTextArea;
	McgSql mcgSql;

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

	public McgSql getModalSql() {
		return mcgSql;
	}

	public void setModalSql(McgSql mcgSql) {
		this.mcgSql = mcgSql;
	}

	public void setFxTextArea(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}
}
