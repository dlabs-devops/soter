package si.dlabs.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class PMDExtension extends PluginBaseExtension {

    @Input String reportFile = "reports/checkstyle/checkstyle.xml"

}
