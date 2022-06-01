package oraksoft.codegen.modal;

import ozpasyazilim.utils.core.FiString;
import ozpasyazilim.utils.core.FiXmlParser;
import ozpasyazilim.utils.ficodegen.FiCodeHelper;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;
import ozpasyazilim.utils.gui.fxcomponents.FxSimpleDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModalXml {

	public static String actXmlToFiFieldList(File fileXml) {

		//List<String> listHeader = new ArrayList<>();
		List<String> listFields = new ArrayList<>();

		String tagname = "";

		FxSimpleDialog fxSimpleDialog2 = FxSimpleDialog.buiTextFieldDialog("Okunacak Xml Elemanı");

		if (fxSimpleDialog2.isClosedWithOk()) {
			tagname = fxSimpleDialog2.getTxValue();
		}

		if(FiString.isEmpty(tagname)){
			FxDialogShow.showPopWarn("Lütfen bir xml elemanı seçiniz !!!");
			return "";
		}

		List<String> xmlHeaderList = FiXmlParser.bui().parseXmlTagsElement(fileXml, tagname);

		//FiConsole.debugListObjectsToString(listHeader,getClass());

		String code = FiCodeHelper.codeFiTableColsFromHeaderAndFieldName2(xmlHeaderList, "Xml", listFields);

		return code;

	}
}
