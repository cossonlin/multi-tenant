# Project Title

Multi tenant

## Getting Started

### Prerequisites

* [JDK 8](http://www.oracle.com/technetwork/pt/java/javase/overview/index.html) - Ensure JAVA_HOME environment variable
  is set and points to your JDK installation

* [Maven](https://maven.apache.org/) - Dependency Management Download from https://maven.apache.org/

### Installing & Running the tests

Run maven clean install command will install the dependencies, compile and run the tests

```
mvn clean install
```

### And coding style tests

```

```

## Deployment

JAR package will be created under target/multi-tenant-{versionNo}.jar after packaging then you can run below
command to bring up the application

```
java -jar target/multi-tenant-{versionNo}.jar
```

## Usage

Dynamically select the datasource base on the parameter in the url


## Built With

* [Maven](https://maven.apache.org/)

## Authors

* **Lin Lin**