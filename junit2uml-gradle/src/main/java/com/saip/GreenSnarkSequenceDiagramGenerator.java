
package com.saip;

import com.saip.model.SequenceStep2;

public class GreenSnarkSequenceDiagramGenerator {

	StringBuilder builder;

	public String generateSequenceDiagram(SequenceStep2 node) {
		builder = new StringBuilder();
		builder.append(node.getOriginObject().split("\\.")[1]);
		builder.append("\n{\n");
		for (SequenceStep2 child : node.getChilds()) {
			recursePrint(child, 1);
		}
		builder.append("\n}\n");
		return builder.toString();
	}

	private void recursePrint(SequenceStep2 child, int level) {
//		String targetSimpleName = child.getTarget().getSimpleName();
//		if (targetSimpleName.equals("") && child.getTarget().isAnonymousClass())
//			targetSimpleName = child.getTarget().getName();
		String targetSimpleName = child.getTargetObject();
		builder.append("\"" + targetSimpleName + "\"" + "." + "\"" + child.getStepNumber() + ":" + child.getMethodSignatureArgs() + " : " +  child
				.getReturnType() + "\"" + "->" + "\"" + child.getReturnType() + "\"");
		if (child.getChilds().size() == 0)
			builder.append("{}\n");
		else
			builder.append("\n");

		if (child.getChilds().size() > 0) {
			builder.append("\n");
			addSpace(level - 1);
			builder.append("{\n");
			addSpace(level);
		}

		for(SequenceStep2 ch : child.getChilds()) {
			recursePrint(ch, level + 1);
		}

		if (child.getChilds().size() > 0) {
			builder.append("\n");
			addSpace(level - 1);
			builder.append("}\n");
		}

	}

	private String computeTargetName(Class target) {
		if (target.isAnonymousClass()) {
			String[] parts = target.getName().split("\\.");
			String name = parts[parts.length - 1];
			return name.split("\\$")[0];
		}
		else
			throw new RuntimeException("Cannot find class name for class : " + target);
	}

	private void addSpace(int level) {
		for(int i = 0; i < 3 * level; i++) {
			builder.append(" ");
		}
	}
}
