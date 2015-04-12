package com.github.blazsolar.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class CheckstyleExtension extends PluginBaseExtension {

    @Input String reportFile = "reports/checkstyle/checkstyle.xml"
    @Input boolean ignoreFailures = false
    @Input boolean showViolations = false

}
