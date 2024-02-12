package com.ultreon.gameutils.tasks

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import com.ultreon.gameutils.GameUtilsExt
import com.ultreon.gameutils.GameUtilsPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

import javax.inject.Inject
import java.time.ZoneOffset

@DisableCachingByDefault
class MetadataTask extends DefaultTask {
    public GameUtilsExt gameUtils

    @OutputFile
    def metadataFile = project.file("$project.projectDir/build/metadata.json")

    @Inject
    MetadataTask() {
        this.gameUtils = GameUtilsPlugin.extension
        this.group = "gameutils"
        this.didWork = true
        this.enabled = true
    }

    @TaskAction
    void createJson() {
        def gson = new GsonBuilder().create()
        def writer = new JsonWriter(new FileWriter(metadataFile))
        writer.indent = "  "

        def json = new JsonObject()
        json.addProperty "javaVersion", gameUtils.javaVersion
        json.addProperty "buildDate", gameUtils.buildDate.atOffset(ZoneOffset.UTC).toEpochSecond()
        json.addProperty "version", gameUtils.projectVersion
        gson.toJson json, writer
        writer.flush()
        writer.close()
    }
}
