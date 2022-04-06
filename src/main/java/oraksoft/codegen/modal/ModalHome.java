package oraksoft.codegen.modal;

import ozpasyazilim.utils.gui.fxcomponents.FxTextArea;

public class ModalHome {

	FxTextArea fxTextArea;
	ModalSql modalSql;

	public ModalHome() {
	}

	public ModalHome(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}

	public void appendTextNewLine(String txValue) {
		getFxTextArea().appendTextLnAsyn(txValue);
	}

	public FxTextArea getFxTextArea() {
		return fxTextArea;
	}

	public ModalSql getModalSql() {
		return modalSql;
	}

	public void setModalSql(ModalSql modalSql) {
		this.modalSql = modalSql;
	}

	public void setFxTextArea(FxTextArea fxTextArea) {
		this.fxTextArea = fxTextArea;
	}
}
