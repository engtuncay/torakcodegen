package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModHomeCodeGenerator;
import ozpasyazilim.utils.configmisc.ServerConfig;
import ozpasyazilim.utils.entitysql.EntSqlTable;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.repoSql.RepoSqlTable;

import java.util.ArrayList;
import java.util.List;

public class ModalDbExport {

	public static void actTableExport1(ModHomeCodeGenerator modHome) {

		if(!ModalShared.checkActiveServerJdbi(modHome)) return;

		ServerConfig serverConfig1 = modHome.getModalSqlInit().getServerConfig1();

		String txMessage = String.format("Lütfen Export edilecek Tabloyu seçiniz.\nServer: %s Db: %s", serverConfig1.getServer(), serverConfig1.getServerDb());
		List<EntSqlTable> entSqlTables = ModalSharedDialogs.showDialogSelectTableMulti( modHome.getModalSqlInit().getJdbi1(), txMessage,true,false);

		if(entSqlTables ==null) entSqlTables = new ArrayList<>();

		if (entSqlTables.size()==1) {
			Loghelper.get(getClassi()).debug("Seçilen Tablo:"+ entSqlTables.get(0).getTABLE_NAME());
			RepoSqlTable repoSqlTable = new RepoSqlTable(modHome.getModalSqlInit().getJdbi1());


		}else{
			FxDialogShow.showPopWarn("Sadece 1 tablo seçiniz.");
			return;
		}

//		StringBuilder sbSql = new StringBuilder();
//		for (SqlTable sqlTable : sqlTables) {
//
//			if(FiBoolean.isTrue(boDateCriteria)){
//				sbSql.append(getSqlInsertSelectWithColsWhereDate(sqlTable.getTABLE_NAME()));
//			}else {
//				sbSql.append(getSqlInsertSelectWithCols(sqlTable.getTABLE_NAME()));
//			}
//			sbSql.append("\n\n");
//		}

//		FxSimpleDialog fxSimpleDialog = new FxSimpleDialog(FxSimpleDialogType.TextField, "Tablo Adını Giriniz:");
//		fxSimpleDialog.openAsDialogSync();
//
//		if (fxSimpleDialog.isClosedWithOk()) {
//
//			FiCodeGeneratorTest fiCodeGeneratorTest = new FiCodeGeneratorTest();
//			FiQueryGenerator fiQueryGenerator = new FiQueryGenerator();
//
//			System.out.println("TxValueDialog:" + fxSimpleDialog.getTxValue());
//			String entityCode = FiQueryGenerator.codeEntityClassCsharp(fxSimpleDialog.getTxValue(), modHome.getAndSetupActiveServerJdbi());
//
//			if (!FiString.isEmpty(entityCode)) {
//				modHome.getCodeGenMainView().getFxTextArea().appendText(entityCode);
//			} else {
//				modHome.getCodeGenMainView().getFxTextArea().appendText("N/A");
//			}
//
//		}


	}

	private static Class<ModalDbExport> getClassi() {
		return ModalDbExport.class;
	}
}
