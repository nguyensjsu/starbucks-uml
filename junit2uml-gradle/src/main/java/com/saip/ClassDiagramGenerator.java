
package com.saip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.util.Pair;
import com.saip.model.ClassInfo;
import com.saip.model.FieldInfo;
import com.saip.model.MethodInfo;

public class ClassDiagramGenerator {

	public String generateClassDiagramText(List<ClassInfo> classInfos) {
		StringBuilder builder = new StringBuilder();
		builder.append("/**\n");
		builder.append(" * @opt attributes\n");
		builder.append(" * @opt operations\n");
		builder.append(" * @opt types\n");
		builder.append(" * @opt visibility\n");
		builder.append(" * @opt horizontal\n");
		builder.append(" * @opt nodefontsize 12\n");
		builder.append(" * @opt nodefillcolor beige\n");
		builder.append(" * @opt bgcolor azure2\n");
		builder.append(" * @hidden\n");
		builder.append(" */\n");
		builder.append("class UMLOptions {}\n");
		builder.append("\n");
		for (ClassInfo classInfo : classInfos) {
			if (classInfo.getCurrent().isAnonymousClass()) continue;
			builder.append("/**\n");
			builder.append(getAssociationString(classInfo.getAssociations()));
			builder.append(" */\n");
			if (classInfo.getType().equals("class")) {
				builder.append("class ");
			}
			else if (classInfo.getType().equals("interface")) {
				builder.append("interface ");
			}
			else if (classInfo.getType().equals("abstract_class")) {
				builder.append("abstract class ");
			}
			String className = !classInfo.getCurrent().getSimpleName().equals("") ? classInfo.getCurrent().getSimpleName() : classInfo.getCurrent()
					.getName();
			builder.append(className);
			if (classInfo.getExtendsClass() != null) {
				builder.append(" extends " + classInfo.getExtendsClass().getSimpleName());
			}
			if (classInfo.getImplementsInterfaces().size() > 0) {
				builder.append(" implements " + getInterfaceString(classInfo.getImplementsInterfaces()));
			}
			builder.append(" {\n");
			for (FieldInfo fieldInfo : classInfo.getFields()) {
				builder.append("    " + fieldInfo.getAccessModifer() + " " + fieldInfo.getType() + " " + fieldInfo.getName() + ";\n");
			}
			for (FieldInfo fieldInfo : classInfo.getAssociations()) {
				builder.append("    " + fieldInfo.getAccessModifer() + " " + fieldInfo.getType() + " " + fieldInfo.getName() + ";\n");
			}
			for(MethodInfo methodInfo : classInfo.getMethods()) {
				builder.append("    " + methodInfo.getAccessModifier() + " " + methodInfo.getReturnType() + " ");
				builder.append(methodInfo.getName() + "(");
				Iterator it = methodInfo.getParameters().entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
					String pName = entry.getKey().toString();
					String pValue = entry.getValue().toString();
					builder.append(pValue + " " + pName);
					if (it.hasNext())
						builder.append(", ");
				}
				builder.append(") {}\n");
			}
			builder.append("}\n\n");
		}
		return builder.toString();
	}

	private String getAssociationString(List<FieldInfo> fieldInfos) {
		StringBuilder builder = new StringBuilder();
		Map<String, List<Pair<String, Boolean>>> fieldInfoMap = new HashMap<>();
		for (FieldInfo fieldInfo : fieldInfos) {
			if (fieldInfoMap.containsKey(fieldInfo.getType())) {
				fieldInfoMap.get(fieldInfo.getType()).add(new Pair<>(fieldInfo.getName(), fieldInfo.isCollection()));
			}
			else
				fieldInfoMap.put(fieldInfo.getType(), new ArrayList<Pair<String, Boolean>>(){{ add(new Pair<>(fieldInfo.getName(), fieldInfo.isCollection())); }});
		}

		Iterator it = fieldInfoMap.entrySet().iterator();
		while (it.hasNext()) {
			builder.append(" * @assoc \"\" - ");
			Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			List<Pair<String, Boolean>> pairs = (List<Pair<String, Boolean>>) entry.getValue();
			builder.append("\"");
			for (Pair<String, Boolean> pair : pairs) {
				if (pair.getValue().equals(true)) {
					builder.append(pair.getKey() + "\\n*\\n" +  "\\r");
				}
				else {
					builder.append(pair.getKey() + "\\n1\\n" +  "\\r");
				}
			}
			builder.append("\"");
			builder.append(" " + key + "\n");
		}
		return builder.toString();
	}

	private String getInterfaceString(List<Class> interfaces) {
		StringBuilder builder = new StringBuilder();
		int cnt = 0;
		for (Class ifaces : interfaces) {
			builder.append(ifaces.getSimpleName());
			cnt++;
			if (cnt < interfaces.size())
				builder.append(", ");
		}
		return builder.toString();
	}
}
