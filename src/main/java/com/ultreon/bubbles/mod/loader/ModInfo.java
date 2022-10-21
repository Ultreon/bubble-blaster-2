package com.ultreon.bubbles.mod.loader;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("unused")
public class ModInfo {
    @NonNull
    @SerializedName("mod-id")
    private final String modId;

    @NonNull
    private final String name;

    @NonNull
    private final String[] author;

    @NonNull
    private final String license;

    @SerializedName("repository")
    private String repo = null;

    private URL website = null;

    @SerializedName("issue-tracker")
    private URL issueTracker = null;

    @SerializedName("contributors")
    private String[] contributors = null;

    private String credits = "";

    @NonNull
    @SerializedName("entry-points")
    private final Map<String, String> entryPoints;

    ModInfo(@NonNull String modId, @NonNull String name, @NonNull String[] author, @NonNull String license, @NonNull Map<String, String> entryPoints) {
        this.modId = modId;
        this.name = name;
        this.author = author;
        this.license = license;
        this.entryPoints = entryPoints;
    }

    public ModInfo(@NonNull String modId, @NonNull String name, @NonNull String[] author, @NonNull String license, String repo, URL website, URL issueTracker, String[] contributors, String credits, @NonNull Map<String, String> entryPoints) {
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

    @NonNull
    public String getModId() {
        return modId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String[] getAuthor() {
        return author;
    }

    @NonNull
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

    @NonNull
    public Map<String, String> getEntryPoints() {
        return Collections.unmodifiableMap(entryPoints);
    }
}
