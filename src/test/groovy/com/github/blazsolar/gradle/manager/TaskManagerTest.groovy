package com.github.blazsolar.gradle.manager
import com.github.blazsolar.gradle.SoterPlugin
import com.github.blazsolar.gradle.task.Checkstyle
import com.github.blazsolar.gradle.task.UploadTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * Created by blazsolar on 18/04/15.
 */
class TaskManagerTest {

    @Test
    public void testCheckstyleBasic() throws Exception {

        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'com.github.blazsolar.soter'

        SoterPlugin plugin = project.plugins.getPlugin(SoterPlugin)

        plugin.createCheckTasks()

        assertTrue project.tasks.findByName("checkstyle") instanceof Checkstyle
        assertTrue project.tasks.findByName("uploadCheckstyle") instanceof UploadTask

    }



}
