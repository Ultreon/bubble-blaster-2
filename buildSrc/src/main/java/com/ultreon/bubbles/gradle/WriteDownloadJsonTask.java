package com.ultreon.bubbles.gradle;

import org.gradle.api.Project;
import org.gradle.api.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class WriteDownloadJsonTask extends BaseTask {
    public WriteDownloadJsonTask() {
        super("writeDownloadJson", "bubbles");

        List<String> collect = getProject().getSubprojects().stream().map(Project::getName).toList();
        this.dependsOn("retrieveUrls");
        for (String project : collect) {
            this.dependsOn(project + ":retrieveUrls");
        }
        this.doFirst(this::execute);
    }

    private void execute(Task task) {
        Task t = getProject().getTasks().getByName("retrieveUrls");
        if (!(t instanceof RetrieveUrlsTask retrieveUrls)) {
            throw new RuntimeException("The task 'retrieveUrls' is not the internal bubble blaster task for retrieving urls.");
        }

        JsonArray urls = retrieveUrls.getUrls();
        Gson gson = new Gson();
        StringWriter sw = new StringWriter();
        JsonWriter jw = new JsonWriter(sw);
        jw.setIndent("  ");
        gson.toJson(urls, jw);
        Project rootProject = getProject().getRootProject();
        try {
            Files.write(Paths.get(rootProject.getProjectDir().getPath() + "/build/libraries.json"), sw.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
