package org.example.processors;

import javax.annotation.processing.*;
import com.google.auto.service.AutoService;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    public BuilderProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

        @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Builder.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation ->
                roundEnv.getElementsAnnotatedWith(annotation)
                        .forEach(this::generateBuilderFile)
        );
        return true;
    }

private void generateBuilderFile(Element element) {
        String className = element.getSimpleName().toString();
        String packageName = element.getEnclosingElement().toString();
        String builderName = className + "Builder";
        String builderFullName = packageName + "." + builderName;
        messager.printMessage(Diagnostic.Kind.NOTE, "Starting to generate Builder for " + className);
        List<? extends Element> fields = element.getEnclosedElements()
                .stream().filter(e -> ElementKind.FIELD.equals(e.getKind())).toList();
        try (PrintWriter writer = new PrintWriter(filer.createSourceFile(builderFullName).openWriter())) {
            writer.println("""
                    package %s;
                         
                    public class %s {
                    """
                    .formatted(packageName, builderName)
            );
            // Private members
            fields.forEach(field ->
                    writer.print("""
                                private %s %s;
                            """.formatted(field.asType().toString(), field.getSimpleName())
                    )
            );

            writer.println();
            // Field method
            fields.forEach(field ->
                    writer.println("""
                                public %s %s(%s value) {
                                    %s = value;
                                    return this;
                                }
                            """.formatted(builderName, field.getSimpleName(),
                            field.asType().toString(), field.getSimpleName())
                    )
            );

            // Build method
            writer.println("""
                        public %s build() {
                            return new %s(%s);
                        }
                    """.formatted(className, className,
                    fields.stream().map(Element::getSimpleName).collect(joining(", ")))
            );
            writer.println("}");
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
