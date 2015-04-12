package com.github.blazsolar.gradle.task

import org.gradle.api.tasks.TaskAction

/**
 * Created by blazsolar on 07/04/15.
 */
class Pmd extends org.gradle.api.plugins.quality.Pmd {

    @Override @TaskAction
    void run() {

        ruleSetFiles = project.files(project.configurations.pmdRules.files)

        super.run()
    }
}
