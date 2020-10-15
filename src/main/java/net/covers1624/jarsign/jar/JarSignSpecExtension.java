package net.covers1624.jarsign.jar;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Jar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.covers1624.jarsign.Utils.resolveBoolean;
import static net.covers1624.jarsign.Utils.resolveString;

/**
 * Created by covers1624 on 19/8/20.
 */
public class JarSignSpecExtension implements JarSignSpec {

    private final Project project;
    private Object alias;
    private Object storePass;
    private Object keyStore;
    private Object storeType;
    private Object keyPass;
    private Object sigFile;
    private Object internalSF;
    private Object sectionsOnly;
    private Object preserveLastModified;
    private Object tsaURL;
    private Object tsaCert;

    private final List<Jar> tasks = new ArrayList<>();
    private final List<Object> afterTasks = new ArrayList<>();

    public JarSignSpecExtension(Project project) {
        this.project = project;
    }

    public void sign(Jar task) {
        tasks.add(task);
    }

    public void after(Object task) {
        afterTasks.add(task);
    }

    public void sign(Collection<Jar> tasks) {
        this.tasks.addAll(tasks);
    }

    public List<Jar> getTasks() {
        return tasks;
    }

    public List<Object> getAfterTasks() {
        return afterTasks;
    }

    //@formatter:off
    @Override public String getAlias() { return resolveString(alias); }
    @Override public String getStorePass() { return resolveString(storePass); }
    @Override public String getKeyStore() { return resolveString(keyStore); }
    @Override public String getStoreType() { return resolveString(storeType); }
    @Override public String getKeyPass() { return resolveString(keyPass); }
    @Override public String getSigFile() { return resolveString(sigFile); }
    @Override public Boolean getInternalSF() { return resolveBoolean(internalSF); }
    @Override public Boolean getSectionsOnly() { return resolveBoolean(sectionsOnly); }
    @Override public Boolean getPreserveLastModified() { return resolveBoolean(preserveLastModified); }
    @Override public String getTsaURL() { return resolveString(tsaURL); }
    @Override public String getTsaCert() { return resolveString(tsaCert); }

    @Override public void setAlias(Object alias) { this.alias = alias; }
    @Override public void setStorePass(Object storePass) { this.storePass = storePass; }
    @Override public void setKeyStore(Object keyStore) { this.keyStore = keyStore; }
    @Override public void setStoreType(Object storeType) { this.storeType = storeType; }
    @Override public void setKeyPass(Object keyPass) { this.keyPass = keyPass; }
    @Override public void setSigFile(Object sigFile) { this.sigFile = sigFile; }
    @Override public void setInternalSF(Boolean internalSF) { this.internalSF = internalSF; }
    @Override public void setSectionsOnly(Boolean sectionsOnly) { this.sectionsOnly = sectionsOnly; }
    @Override public void setPreserveLastModified(Boolean preserveLastModified) { this.preserveLastModified = preserveLastModified; }
    @Override public void setTsaURL(Object tsaURL) { this.tsaURL = tsaURL; }
    @Override public void setTsaCert(Object tsaCert) { this.tsaCert = tsaCert; }
    //@formatter:on
}
