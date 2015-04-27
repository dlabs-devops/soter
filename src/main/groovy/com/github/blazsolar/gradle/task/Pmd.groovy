package com.github.blazsolar.gradle.task

import com.android.SdkConstants
import com.android.build.gradle.api.AndroidSourceSet
import com.github.blazsolar.gradle.extensions.PMDExtension
import org.gradle.api.tasks.TaskAction

/**
 * Created by blazsolar on 07/04/15.
 */
class Pmd extends org.gradle.api.plugins.quality.Pmd {

    @Override @TaskAction
    void run() {

        ruleSetFiles = project.files(project.configurations.pmdRules.files)

        super.run()
    }

    void setup(PMDExtension extension) {

        description "PMD for debug source"
        group "Check"

        ignoreFailures = false
        ruleSets = ["java-basic", "java-braces", "java-strings", "java-android"]

        project.android.sourceSetsContainer.all { AndroidSourceSet sourceSet ->
            if (!sourceSet.name.startsWith("test") && !sourceSet.name.startsWith(SdkConstants.FD_TEST)) {
                source sourceSet.java.srcDirs
            }
        }

        include '**/*.java'
        exclude '**/gen/**'

    }

}
