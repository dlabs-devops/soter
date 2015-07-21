package si.dlabs.gradle.manager
import si.dlabs.gradle.SoterPlugin
import si.dlabs.gradle.task.Checkstyle
import si.dlabs.gradle.task.UploadTask
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
        project.apply plugin: 'si.dlabs.soter'

        SoterPlugin plugin = project.plugins.getPlugin(SoterPlugin)

        plugin.createCheckTasks()

        assertTrue project.tasks.findByName("checkstyle") instanceof Checkstyle
        assertTrue project.tasks.findByName("uploadCheckstyle") instanceof UploadTask

    }



}
