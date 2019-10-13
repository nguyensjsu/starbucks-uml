

package com.saip;

import com.saip.model.SequenceStep2;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

public aspect AnnotationAspect extends AbstractAspectBase {

	pointcut allMethodCalls(): call(* starbucks..*(..)) && call(!@org.junit.* * *(..));

	pointcut testMethod(): execution(@org.junit.Test * *(..));

	boolean testStarted;
	int cnt = 0;

	ClassDiagramGenerator classDiagramGenerator;
	UmlGraphSequenceDiagramGenerator umlGraphSequenceDiagramGenerator;
	GreenSnarkSequenceDiagramGenerator greenSnarkSequenceDiagramGenerator;
	public AnnotationAspect() {
		classDiagramGenerator = new ClassDiagramGenerator();
		umlGraphSequenceDiagramGenerator = new UmlGraphSequenceDiagramGenerator();
		greenSnarkSequenceDiagramGenerator = new GreenSnarkSequenceDiagramGenerator();
	}

	// Advice "after returning".
	before() : allMethodCalls() {
		if (thisJoinPoint.getKind().equals("method-call"))
			cnt++;
		if (!thisJoinPoint.getKind().equals("method-call"))
			return;
		if (!testStarted) return;
		Class instance = thisJoinPoint.getThis().getClass();
		Class target = thisJoinPoint.getTarget() != null ? thisJoinPoint.getTarget().getClass() : thisJoinPoint.getStaticPart().getSignature()
				.getDeclaringType();
		captureSequenceStep2(thisJoinPoint);
		System.err.println(instance.getSimpleName() + "." + thisJoinPoint.getSignature().getName() + " called " + target.getSimpleName() +
				"." + thisJoinPoint.getSignature().getName());

		if (target != null && !target.getName().contains("Test") && !target.isAnonymousClass()) {
			if (classInfos.stream().filter(cl -> cl.getCurrent().getSimpleName().equals(instance.getSimpleName())).count() == 0) {
				addClass(target);
			}
		}

		if (instance != null && !instance.getName().contains("Test") && !instance.isAnonymousClass()) {
			if (classInfos.stream().filter(cl -> cl.getCurrent().getSimpleName().equals(instance.getSimpleName())).count() == 0) {
				addClass(target);
			}
		}
	}

	after() : allMethodCalls() {
		JoinPoint jp = ((JoinPoint)thisJoinPoint);
		Object instance = jp.getThis();
	}

	after() returning(Object r): allMethodCalls() {
		if (!testStarted) return;
		Class instance = thisJoinPoint.getThis().getClass();
		Class target = thisJoinPoint.getTarget() != null ? thisJoinPoint.getTarget().getClass() : thisJoinPoint.getStaticPart().getSignature()
				.getDeclaringType();
		Signature signature = thisJoinPoint.getSignature();
		if (r != null) {
			captureSequenceStep2(thisJoinPoint, r);
			System.err.println(target.getSimpleName() + " returned back to " + instance.getSimpleName() +"." + thisJoinPoint
					.getSignature().getName() + " with value " + r.toString());
		}
		else {
			captureSequenceStep2(thisJoinPoint, null);
			System.err.println(target.getSimpleName() + " returned back to " + instance.getSimpleName() + "." + thisJoinPoint
					.getSignature().getName() + " with no return " + "value");
		}
	}

	before() : testMethod() {
		testStarted = true;
		registerShutdownHook();
	}

	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() ->  {
			System.out.println("Shutdown Hook is running !");
			addMissingTypes();
			addInterfaces();
			addArgumentTypes();
			String classDiagramText = classDiagramGenerator.generateClassDiagramText(classInfos);
			System.out.println(classDiagramText);
			writeToFile("class.java", classDiagramText);
		}));
	}

	after() : testMethod() {
		String className = thisJoinPoint.getThis().getClass().getSimpleName();
		String testName = thisJoinPoint.getSignature().getName();
		String sequenceDiagramText1 = greenSnarkSequenceDiagramGenerator.generateSequenceDiagram(main);
		String filename = "myseq_" + className + "_" + testName + ".seq";
		writeToFile(filename, sequenceDiagramText1);
		main = new SequenceStep2();
		currentNode = main;
	}

	private void writeToFile(String filename, String text) {
		String path = "/Users/nishant/umloutput";
		path = System.getProperty("user.dir");
		try (FileWriter file = new FileWriter(Paths.get(path,filename).toString())) {
			file.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createPNG() {
		List<String> commands = getCommandLines("/Users/nishant/umloutput/cmd.sh");
		for (String command : commands) {
			try {
				String[] commandPart = new String[3];
				Process p;
				if (command.contains("|")) {
					commandPart[0] = "/bin/sh";
					commandPart[1] = "-c";
					commandPart[2] = command;
					p = Runtime.getRuntime().exec(commandPart);
				}
				else
					p = Runtime.getRuntime().exec(command);

				StringBuilder output = new StringBuilder();
				StringBuilder eoutput = new StringBuilder();

				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}

				BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				String error;
				while ((error = errorReader.readLine()) != null) {
					eoutput.append(error + "\n");
				}

				int exitVal = p.waitFor();
				if (exitVal == 0) {
					System.out.println("Success!");
					System.out.println(output);
				} else {
					//abnormal...
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> getCommandLines(String file) {
		List<String> cmds = null;
		try {
			cmds = Files.readAllLines(Paths.get(file));
			cmds = cmds.stream().filter(cmd -> !cmd.equals("")).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cmds;
	}

}
