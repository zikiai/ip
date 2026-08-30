# Zikiai

Zikiai is a chatbot project written in Java. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/zikiai/Zikiai.java` file, right-click it, and choose `Run Zikiai.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _____ _ _    _       _
   |__  /(_) | _(_) __ _(_)
     / / | | |/ / |/ _` | |
    / /_ | |   <| | (_| | |
   /____||_|_|\_\_|\__,_|_|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Checking code style and running tests

Use JDK 25 and run these commands from the project root (the folder containing
`build.gradle`):

```bash
./gradlew checkstyleMain checkstyleTest
./gradlew check
```

The first command checks production and test Java code for style violations.
The second runs both Checkstyle and the JUnit tests. `./gradlew build` also
includes these checks. On Windows, replace `./gradlew` with `gradlew.bat`.
Any Checkstyle errors or warnings fail the build.

Checkstyle 11.0.0 uses the rules in `config/checkstyle/checkstyle.xml` and the
test Javadoc exceptions in `config/checkstyle/suppressions.xml`. These files
come from [AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle),
following the [SE-EDU setup tutorial](https://se-education.org/guides/tutorials/checkstyle.html).
The configuration complements code review; passing Checkstyle does not prove
that every coding-standard rule or program behavior is correct.

Open the generated reports in a browser to see violations and their locations:

- `build/reports/checkstyle/main.html`
- `build/reports/checkstyle/test.html`
- `build/reports/tests/test/index.html` (JUnit results)

For optional editor feedback in IntelliJ, install **Checkstyle-IDEA**, select
Checkstyle **11.0.0**, and activate the local `config/checkstyle/checkstyle.xml`
configuration under **Settings > Tools > Checkstyle**. Include test sources in
the scan scope. Reload the Gradle project after changing `build.gradle`.

## Creating an executable JAR

Zikiai uses the Shadow plugin to create a self-contained JAR containing the
application and its runtime dependencies.

From the project root, build the JAR using:

```bash
./gradlew clean shadowJar
```

On Windows, use `gradlew.bat clean shadowJar` instead. The generated JAR is:

```text
build/libs/zikiai.jar
```

To run the JAR as a distributed application:

1. Copy `zikiai.jar` into an empty folder.
2. Open a terminal in that folder.
3. Run the following command using JDK 25:

   ```bash
   java -jar "zikiai.jar"
   ```

Zikiai creates its `data` folder relative to the folder from which the JAR is
run. Do not commit the generated JAR or the `build` folder to Git. For a GitHub
release, attach `build/libs/zikiai.jar` as the release binary instead.
