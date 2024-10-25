package oraksoft.codegen.modal;


import oraksoft.codegen.entity.EntityClazz;
import oraksoft.codegen.modules.OccEntityListCont;
import ozpasyazilim.utils.ficodegen.FiTypescriptHelper;

public class MlcgTypeScript {

	public static String actBtnTypescriptEntity() {
		OccEntityListCont occEntityListCont = OcmSharedDialogs.showDialogSelectEntityClass();
		EntityClazz selectedEntity = occEntityListCont.getSelectedEntity();
		if (selectedEntity != null) {
			return FiTypescriptHelper.tsEntity(selectedEntity.getClazz());
		}

		return "no result";
	}


}
