package com.github.blazsolar.gradle.task

import com.github.blazsolar.gradle.extensions.FindbugsExtension
import org.gradle.api.tasks.TaskAction
/**
 * Created by blazsolar on 07/04/15.
 */
class FindBugs extends org.gradle.api.plugins.quality.FindBugs {

    @Override @TaskAction
    void run() {

        excludeFilter = project.configurations.findbugsRules.singleFile

        super.run()
    }

    void setup(FindbugsExtension extension) {

        description "Findbugs for debug source"
        group "Check"

        ignoreFailures false
        effort extension.effort
        reportLevel extension.reportLevel
        classes = project.files("$project.buildDir/intermediates/classes")

        source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
        include '**/*.java'
        exclude '**/gen/**'

        classpath = project.files()
        dependsOn "compileDebugJava"

    }
}
