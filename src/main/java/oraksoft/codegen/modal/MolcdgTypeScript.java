package oraksoft.codegen.modal;


import oraksoft.codegen.entity.EntityClazz;
import oraksoft.codegen.modules.ModEntityListCont;
import ozpasyazilim.utils.ficodegen.FiTypescriptHelper;

public class MolcdgTypeScript {

	public static String actBtnTypescriptEntity() {
		ModEntityListCont modEntityListCont = MolcdgSharedDialogs.showDialogSelectEntityClass();
		EntityClazz selectedEntity = modEntityListCont.getSelectedEntity();
		if (selectedEntity != null) {
			return FiTypescriptHelper.tsEntity(selectedEntity.getClazz());
		}

		return "no result";
	}


}
