package gapisoft.codegen.entity.dpro;

import ozpasyazilim.utils.fidbanno.FiId;
import ozpasyazilim.utils.fidbanno.FiTable;

@FiTable
public class DprUser {

	@FiId
	private Integer userLnId;
	private String userTxAlias;
	private String userTxPassmd;
	private String userTxFullName;
	private Integer userLnMuhaId;

	// Getter and Setter


}
