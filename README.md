# ☕ JavaUnderstanding — Complete Java Learning Repository

A comprehensive, well-structured Java learning project with detailed notes, examples, and Mac-specific setup instructions. Every concept is covered from basics to advanced, with each file independently runnable.

---

## 🍎 Mac Setup Guide

### Step 1: Install Java JDK on Mac

#### Option A: Using Homebrew (Recommended)

```bash
# Install Homebrew if you haven't already
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install OpenJDK 17 (LTS)
brew install openjdk@17

# Create a symlink so macOS can find it
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
    /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

#### Option B: Download from Adoptium (GUI Installer)

1. Go to https://adoptium.net/
2. Download **Temurin JDK 17** for **macOS aarch64** (Apple Silicon) or **x64** (Intel Mac)
3. Run the `.pkg` installer — it sets everything up automatically

#### Option C: Download from Oracle

1. Go to https://www.oracle.com/java/technologies/downloads/
2. Download **JDK 17** for macOS
3. Run the `.dmg` installer

---

### Step 2: Set JAVA_HOME and PATH

Mac uses **zsh** as the default shell. Edit your `~/.zshrc`:

```bash
# Open ~/.zshrc in nano (or use any editor like VS Code)
nano ~/.zshrc
```

Add these lines at the bottom:

```bash
# Java configuration
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

> **Apple Silicon (M1/M2/M3) Mac with Homebrew:**
> ```bash
> export JAVA_HOME=/opt/homebrew/opt/openjdk@17
> export PATH="$JAVA_HOME/bin:$PATH"
> ```

> **Intel Mac with Homebrew:**
> ```bash
> export JAVA_HOME=/usr/local/opt/openjdk@17
> export PATH="$JAVA_HOME/bin:$PATH"
> ```

Apply the changes:

```bash
source ~/.zshrc
```

---

### Step 3: Verify Installation

```bash
java -version
# Expected output: openjdk version "17.x.x" ...

javac -version
# Expected output: javac 17.x.x

echo $JAVA_HOME
# Expected output: /opt/homebrew/opt/openjdk@17 (or similar path)
```

---

## 🚀 How to Compile and Run

### Compile a Single File

```bash
# Navigate to the project root
cd JavaUnderstanding

# Compile a single file into the 'out' directory
javac -encoding UTF-8 -d out src/basics/HelloWorld.java

# Run it (use package.ClassName format)
java -cp out basics.HelloWorld
```

### Compile All Files at Once

```bash
# Generate a list of all .java source files
find src -name "*.java" > sources.txt

# Compile all of them
javac -encoding UTF-8 -d out @sources.txt

# Run any file
java -cp out basics.HelloWorld
java -cp out oops.Inheritance
java -cp out java8features.StreamAPI
```

### Using the run.sh Script

A convenient shell script is included:

```bash
# Make it executable (only needed once)
chmod +x run.sh

# Compile and run any class
./run.sh basics.HelloWorld
./run.sh oops.Polymorphism
./run.sh java8features.LambdaExpressions
```

---

## 🗂️ Project Structure

```
JavaUnderstanding/
├── README.md                  ← This guide
├── run.sh                     ← Compile & run helper script
├── .gitignore                 ← Ignores build artifacts
└── src/
    ├── basics/                ← Core Java fundamentals
    │   ├── HelloWorld.java    ← First program, print statements
    │   ├── DataTypes.java     ← Primitive & reference types
    │   ├── Variables.java     ← Variable declaration & scope
    │   ├── Operators.java     ← Arithmetic, logical, bitwise operators
    │   ├── TypeCasting.java   ← Implicit & explicit casting
    │   └── UserInput.java     ← Scanner class for user input
    ├── controlflow/           ← Control flow statements
    │   ├── IfElse.java        ← if, else if, else, ternary
    │   ├── SwitchCase.java    ← switch statement & expression
    │   ├── ForLoop.java       ← for, enhanced for loop
    │   ├── WhileLoop.java     ← while loop
    │   ├── DoWhileLoop.java   ← do-while loop
    │   └── BreakContinue.java ← break, continue, labeled
    ├── arrays/                ← Array concepts
    │   ├── ArrayBasics.java   ← 1D arrays, declaration, traversal
    │   ├── MultiDimensionalArray.java ← 2D & jagged arrays
    │   └── ArrayMethods.java  ← Arrays utility class, sorting
    ├── strings/               ← String handling
    │   ├── StringBasics.java  ← String creation, immutability
    │   ├── StringMethods.java ← Common String methods
    │   └── StringBuilderDemo.java ← StringBuilder & StringBuffer
    ├── oops/                  ← Object-Oriented Programming
    │   ├── ClassesAndObjects.java ← Classes, objects, fields
    │   ├── Constructors.java  ← Default, parameterized, copy constructor
    │   ├── Encapsulation.java ← Private fields, getters/setters
    │   ├── Inheritance.java   ← extends, super, method overriding
    │   ├── Polymorphism.java  ← Compile-time & runtime polymorphism
    │   ├── Abstraction.java   ← Abstract classes
    │   └── Interfaces.java    ← Interface, default/static methods
    ├── exceptionhandling/     ← Exception handling
    │   ├── TryCatch.java      ← try-catch-finally basics
    │   ├── ThrowThrows.java   ← throw vs throws keyword
    │   ├── CustomException.java ← Creating custom exceptions
    │   └── FinallyBlock.java  ← finally block behavior
    ├── collections/           ← Java Collections Framework
    │   ├── ArrayListDemo.java ← ArrayList operations
    │   ├── LinkedListDemo.java ← LinkedList operations
    │   ├── HashMapDemo.java   ← HashMap key-value storage
    │   ├── HashSetDemo.java   ← HashSet unique elements
    │   └── IteratorDemo.java  ← Iterator & ListIterator
    ├── fileio/                ← File Input/Output
    │   ├── FileReading.java   ← Reading files with FileReader
    │   ├── FileWriting.java   ← Writing files with FileWriter
    │   └── BufferedReaderWriter.java ← Buffered I/O
    ├── multithreading/        ← Multithreading
    │   ├── ThreadBasics.java  ← Thread class, lifecycle
    │   ├── RunnableInterface.java ← Runnable interface
    │   └── SynchronizationDemo.java ← synchronized keyword
    ├── java8features/         ← Java 8+ Modern Features
    │   ├── LambdaExpressions.java ← Lambda syntax & usage
    │   ├── StreamAPI.java     ← Stream operations
    │   ├── OptionalClass.java ← Optional to avoid NullPointerException
    │   └── FunctionalInterfaces.java ← Predicate, Function, Consumer
    └── advanced/              ← Advanced Java Topics
        ├── Generics.java      ← Generic classes & methods
        ├── EnumDemo.java      ← Enum types & methods
        ├── AnnotationsDemo.java ← Built-in & custom annotations
        └── RecordsDemo.java   ← Java 16+ Records
```

---

## 🛠️ Common Mac Issues & Fixes

### Issue 1: `javac: command not found`

**Cause:** Java is not installed or PATH is not set correctly.

**Fix:**
```bash
# Check if Java is installed
/usr/libexec/java_home -V

# If not found, install using Homebrew
brew install openjdk@17

# Add to ~/.zshrc (Apple Silicon)
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### Issue 2: `JAVA_HOME` not set

**Cause:** Environment variable not configured.

**Fix:**
```bash
# Check current value
echo $JAVA_HOME

# Set it automatically based on installed Java
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Add permanently to ~/.zshrc
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

### Issue 3: `Error: Could not find or load main class`

**Cause:** Running with the wrong classpath or wrong class name.

**Fix:**
```bash
# WRONG - don't run from src/ directory
java basics.HelloWorld

# CORRECT - compile first, then run from project root with -cp
javac -encoding UTF-8 -d out src/basics/HelloWorld.java
java -cp out basics.HelloWorld
```

### Issue 4: `error: class HelloWorld is public, should be declared in a file named HelloWorld.java`

**Cause:** The filename must exactly match the public class name.

**Fix:** Ensure the file is named exactly as the public class, including case sensitivity. Mac's filesystem is case-insensitive by default but Java is case-sensitive.

### Issue 5: Multiple Java versions installed

**Cause:** Homebrew, Xcode, and manual installs may conflict.

**Fix:**
```bash
# List all installed Java versions
/usr/libexec/java_home -V

# Use a specific version
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Or switch with jenv (install: brew install jenv)
jenv add $(/usr/libexec/java_home -v 17)
jenv global 17
```

### Issue 6: `package X does not exist` or classpath errors

**Cause:** Compiled .class files not found or wrong -cp argument.

**Fix:**
```bash
# Always compile from project root, output to 'out' directory
javac -encoding UTF-8 -d out src/basics/HelloWorld.java

# Run from project root with correct classpath
java -cp out basics.HelloWorld
```

### Issue 7: `Exception in thread "main" java.io.FileNotFoundException`

**Cause:** File I/O examples need sample files that don't exist yet.

**Fix:** The FileIO examples create sample files automatically when run. Run them from the project root directory.

---

## 💻 IDE Setup on Mac

### VS Code (Recommended for beginners)

1. Download from https://code.visualstudio.com/
2. Install the **Extension Pack for Java** (search in Extensions tab)
   - Includes: Language Support for Java, Debugger for Java, Test Runner for Java, Maven for Java, Project Manager for Java
3. Open the `JavaUnderstanding` folder: `File → Open Folder`
4. VS Code will auto-detect Java files and set up the project
5. Right-click any Java file → **Run Java** to execute it directly

### IntelliJ IDEA Community Edition (Recommended for serious learners)

1. Download from https://www.jetbrains.com/idea/download/ (Community = free)
2. Open IntelliJ IDEA → **Open** → select `JavaUnderstanding` folder
3. Go to **File → Project Structure → SDKs** → Add your JDK path
   - Apple Silicon: `/opt/homebrew/opt/openjdk@17`
   - Intel: `/usr/local/opt/openjdk@17`
4. Set **Project SDK** to the JDK you added
5. Mark `src` as **Sources Root**: right-click `src` folder → **Mark Directory as → Sources Root**
6. Right-click any Java file → **Run** to execute it

---

## 📚 Learning Path

Follow this recommended order:

1. **basics/** — Start here! Learn syntax fundamentals
2. **controlflow/** — Decision making and loops
3. **arrays/** — Working with collections of data
4. **strings/** — String manipulation (very common in real projects)
5. **oops/** — Core OOP concepts — most important for Java!
6. **exceptionhandling/** — Writing robust, error-safe code
7. **collections/** — Java's built-in data structures
8. **fileio/** — Reading and writing files
9. **multithreading/** — Concurrent programming
10. **java8features/** — Modern Java features (used heavily in industry)
11. **advanced/** — Generics, Enums, Annotations, Records

---

## ✅ Quick Start (TL;DR)

```bash
# 1. Install Java (Mac)
brew install openjdk@17

# 2. Add to ~/.zshrc (Apple Silicon)
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 3. Clone and enter the repo
git clone https://github.com/anandraoS/JavaUnderstanding.git
cd JavaUnderstanding

# 4. Make run script executable
chmod +x run.sh

# 5. Run your first Java program
./run.sh basics.HelloWorld
```
