#!/bin/bash
# compiles and runs QuizOutput.java, then cleans .class files

cd ../output
javac QuizOutput.java
java QuizOutput
rm *.class
