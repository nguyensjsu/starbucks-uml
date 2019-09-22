mvn -f {{path}}/pom.xml aspectj:compile
mvn -f {{path}}/pom.xml aspectj:test-compile
mvn -f {{path}}/pom.xml -Dtest={{test}} surefire:test