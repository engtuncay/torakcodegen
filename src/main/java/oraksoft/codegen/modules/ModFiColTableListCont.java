package oraksoft.codegen.modules;

import oraksoft.codegen.entity.EntityClazz;
import org.reflections.Reflections;
import ozpasyazilim.utils.fidbanno.FiColTable;
import ozpasyazilim.utils.fidbanno.FiTable;
import ozpasyazilim.utils.fxwindow.FiArbAbsTableWindowCont;
import ozpasyazilim.utils.fxwindow.FxGenWindowContWindow;
import ozpasyazilim.utils.mvc.IFiModCont;
import ozpasyazilim.utils.mvc.IFxTableSelectionCont;
import ozpasyazilim.utils.table.FiCol;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModFiColTableListCont extends FiArbAbsTableWindowCont<EntityClazz> implements  IFxTableSelectionCont<EntityClazz>
{

    public List<EntityClazz> listClassDefault;
    EntityClazz entitySelected;

    @Override
    public void initCont() {
        super.initCont();

        if (getListClassDefault() == null) {
            setupEntityClasses();
        }

        List<FiCol> listCols = getTableCols();

        getFxTableView().setEnableLocalFilterEditor(true);
        getFxTableView().addAllFiColsAuto(listCols);
        getFxTableView().setItemsAsFilteredList(listClassDefault);
        // entitySelected metodları implemente edilmeli
        getFxTableView().activateExtensionFxTableSelectAndClose(this);

    }

    @Override
    protected void pullTableData() {

    }

    @Override
    protected void pullTableDataThreadBody() {

    }

    public void setupEntityClasses() {

        Set<Class<?>> allClasses = new HashSet<>();

        List<String> listPrefix = new ArrayList<>();
        listPrefix.add("ozpasyazilim.mikro.metadata.metaMikro");

        for (String prefix : listPrefix) {
            allClasses.addAll(getClassSet(prefix));
        }

        List<EntityClazz> listClazz = new ArrayList<>();

        allClasses.forEach(aClass -> {
            //getCodeGenMainView().getFxTextArea().appendTextNl(aClass.getName());
            listClazz.add(new EntityClazz(aClass.getSimpleName(), aClass.getName(), aClass));
        });

        listClassDefault = listClazz;
    }

    private List<FiCol> getTableCols() {

        List<FiCol> listCol = new ArrayList<>();

        listCol.add(FiCol.build("Sınıf İsmi", "simpleName"));
        listCol.add(FiCol.build("Tam İsmi", "fullName"));

        return listCol;
    }

    private Set<Class<?>> getClassSet(String prefix) {
        Reflections reflections = new Reflections(prefix);
        //Reflections reflections = new Reflections(new String[]{prefix});
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(FiColTable.class, false);
        //typesAnnotatedWith.addAll(reflections.getTypesAnnotatedWith(FiTable.class, false));
        //typesAnnotatedWith.addAll(reflections.getTypesAnnotatedWith(Table.class, false));

        return typesAnnotatedWith;
    }

    public List<EntityClazz> getListClassDefault() {
        return listClassDefault;
    }

    public void setListClassDefault(List<EntityClazz> listClassDefault) {
        this.listClassDefault = listClassDefault;
    }

    @Override
    public EntityClazz getEntitySelected() {
        return entitySelected;
    }

    @Override
    public void setEntitySelected(EntityClazz entitySelected) {
        this.entitySelected = entitySelected;
    }
}
