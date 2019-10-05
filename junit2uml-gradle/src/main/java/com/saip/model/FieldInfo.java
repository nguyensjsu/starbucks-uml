
package com.saip.model;

public class FieldInfo {

	String type;

	Class typeClass;

	String name;

	boolean isCollection;

	String accessModifer;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Class getTypeClass() {
		return typeClass;
	}

	public void setTypeClass(Class typeClass) {
		this.typeClass = typeClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean collection) {
		isCollection = collection;
	}

	public String getAccessModifer() {
		return accessModifer;
	}

	public void setAccessModifer(String accessModifer) {
		this.accessModifer = accessModifer;
	}
}
