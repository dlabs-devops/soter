# AndroidCheckPlugin

Gradle plugin that adds support for Findbugs, Checkstyle and PMD to android projects.

## Usage

### Apply plugin

    buildscript {
        repositories {
            maven { url "http://repo.dlabs.si:8081/artifactory/simple/plugins-release-local/" }
        }
    
        dependencies {
            classpath 'si.dlabs.gradle:android-check-plugin:<version>'
        }
    }
    
    apply plugin: 'si.dlabs.dlabs-check'
    
### Adding plugin rules

    dependencies {
        rulesCheckstyle "<checkstyle_rules>"
        rulesFindbugs "<findbugs_rules>"
        rulesPMD "<pmd_rules>"
    }
    
### Configuration

    check {
        checkstyle {
            enabled true
        }
        findbugs {
            enabled true
        }
        pmd {
            enabled true
        }
    }