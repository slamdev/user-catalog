# User catalog [![Build Status](https://travis-ci.org/slamdev/catalog.svg?branch=master)](https://travis-ci.org/slamdev/catalog)

## Technologies stack
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Gradle 2.14](https://docs.gradle.org/current/userguide/userguide.html) - project build system
* [Spring 4.3.2](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/) - main framework for server side

## Development workflow
[GitHub Flow](https://guides.github.com/introduction/flow/) is used on this project:
* each task should be done in separate branch
* after task is developed, [pull request](https://help.github.com/articles/proposing-changes-to-a-project-with-pull-requests/) to **master** branch should be created and assigned to responsible for application part person
* after pull request is created, CI server will process static code checks (build project, validate code, run tests), if any check fails the pull request will be marked as failed and developer should and fix issues
* after all checks passed, code should be reviewed by pull request assignee, and if there are no remarks, he\she should merge it to master branch and remove the obsolete task branch

## Project setup
[IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download/) is used for project development. The plugins below should be installed:
* Git Integration
* Github
* Gradle
* JavaScript Support
* JUnit
* Lombok Plugin
* Spring (all spring plugins)

### Step-by-step
1. Open idea and select **Checkout from Version Control** -> **GitHub**
2. Set Git Repository URL to **git@github.com:slamdev/catalog.git**
3. Press **Clone**
4. In the **Import project** window select **Import project from the external project model** and press **Next**
5. In the **Import Project** window leave all default options and press **Finish**
6. In the **Gradle project data to import** leave all default options and press **OK**
7. If **Unregistered VCS root detected** warning appears, press **Add root**
8. Enable **annotation processing** in IDEA settings
9. Select **View** -> **Tool windows** -> **Gradle** menu item
10. In the appeared **Gradle projects** window select **Execute gradle task** icon (green circle)
11. In the appeared **Run Gradle Task** window type **clean build** to the **Command line** input and press **OK**
12. Wait until gradle download all dependencies and build the project. The first time it could take up to 5 minutes

## Project run
There are two ways to run project from idea:
* Using gradle spring-boot plugin:
  1. Select **View** -> **Tool windows** -> **Gradle** menu item
  2. In the appeared **Gradle projects** window select **:[module-name]** -> **Tasks** -> **application** -> **bootRun**
  3. Open http://localhost:8080/ in the browser window
* Using the application main class:
  1. Open **[Module-Name]Application** class
  2. Right click on the **main** method and select **Run 'Application.main()'**
  3. Open http://localhost:8080/ in the browser window

## Tips and tricks

### Hot reloading

Spring provides and ability for fast reloading changed classes. Here are the details: https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html

But this feature works only if the class file is compiled. IDEA doesn't compile classes automatically after they are changed so in order to start fast reloading you need to compile the class manually by pressing **CTRL+F9**. Here are the details: http://stackoverflow.com/questions/24371111/spring-boot-spring-loaded-intellij-gradle

In order to make this process a bit faster you can use the [Reformat and Compile](https://plugins.jetbrains.com/plugin/8231?pr=idea_ce) that compile the code on pressing **CTRL+S** button or on *focus lost*. This plugin does not always run recompilation, so you need to be careful. Also this plugin formats the code and optimizes imports that is also useful.

### Adjust spring properties

You can adjust property values from the *application.properties* by passing them as **VM options** in IDEA run configurations. This will work only if you run the app via *Application.java* main class.

Eg. of **VM options**: *-DoptionName=optionValue*
