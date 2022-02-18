package oraksoft.codegen.entity.dpro;

import ozpasyazilim.utils.fidbanno.FiTable;
import ozpasyazilim.utils.fidbanno.FiId;

@FiTable
public class DprDestekTalep {

	@FiId
	private Integer detaLnId;
	private Integer detaLnMustId;
	private String detaTxDurumKod;
	private String detaTxKisaMetin;
	private String detaDtCreate;
	private String detaDtChange;

	// Getter and Setter

}
