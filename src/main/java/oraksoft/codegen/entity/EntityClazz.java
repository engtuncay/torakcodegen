package oraksoft.codegen.entity;

public class EntityClazz {

	String simpleName;
	String fullName;
	Class<?> clazz;
	String tableName;
	Boolean boInsertSelectClass;

	public EntityClazz() {
	}

	public EntityClazz(String simpleName, String fullName, Class<?> clazz) {
		this.simpleName = simpleName;
		this.fullName = fullName;
		this.clazz = clazz;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Boolean getBoInsertSelectClass() {
		return boInsertSelectClass;
	}

	public void setBoInsertSelectClass(Boolean boInsertSelectClass) {
		this.boInsertSelectClass = boInsertSelectClass;
	}
}
