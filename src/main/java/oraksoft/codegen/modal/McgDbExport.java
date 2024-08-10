package oraksoft.codegen.modal;

import oraksoft.codegen.modules.GocHomeWindowCont;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.configmisc.ServerConfig;
import ozpasyazilim.utils.core.FiBool;
import ozpasyazilim.utils.core.FiCollection;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.datatypes.FiKeyBean;
import ozpasyazilim.utils.datatypes.FiListFkb;
import ozpasyazilim.utils.entitysql.EntSqlColumn;
import ozpasyazilim.utils.entitysql.EntSqlTable;
import ozpasyazilim.utils.entitysql.TutMetaEntSqlColTxKeyTypes;
import ozpasyazilim.utils.fidborm.RepoFkbJdbi;
import ozpasyazilim.utils.gui.fxcomponents.FiDbDataHelper;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.repoSql.RepoSqlColumn;
import ozpasyazilim.utils.returntypes.Fdr;

import java.util.ArrayList;
import java.util.List;

public class McgDbExport {

	/**
	 *
	 * @param modHome
	 * @param boExcludePk Primary Key Alanlarını Dahil Etmez
	 */
    public static void actTableExport1(GocHomeWindowCont modHome, Boolean boExcludePk) {

        if (!MlcgShared.checkAndSetupActiveServerJdbi(modHome)) return;

        ServerConfig serverConfig1 = modHome.getMcgSqlInit().getServerConfig1();

        String txMessage = String.format("Lütfen Export edilecek Tabloyu seçiniz.\nServer: %s Db: %s", serverConfig1.getServer(), serverConfig1.getServerDb());
        Jdbi jdbi1 = modHome.getMcgSqlInit().getJdbi1();
        List<EntSqlTable> entSqlTables = McgSharedDialogs.showDialogSelectTable(jdbi1, txMessage, true, false);

        if (entSqlTables == null) entSqlTables = new ArrayList<>();

        if (entSqlTables.size() == 1) {
            String txTableName = entSqlTables.get(0).getTABLE_NAME();
            Loghelper.get(getClassi()).debug("Seçilen Tablo:" + txTableName);
            RepoSqlColumn repoSqlColumn = new RepoSqlColumn(jdbi1);
            Fdr<List<EntSqlColumn>> fdrColList = repoSqlColumn.selectColumnsAllDetailed(txTableName);

            if (fdrColList.isTrueBoResult()) {
                List<EntSqlColumn> sqlColumnList = fdrColList.getValue();
                //Loghelper.get(getClassi()).debug(FiConsole.textListObjectsNotNullFields(sqlColumnList));

                if(FiBool.isTrue(boExcludePk)){

                    FiCollection.removeListItems(sqlColumnList, entSqlColumn -> {
                        if (FiString.isEqual(entSqlColumn.getTX_KEY_TYPE(), TutMetaEntSqlColTxKeyTypes.primaryKey().toString())) return true;
                        return false;
                    });

                }

                RepoFkbJdbi repoFkbJdbi = new RepoFkbJdbi(jdbi1);
                Fdr<FiListFkb> fdrListData = repoFkbJdbi.selAll(txTableName);
//              Loghelper.get(getClassi()).debug(FiConsole.textListFiKeyBean(fdrListData.getValue()));

                if (fdrListData.isTrueBoResult()) {

                    StringBuilder sbInsertQuery = new StringBuilder();

                    for (FiKeyBean fkbRow : fdrListData.getValue()) {
                        sbInsertQuery.append(makeInsertQuery(txTableName, sqlColumnList, fkbRow));
                        sbInsertQuery.append("\n");
                    }
                    modHome.appendTextNewLine(sbInsertQuery.toString());

                } else {
                    FxDialogShow.showPopError("Tablo bilgileri okunamadı.");
                    FxDialogShow.showFdr1PopOrFailModal(fdrListData);
                }


            }


        } else {
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

    private static StringBuilder makeInsertQuery(String txTableName, List<EntSqlColumn> sqlColumnList, FiKeyBean fkbRow) {
        StringBuilder sbInsertQuery = new StringBuilder();

        //INSERT INTO table_name (column1, column2, column3, ...)
        //VALUES (value1, value2, value3, ...);

        sbInsertQuery.append("INSERT INTO " + txTableName + " (");
        String txJoined = FiString.joinStrings(sqlColumnList, EntSqlColumn::getCOLUMN_NAME, ",");
        sbInsertQuery.append(txJoined);
        sbInsertQuery.append(")\n");
        sbInsertQuery.append("VALUES (");

        List<String> listDataValues = makeListDataValuesForQueryInsert(sqlColumnList, fkbRow);
        String txJoinedValues = FiString.joinStrings(listDataValues, ",");

        sbInsertQuery.append(txJoinedValues);
        sbInsertQuery.append(");");
        return sbInsertQuery;
    }

    private static List<String> makeListDataValuesForQueryInsert(List<EntSqlColumn> sqlColumnList, FiKeyBean fkbRow) {
        List<String> listDataValues = new ArrayList<>();
        for (int indexCol = 0; indexCol < sqlColumnList.size(); indexCol++) {
            EntSqlColumn entSqlColumn = sqlColumnList.get(indexCol);
            Object objValue = fkbRow.get(entSqlColumn.getCOLUMN_NAME());
            String sqlValue = FiDbDataHelper.getSqlValueWithQuote(objValue, entSqlColumn.getDATA_TYPE());
            listDataValues.add(sqlValue);
        }
        return listDataValues;
    }

    private static Class<McgDbExport> getClassi() {
        return McgDbExport.class;
    }
}
