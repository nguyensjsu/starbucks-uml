#!/bin/sh
CURRENT_DIR=${PWD}
# All the jars required for generating uml diagrams and sequence diagrams arguments
# are present in this location
JAR_PATH=$CURRENT_DIR/uml-jars

if [ "$#" -lt 3 ]
then
  echo 'Please provide three arguments - Project root path, Test to run and Output location'
  exit 1
fi

# Starbucks Project Root Path (where build.gradle is present)
PROJECT_ROOT=$1
# Test to run - This can be a single test or a test class
TEST_TO_RUN=$2
# Location for all output diagrams
OUTPUT_LOCATION=$3

# Create output directory if not exists
mkdir -p $OUTPUT_LOCATION

# Remove starbucks build directory if already exists to ensure fresh test run
BUILD_DIR=$PROJECT_ROOT/build
if [ -d "$BUILD_DIR" ]; then
  rm -rf $BUILD_DIR
fi

# ApectJ weaving for starbubcks src code
$PROJECT_ROOT/gradlew -p $PROJECT_ROOT compileAspect
# ApectJ weaving for starbucks test code
$PROJECT_ROOT/gradlew -p $PROJECT_ROOT compileTestAspect
# Running the test provided by the user
$PROJECT_ROOT/gradlew -p $PROJECT_ROOT test --tests *$TEST_TO_RUN*

# Finding location of graphviz's dot
DOT_PATH="$(which dot)"

# Generating the class diagram
java -classpath "$JAR_PATH/UmlGraph.jar:$JAR_PATH/tools.jar" org.umlgraph.doclet.UmlGraph -private -output - $PROJECT_ROOT/class.java -ranksep 1 | $DOT_PATH -Tpng -o$OUTPUT_LOCATION/class_diagram.png
# Removing the intermediate file once diagram is generated
rm $PROJECT_ROOT/class.java

REPLACER=".png"
# Iterating over all ".seq" files in the PROJECT_ROOT.
# Our Junit2Uml library generates an intermediate ".seq" file for every test that is run.
for filename in $PROJECT_ROOT/*.seq; do
  # Generating the sequence diagram for current file
  java -jar $JAR_PATH/sequence-10.0.jar --headless $filename
  # moving the png file to the output location provided by the user
  mv ${filename/.seq/$REPLACER} $OUTPUT_LOCATION
  # removing the intermediate ".seq" file after diagram is generated
  rm $filename
done

exit 0
