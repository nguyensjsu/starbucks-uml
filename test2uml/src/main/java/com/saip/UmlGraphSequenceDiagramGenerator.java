
package com.saip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;

import com.saip.model.SequenceStep;

public class UmlGraphSequenceDiagramGenerator {

	Map<String, String> createdObjects = new HashMap<>();
	Map<String, Integer> activeObjects = new HashMap<>();
	Random random = new Random();
	Stack<String> callStack = new Stack<>();
	public String generateSequenceDiagram(List<SequenceStep> sequenceSteps) {
		StringBuilder builder = new StringBuilder();
		builder.append(".PS\n");
		builder.append("copy \"/Users/nishant/umloutput/sequence.pic\";\n");
		builder.append("maxpswid=130\n");
		builder.append("maxpsht=130\n");
		builder.append("boxwid=6\n");
		builder.append("boxht=0.9\n");
		builder.append("awid=0.4\n");
		createAllObjects(builder, sequenceSteps);
		builder.append("step();\n");
		builder.append("step();\n");
		String firstObjectId = createdObjects.get(sequenceSteps.get(0).getOrigin().getSimpleName());
		for(SequenceStep step : sequenceSteps) {
			String caller = step.getOrigin().getSimpleName();
			String callerId = createdObjects.get(caller);

			String called = step.getTarget().getSimpleName();
			String calledId = createdObjects.get(called);

			if (step.getSequenceType().equals("CALL")) {
				if (callerId.equals(calledId)) {
					builder.append("active(" + callerId + ");\n");
					builder.append("step();\n");
					activeObjects.put(callerId, activeObjects.get(callerId) + 1);
				}
				else {
					if (activeObjects.get(callerId) == 0) {
						builder.append("active(" + callerId + ");\n");
						activeObjects.put(callerId, 1);
					}
					if (activeObjects.get(calledId) == 0) {
						builder.append("active(" + calledId + ");\n");
						activeObjects.put(calledId, 1);
					}
				}
				builder.append("message(" + callerId + "," + calledId + "," + "\"" + step.getMethodSignature() + "\"" + ");\n");
				callStack.push(called + step.getMethodSignature());
				builder.append("step();\n");
				builder.append("step();\n");
				builder.append("step();\n");
			}
			else if (step.getSequenceType().equals("RETURN")) {
				String popped = callStack.pop();
				if (callerId.equals(calledId)) {
					builder.append("inactive(" + callerId + ");\n");
					activeObjects.put(callerId, activeObjects.get(callerId) - 1);
				} else {
					if (activeObjects.get(calledId) == 0) {
						builder.append("active(" + calledId + ");\n");
						activeObjects.put(calledId, 1);
					}
					builder.append("return_message(" + callerId + "," + calledId + "," + "\"" + "return" + "\"" + ");\n");
					if (activeObjects.get(callerId) == 1) {
						builder.append("inactive(" + callerId + ");\n");
						activeObjects.put(callerId, 0);
					}
				}
				builder.append("step();\n");
				builder.append("step();\n");
				builder.append("step();\n");
			}

		}
		builder.append("inactive(" + firstObjectId + ");\n");
		completeLifeline(builder, sequenceSteps);
		builder.append(".PE\n");
		return builder.toString();
	}

	private void completeLifeline(StringBuilder builder, List<SequenceStep> sequenceSteps) {
		for( Entry entry : activeObjects.entrySet()) {
			builder.append("complete(" + entry.getKey() + ");\n");
		}
	}

	private void createAllObjects(StringBuilder builder, List<SequenceStep> sequenceSteps) {
		for(SequenceStep step : sequenceSteps) {
			createObject(builder, step.getOrigin());
			createObject(builder, step.getTarget());
		}
	}

	private void createObject(StringBuilder builder, Class clazz) {
		if (!createdObjects.containsKey(clazz.getSimpleName())) {
			if (!clazz.getSimpleName().equals("")) {
				String id = getObjectId(clazz.getSimpleName());
				builder.append("object(" + id + ", " + "\"" + id.toLowerCase() + ":" + clazz.getSimpleName() + "\");" + "\n");
				builder.append("placeholder_object(" + "Dummy" + random.nextInt(10000) + ");\n");
				builder.append("placeholder_object(" + "Dummy" + random.nextInt(10000) + ");\n");
				builder.append("placeholder_object(" + "Dummy" + random.nextInt(10000) + ");\n");
				activeObjects.put(id, 0);
				createdObjects.put(clazz.getSimpleName(), id);
			}
		}
	}

	private String getObjectId(String caller) {
		String firstLetter = caller.substring(0, 1).toUpperCase();
		int i = 0;
		while (createdObjects.containsValue(firstLetter + Integer.toString(i))) {
			i++;
		}
		return firstLetter + Integer.toString(i);
	}
}
