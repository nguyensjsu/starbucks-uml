package com.saip;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String[] args) throws IOException {

        Options options = new Options();

        Option configFile = new Option("c", "configFile", true, "config file location");
        configFile.setRequired(true);
        options.addOption(configFile);

        Option testMethod = new Option("t", "testMethod", true, "testMethod");
        testMethod.setRequired(true);
        options.addOption(testMethod);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        String testMethodValue = cmd.getOptionValue("testMethod");
        String configFilePath = cmd.getOptionValue("configFile");

        File file = new File(configFilePath);
        Config cfg = ConfigFactory.parseFile(file);
        String outputLoc = cfg.getString("outputLocation");
        String projectRootPath = cfg.getString("projectRootPath");
        String umlGraphPath = cfg.getString("umlGraphPath");
        String sequenceJarPath = cfg.getString("sequenceJarPath");
        String dotPath = cfg.getString("dotPath");

//		runMavenScript(projectRootValue, testMethodValue, outputValue);
//        runGradleScript(projectRootValue, testMethodValue, outputValue);
        runGradleScript(projectRootPath, testMethodValue, outputLoc,umlGraphPath,dotPath,sequenceJarPath);
    }

    private static void runMavenScript(String projectRootValue, String testMethodValue, String outputValue) throws IOException {
        StringBuilder result = new StringBuilder();
        Files.createDirectories(Paths.get(outputValue));
        result.append("mvn -f {{path}}/pom.xml aspectj:compile\n");
        result.append("mvn -f {{path}}/pom.xml aspectj:test-compile\n");
        result.append("mvn -f {{path}}/pom.xml -Dtest={{test}} surefire:test\n");
        result.append("java -classpath \"/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/lib/UmlGraph"
                + ".jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/lib/tools.jar\" org.umlgraph.doclet.UmlGraph -private "
                + "-output - {{path}}/class.java -ranksep 1 | /opt/local/bin/dot -Tpng -o{{output}}/umlgraph.png\n");
        result.append("rm {{path}}/class.java\n");
        testMethodValue = testMethodValue.replace(".", "#");
        String commands = result.toString().replace("{{path}}", projectRootValue).replace("{{test}}", testMethodValue).replace("{{output}}", outputValue);
        processCommands(commands.toString().split("\n"));
        processSequenceCommands(projectRootValue, testMethodValue, outputValue);
    }

    private static void runGradleScript1(String projectRootValue, String testMethodValue, String outputValue) throws IOException {
        StringBuilder result = new StringBuilder();
        Files.createDirectories(Paths.get(outputValue));
        result.append("rm -rf {{path}}/build\n");
        result.append("{{path}}/gradlew -p {{path}} compileAspect\n");
        result.append("{{path}}/gradlew -p {{path}} compileTestAspect\n");
        result.append("{{path}}/gradlew -p {{path}} test --tests *{{test}}*\n");
        result.append("java -classpath \"/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/lib/UmlGraph"
                + ".jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/lib/tools.jar\" org.umlgraph.doclet.UmlGraph -private "
                + "-output - {{path}}/class.java -ranksep 1 | /usr/local/bin/dot -Tpng -o{{output}}/umlgraph.png\n");
        result.append("rm {{path}}/class.java\n");
        String commands = result.toString().replace("{{path}}", projectRootValue).replace("{{test}}", testMethodValue).replace("{{output}}", outputValue);
        processCommands(commands.toString().split("\n"));
        processSequenceCommands(projectRootValue, testMethodValue, outputValue);
    }

    private static void runGradleScript(String projectRootValue, String testMethodValue, String outputValue, String umlGraphPath, String dotPath, String sequenceJarPath) throws IOException {
        StringBuilder result = new StringBuilder();
        Files.createDirectories(Paths.get(outputValue));
        result.append("rm -rf {{path}}/build\n");
        result.append("{{path}}/gradlew -p {{path}} compileAspect\n");
        result.append("{{path}}/gradlew -p {{path}} compileTestAspect\n");
        result.append("{{path}}/gradlew -p {{path}} test --tests *{{test}}*\n");
        result.append("java -classpath \"{{umlGraphPath}}/UmlGraph"
                + ".jar:{{umlGraphPath}}/tools.jar\" org.umlgraph.doclet.UmlGraph -private "
                + "-output - {{path}}/class.java -ranksep 1 | {{dotPath}}/dot -Tpng -o{{output}}/umlgraph.png\n");
        result.append("rm {{path}}/class.java\n");
        String commands = result.toString().replace("{{path}}", projectRootValue)
                .replace("{{test}}", testMethodValue)
                .replace("{{umlGraphPath}}", umlGraphPath)
                .replace("{{dotPath}}", dotPath)
                .replace("{{output}}", outputValue);
        processCommands(commands.split("\n"));
        processSequenceCommands(projectRootValue, sequenceJarPath, outputValue);
    }

    private static void processSequenceCommands(String projectRootValue, String sequenceJarPath, String outputValue) {
        StringBuilder cmd = new StringBuilder();
        File[] files = new File(projectRootValue).listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().contains("myseq_")) {
                cmd.append("java -jar {{sequenceJarPath}}/sequence-10.0.jar --headless" + " " + file.getAbsolutePath() + "\n");
                cmd.append("mv " + file.getAbsolutePath().replace(".seq", "") +".png " + "{{output}}/" + file.getName().replace(".seq", "") +".png\n");
                cmd.append("rm " + file.getAbsolutePath() +"\n");
            }
        }
        String resolvedCmd = cmd.toString().replace("{{output}}", outputValue).replace("{{sequenceJarPath}}", sequenceJarPath);
        processCommands(resolvedCmd.split("\n"));
    }

    private static void processSequenceCommands1(String projectRootValue, String testMethodValue, String outputValue) {
        StringBuilder cmd = new StringBuilder();
        File[] files = new File(projectRootValue).listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().contains("myseq_")) {
                cmd.append("java -jar /Users/sainishved/saivednish_sjsu/202_TA/starbucks-uml/sequence/sequence-10.0.jar --headless" + " " + file.getAbsolutePath() + "\n");
                cmd.append("mv " + file.getAbsolutePath().replace(".seq", "") +".png " + "{{output}}/" + file.getName().replace(".seq", "") +".png\n");
                cmd.append("rm " + file.getAbsolutePath() +"\n");
            }
        }
        String resolvedCmd = cmd.toString().replace("{{output}}", outputValue);
        processCommands(resolvedCmd.split("\n"));
    }

    private static void processCommands(String[] commands) {
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
                while (reader.ready() && (line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }
                reader.close();

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String error;
                while (errorReader.ready() && (error = errorReader.readLine()) != null) {
                    eoutput.append(error + "\n");
                }
                errorReader.close();

                int exitVal = p.waitFor();
                if (exitVal == 0) {
                    System.out.println("Success : " + command);
                } else {
                    System.out.println("Failed : " + command);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
