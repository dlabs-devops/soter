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
            uploadReports true
        }
    
        findbugs {
            enabled true
            uploadReports true
            reportLevel "low"
        }
        
        pmd {
            enabled true
            uploadReports true
        }
        
        logs {
            uploadReports true
        }
        
        tests {
            uploadReports true
        }
        
        publish {
            enabled true
        
            amazon {
                upload true
                variant "release"
            }
        }
        
        notifications {
    
            enabled true
        
            hipchat {
                enabled true
                token "<hipchat_token>"
                roomId "<hipchat_room_id>"
                userId "<hipchat_user_id>"
            }
        
        }
        
        amazon {
            enabled true
            accessKey "<amazon_access_key>"
            secretKey "<amazon_secret_key>"
            bucket "<bucket>"
            path "path/in/amazon/bucket"
        }
        
        remote {
            pushToRemote true
            remote "remote"
            branch "master"
        }
        
    }