package si.dlabs.gradle.task

import com.android.SdkConstants
import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.NamedDomainObjectContainer
import si.dlabs.gradle.commons.Utils
import si.dlabs.gradle.extensions.CheckstyleExtension
import org.gradle.api.tasks.TaskAction

/**
 * Created by blazsolar on 07/04/15.
 */
class Checkstyle extends org.gradle.api.plugins.quality.Checkstyle {

    @Override @TaskAction
    void run() {

        configFile project.configurations.checkstyleRules.singleFile

        super.run()
    }

    void setup(CheckstyleExtension extension) {

        description "Checkstyle for debug source"
        group "Check"

        ignoreFailures extension.ignoreFailures
        showViolations extension.showViolations

        def sets;
        if (Utils.is140orAbove()) {
            sets = project.android.sourceSets;
        } else {
            sets = project.android.sourceSetsContainer;
        }

        sets.all { AndroidSourceSet sourceSet ->
            if (!sourceSet.name.startsWith("test") && !sourceSet.name.startsWith(SdkConstants.FD_TEST)) {
                source sourceSet.java.srcDirs
            }
        }

        include '**/*.java'
        exclude '**/gen/**'

        classpath = project.files()

    }
}
