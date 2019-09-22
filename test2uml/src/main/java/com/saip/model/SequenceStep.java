

package com.saip.model;

import java.util.UUID;

public class SequenceStep {

	String stepId;

	Class origin;

	Class target;

	String sequenceType;

	Object returnValue;

	Class returnType;

	String methodSignature;

	public String getStepId() {
		return stepId;
	}

	public SequenceStep() {
		this.stepId = UUID.randomUUID().toString();
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

	public Class getReturnType() {
		return returnType;
	}

	public void setReturnType(Class returnType) {
		this.returnType = returnType;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}
}
