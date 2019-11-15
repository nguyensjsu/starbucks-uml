# starbucks-uml
Starbucks UML Generator

## Steps to setup the UML generator application

1. Copy the folder "uml-script" which contains the script and required jars to your desired location.
2. Make changes as per section 2 to starbucks build.gradle file. 
3. Command to run 

```
./umlgen.sh <starbucks project root path> <test to run (starbucks.MainTest)> <output location>
```


## Section 2 - Additions to build.gradle file of starbucks (to enable aspect weaving)
- Change the buildscript block to the below.

```
buildscript {
    repositories {
        maven { url "https://maven.eveoh.nl/content/repositories/releases" }
    }
    dependencies {
        classpath("nl.eveoh:gradle-aspectj:2.0")
        classpath fileTree(dir: 'libs', include: '*.jar')
    }
}
```
- Add the aspectjVersion and plugin as below,

```
project.ext {
    aspectjVersion = '1.8.12'
}

apply plugin: "aspectj"
```

- Add the following to dependencies,

```
compileAspect {
        additionalAjcArgs = ['encoding': 'UTF-8', 'source': '1.8', 'target': '1.8']
    }

    compileTestAspect {
        additionalAjcArgs = ['encoding': 'UTF-8', 'source': '1.8', 'target': '1.8']
    }

    ajInpath files("<uml-library location containing junit2uml jar>"){

    }

    testAjInpath files("<uml-library location containing junit2uml jar>") {

    }
```

#### Please refer to https://github.com/nguyensjsu/starbucks-uml/blob/master/starbucks/build.gradle for detailed example.
