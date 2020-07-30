package gapisoft.codegen.modules;

import gapisoft.codegen.entity.EntityClazz;
import org.reflections.Reflections;
import ozpasyazilim.utils.fidbanno.FiTable;
import ozpasyazilim.utils.fxwindow.FxGenWindowContWindow;
import ozpasyazilim.utils.mvc.IFxSimpleCont;
import ozpasyazilim.utils.table.FiTableCol;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModEntityListCont extends FxGenWindowContWindow<String, EntityClazz> implements IFxSimpleCont {

	public static List<EntityClazz> listClassDefault;

	@Override
	public void initCont() {
		super.initCont();

		if(getListClassDefault()==null){
			setupEntityClasses();
		}

		List<FiTableCol> listCols = getTableCols();

		getFxTableView().setEnableLocalFilterEditor(true);
		getFxTableView().addAllFiTableColsAuto(listCols);
		getFxTableView().setItemsAsFilteredList(listClassDefault);
		getFxTableView().activateExtensionFxTableSelectAndClose(this);

	}

	public void setupEntityClasses() {

		Set<Class<?>> allClasses = new HashSet<>();

		List <String> listPrefix= new ArrayList<>();
		listPrefix.add("gapisoft.codegen.entity");
		//listPrefix.add("ozpasyazilim.mikro.dbentity.dbmikroentegre");
		listPrefix.add("ozpasyazilim.mikro.dbentity");
		listPrefix.add("ozpasyazilim.mikro.formEntity");
		//listPrefix.add("ozpasyazilim.mikro.dbentity.dbmikro2");

		for (String prefix : listPrefix) {
			allClasses.addAll(getClassSet(prefix));
		}

		List<EntityClazz> listClazz = new ArrayList<>();

		allClasses.forEach(aClass -> {
			//getCodeGenMainView().getFxTextArea().appendTextNl(aClass.getName());
			listClazz.add(new EntityClazz(aClass.getSimpleName(),aClass.getName(),aClass));
		});

		listClassDefault = listClazz;
	}

	private List<FiTableCol> getTableCols() {

		List<FiTableCol> listCol = new ArrayList<>();

		listCol.add(FiTableCol.build("Sınıf İsmi", "simpleName"));
		listCol.add(FiTableCol.build("Tam İsmi", "fullName"));

		return listCol;
	}

	private Set<Class<?>> getClassSet(String prefix) {
		Reflections reflections = new Reflections(prefix);
		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Entity.class, false);
		typesAnnotatedWith.addAll(reflections.getTypesAnnotatedWith(FiTable.class, false));
		typesAnnotatedWith.addAll(reflections.getTypesAnnotatedWith(Table.class, false));

		return typesAnnotatedWith;
	}

	public static List<EntityClazz> getListClassDefault() {
		return listClassDefault;
	}

	public static void setListClassDefault(List<EntityClazz> listClassDefault) {
		ModEntityListCont.listClassDefault = listClassDefault;
	}
}
