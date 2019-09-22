
package com.saip.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class SequenceStep2 {

	String stepId;

	Class origin;

	String originObject;

	Class target;

	String targetObject;

	String sequenceType;

	Object returnValue;

	String returnType;

	String methodSignature;

	String methodSignatureArgs;

	String stepNumber;

	List<SequenceStep2> childs;

	SequenceStep2 parent;



	public String getStepId() {
		return stepId;
	}

	public SequenceStep2() {
		this.stepId = UUID.randomUUID().toString();
		this.childs = new ArrayList<>();
	}

	public Class getOrigin() {
		return origin;
	}

	public void setOrigin(Class origin) {
		this.origin = origin;
	}

	public Class getTarget() {
		return target;
	}

	public void setTarget(Class target) {
		this.target = target;
	}

	public String getSequenceType() {
		return sequenceType;
	}

	public void setSequenceType(String sequenceType) {
		this.sequenceType = sequenceType;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	public String getMethodSignatureArgs() {
		return methodSignatureArgs;
	}

	public void setMethodSignatureArgs(String methodSignatureArgs) {
		this.methodSignatureArgs = methodSignatureArgs;
	}

	public String getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
	}

	public List<SequenceStep2> getChilds() {
		return childs;
	}

	public void setChilds(List<SequenceStep2> childs) {
		this.childs = childs;
	}

	public SequenceStep2 getParent() {
		return parent;
	}

	public void setParent(SequenceStep2 parent) {
		this.parent = parent;
	}

	public String getOriginObject() {
		return originObject;
	}

	public void setOriginObject(String originObject) {
		this.originObject = originObject;
	}

	public String getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}

	public void addChild(SequenceStep2 sequenceStep2) {
		this.childs.add(sequenceStep2);
	}
}
