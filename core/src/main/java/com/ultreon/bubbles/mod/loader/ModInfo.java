package com.ultreon.bubbles.mod.loader;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("unused")
public class ModInfo {
    @NotNull
    @SerializedName("mod-id")
    private final String modId;

    @NotNull
    private final String name;

    @NotNull
    private final String[] author;

    @NotNull
    private final String license;

    @SerializedName("repository")
    private String repo = null;

    private URL website = null;

    @SerializedName("issue-tracker")
    private URL issueTracker = null;

    @SerializedName("contributors")
    private String[] contributors = null;

    private String credits = "";

    @NotNull
    @SerializedName("entry-points")
    private final Map<String, String> entryPoints;

    ModInfo(@NotNull String modId, @NotNull String name, @NotNull String[] author, @NotNull String license, @NotNull Map<String, String> entryPoints) {
        this.modId = modId;
        this.name = name;
        this.author = author;
        this.license = license;
        this.entryPoints = entryPoints;
    }

    public ModInfo(@NotNull String modId, @NotNull String name, @NotNull String[] author, @NotNull String license, String repo, URL website, URL issueTracker, String[] contributors, String credits, @NotNull Map<String, String> entryPoints) {
        this.modId = modId;
        this.name = name;
        this.author = author;
        this.license = license;
        this.repo = repo;
        this.website = website;
        this.issueTracker = issueTracker;
        this.contributors = contributors;
        this.credits = credits;
        this.entryPoints = entryPoints;
    }

    @NotNull
    public String getModId() {
        return modId;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String[] getAuthor() {
        return author;
    }

    @NotNull
    public String getLicense() {
        return license;
    }

    public String getRepo() {
        return repo;
    }

    public URL getWebsite() {
        return website;
    }

    public URL getIssueTracker() {
        return issueTracker;
    }

    public String[] getContributors() {
        return contributors;
    }

    public String getCredits() {
        return credits;
    }

    @NotNull
    public Map<String, String> getEntryPoints() {
        return Collections.unmodifiableMap(entryPoints);
    }
}
