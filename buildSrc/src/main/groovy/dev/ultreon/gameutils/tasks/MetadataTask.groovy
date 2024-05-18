package dev.ultreon.gameutils.tasks

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import dev.ultreon.gameutils.GameUtilsExt
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

import javax.inject.Inject
import java.time.ZoneOffset

@DisableCachingByDefault
class MetadataTask extends DefaultTask {
    @OutputFile
    File getMetadataFile() {
        return project.file("$project.rootProject.projectDir/build/appmeta.json")
    }

    @Inject
    MetadataTask() {
        this.metadataFile.delete()

        this.group = "gameutils"
        this.didWork = true
        this.enabled = true
    }

    @TaskAction
    void createJson() {
        def gameUtils = project.rootProject.extensions.getByType(GameUtilsExt)

        def gson = new GsonBuilder().create()
        def writer = new JsonWriter(new FileWriter(metadataFile))
        writer.indent = "  "

        def json = new JsonObject()
        json.addProperty "javaVersion", gameUtils.javaVersion
        json.addProperty "buildDate", gameUtils.buildDate.atOffset(ZoneOffset.UTC).toEpochSecond()
        json.addProperty "version", project.rootProject.version.toString()
        gson.toJson json, writer
        writer.flush()
        writer.close()
    }
}
