package net.covers1624.jarsign.jar;

/**
 * Created by covers1624 on 19/8/20.
 */
public interface JarSignSpec {

    String getAlias();

    String getStorePass();

    String getKeyStore();

    String getStoreType();

    String getKeyPass();

    String getSigFile();

    Boolean getInternalSF();

    Boolean getSectionsOnly();

    Boolean getPreserveLastModified();

    String getTsaURL();

    String getTsaCert();

    void setAlias(Object alias);

    void setStorePass(Object storePass);

    void setKeyStore(Object keyStore);

    void setStoreType(Object storeType);

    void setKeyPass(Object keyPass);

    void setSigFile(Object sigFile);

    void setInternalSF(Boolean internalSF);

    void setSectionsOnly(Boolean sectionsOnly);

    void setPreserveLastModified(Boolean preserveLastModified);

    void setTsaURL(Object tsaURL);

    void setTsaCert(Object tsaCert);

    //@formatter:off
    default void alias(Object alias) { setAlias(alias); }
    default void storePass(Object storePass) { setStorePass(storePass); }
    default void keyStore(Object keyStore) { setKeyStore(keyStore); }
    default void storeType(Object storeType) { setStoreType(storeType); }
    default void keyPass(Object keyPass) { setKeyPass(keyPass); }
    default void sigFile(Object sigFile) { setSigFile(sigFile); }
    default void internalSF(Boolean internalSF) { setInternalSF(internalSF); }
    default void sectionsOnly(Boolean sectionsOnly) { setSectionsOnly(sectionsOnly); }
    default void preserveLastModified(Boolean preserveLastModified) { setPreserveLastModified(preserveLastModified); }
    default void tsaURL(Object tsaURL) { setTsaURL(tsaURL); }
    default void tsaCert(Object tsaCert) { setTsaCert(tsaCert); }
    //@formatter:on

    default void copyFrom(JarSignSpec spec) {
        if (spec.getAlias() != null) {
            setAlias(spec.getAlias());
        }
        if (spec.getStorePass() != null) {
            setStorePass(spec.getStorePass());
        }
        if (spec.getKeyStore() != null) {
            setKeyStore(spec.getKeyStore());
        }
        if (spec.getStoreType() != null) {
            setStoreType(spec.getStoreType());
        }
        if (spec.getKeyPass() != null) {
            setKeyPass(spec.getKeyPass());
        }
        if (spec.getSigFile() != null) {
            setSigFile(spec.getSigFile());
        }
        if (spec.getInternalSF() != null) {
            setInternalSF(spec.getInternalSF());
        }
        if (spec.getSectionsOnly() != null) {
            setSectionsOnly(spec.getSectionsOnly());
        }
        if (spec.getPreserveLastModified() != null) {
            setPreserveLastModified(spec.getPreserveLastModified());
        }
        if (spec.getTsaURL() != null) {
            setTsaURL(spec.getTsaURL());
        }
        if (spec.getTsaCert() != null) {
            setTsaCert(spec.getTsaCert());
        }
    }
}
