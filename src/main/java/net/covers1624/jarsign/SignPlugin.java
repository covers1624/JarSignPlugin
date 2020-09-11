package net.covers1624.jarsign;

import net.covers1624.jarsign.jar.JarSignSpecExtension;
import net.covers1624.jarsign.jar.SignJarTask;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 19/8/20.
 */
public class SignPlugin implements Plugin<Project> {

    private SignExtension extension;

    @Override
    public void apply(Project project) {
        extension = project.getExtensions().create("signing", SignExtension.class, project);
        project.afterEvaluate(this::afterEvaluate);
    }

    public void afterEvaluate(Project project) {
        //Validate there arent any duplicates defined.
        Set<Jar> seenTasks = new HashSet<>();
        List<JarSignSpecExtension> extensions = extension.getJarExtensions().stream()//
                .map(Supplier::get)//
                .collect(Collectors.toList());
        extensions.forEach(e -> {
            e.getTasks().forEach(task -> {
                if (!seenTasks.add(task)) {
                    throw new RuntimeException("Task '" + task.getName() + "' declared on multiple jars blocks.");
                }
            });
        });

        extensions.forEach(e -> {
            e.getTasks().forEach(task -> {
                project.getLogger().lifecycle("Adding task for: " + task.getName());
                SignJarTask signTask = project.getTasks().create("sign" + StringUtils.capitalize(task.getName()), SignJarTask.class);
                task.finalizedBy(signTask);
                signTask.dependsOn(task);
                signTask.setInput(task.getArchivePath());
                signTask.setOutput(task.getArchivePath());
                signTask.copyFrom(e);
            });
        });
    }
}
