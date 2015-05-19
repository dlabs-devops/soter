package si.dlabs.gradle.task

import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * Created by blazsolar on 10/11/14.
 */
class PushRemoteTask extends DefaultTask {

    String gitDir = project.rootDir;

    String remote;
    String branch
    String username;
    String password;

    private def grgit;

    @TaskAction
    public void push() {

        if (remote) {

            grgit = Grgit.open(dir: gitDir, creds: new Credentials(username, password))

            if (!branch) {
                branch = grgit.branch.current.getName()
            }

            grgit.remote.add(name: 'soter', url: remote)
            grgit.push(all: false, remote: 'soter', tags: true, refsOrSpecs: ["refs/heads/$branch:refs/heads/$branch"])


        } else {
            throw new IllegalStateException("Remote cannot be null");
        }

    }

}
