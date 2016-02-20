package si.dlabs.gradle.task

import si.dlabs.gradle.extensions.FindbugsExtension
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

        ignoreFailures extension.ignoreFailures
        effort extension.effort
        reportLevel extension.reportLevel
        classes = project.files("$project.buildDir/intermediates/classes")

        include '**/*.java'
        exclude '**/gen/**'

        classpath = project.files()

    }
}
