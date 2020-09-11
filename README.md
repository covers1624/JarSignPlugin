# JarSignPlugin
Simple gradle plugin for Jar signing.

### Usage:
```groovy
signing {
    //Can have multiple blocks, but tasks must not be duplicated across them
    jars {
        //Must be given Jar tasks.
        task jar
        //Or
        tasks [jar, srcJar, someOtherJar]        
        
        //The following are required as per Ant's sign task
        alias = 'someAlias'
        storePass = 'password'

        //The following are optional as per Ant's sign task
        keyStore = 'path/to/some/file/'
        storeType = 'jks'
        keyPass = 'password'
        sigFile = 'SomeName'
        internalSF = false
        sectionsOnly = false
        preserveLastModified = false
        tsaURL = 'http:signing.someplace.com/'
        tsaCert = 'tsaAlias'
    }
}
```
More documentation can be found on each of the individual parameters [here](https://ant.apache.org/manual/Tasks/signjar.html).

### Including or excluding specific files from signing
Standard include/exclude rules apply.
```groovy
//sign + Task name with upper first character, 'signSrcJar', 'signSomeOtherJar'
signJar {
    include '**/*.class'
    exclude '**/com/apache/**.class'
}
```
