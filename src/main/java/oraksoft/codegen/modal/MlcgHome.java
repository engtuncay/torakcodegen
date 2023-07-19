package oraksoft.codegen.modal;

import ozpasyazilim.utils.gui.fxcomponents.FxTextArea;

public class MlcgHome {

	FxTextArea fxTextArea;
	MlcgSql mlcgSql;

	public MlcgHome() {
	}

	public MlcgHome(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}

	public void appendTextNewLine(String txValue) {
		getFxTextArea().appendTextLnAsyn(txValue);
	}

	public FxTextArea getFxTextArea() {
		return fxTextArea;
	}

	public MlcgSql getModalSql() {
		return mlcgSql;
	}

	public void setModalSql(MlcgSql mlcgSql) {
		this.mlcgSql = mlcgSql;
	}

	public void setFxTextArea(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}
}
