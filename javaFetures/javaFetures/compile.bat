@echo off
REM Batch script to compile and run Java Collections Framework examples

echo ========================================
echo Compiling Java Collections Framework
echo ========================================

cd src

echo.
echo [1/8] Compiling ListExamples...
javac collections\ListExamples.java

echo [2/8] Compiling SetExamples...
javac collections\SetExamples.java

echo [3/8] Compiling MapExamples...
javac collections\MapExamples.java

echo [4/8] Compiling QueueExamples...
javac collections\QueueExamples.java

echo [5/8] Compiling CollectionUtilities...
javac collections\CollectionUtilities.java

echo [6/8] Compiling AdvancedCollectionConcepts...
javac collections\AdvancedCollectionConcepts.java

echo [7/8] Compiling Streams...
javac streams\*.java

echo [8/8] Compiling Multithreading...
javac multithreading\*.java

echo.
echo Compiling Main...
javac Main.java

echo.
echo ========================================
echo Compilation Complete!
echo ========================================
echo.
echo To run the interactive menu:
echo   java Main
echo.
echo To run individual examples:
echo   java collections.ListExamples
echo   java collections.SetExamples
echo   java collections.MapExamples
echo   java collections.QueueExamples
echo   java collections.CollectionUtilities
echo   java collections.AdvancedCollectionConcepts
echo   java streams.StreamsRunner
echo   java multithreading.ThreadingRunner
echo.

cd ..
pause
