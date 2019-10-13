parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
java -jar $parent_path/build/libs/junit2uml-runner-gradle-1.0-SNAPSHOT-all.jar com.saip.Main $1 $2 $3 $4