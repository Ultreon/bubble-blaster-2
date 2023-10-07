package com.ultreon.commons.crash;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
@ParametersAreNonnullByDefault
public final class CrashLog extends CrashCategory {
    private final List<CrashCategory> categories = new ArrayList<>();

    public CrashLog(String details, CrashLog report) {
        super(details);

        this.throwable = this.addCrashLog(report).throwable;
    }

    public CrashLog(String details, @Nullable CrashLog report, Throwable t) {
        this(details, t);

        if (report != null) this.addCrashLog(report);
    }

    public CrashLog(String details, Throwable t) {
        super(details, t);
    }

    private CrashLog addCrashLog(CrashLog log) {
        var cat = new CrashCategory(log.getDetails(), log.getThrowable());
        cat.entries.clear();
        cat.entries.addAll(log.entries);
        this.addCategory(cat);

        for (var category : log.getCategories()) {
            this.addCategory(category);
        }

        return log;
    }

    private CrashLog addCrash(ApplicationCrash exception) {
        var crashLog = exception.getCrashLog();
        var crashLog1 = new CrashLog(crashLog.details, crashLog.throwable);
        crashLog1.categories.addAll(crashLog.categories.subList(0, crashLog.categories.size() - 1));
        crashLog1.entries.addAll(crashLog.entries);
        return this.addCrashLog(crashLog1);
    }

    @Override
    @NotNull
    public Throwable getThrowable() {
        return this.throwable;
    }

    public void addCategory(CrashCategory crashCategory) {
        this.categories.add(crashCategory);
    }

    public List<CrashCategory> getCategories() {
        return Collections.unmodifiableList(this.categories);
    }

    private CrashLog getFinalForm() {
        var crashLog = new CrashLog(this.details, this.throwable);
        crashLog.categories.addAll(this.categories);
        crashLog.entries.addAll(this.entries);

        var runtime = Runtime.getRuntime();

        var category = new CrashCategory("System Details");
        category.add("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        category.add("Memory", runtime.totalMemory() - runtime.freeMemory() + "/" + runtime.totalMemory());

        crashLog.addCategory(category);
        return crashLog;
    }

    public ApplicationCrash createCrash() {
        return new ApplicationCrash(this.getFinalForm());
    }

    @Override
    public String toString() {
        var s1 = "// " + this.details + "\r\n";
        var cs = new StringBuilder();
        var sb = new StringBuilder();

        var runtime = Runtime.getRuntime();

        if (!this.entries.isEmpty()) {
            sb.append("Details:").append(System.lineSeparator());
            for (var entry : this.entries) {
                sb.append("  ").append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append("\r\n");
            }
        }

        for (var category : this.categories) {
            cs.append(System.lineSeparator()).append("=------------------------------------------------------------------=");
            cs.append(System.lineSeparator()).append(category.toString());
        }

        cs.append("=------------------------------------------------------------------=");

        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        this.throwable.printStackTrace(pw);

        return ">>> C R A S H   R E P O R T <<<\r\n" + s1 + "\r\n" + sw + cs + "\r\n" + sb;
    }

    public String getDefaultFileName() {
        var now = LocalDateTime.now();
        return "Crash [" + now.format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH.mm.ss")) + "].txt";
    }

    public void defaultSave() {
        var file = new File("game-crashes");
        if (!file.exists()) {
            try {
                Files.createDirectories(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.writeToFile(new File(file, this.getDefaultFileName()));
    }

    public void writeToFile(File file) {
        try (var fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(this.toString().getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
