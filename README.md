# JsonK

**A lightweight, reflection-free JSON library for Java.**

JsonK is a high-performance JSON library for Java. Its key feature is the absence of reflection, which makes it an excellent choice for Java applications targeting **GraalVM Native Image**.

## Features

*   **🚀 High Performance**: Fast serialization and deserialization with minimal overhead.
*   **✨ Simple API**: An intuitive and easy-to-use API for converting objects to and from JSON.
*   **🚫 Reflection-Free**: Operates without reflection, ensuring seamless compatibility with GraalVM Native Image out-of-the-box.

## Getting Started

To use JsonK in your project, add the appropriate dependency for your build system.

### Maven

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.jsonk</groupId>
    <artifactId>jsonk</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Gradle

Add the dependency to your `build.gradle` or `build.gradle.kts` file:

**Groovy DSL (`build.gradle`)**
```groovy
dependencies {
    implementation 'org.jsonk:jsonk:0.0.1'
}
```

**Kotlin DSL (`build.gradle.kts`)**
```kotlin
dependencies {
    implementation("org.jsonk:jsonk:0.0.1")
}
```

## Usage

Here is a quick example of how to use JsonK.

First, let's define a simple `User` class and annotate it with `@Json`:
```java
@Json
public record User(int id, String name) {
}
```

### Serialization

You can convert an object into a JSON string or write it to a `Writer`.

```java
var user = new User(1, "John Doe");

// 1. Convert to a JSON string
var json = Jsonk.toJson(user);
// Output: {"id":1,"name":"John Doe"}
System.out.println(json);

// 2. Write to a StringWriter (or any other Writer)
var writer = new StringWriter();
Jsonk.toJson(user, writer);
var jsonFromWriter = writer.toString();
```

### Deserialization

To convert a JSON string back into an object, simply provide the target class and the JSON data.

```java
var json = "{\"id\":2,\"name\":\"Jane Doe\"}";

var user = Jsonk.fromJson(json, User.class);

System.out.println(user.getName()); // Output: Jane Doe
```