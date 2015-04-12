package com.github.blazsolar.gradle.task

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
}
