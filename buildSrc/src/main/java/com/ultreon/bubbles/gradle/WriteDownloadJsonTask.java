package com.ultreon.bubbles.gradle;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class WriteDownloadJsonTask extends BaseTask {
    public WriteDownloadJsonTask() {
        super("writeDownloadJson", "bubbles");

        List<String> collect = this.getProject().getSubprojects().stream().map(Project::getName).collect(Collectors.toList());
        this.dependsOn("retrieveUrls");
        for (String project : collect) {
            this.dependsOn(project + ":retrieveUrls");
        }
        this.doFirst(this::execute);
    }

    private void execute(Task task) {
        Task t = this.getProject().getTasks().getByName("retrieveUrls");
        Task t1 = this.getProject().getTasks().getByName("bubbles");
        if (!(t instanceof RetrieveUrlsTask retrieveUrls)) {
            throw new RuntimeException("The task 'retrieveUrls' is not the internal bubble blaster task for retrieving urls.");
        }

        if (!(t1 instanceof BubblesTask bubbles)) {
            throw new RuntimeException("The task 'bubbles' is not the internal bubble blaster task for retrieving urls.");
        }

        Gson gson = new Gson();
        StringWriter sw = new StringWriter();
        JsonWriter jw = new JsonWriter(sw);
        jw.setIndent("  ");
        gson.toJson(new BuildVersion(this.getProject().getVersion().toString(), null, Date.from(Instant.now()), bubbles.getVersionType(), retrieveUrls.getGameDeps()), BuildVersion.class, jw);
        Project rootProject = this.getProject().getRootProject();
        try {
            Files.write(Paths.get(rootProject.getProjectDir().getPath() + "/build/libs/libraries.json"), sw.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
