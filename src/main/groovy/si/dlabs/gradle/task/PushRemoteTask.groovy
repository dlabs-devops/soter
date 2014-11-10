package si.dlabs.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * Created by blazsolar on 10/11/14.
 */
class PushRemoteTask extends DefaultTask {

    String gitDir = project.rootDir;

    String remote;

    private def grgit;

    @TaskAction
    public void push() {

        if (remote) {

//            grgit = Grgit.open(gitDir);
//            grgit.remote.add(name: 'check-plugin-remote', url: remote)
//            grgit.push(all: false, remote: 'check-plugin-remote', tags: true)

        } else {
            throw new IllegalStateException("Remote cannot be null");
        }

    }

}
