# Custom Annotation Processor

This project is a working sample for custom annotation processor. There are two modules:
* Annotation-processor
* Annotation-user

**Annotation-processor module**

This module contains the annotation definition as well as the processor code.

_Note: Uncomment the section in pom.xml if we place the AnnotationProcessor Path in the parent pom.xml.
Now it is kept in the annotation-user's pom.xml. The compiler arg -proc:none is used to tell to avoid looking for the annotation processor_

**Annotation-user**

This module makes use of the Builder annotation defined in annotation-processor. Once the parent is compiled it would generate the EmployeeBuilder.

_Note: We will need to compile the project once and mark the generated-sources of this module (under target) as Generated Sources Root._


### Annotation Processor In Detail

After we mark the elements with the desired annotations, the java file is compiled using 'javac' (Java Compiler). However the compiler does not know what to do with these annotations.
It needs some help in unfolding the meaning of these alien entities. This is where annotation processors come into play.
Annotation processors unfold the true intent of their respective supported annotation. The compiler runs in a loop until every annotation is resolved before it can generate the class files.
Just by having an annotation processor on our classpath when our code is compiling, our annotation processor will run.
It does so with the help of something called ‘Service Loader’, which finds the processors. We need to implement the processors in order to create our custom one.

1. The custom processor in the example is BuilderProcessor

`class BuilderProcessor extends AbstractProcessor { }`

2. The first function that we will implement is called ‘getSupportedSourceVersion()’. It is a good idea to always support the latest version that our javac is running on.

```
class BuilderProcessorextends AbstractProcessor {
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
```

3. The second function that we will implement is called ‘getSupportedAnnotationTypes()’, which tells javac what the fully qualified names of the annotations we care about are. So javac will only call our processor for the annotations that we tell it to.

```
class BuilderProcessorextends AbstractProcessor {
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(Builder.class.getCanonicalName());
  }
}
```

4. The third function is the most important one is called ‘process()’. Lets us understand what happens inside the process function.

    `public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) { }`
* We query our environment to get all the elements that our annotated with our annotation:
* We can use these collected elements, which could be classes, variables, functions and write the logic for what the annotation is supposed to do.You can also make use of libraries called ‘javaPoet’ and produce java files.


5. The fourth function that is present in the AbstractProcessor is called 'init'. 
``` 
@Override
public synchronized void init(ProcessingEnvironment processingEnv) {
  super.init(processingEnv);
  messager = processingEnv.getMessager();
  filer = processingEnv.getFiler();
}
```
  In 'init' we do all the initialization for our custom processor. The ProcessingEnvironment provides two functions:
  * getMessager() - Returns a Messager, which can be used to communicate the warnings or errors back to the user.
  * getFiler() - Returns a Filer object. In short, Filer helps us to actually create and write Java files.

