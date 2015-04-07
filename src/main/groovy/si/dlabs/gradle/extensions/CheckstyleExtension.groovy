package si.dlabs.gradle.extensions

/**
 * Created by blazsolar on 02/09/14.
 */
class CheckstyleExtension extends PluginBaseExtension {

    String reportFile = "reports/checkstyle/checkstyle.xml"
    boolean ignoreFailures = false
    boolean showViolations = false

}
