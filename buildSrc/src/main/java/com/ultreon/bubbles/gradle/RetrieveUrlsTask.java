package com.ultreon.bubbles.gradle;

import com.google.gson.JsonArray;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.repositories.UrlArtifactRepository;
import org.gradle.api.internal.artifacts.dependencies.AbstractModuleDependency;
import org.gradle.api.tasks.Internal;
import org.gradle.jvm.tasks.Jar;

import java.net.URI;
import java.net.URL;

/**
 * Retrieve URLs for the dependencies that will be downloaded by the game.
*/
public class RetrieveUrlsTask extends BaseTask {
    private final JsonArray urls = new JsonArray();

    public RetrieveUrlsTask() {
        super("retrieveUrls", "bubbles");

        this.doFirst(this::execute);
    }

    private void execute(Task task) {
        var project = task.getProject();
        var dependencies = project.getConfigurations().getByName("api").getDependencies();
        for (var dependency : dependencies) {
            if (dependency instanceof AbstractModuleDependency dep) {
                for (var repository : project.getRepositories()) {
                    if (repository instanceof UrlArtifactRepository urlRepo) {
                        var url = urlRepo.getUrl();
                        retrieveDependencyInfo(dependency, url, dep);
                        retrieveArtifactsInfo(dependency, url, dep);
                    }
                }
            }
            if (dependency instanceof ProjectDependency dep) {
                Task jarTask = dep.getDependencyProject().getTasks().getByName("jar");
                if (!(jarTask instanceof Jar jar)) {
                    throw new IllegalArgumentException("Gradle task 'jar' isn't an instance of org.gradle.jvm.tasks.Jar");
                }

                String name = jar.getArchiveBaseName().get();
                String group = project.getGroup().toString();
                String version = project.getVersion().toString();

                Object nullableUrl = project.property("bubbles.project_dependency_url");
                if (nullableUrl == null) continue;
                String url = nullableUrl.toString();

                var jarUrl = String.format("%s%s/%s/%s/%s-%s-%s.%s",
                        url, group.replaceAll("\\.", "/"),
                        name, version,
                        name, version,
                        jar.getArchiveClassifier().get(), jar.getArchiveExtension());

                try {
                    var jarFile = new URL(jarUrl);
                    try (var in = jarFile.openStream()) {
                        if (in != null) {
                            getLogger().info(String.format("%s:%s:%s:%s:%s",
                                    dependency.getGroup(), dependency.getName(), dependency.getVersion(),
                                    jar.getArchiveClassifier(), jar.getArchiveExtension()));
                            this.urls.add(jarFile.toString());
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void retrieveArtifactsInfo(Dependency dependency, URI url, AbstractModuleDependency dep) {
        for (DependencyArtifact artifact : dep.getArtifacts()) {
            if (dependency.getGroup() != null) {
                var jarUrl = String.format("%s%s/%s/%s/%s-%s-%s.%s",
                        url, dependency.getGroup().replaceAll("\\.", "/"),
                        dependency.getName(), dependency.getVersion(),
                        dependency.getName(), dependency.getVersion(),
                        artifact.getClassifier(), artifact.getExtension());

                try {
                    var jarFile = new URL(jarUrl);
                    try (var in = jarFile.openStream()) {
                        if (in != null) {
                            getLogger().info(String.format("%s:%s:%s:%s:%s",
                                    dependency.getGroup(), dependency.getName(), dependency.getVersion(),
                                    artifact.getClassifier(), artifact.getExtension()));
                            this.urls.add(jarFile.toString());
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void retrieveDependencyInfo(Dependency dependency, URI url, AbstractModuleDependency moduleDependency) {
        var artifacts = moduleDependency.getArtifacts();
        if (artifacts.isEmpty()) {
            if (dependency.getGroup() != null) {
                var jarUrl = String.format("%s%s/%s/%s/%s-%s.jar",
                        url, dependency.getGroup().replaceAll("\\.", "/"),
                        dependency.getName(), dependency.getVersion(),
                        dependency.getName(), dependency.getVersion());

                try {
                    var jarFile = new URL(jarUrl);
                    try (var in = jarFile.openStream()) {
                        if (in != null) {
                            getLogger().info(String.format("%s:%s:%s", dependency.getGroup(), dependency.getName(), dependency.getVersion()));
                            this.urls.add(jarFile.toString());
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    @Internal
    public JsonArray getUrls() {
        return urls;
    }
}
