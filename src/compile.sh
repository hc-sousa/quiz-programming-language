#!/bin/bash
# builds language, runs quizMain using 1st argument (name of an example in the examples folder) 

./build.sh

java quizMain < ../examples/$1

mv QuizOutput.java ../output/QuizOutput.java
