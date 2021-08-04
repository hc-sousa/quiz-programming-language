#!/bin/bash
# builds language

#rm QuizOutput*
# rm ../tmp/*

# secondary
mv quiz.g4 ../tmp
antlr4-build

# primary
mv ../tmp/quiz.g4 .
mv QuestionBank.g4 ../tmp
antlr4-build

mv ../tmp/QuestionBank.g4 .
