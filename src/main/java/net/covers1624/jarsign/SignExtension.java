package net.covers1624.jarsign;

import groovy.lang.Closure;
import net.covers1624.jarsign.jar.JarSignSpecExtension;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by covers1624 on 19/8/20.
 */
public class SignExtension {

    private final Project project;
    private final List<Supplier<JarSignSpecExtension>> jarExtensions = new ArrayList<>();

    public SignExtension(Project project) {
        this.project = project;
    }

    public void jars(Closure<JarSignSpecExtension> closure) {
        //Delay everything, we process this list in AfterEvaluate
        jarExtensions.add(() -> {
            JarSignSpecExtension ext = new JarSignSpecExtension(project);
            Utils.executeDelegate(ext, closure);
            return ext;
        });
    }

    public List<Supplier<JarSignSpecExtension>> getJarExtensions() {
        return jarExtensions;
    }
}
