package net.covers1624.jarsign.jar;

import groovy.lang.Closure;
import net.covers1624.jarsign.Utils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static net.covers1624.jarsign.Utils.*;

/**
 * Inspired off ForgeGradle's implementation,
 * done a bit smarter and cleaner though.
 * <p>
 * Created by covers1624 on 19/8/20.
 */
public class SignJarTask extends DefaultTask implements PatternFilterable, JarSignSpec {

    private final PatternSet patternSet = new PatternSet();

    private Object input;
    private Object output;
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

    @TaskAction
    public void doTask() throws Throwable {
        File unsigned = new File(getTemporaryDir(), "unsigned.jar");
        File signed = new File(getTemporaryDir(), "signed.jar");

        Map<ZipEntry, byte[]> extras = new HashMap<>();

        Spec<FileTreeElement> spec = patternSet.getAsSpec();

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(unsigned))) {
            getProject().zipTree(getInput()).visit(Utils.sneakA(file -> {
                if (file.isDirectory()) {
                    out.putNextEntry(new ZipEntry(StringUtils.appendIfMissing(file.getPath(), "/")));
                } else {
                    ZipEntry entry = new ZipEntry(file.getPath());
                    entry.setTime(file.getLastModified());
                    if (spec.isSatisfiedBy(file)) {
                        out.putNextEntry(entry);
                        file.copyTo(out);
                    } else {
                        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                            file.copyTo(os);
                            extras.put((ZipEntry) entry.clone(), os.toByteArray());
                        }
                    }
                }
            }));
        }

        Map<String, Object> args = new HashMap<>();
        args.put("jar", unsigned);
        args.put("signedJar", signed);
        args.put("alias", getAlias());
        args.put("storePass", getStorePass());
        args.put("keystore", getKeyStore());
        args.put("storetype", getStoreType());
        args.put("keypass", getKeyPass());
        args.put("sigfile", getSigFile());
        args.put("internalsf", getInternalSF());
        args.put("sectiononly", getSectionsOnly());
        args.put("preservelastmodified", getPreserveLastModified());
        args.put("tsaurl", getTsaURL());
        args.put("tsacert", getTsaCert());
        args.values().removeIf(Objects::isNull);
        getProject().getAnt().invokeMethod("signjar", args);

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getOutput()))) {
            try (ZipFile zip = new ZipFile(signed)) {
                zip.stream().forEach(sneakC(entry -> {
                    out.putNextEntry((ZipEntry) entry.clone());
                    if (!entry.isDirectory()) {
                        copy(zip.getInputStream(entry), out);
                    }
                }));
            }
            for (Map.Entry<ZipEntry, byte[]> entry : extras.entrySet()) {
                out.putNextEntry(entry.getKey());
                out.write(entry.getValue());
            }
        }
    }

    //@formatter:off
    @InputFile                      public File getInput() { return resolveFile(getProject(), input); }
    @OutputFile                     public File getOutput() { return resolveFile(getProject(), output); }
    @Input                @Override public String getAlias() { return resolveString(alias); }
    @Input                @Override public String getStorePass() { return resolveString(storePass); }
    @Input      @Optional @Override public String getKeyStore() { return resolveString(keyStore); }
    @Input      @Optional @Override public String getStoreType() { return resolveString(storeType); }
    @Input      @Optional @Override public String getKeyPass() { return resolveString(keyPass); }
    @Input      @Optional @Override public String getSigFile() { return resolveString(sigFile); }
    @Input      @Optional @Override public Boolean getInternalSF() { return resolveBoolean(internalSF); }
    @Input      @Optional @Override public Boolean getSectionsOnly() { return resolveBoolean(sectionsOnly); }
    @Input      @Optional @Override public Boolean getPreserveLastModified() { return resolveBoolean(preserveLastModified); }
    @Input      @Optional @Override public String getTsaURL() { return resolveString(tsaURL); }
    @Input      @Optional @Override public String getTsaCert() { return resolveString(tsaCert); }

    public void setInput(Object input) { this.input = input; }
    public void setOutput(Object output) { this.output = output; }
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

    @Input @Override public Set<String> getIncludes() { return patternSet.getIncludes(); }
    @Input @Override public Set<String> getExcludes() { return patternSet.getExcludes(); }
    @Override public SignJarTask setIncludes(Iterable<String> includes) { patternSet.setIncludes(includes); return this; }
    @Override public SignJarTask setExcludes(Iterable<String> excludes) { patternSet.setExcludes(excludes); return this; }
    @Override public SignJarTask include(String... includes) { patternSet.include(includes); return this; }
    @Override public SignJarTask include(Iterable<String> includes) { patternSet.include(includes); return this; }
    @Override public SignJarTask include(Spec<FileTreeElement> includeSpec) { patternSet.include(includeSpec); return this; }
    @Override public SignJarTask include(Closure includeSpec) { patternSet.include(includeSpec); return this; }
    @Override public SignJarTask exclude(String... excludes) { patternSet.exclude(excludes); return this; }
    @Override public SignJarTask exclude(Iterable<String> excludes) { patternSet.exclude(excludes); return this; }
    @Override public SignJarTask exclude(Spec<FileTreeElement> excludeSpec) { patternSet.exclude(excludeSpec); return this; }
    @Override public SignJarTask exclude(Closure excludeSpec) { patternSet.exclude(excludeSpec); return this; }
    //@formatter:on
}
