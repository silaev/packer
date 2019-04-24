# A java core based application for collecting an optimal package [![Build Status](https://travis-ci.org/silaev/packer.svg?branch=master)](https://travis-ci.org/silaev/packer) 

#### Prerequisites
Java 8+

#### General info
The app parses an input file so that to get a list of ItemPackages. 
  
#### Further improvements 
1. Add logs;
2. In order to check the uniqueness of items within an ItemPackage,
consider a following addition: a HashMap (keys: itemNumbers, values: counter);
3. Consider changing APIException to unchecked one so that 
to use in Java Stream API without a workaround. 
          
#### Requirements to consider before using the app.
1. The project should be built by means of Gradle. For that reason, run `gradlew clean build`.
Subsequently, to start the application make use of 
`gradlew run --args='src/main/resources/input.txt'`
where `src/main/resources/input.txt` is a path to file.
2. Most part of the code is covered with unit tests based on Junit5. 

