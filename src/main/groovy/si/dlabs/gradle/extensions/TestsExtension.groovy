package si.dlabs.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class TestsExtension {

    @Input boolean uploadReports = false;
    @Input String reportFile = "reports/logs/logs.log"

}
