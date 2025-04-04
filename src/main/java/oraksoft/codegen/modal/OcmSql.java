package oraksoft.codegen.modal;

import javafx.util.Pair;
import oraksoft.codegen.entity.EntityClazz;
import oraksoft.codegen.modules.ModFiColTableListCont;
import ozpasyazilim.mikro.metadata.metaMikro.FiColsEntegre;
import ozpasyazilim.utils.core.FiCollection;
import ozpasyazilim.utils.core.FiReflection;
import ozpasyazilim.utils.datatypes.FiListKeyString;
import ozpasyazilim.utils.datatypes.FiKeyString;
import ozpasyazilim.utils.entitysql.EntSqlTable;
import ozpasyazilim.utils.repoSql.RepoSqlTable;
import org.jdbi.v3.core.Jdbi;
import org.reflections.Reflections;
import ozpasyazilim.mikro.dbrepo.repoMikro2.RepoJdbiGeneric;
import ozpasyazilim.utils.configmisc.ServerConfig;
import ozpasyazilim.utils.core.FiBool;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.datatypes.FiMapList;
import ozpasyazilim.utils.fidbanno.FiTable;
import ozpasyazilim.utils.fidborm.*;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.log.Loghelper;
import ozpasyazilim.utils.returntypes.Fdr;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

public class OcmSql {

	Jdbi jdbi1;
	Jdbi jdbi2;
	Boolean boEnableDbOperation;
	String txSqlTransferDate;
	private ServerConfig serverConfig1;
	private ServerConfig serverConfig2;
	FiListKeyString listMapDateField;
	OcmHome ocmHome;

	public OcmSql() {
	}

	public OcmSql(Jdbi jdbi1) {
		this.jdbi1 = jdbi1;
	}

	public OcmSql(Jdbi jdbi1, boolean boEnableDbOperation) {
		this.jdbi1 = jdbi1;
		this.boEnableDbOperation = boEnableDbOperation;
	}

	public String textSqlTableList(Jdbi jdbi) {

		if (jdbi == null && getJdbi1() != null) {
			jdbi = getJdbi1();
		} else {
			return textVtBaglantisiYapilmamis();
		}

		RepoJdbiGeneric<EntSqlTable> repoJdbiCustom = new RepoJdbiGeneric<>(jdbi, EntSqlTable.class);

		Fdr<List<EntSqlTable>> listFdr = repoJdbiCustom.jdSelectAll();
		StringBuilder sb = new StringBuilder();

		if (listFdr.isTrueBoResult()) {
			for (EntSqlTable entSqlTable : listFdr.getValue()) {
				sb.append(entSqlTable.getTABLE_NAME() + "\n");
			}
		}

		return sb.toString();
	}

	public List<EntSqlTable> getSqlTableList(Jdbi jdbi) {
		if(!checkJdbi(jdbi))return new ArrayList<>();
		RepoSqlTable repoJdbiCustom = new RepoSqlTable(jdbi);
		Fdr<List<EntSqlTable>> listFdr = repoJdbiCustom.selectTables();
		return listFdr.getValue();
	}

	public List<EntSqlTable> getSqlTableListWithCount(Jdbi jdbi) {
		if(!checkJdbi(jdbi))return new ArrayList<>();
		RepoSqlTable repoJdbiCustom = new RepoSqlTable(jdbi);
		Fdr<List<EntSqlTable>> listFdr = repoJdbiCustom.selectTablesWithCount();
		return listFdr.getValue();
	}

	public Integer getSqlTableCount(Jdbi jdbi, EntSqlTable entSqlTable) {
		if(!checkJdbi(jdbi))return -1;

//		new RepoSqlTable(jdbi).selectTableCount()

		return null;
	}

	private Boolean checkJdbi(Jdbi jdbi) {
		if (jdbi == null) {
			FxDialogShow.showPopWarn("Server baglantı ayarları aktarılamadı.Admine danışın.");
			return false;
		}
		return true;
	}

	public static String entityFillerMethodFromDb(Jdbi jdbi, Class selectedClass) {

		Integer idNo = OcmSharedDialogs.actDialogIdSelection();
		Loghelper.get(OcmSql.class).debug("Id:" + idNo);

		if (idNo != null) {

			Fdr<Optional<Object>> fdr = new RepoJdbiCustom(jdbi, selectedClass).jdSelectEntityOptById(idNo);

			if (fdr.getValue().isPresent()) {
				//FiConsole.printFieldsNotNull(fiDbResult.getResValue().get());
				String result = FiQugen.codeEntityFieldsInitMethod(jdbi, selectedClass, fdr.getValue());
				return result;
			} else {
				System.out.println("Db den Veri Okunamadı");
			}

		} else {
			FxDialogShow.showPopWarn("Lütfen Geçerli Bir Id No Giriniz !!!");
		}

		return "no result";
	}

	public static OcmSql bui() {
		return new OcmSql();
	}

	/**
	 * Class Tanımından
	 *
	 * @param selectedClass
	 *
	 * @return
	 */
	public String createQuery(Class selectedClass) {

		if (!checkSelClass(selectedClass)) return "";

		String sqlCreate = FiQugen.createQuery20(selectedClass);
		//getCodeGenMainView().getFxTextArea().appendTextLnAsyn(sqlCreate);

		if (FiBool.isTrue(getBoEnableDbOperation()) && checkJdbiIsNull(getJdbi1())) {
			Fdr fdr = new RepoJdbiString(getJdbi1()).jdUpdateBindMap(sqlCreate, null);
			if (fdr.isTrueBoResult()) {
				fdr.setMessage("Sql Başarılı Şekilde Çalıştırıldı.");
			}
			FxDialogShow.showDbResult(fdr);
		}

		return sqlCreate;
	}

	public String createQueryByFiCol() {

		//if (!checkSelClass(selectedClass)) return "";
		ModFiColTableListCont modEntityListCont = new ModFiColTableListCont();
		modEntityListCont.initCont();
		modEntityListCont.openAsNonModal();
		//McgSharedDialogs.showDialogSelectEntityClass();
		EntityClazz selectedEntity = modEntityListCont.getEntitySelected();

		if (selectedEntity != null) {
			//setClassSelected(selectedEntity.getClazz());
			//getGcgHomeCodeGenView().getBtnClassSec().setText("Seçilen Sınıf:" + selectedEntity.getClazz().getSimpleName());
			Loghelper.get(getClass()).debug("FiColTable seçildi");

		} else {
			FxDialogShow.showPopWarn("FiColTable Seçilmedi !!!");
			return "";
		}

		IFiTableMeta iFiTableMeta = (IFiTableMeta) FiReflection.generateObject(selectedEntity.getClazz());

        String sqlCreate = FiQugen.createQueryByIFiTableMeta(iFiTableMeta); //new FiColsEnmMutabakat()
		//getCodeGenMainView().getFxTextArea().appendTextLnAsyn(sqlCreate);

		if (FiBool.isTrue(getBoEnableDbOperation()) && checkJdbiIsNull(getJdbi1())) {
			Fdr fdr = new RepoJdbiString(getJdbi1()).jdUpdateBindMap(sqlCreate, null);
			if (fdr.isTrueBoResult()) {
				fdr.setMessage("Sql Başarılı Şekilde Çalıştırıldı.");
			}
			FxDialogShow.showDbResult(fdr);
		}

		return sqlCreate;
	}

	public String sqlTableCopySrv1ToSrv2(Boolean boDateCriteria) {

		if (getServerConfig1()==null || getServerConfig2()==null) {
			FxDialogShow.showPopWarn("Lütfen Server Bağlantılarını yapınız.");
			return "";
		}
		String txMessage = String.format("Lütfen Kopyalanacak Tabloları Seçiniz.\nServer: %s Db: %s", getServerConfig1().getServer(), getServerConfig1().getServerDb());
		List<EntSqlTable> entSqlTables = OcmSharedDialogs.showDialogSelectTable(getJdbi1(), txMessage,true,true);

		if(entSqlTables ==null) entSqlTables = new ArrayList<>();

		StringBuilder sbSql = new StringBuilder();
		for (EntSqlTable entSqlTable : entSqlTables) {

			if(FiBool.isTrue(boDateCriteria)){
				sbSql.append(getSqlInsertSelectWithColsWhereDate(entSqlTable.getTABLE_NAME()));
			}else {
				sbSql.append(getSqlInsertSelectWithCols(entSqlTable.getTABLE_NAME()));
			}
			sbSql.append("\n\n");
		}

		return FiString.orEmpty(sbSql.toString());
	}

	public void sqlTableCopySrv1ToSrv2Auto() {
		if (getServerConfig1()==null || getServerConfig2()==null) {
			FxDialogShow.showPopWarn("Lütfen Server Bağlantılarını yapınız.");
			return;
		}

		if(FiCollection.isEmpty(getListMapDateField())){
			FxDialogShow.showPopWarn("Excelde Tablo bilgileri girilmemiş");
			return;
		}

		StringBuilder sbSql = new StringBuilder();
		sbSql.append("\n -- start sql copy\n\n");
		for (FiKeyString fiKeyString : getListMapDateField()) {
			String txTable = fiKeyString.getTos(FiColsEntegre.txTable());
			if(FiString.isEmpty(txTable))continue;
			if(!FiString.isEmpty(fiKeyString.getTos(FiColsEntegre.txDateField()))){
				sbSql.append(getSqlInsertSelectWithColsWhereDate(txTable));
			}else {
				sbSql.append(getSqlInsertSelectWithCols(txTable));
			}
			sbSql.append("\n\n");
		}
		sbSql.append("\n -- end sql copy");

		getMcgHome().getFxTextArea().appendNewLine();
		getMcgHome().getFxTextArea().appendTextLnAsyn(sbSql.toString());
	}

	private String getSqlInsertSelectWithDate(String selectedTable) {

		Pair<String, EntityClazz> pairFieldDate = getFieldNameOfDateSepFieldByClass(selectedTable);
		String txFieldDate = pairFieldDate.getKey();

		if(txFieldDate==null){
			FxDialogShow.showPopWarn("Ayırıcı alan bulunamadı.");
			return "";
		}

		String sql = FiQugen.insertSelectQueryWithDate(selectedTable, getServerConfig1().getServerDb(), getServerConfig2().getServerDb(),txFieldDate,getTxSqlTransferDate());

		return sql;
	}

	private String getSqlInsertSelectWithColsWhereDate(String selectedTable) {

//		Pair<String, EntityClazz> pairFieldDate = getFieldNameOfDateSepFieldByClass(selectedTable);
		String txFieldDate = getListMapDateField().getRow(FiColsEntegre.txTable().toString(), selectedTable).or(new FiKeyString()).get(FiColsEntegre.txDateField().toString());
		//String txFieldDate = "";  // pairFieldDate.getKey();
		if(FiString.isEmpty(txFieldDate)){ //|| pairFieldDate.getKey()==null
			FxDialogShow.showPopWarn(selectedTable + " tablosu için ayırıcı tarih alanı bulunamadı.");
			return "-- "+selectedTable + " tablosu için ayırıcı tarih alanı bulunamadı.";
		}

		if(FiString.isEmpty(getTxSqlTransferDate())) setTxSqlTransferDate("@txTransferDate");

		String sql = FiQugen.insertSelectQueryWithColsAndDate(selectedTable, getServerConfig1().getServerDb(), getServerConfig2().getServerDb(),txFieldDate, getTxSqlTransferDate(),getJdbi1());
		//pairFieldDate.getValue().getClazz()

		return sql;
	}

	private String getSqlInsertSelectWithCols(String selectedTable) {
		//EntityClazz entityClazz = getEntityClassByTableName(selectedTable);
		String sql = FiQugen.insertSelectQueryWithCols(selectedTable, getServerConfig1().getServerDb(), getServerConfig2().getServerDb(), getJdbi1());
		return sql;
	}

	/**
	 *
	 * FiDateSeperator Field tanımlı alanın adını döner
	 *
	 * @param selectedTable
	 * @return
	 */
	private Pair<String,EntityClazz> getFieldNameOfDateSepFieldByClass(String selectedTable) {

		FiMapList<String, EntityClazz> entityClassMap = getDbClassMap();

		if (entityClassMap.containsKey(selectedTable)) {
			List<EntityClazz> entityClazzes = entityClassMap.get(selectedTable);

			for (EntityClazz entityClazz : entityClazzes) {
				Loghelper.get(getClass()).debug("entity class map de bulundu:"+ entityClazz.getSimpleName());
				List<FiField> listFieldsDateSeperatorField = FiReflectClass.getListFieldsDateSeperatorField(entityClazz.getClazz());
				if(listFieldsDateSeperatorField.size()>0){
					String dbFieldName = listFieldsDateSeperatorField.get(0).getOfcTxDbField();
					Pair<String, EntityClazz> pairReturn = new Pair<>(dbFieldName,entityClazz);
					return pairReturn;
				}
			}
		}

		return null;
	}

	private EntityClazz getEntityClassByTableName(String selectedTable) {

		FiMapList<String, EntityClazz> entityClassMap = getDbClassMap();

		if (entityClassMap.containsKey(selectedTable)) {
			List<EntityClazz> entityClazzes = entityClassMap.get(selectedTable);
			//Loghelper.get(getClass()).debug("entity class map de bulundu:"+ entityClazz.getSimpleName());
			if(entityClazzes.size()==1) return entityClazzes.get(0);

			if(entityClazzes.size()>1){
				for (EntityClazz entityClazz : entityClazzes) {
					if(FiBool.isTrue(entityClazz.getBoInsertSelectClass())) return entityClazz;
				}
				FxDialogShow.showPopWarn(selectedTable + " tablosu için birden fazla class var."+"\nKullanılan Sınıf:"+entityClazzes.get(0).getSimpleName());
				return entityClazzes.get(0);
			}
		}
		return null;
	}

	public FiMapList<String,EntityClazz> getDbClassMap() {

		Set<Class<?>> allClasses = new HashSet<>();

		List<String> listPrefix= new ArrayList<>();
		listPrefix.add("gapisoft.codegen.entity");
		//listPrefix.add("ozpasyazilim.mikro.dbentity.dbmikroentegre");
		listPrefix.add("ozpasyazilim.mikro.dbentity");
		listPrefix.add("ozpasyazilim.mikro.formEntity");
		listPrefix.add("ozpasyazilim.mikro.dao");
		//listPrefix.add("ozpasyazilim.mikro.dbentity.dbmikro2");

		for (String prefix : listPrefix) {
			allClasses.addAll(getClassSet(prefix));
		}

		FiMapList<String, EntityClazz> fiMapList = new FiMapList<>();

		allClasses.forEach(aClass -> {
			String tableName = FiQugen.getTableName(aClass);
			Boolean boInsertSelectClass = FiQugen.checkAnnoClassOfInsertSelect(aClass);
			EntityClazz entityClazz = new EntityClazz(aClass.getSimpleName(), aClass.getName(), aClass);
			entityClazz.setTableName(tableName);
			entityClazz.setBoInsertSelectClass(boInsertSelectClass);
			fiMapList.add(tableName,entityClazz);
		});

		return fiMapList;
	}

	private Set<Class<?>> getClassSet(String prefix) {
		Reflections reflections = new Reflections(prefix);
		//Reflections reflections = new Reflections(new String[]{prefix});
		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Entity.class, false);
		typesAnnotatedWith.addAll(reflections.getTypesAnnotatedWith(FiTable.class, false));
		typesAnnotatedWith.addAll(reflections.getTypesAnnotatedWith(Table.class, false));

		return typesAnnotatedWith;
	}


	public static Boolean checkJdbiIsNull(Jdbi jdbi) {
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

	public Boolean getBoEnableDbOperation() {
		return boEnableDbOperation;
	}

	public void setBoEnableDbOperation(Boolean boEnableDbOperation) {
		this.boEnableDbOperation = boEnableDbOperation;
	}

	public String textVtBaglantisiYapilmamis() {
		return "Veritabanı baglantısı yapılmamış";
	}

	public String getTxSqlTransferDate() {
		return txSqlTransferDate;
	}

	public void setTxSqlTransferDate(String txSqlTransferDate) {
		this.txSqlTransferDate = txSqlTransferDate;
	}

	public ServerConfig getServerConfig1() {
		return serverConfig1;
	}

	public void setServerConfig1(ServerConfig serverConfig1) {
		this.serverConfig1 = serverConfig1;
	}

	public ServerConfig getServerConfig2() {
		return serverConfig2;
	}

	public void setServerConfig2(ServerConfig serverConfig2) {
		this.serverConfig2 = serverConfig2;
	}

	public Jdbi getJdbi2() {
		return jdbi2;
	}

	public void setJdbi2(Jdbi jdbi2) {
		this.jdbi2 = jdbi2;
	}

	public FiListKeyString getListMapDateField() {
		return listMapDateField;
	}

	public void setListMapDateField(FiListKeyString listMapDateField) {
		this.listMapDateField = listMapDateField;
	}

	public OcmHome getMcgHome() {
		return ocmHome;
	}

	public void setMcgHome(OcmHome ocmHome) {
		this.ocmHome = ocmHome;
	}

	public String queryUnique1Fields(Class selectedClass) {

		if (!checkSelClass(selectedClass)) return "";

		String sqlCreate = FiQugen.uniqueQuery(selectedClass);
		//getCodeGenMainView().getFxTextArea().appendTextLnAsyn(sqlCreate);

		if (FiBool.isTrue(getBoEnableDbOperation()) && checkJdbiIsNull(getJdbi1())) {
			Fdr fdr = new RepoJdbiString(getJdbi1()).jdUpdateBindMap(sqlCreate, null);
			if (fdr.isTrueBoResult()) {
				fdr.setMessage("Sql Başarılı Şekilde Çalıştırıldı.");
			}
			FxDialogShow.showDbResult(fdr);
		}

		return sqlCreate;
	}
}