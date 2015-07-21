package si.dlabs.gradle

import com.android.annotations.VisibleForTesting
import si.dlabs.gradle.extensions.SoterExtension
import si.dlabs.gradle.manager.TaskManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject
/**
 * Created by blazsolar on 02/09/14.z
 */
class SoterPlugin implements Plugin<Project> {

    private final Instantiator instantiator

    private Project project;
    private TaskManager taskManager;

    private SoterExtension extension;

    @Inject
    SoterPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    boolean isAndroidProject(Project project) {
        project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')
    }

    @Override
    void apply(Project project) {
        if (!isAndroidProject(project)) {
            throw new IllegalStateException("SoterPlugin only works with Android projects")
        }

        this.project = project;

        createExtensions()
        createTasks()
    }

    private void createExtensions() {
        extension = project.extensions.create('soter', SoterExtension,
                instantiator)
        taskManager = new TaskManager(project, extension);
    }

    private void createTasks() {

        taskManager.createTasks()

        project.afterEvaluate {
            createCheckTasks()
        }

    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PROTECTED)
    final void createCheckTasks() {
        taskManager.createCheckTasks()
    }

}
