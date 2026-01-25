package oraksoft.codegen.modal;

import oraksoft.codegen.entity.EntityClazz;
import oraksoft.codegen.modules.OccEntityListCont;
import oraksoft.codegen.modules.OccHomeCont;
import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.datatypes.FiKeybean;
import ozpasyazilim.utils.datatypes.FiKeytext;
import ozpasyazilim.utils.ficodegen.FiTypescriptHelper;
import ozpasyazilim.utils.ficols.FicRfcCoding;
import ozpasyazilim.utils.fidborm.FiField;
import ozpasyazilim.utils.fidborm.FiReflectClass;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;

import java.util.List;

public class OcmTypescript {

//    public static void main(String[] args) {
//        classToTsClass(null);
//    }

    public static void classToTsClass(OccHomeCont occHomeCont) {

        Class classSelected1 = occHomeCont.getClassSelected1();

        if (classSelected1 == null) {
            FxDialogShow.showPopWarn("Lütfen bir class seçiniz");
            return;
        }

        String txTempClass = String.format("export class {{%s}} {\n" +
                "  \n" +
                "{{%s}}" +
                "\n\n}", FicRfcCoding.rfcTxClassName(), FicRfcCoding.rfcTxClassBody());

        FiKeytext fksTsTypes = getFksTsTypes();
        StringBuilder sbFieldDef = new StringBuilder();

        List<FiField> fiFieldList = FiReflectClass.getListFieldsWoutStatic(classSelected1, true);

        for (FiField fiField : fiFieldList) {
            //System.out.println(FiConsole.textObjectFieldsNtn(fiField));

            String txType = null;

            if (fksTsTypes.containsKey(fiField.getClassNameSimple())) {
                txType = fksTsTypes.get(fiField.getClassNameSimple());
            } else {
                txType = fiField.getClassNameSimple();
            }

            sbFieldDef.append("\t").append(fiField.getOfcTxFieldName())
                    .append("?:").append(txType).append(";\n");

        }

        FiKeybean fkbClass = new FiKeybean();

        fkbClass.putKeyTos(FicRfcCoding.rfcTxClassName(), classSelected1.getSimpleName());
        fkbClass.putKeyTos(FicRfcCoding.rfcTxClassBody(), sbFieldDef.toString());

        String txTsClass = FiString.substitutor(txTempClass, fkbClass);

        occHomeCont.appendTextNewLine(txTsClass);

        //class Person {
        //  name: string;
        //  age: number;

        //ofcTxFieldName	lnFailureOpCount
        //ofcTxDbFieldName	lnFailureOpCount
        //classNameSimple	Integer

        //System.out.println(txTempClass);

    }

    private static FiKeytext getFksTsTypes() {

        FiKeytext fksTs = new FiKeytext();

        fksTs.put(Integer.class.getSimpleName(), "number");
        fksTs.put(Double.class.getSimpleName(), "number");
        fksTs.put(String.class.getSimpleName(), "string");
        fksTs.put(Boolean.class.getSimpleName(), "boolean");
        fksTs.put(Object.class.getSimpleName(), "object");
        fksTs.put(List.class.getSimpleName(), "Array<object>");

        return fksTs;
    }

    public static String actBtnTypescriptEntity() {
        OccEntityListCont occEntityListCont = OcmSharedDialogs.showDialogSelectEntityClass();
        EntityClazz selectedEntity = occEntityListCont.getSelectedEntity();
        if (selectedEntity != null) {
            return FiTypescriptHelper.tsEntity(selectedEntity.getClazz());
        }

        return "no result";
    }
}
