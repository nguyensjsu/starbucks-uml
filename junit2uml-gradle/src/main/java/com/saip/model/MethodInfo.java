
package com.saip.model;

import java.util.HashMap;
import java.util.Map;

public class MethodInfo {

	String name;

	String returnType;

	Map<String, String> parameters;

	String accessModifier;

	public MethodInfo() {
		parameters = new HashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void addParameters(String paramName, String paramType) {
		parameters.put(paramName, paramType);
	}

	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}
}
