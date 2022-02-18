package oraksoft.codegen.modal;

import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.core.FiBoolean;
import ozpasyazilim.utils.fidborm.FiQueryGenerator;
import ozpasyazilim.utils.fidborm.RepoJdbiCustom;
import ozpasyazilim.utils.fidborm.RepoJdbiString;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.returntypes.Fdr;

import java.util.Optional;

public class ModalSql {

	Jdbi jdbi1;
	Boolean enableDbOperation;

	public ModalSql() {
	}

	public ModalSql(Jdbi jdbi1) {
		this.jdbi1 = jdbi1;
	}

	public ModalSql(Jdbi jdbi1, boolean enableDbOperation) {
		this.jdbi1 = jdbi1;
		this.enableDbOperation = enableDbOperation;
	}

	public static String entityFillerMethodFromDb(Jdbi jdbi, Class selectedClass) {

		Integer idNo = ModalDialog.actDialogIdSelection();
		Loghelper.get(ModalSql.class).debug("Id:" + idNo);

		if (idNo != null) {

			Fdr<Optional<Object>> fdr = new RepoJdbiCustom(jdbi, selectedClass).jdSelectEntityById(idNo);

			if (fdr.getValue().isPresent()) {
				//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
				String result = FiQueryGenerator.codeEntityFieldsInitMethod(jdbi, selectedClass, fdr.getValue());
				return result;
			} else {
				System.out.println("Db den Veri Okunamadı");
			}

		} else {
			FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
		}

		return "no result";
	}

	public static ModalSql bui() {
		return new ModalSql();
	}

	/**
	 *
	 * Class Tanımından
	 *
	 * @param selectedClass
	 * @return
	 */
	public String createQuery(Class selectedClass) {

		if (!checkSelClass(selectedClass)) return "";

		String sqlCreate = FiQueryGenerator.createQuery20(selectedClass);
		//getCodeGenMainView().getFxTextArea().appendTextLnAsyn(sqlCreate);

		if (FiBoolean.isTrue(getEnableDbOperation()) && checkDb(getJdbi1())) {
			Fdr fdr = new RepoJdbiString(getJdbi1()).jdUpdateBindMapViaAtTire(sqlCreate, null);
			if (fdr.getBoResultInit()) {
				fdr.setMessage("Sql Başarılı Şekilde Çalıştırıldı.");
			}
			FxDialogShow.showDbResult(fdr);
		}

		return sqlCreate;
	}

	public static Boolean checkDb(Jdbi jdbi) {
		if (jdbi == null) {
			FxDialogShow.showPopWarn("Lütfen Sunucu Seçiniz.");
			return false;
		}
		return true;
	}

	public static Boolean checkSelClass(Class selectedClass) {
		if (selectedClass == null) {
			FxDialogShow.showPopWarn("Lütfen Bir Sınıf Seçiniz.");
			return false;
		}
		return true;
	}

	public Jdbi getJdbi1() {
		return jdbi1;
	}

	public void setJdbi1(Jdbi jdbi1) {
		this.jdbi1 = jdbi1;
	}

	public Boolean getEnableDbOperation() {
		return enableDbOperation;
	}

	public void setEnableDbOperation(Boolean enableDbOperation) {
		this.enableDbOperation = enableDbOperation;
	}
}