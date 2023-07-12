package oraksoft.codegen.modal;

import ozpasyazilim.utils.gui.fxcomponents.FxTextArea;

public class MolcdgHome {

	FxTextArea fxTextArea;
	MolcdgSql molcdgSql;

	public MolcdgHome() {
	}

	public MolcdgHome(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}

	public void appendTextNewLine(String txValue) {
		getFxTextArea().appendTextLnAsyn(txValue);
	}

	public FxTextArea getFxTextArea() {
		return fxTextArea;
	}

	public MolcdgSql getModalSql() {
		return molcdgSql;
	}

	public void setModalSql(MolcdgSql molcdgSql) {
		this.molcdgSql = molcdgSql;
	}

	public void setFxTextArea(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}
}
