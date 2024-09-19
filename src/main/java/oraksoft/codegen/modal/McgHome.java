package oraksoft.codegen.modal;

import ozpasyazilim.utils.gui.fxcomponents.FxTextArea;

/**
 * Mcg : Model Code Generator (Home Module)
 */
public class McgHome {

	FxTextArea fxTextArea;
	McgSql mcgSql;

	public McgHome() {
	}

	public McgHome(FxTextArea fxTextArea) {
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
