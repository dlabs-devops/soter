package si.dlabs.gradle

import com.android.annotations.VisibleForTesting
import com.android.build.gradle.AppPlugin
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

    @Override
    void apply(Project project) {

        this.project = project;

        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalStateException("SoterPlugin requires android plugin")
        }

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
