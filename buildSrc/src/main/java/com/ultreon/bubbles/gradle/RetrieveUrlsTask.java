package com.ultreon.bubbles.gradle;

import com.google.gson.JsonArray;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.UrlArtifactRepository;
import org.gradle.api.internal.artifacts.dependencies.AbstractModuleDependency;
import org.gradle.api.tasks.Internal;
import org.gradle.jvm.tasks.Jar;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Retrieve URLs for the dependencies that will be downloaded by the game.
*/
public class RetrieveUrlsTask extends BaseTask {
    private final JsonArray urls = new JsonArray();
    private final List<com.ultreon.bubbles.gradle.Dependency> gameDeps = new ArrayList<>();

    public RetrieveUrlsTask() {
        super("retrieveUrls", "bubbles");

        this.doFirst(this::execute);
    }

    private void execute(Task task) {
        Project project = task.getProject();
        DependencySet dependencies = project.getConfigurations().getByName("api").getDependencies();
        for (Dependency dependency : dependencies) {
            if (dependency instanceof AbstractModuleDependency) {
                AbstractModuleDependency dep = (AbstractModuleDependency) dependency;
                for (ArtifactRepository repository : project.getRepositories()) {
                    if (repository instanceof UrlArtifactRepository) {
                        UrlArtifactRepository urlRepo = (UrlArtifactRepository) repository;
                        URI url = urlRepo.getUrl();
                        this.retrieveDependencyInfo(dependency, url, dep);
                        this.retrieveArtifactsInfo(dependency, url, dep);
                    }
                }
            }
            if (dependency instanceof ProjectDependency) {
                ProjectDependency dep = (ProjectDependency) dependency;
                Task jarTask = dep.getDependencyProject().getTasks().getByName("jar");
                if (!(jarTask instanceof Jar)) {
                    throw new IllegalArgumentException("Gradle task 'jar' isn't an instance of org.gradle.jvm.tasks.Jar");
                }
                Jar jar = (Jar) jarTask;

                String name = jar.getArchiveBaseName().get();
                String group = project.getGroup().toString();
                String version = project.getVersion().toString();

                Object nullableUrl = project.property("bubbles.project_dependency_url");
                if (nullableUrl == null) continue;
                String url = nullableUrl.toString();

                String jarUrl = String.format("%s%s/%s/%s/%s-%s-%s.%s",
                        url, group.replaceAll("\\.", "/"),
                        name, version,
                        name, version,
                        jar.getArchiveClassifier().get(), jar.getArchiveExtension());

                Platform platform = Platform.get(dep.getTargetConfiguration());
                if (platform == null) {
                    continue;
                }
                try {
                    URL jarFile = new URL(jarUrl);
                    try (InputStream in = jarFile.openStream()) {
                        if (in != null) {
                            this.getLogger().info(String.format("%s:%s:%s:%s:%s",
                                    dependency.getGroup(), dependency.getName(), dependency.getVersion(),
                                    jar.getArchiveClassifier(), jar.getArchiveExtension()));
                            this.urls.add(jarFile.toString());
                            this.gameDeps.add(new com.ultreon.bubbles.gradle.Dependency(
                                    Objects.requireNonNull(dependency.getGroup(), "Dependency should have group."),
                                    dependency.getName(),
                                    Objects.requireNonNull(dependency.getVersion(), "Dependency should have version."),
                                    platform,
                                    jar.getArchiveClassifier().get(),
                                    jar.getArchiveExtension().get(),
                                    url));
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void retrieveArtifactsInfo(Dependency dependency, URI url, AbstractModuleDependency dep) {
        for (DependencyArtifact artifact : dep.getArtifacts()) {
            Platform platform = Platform.get(dep.getTargetConfiguration());
            if (platform == null) {
                return;
            }
            if (dependency.getGroup() != null) {
                String jarUrl = String.format("%s%s/%s/%s/%s-%s-%s.%s",
                        url, dependency.getGroup().replaceAll("\\.", "/"),
                        dependency.getName(), dependency.getVersion(),
                        dependency.getName(), dependency.getVersion(),
                        artifact.getClassifier(), artifact.getExtension());

                try {
                    URL jarFile = new URL(jarUrl);
                    try (InputStream in = jarFile.openStream()) {
                        if (in != null) {
                            this.getLogger().info(String.format("%s:%s:%s:%s:%s",
                                    dependency.getGroup(), dependency.getName(), dependency.getVersion(),
                                    artifact.getClassifier(), artifact.getExtension()));
                            this.urls.add(jarFile.toString());
                            this.gameDeps.add(new com.ultreon.bubbles.gradle.Dependency(
                                    Objects.requireNonNull(dependency.getGroup(), "Dependency should have group."),
                                    dependency.getName(),
                                    Objects.requireNonNull(dependency.getVersion(), "Dependency should have version."),
                                    platform,
                                    artifact.getClassifier(),
                                    artifact.getExtension(),
                                    url.toString()));
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void retrieveDependencyInfo(Dependency dependency, URI url, AbstractModuleDependency dep) {
        Set<DependencyArtifact> artifacts = dep.getArtifacts();
        Platform platform = Platform.get(dep.getTargetConfiguration());
        if (platform == null) {
            return;
        }
        if (artifacts.isEmpty()) {
            if (dependency.getGroup() != null) {
                String jarUrl = String.format("%s%s/%s/%s/%s-%s.jar",
                        url, dependency.getGroup().replaceAll("\\.", "/"),
                        dependency.getName(), dependency.getVersion(),
                        dependency.getName(), dependency.getVersion());

                try {
                    URL jarFile = new URL(jarUrl);
                    try (InputStream in = jarFile.openStream()) {
                        if (in != null) {
                            this.getLogger().info(String.format("%s:%s:%s", dependency.getGroup(), dependency.getName(), dependency.getVersion()));
                            this.urls.add(jarFile.toString());
                            this.gameDeps.add(new com.ultreon.bubbles.gradle.Dependency(
                                    Objects.requireNonNull(dependency.getGroup(), "Dependency should have group."),
                                    dependency.getName(),
                                    Objects.requireNonNull(dependency.getVersion(), "Dependency should have version."),
                                    platform,
                                    url.toString()));
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    @Internal
    public List<com.ultreon.bubbles.gradle.Dependency> getGameDeps() {
        return this.gameDeps;
    }

    @Internal
    public JsonArray getUrls() {
        return this.urls;
    }
}
