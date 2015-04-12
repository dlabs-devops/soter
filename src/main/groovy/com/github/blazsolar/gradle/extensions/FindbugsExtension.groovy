package com.github.blazsolar.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class FindbugsExtension extends PluginBaseExtension {

    @Input String reportFile = "reports/checkstyle/checkstyle.xml"
    @Input String effort = "max"
    @Input String reportLevel = "low"

}
