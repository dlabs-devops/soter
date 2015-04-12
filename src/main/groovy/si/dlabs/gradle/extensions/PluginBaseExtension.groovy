package si.dlabs.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 11/09/14.
 */
class PluginBaseExtension {

    @Input boolean enabled = true;
    @Input boolean uploadReports = false;

}
