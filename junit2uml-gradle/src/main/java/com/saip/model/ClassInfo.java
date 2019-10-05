

package com.saip.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClassInfo implements Serializable {

	Class current;
	String type;
	Class extendsClass;
	List<Class> implementsInterfaces;
	List<FieldInfo> associations;
	List<FieldInfo> fields;
	List<MethodInfo> methods;

	public ClassInfo() {
		this.associations = new ArrayList<FieldInfo>();
		this.fields = new ArrayList<FieldInfo>();
		this.methods = new ArrayList<MethodInfo>();
		this.implementsInterfaces = new ArrayList<Class>();
	}

	public Class getCurrent() {
		return current;
	}

	public void setCurrent(Class current) {
		this.current = current;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Class getExtendsClass() {
		return extendsClass;
	}

	public void setExtendsClass(Class extendsClass) {
		this.extendsClass = extendsClass;
	}

	public List<Class> getImplementsInterfaces() {
		return implementsInterfaces;
	}

	public void setImplementsInterfaces(List<Class> implementsInterfaces) {
		this.implementsInterfaces = implementsInterfaces;
	}

	public List<FieldInfo> getAssociations() {
		return associations;
	}

	public void setAssociations(List<FieldInfo> associations) {
		this.associations = associations;
	}

	public List<FieldInfo> getFields() {
		return fields;
	}

	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}

	public List<MethodInfo> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodInfo> methods) {
		this.methods = methods;
	}

	public void addFields(FieldInfo object) {
		this.fields.add(object);
	}

	public void addAssociations(FieldInfo object) {
		this.associations.add(object);
	}

	public void addMethods(MethodInfo method) {
		this.methods.add(method);
	}

	public void addImplementedInterface(Class intrface) {
		this.implementsInterfaces.add(intrface);
	}
}
