
package com.saip;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;

import com.saip.model.ClassInfo;
import com.saip.model.FieldInfo;
import com.saip.model.MethodInfo;
import com.saip.model.SequenceStep;
import com.saip.model.SequenceStep2;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class AbstractAspectBase {

	List<ClassInfo> classInfos = new ArrayList<>();
	List<SequenceStep> sequenceSteps = new ArrayList<>();
	List<SequenceStep> sequenceSteps2 = new ArrayList<>();
	Map<String, Boolean> processedMap = new HashMap<>();
	List<Class> argumentTypes = new ArrayList<>();
	protected void addMissingTypes() {
		List<Class> missingTypes = new ArrayList<>();
		for(ClassInfo classInfo : classInfos) {
			for (FieldInfo fieldInfo: classInfo.getAssociations()) {
				if (classInfos.stream().filter(cl -> cl.getCurrent().getSimpleName().equals(fieldInfo.getTypeClass().getSimpleName())).count() == 0) {
					if (fieldInfo.getTypeClass().getPackage().getName().equals("starbucks"))
						missingTypes.add(fieldInfo.getTypeClass());
				}
			}
		}

		for (Class clazz : missingTypes) {
			addClass(clazz);
		}
	}

	protected void addArgumentTypes() {
		for (Class clazz: argumentTypes) {
			if (classInfos.stream().filter(cl -> cl.getCurrent().getSimpleName().equals(clazz.getSimpleName())).count() == 0) {
				if (clazz.getPackage().getName().equals("starbucks"))
					addClass(clazz);
			}
		}
	}

	protected void addClass(Class clazz) {
		while (clazz != null && !clazz.equals(Object.class)) {
			ClassInfo classInfo = new ClassInfo();
			classInfo.setCurrent(clazz);
			classInfo.setType(getClassType(clazz));
			List<Field> fieldList = new ArrayList<>();
			fieldList = Arrays.asList(clazz.getDeclaredFields());
			for (int i = 0; i < fieldList.size(); i++) {
				if (fieldList.get(i).getType().getPackage() == null) {
					FieldInfo fieldInfo = new FieldInfo();
					fieldInfo.setName(fieldList.get(i).getName());
					fieldInfo.setCollection(false);
					fieldInfo.setAccessModifer(getFieldAccessModifiers(fieldList.get(i)));
					fieldInfo.setType(fieldList.get(i).getType().getSimpleName());
					fieldInfo.setTypeClass(fieldList.get(i).getType());
					classInfo.addFields(fieldInfo);
				}
				else if (fieldList.get(i).getType().getPackage().getName().equals("starbucks")) {
					FieldInfo fieldInfo = new FieldInfo();
					fieldInfo.setName(fieldList.get(i).getName());
					fieldInfo.setCollection(false);
					fieldInfo.setAccessModifer(getFieldAccessModifiers(fieldList.get(i)));
					fieldInfo.setType(fieldList.get(i).getType().getSimpleName());
					fieldInfo.setTypeClass(fieldList.get(i).getType());
					classInfo.addAssociations(fieldInfo);
					argumentTypes.add(fieldList.get(i).getType());
				}
				else if (Collection.class.isAssignableFrom(fieldList.get(i).getType())) {
					FieldInfo fieldInfo = new FieldInfo();
					fieldInfo.setCollection(true);
					fieldInfo.setAccessModifer(getFieldAccessModifiers(fieldList.get(i)));
					fieldInfo.setName(fieldList.get(i).getName());
					String typeWithPAckage = ((ParameterizedType)fieldList.get(i).getGenericType()).getActualTypeArguments()[0].getTypeName();
					String[] parts = typeWithPAckage.split("\\.");
					String onlyClassName = parts[parts.length - 1];
					fieldInfo.setType(onlyClassName);
					fieldInfo.setTypeClass((Class) ((ParameterizedType)fieldList.get(i).getGenericType()).getActualTypeArguments()[0]);
					classInfo.addAssociations(fieldInfo);
				}
			}

			List<Method> methodList;
			Paranamer paranamer = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));
			methodList = Arrays.asList(clazz.getDeclaredMethods());
			for (int i = 0; i < methodList.size(); i++) {
				if (!methodList.get(i).getName().startsWith("ajc")) {
					MethodInfo methodInfo = new MethodInfo();
					methodInfo.setName(methodList.get(i).getName());
					methodInfo.setAccessModifier(getMethodAccessModifiers(methodList.get(i)));
					methodInfo.setReturnType(methodList.get(i).getReturnType().getSimpleName());
					Parameter[] parameters = methodList.get(i).getParameters();
					String[] parameterNames = paranamer.lookupParameterNames(methodList.get(i), false);
					if (clazz.isInterface()) {
						for (int j = 0; j < parameters.length; j++) {
							String parameterName = parameters[j].getName();
							String parameterType = parameters[j].getType().getSimpleName();
							methodInfo.addParameters(parameterName, parameterType);
						}
					}
					else {
						if (parameterNames.length != parameters.length) {
							continue;
						}
						for (int j = 0; j < parameters.length; j++) {
							String parameterName = parameterNames[j];
							String parameterType = parameters[j].getType().getSimpleName();
							methodInfo.addParameters(parameterName, parameterType);
						}
					}
					classInfo.addMethods(methodInfo);
				}
			}
			String className = clazz.getSimpleName();
			Class superClass = clazz.getSuperclass();
			if (superClass != null && superClass != Object.class) {
				classInfo.setExtendsClass(superClass);
			}
			if (classInfos.stream().filter(cl -> cl.getCurrent().getSimpleName().equals(className)).count() == 0) {
				classInfos.add(classInfo);
			}
			clazz = superClass;
		}

	}

	protected void addInterfaces() {
		List<Class> allInterfaces = new ArrayList<>();
		for (ClassInfo classInfo : classInfos) {
			List<Class> interfaces = Arrays.asList(classInfo.getCurrent().getInterfaces());
			if (classInfo.getCurrent().isInterface()) {
				if (interfaces.size() == 1)
					classInfo.setExtendsClass(interfaces.get(0));
				else if(interfaces.size() > 1)
					throw new RuntimeException("more than one interface extended by an interface: " + classInfo.getCurrent().getSimpleName());
			}
			else {
				for (Class interfce : interfaces) {
					if (interfce.getPackage().getName().equals("starbucks"))
						classInfo.setImplementsInterfaces(interfaces);
				}
			}
			for(Class interfce : interfaces) {
				if (!allInterfaces.contains(interfce) && interfce.getPackage().getName().equals("starbucks"))
					allInterfaces.add(interfce);
			}
		}

		for (Class interfce : allInterfaces) {
			addClass(interfce);
		}
	}

	protected String getClassType(Class clazz) {
		if (Modifier.isInterface(clazz.getModifiers()))
			return "interface";
		else if (Modifier.isAbstract(clazz.getModifiers()))
			return "abstract_class";
		else
			return "class";
	}

	protected String getMethodAccessModifiers(Method method) {
		if (Modifier.isPublic(method.getModifiers()))
			return "public";
		else if (Modifier.isPrivate(method.getModifiers()))
			return "private";
		else if (Modifier.isProtected(method.getModifiers()))
			return "protected";
		else
			return "";
	}

	protected String getFieldAccessModifiers(Field field) {
		if (Modifier.isPublic(field.getModifiers()))
			return "public";
		else if (Modifier.isPrivate(field.getModifiers()))
			return "private";
		else if (Modifier.isProtected(field.getModifiers()))
			return "protected";
		else
			return "";
	}

	int top = - 1;
	protected void captureSequenceStep(JoinPoint joinPoint) {
		SequenceStep sequenceStep = new SequenceStep();
		sequenceStep.setOrigin(joinPoint.getThis().getClass());
		sequenceStep.setTarget(joinPoint.getTarget().getClass());
		String[] parts = joinPoint.getSignature().toString().split("\\.");
		sequenceStep.setMethodSignature(parts[parts.length - 1]);
		sequenceStep.setSequenceType("CALL");
		sequenceSteps.add(sequenceStep);
	}

	protected void captureSequenceStep(JoinPoint joinPoint, Object returnValue) {
		SequenceStep sequenceStep = new SequenceStep();
		sequenceStep.setOrigin(joinPoint.getTarget().getClass());
		sequenceStep.setTarget(joinPoint.getThis().getClass());
		String[] parts = joinPoint.getSignature().toString().split("\\.");
		sequenceStep.setMethodSignature(parts[parts.length - 1]);
		sequenceStep.setReturnValue(returnValue);
		sequenceStep.setSequenceType("RETURN");
		if ( returnValue != null && !returnValue.getClass().isPrimitive())
			sequenceStep.setReturnType(returnValue.getClass());
		sequenceSteps.add(sequenceStep);
	}

	SequenceStep2 main = new SequenceStep2();
	SequenceStep2 currentNode = main;
	protected void captureSequenceStep2(JoinPoint thisJoinPoint) {
		SequenceStep2 node = new SequenceStep2();
		node.setOrigin(thisJoinPoint.getThis().getClass());
		node.setOriginObject(thisJoinPoint.getThis().toString());
		Class target = thisJoinPoint.getTarget() != null ? thisJoinPoint.getTarget().getClass() : thisJoinPoint.getStaticPart().getSignature()
				.getDeclaringType();
		String targetObject = thisJoinPoint.getTarget() != null ? thisJoinPoint.getTarget().toString() : thisJoinPoint.getStaticPart().getSignature()
				.getDeclaringType().getName();
		node.setTargetObject(targetObject);
		node.setTarget(target);
		String[] parts = thisJoinPoint.getSignature().toString().split("\\.");
		node.setMethodSignature(parts[parts.length - 1]);
		node.setMethodSignatureArgs(getMethodSignatureWithArgs(node.getMethodSignature(), thisJoinPoint));
		node.setReturnType(thisJoinPoint.getSignature().toString().split(" ")[0]);
		node.setSequenceType("CALL");
		node.setParent(currentNode);
		currentNode.setOrigin(node.getOrigin());
		currentNode.setOriginObject(node.getOriginObject());
		currentNode.addChild(node);
		node.setStepNumber(computeStepNumber(node));
		currentNode = node;
	}

	private String getMethodSignatureWithArgs(String methodSignature, JoinPoint jp) {
		String methodName = methodSignature.split("\\(")[0];
		Object[] args = jp.getArgs();
		StringBuilder signature = new StringBuilder();
		signature.append(methodName);
		signature.append("(");
		for (int i = 0; i < args.length; i++) {
			signature.append(args[i]);
			if(i < args.length - 1)
				signature.append(", ");
		}
		signature.append(")");
		return signature.toString();

	}

	private String computeStepNumber(SequenceStep2 node) {
		SequenceStep2 parent = node.getParent();
		String parentNumber;
		if (parent.getStepNumber() == null || parent.getStepNumber() == "")
			parentNumber = "";
		else
			parentNumber = parent.getStepNumber() + ".";
		String stepNumber = parentNumber + String.valueOf(parent.getChilds().indexOf(node) + 1);
		return stepNumber;

	}

	protected void captureSequenceStep2(JoinPoint thisJoinPoint, Object r) {
		if (r == null) {
			currentNode.setReturnValue("void");
		}
		else {
			if (r.getClass().isAssignableFrom(String.class)) {
				r = r.toString().replace("\n", "\\n");
				if (r.toString().length() > 40)
					r = r.toString().substring(0,40) + "...";
			}
			if (r.getClass().isAssignableFrom(String.class) && r.equals("")) {
				r = null;
			}

			currentNode.setReturnValue(r);
		}
		currentNode = currentNode.getParent();
	}
}
