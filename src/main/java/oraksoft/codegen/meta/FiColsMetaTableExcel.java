package oraksoft.codegen.meta;

import ozpasyazilim.utils.table.FiCol;
import ozpasyazilim.utils.table.OzColType;

public class FiColsMetaTableExcel {

    public static FiCol field_type() {
        FiCol fiCol = new FiCol("colType", "field_type");
        fiCol.buiColType(OzColType.String);
        return fiCol;
    }

    public static FiCol field_name() {
        FiCol fiCol = new FiCol("fieldName", "field_name");
        fiCol.buiColType(OzColType.String);
        return fiCol;
    }


}
