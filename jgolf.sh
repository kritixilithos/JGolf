#!/bin/bash

FILENAME=$1               #EXTRACT FILE NAME WITHOUT EXTENSION
java compile $FILENAME    #COMPILE JGOLF TO JAVA
echo "jgolf.sh: Finished converting code to Java"
FILENAME="$FILENAME.java" #ADD JAVA EXTENSION
javac $FILENAME           #COMPILE JAVA PROGRAM
echo "jgolf.sh: Finished compiling the Java code"
echo "jgolf.sh: There may have been errors above ^"
sleep 1
echo ""
echo "jgolf.sh: Executing JGolf program..."
java $1 "${@:2}"          #EXECUTE JAVA PROGRAM WITH ARGS
echo "jgolf.sh: Execution done"
