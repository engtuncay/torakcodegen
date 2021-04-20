package gapisoft.codegen.entity.dpro;

import ozpasyazilim.utils.fidbanno.FiId;
import ozpasyazilim.utils.fidbanno.FiTable;

@FiTable
public class DprUser {

	@FiId
	private Integer usrLnId;
	private String usrTxAlias;
	private String usrTxPassmd;
	private String usrTxFullName;
	private Integer usrLnMuhaId;

	// Getter and Setter


}
