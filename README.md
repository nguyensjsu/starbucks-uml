# starbucks-uml
Starbucks UML Generator

## Steps to setup the UML generator application

1. Copy the content of uml-graph to java lib folder. (For ex: "/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/lib")
2. Copy the sequence folder to any location in the machine.
3. Install graphviz (brew install graphviz).
4. Modify the junit2umlConfig.conf file variables as follows, 
 - outputLocation - The desired output path for your UML and Sequence diagrams.
 - projectRootPath - The root path of your Starbucks project
 - umlGraphPath - The path of the java lib folder in your machine. This is where the UmlGraph jar should be present. (This is included in the git repo in the uml-graph folder.)
 - sequenceJarPath - The path where sequence-10.jar file is present. (This is included in the git repo in the sequence folder.)
 - dotPath - This is the path containing the dot executable (for ex: "/usr/local/bin/dot")

## An example config file looks like this,

	outputLocation = "/Users/saiprithipa/output"
	projectRootPath = "/Users/saiprithipa/git/starbucks-uml/starbucks"
	umlGraphPath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/lib"
	sequenceJarPath = "/Users/saiprithipa/git/Updated/starbucks-uml/sequence"
	dotPath = "/usr/local/bin"

5. Build the project "junit2uml-runner-gradle" using "./gradlew clean build".
6. Build the project "junit2uml-gradle" using "./gradlew clean build".
7. Add the following entry in your bash profile, "alias junit2uml='/Users/saiprithipa/git/starbucks-uml/junit2uml-runner-gradle/test2uml.sh'" (test2uml.sh file can be found in junit2uml-runner-gradle project.)
8. Run the following command, "junit2uml -c <config_file_location> -t "starbucks.MainTest" 

Check the results in the output folder, which you provided in the config file.

