package com.github.blazsolar.gradle.task

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
}
