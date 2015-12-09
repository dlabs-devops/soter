package si.dlabs.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class CheckstyleExtension extends PluginBaseExtension {

    @Input boolean ignoreFailures = false
    @Input boolean showViolations = false

    String toolVersion = "6.7";

}
