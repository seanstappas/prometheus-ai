# Prometheus AI
Prometheus AI model, containing the following layers:
* [Neural Network (NN)](src/main/java/nn)
* [Knowledge Node Network (KNN)](src/main/java/knn)
* [Expert System (ES)](src/main/java/es)
* [Meta Reasoner (META)](src/main/java/meta)

The KNN and ES are still under active development, and the NN and META are basic skeletons at this point.

![alt text](docs/knn_graph.png)

## Contributing
To contribute changes to the code base, create a branch and submit a [pull request](https://help.github.com/articles/about-pull-requests/). The branch merge will require a code review and that all tests (in the [`test`](src/test) directory) are passed. These tests will automatically be run at every change to the main branch with (Travis CI)[https://travis-ci.com/seanstappas/prometheus-ai].

## Background
### Prometheus Theory
For a basic and slightly outdated overview of Prometheus, see the [report provided in the `docs` directory](docs/prometheus-ai-1.pdf). For further background on the theory of Prometheus, contact [Prof. Vybihal](http://www.cs.mcgill.ca/~jvybihal/).

### Maven
This project is a Java Maven project, with library dependencies specified in the [`pom.xml`](pom.xml) file. Maven allows dependencies to be specified in a file without needing to keep track of jar files. An introduction to Maven can be found [here](https://maven.apache.org/what-is-maven.html).

### Google Guice
Google Guice is used as the backbone for the various dependencies within the code. Guice neatly allows the implementation of various important OOP principles, like dependency inversion. An introduction to Guice can be found [here](https://github.com/google/guice/wiki/Motivation).

### Mockito
Mockito is used to create mock objects for behavior-driven unit tests. Mock objects are essentially "fake" versions of objects used to simulate dependencies in unit tests. Mockito couples very well with the dependency injection of Guice. An overview of Mockito can be found [here](http://site.mockito.org/) and an example of behavior-driven development (BDD) with Mockito can be found [here](https://www.tutorialspoint.com/mockito/mockito_bdd.htm).

### TestNG
TestNG is a testing library much like JUnit. It is used for all the unit tests and integration tests found under the [`test`](src/test) directory.

### Apache Commons Lang 3
Apache Commons Lang provides many nice objects to avoid boilerplate code for Java objects. It is used for creating the `hashCode()`, `equals()` and `toString()` methods of most objects in the code. Details on this library can be found [here](https://commons.apache.org/proper/commons-lang/).

### GraphStream
Graphstream is used for plotting the KNN network. Details on this library can be found [here](http://graphstream-project.org/).

## Javadoc
Javadoc can be found [here](http://cs.mcgill.ca/~sstapp/prometheus/index.html).

