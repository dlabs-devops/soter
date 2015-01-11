package si.dlabs.gradle
import com.android.build.gradle.api.ApplicationVariant
import com.github.blazsolar.gradle.hipchat.tasks.SendMessageTask
import com.github.hipchat.api.messages.Message
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import si.dlabs.gradle.extensions.*
import si.dlabs.gradle.task.AfterAllTask
import si.dlabs.gradle.task.PushRemoteTask
import si.dlabs.gradle.task.UploadTask
/**
 * Created by blazsolar on 02/09/14.z
 */
class CheckPlugin implements Plugin<Project> {

    def afterAll
    def success
    def failed

    @Override
    void apply(Project project) {

        if (!project.plugins.hasPlugin(com.android.build.gradle.AppPlugin)) {
            throw new RuntimeException("CheckPlugin requires android plugin")
        }

        CheckExtension baseExtension = project.extensions.create("check", CheckExtension)

        project.check.extensions.create("checkstyle", CheckstyleExtension)
        project.check.extensions.create("findbugs", FindbugsExtension)
        project.check.extensions.create("pmd", PMDExtension)
        project.check.extensions.create("logs", LogsExtension)
        project.check.extensions.create("tests", TestsExtension)

        project.check.extensions.create("publish", PublishExtension)
        project.check.publish.extensions.create("amazon", AmazonApkExtension)
        project.check.publish.extensions.create("crashlytics", CrashlyticsExtension)

        project.check.extensions.create("notifications", NotificationsExtension)
        project.check.notifications.extensions.create("hipchat", HipChatExtension)

        project.check.extensions.create("amazon", AmazonExtension)

        project.check.extensions.create("remote", RemoteExtension)

        project.check.extensions.create("afterAll", AfterAllExtension)

        project.apply plugin: "com.github.blazsolar.hipchat"

        project.configurations {
            rulesCheckstyle
            rulesFindbugs
            rulesPMD
        }

        afterAll = addAfterAll(project)
        success = addSuccessTask(project)
        failed = addFailedTask(project)

        addComplete(project)

        project.afterEvaluate {
            addCheckstyleTask(project)
            addFindbugsTask(project)
            addPMDTask(project)
            addLogsTask(project)
            addTestsTasks(project)
            addPublishTasks(project)
            addNotificationsTasks(project)
            addRemotePushTask(project)
        }

    }

    /**
     * Adds task that waites for all builds to finish.
     */
    private AfterAllTask addAfterAll(Project project) {

        def afterAll = project.tasks.create("afterAll", AfterAllTask)
        afterAll.setDescription("Waits that all jobs are executed")
        afterAll.setGroup("CI")

        project.tasks.create("thisSuccess") << {
            afterAll.thisSuccess = true
        }

        project.tasks.create("thisFailed") << {
            afterAll.thisSuccess = false
        }

        return afterAll

    }

    /**
     * Adds success task to gradle.
     */
    private Task addSuccessTask(Project project) {
        def success = project.tasks.create("success");
        return success;
    }

    /**
     * Adds failed task to gradle.
     */
    private Task addFailedTask(Project project) {
        def failed = project.tasks.create("failed");
        return failed;
    }

    private Task addComplete(Project project) {

        def task = project.tasks.create("complete")

        File propFile = new File("/tmp/ci.properties");

        if (propFile.exists()) {
            Properties properties = new Properties();
            properties.load(propFile.newReader())

            boolean success = Boolean.valueOf(properties.getProperty("success", "false"))

            if (success) {
                task.dependsOn this.success
            } else {
                task.dependsOn failed
            }
        }
    }

    /**
     * Generates Checkstyle report for debug build.
     */
    private void addCheckstyleTask(Project project) {

        if (project.check.checkstyle.enabled) {

            project.apply plugin: 'checkstyle'

            Task checkstyle = project.tasks.create("checkstyle", org.gradle.api.plugins.quality.Checkstyle);
            checkstyle.setDescription("Checkstyle for debug source")
            checkstyle.setGroup("Check")

            checkstyle.source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
            checkstyle.include '**/*.java'
            checkstyle.exclude '**/gen/**'

            checkstyle.classpath = project.files()

            String outputDir = "$project.buildDir/outputs/reports/checkstyle"

            checkstyle.reports {
                xml {
                    destination outputDir + "/checkstyle.xml"
                }
            }

            checkstyle.configFile project.configurations.rulesCheckstyle.singleFile

            project.tasks.check.dependsOn checkstyle

            // upload checkstyle
            if (project.check.checkstyle.uploadReports && project.check.amazon.enabled) {
                checkstyle.finalizedBy addUploadTask(project, "uploadCheckstyle",
                        "Upload checkstyle reports to amazon s3", project.file(outputDir),
                        "reports/", false)
            }

        }

    }

    /**
     * Findbugs task fro debug source set
     */
    private void addFindbugsTask(Project project) {

        if (project.check.findbugs.enabled) {

            project.apply plugin: 'findbugs'

            Task findbugs = project.tasks.create("findbugs", org.gradle.api.plugins.quality.FindBugs)
            findbugs.setDescription("Findbugs for debug source")
            findbugs.setGroup("Check")

            String outputDir = "$project.buildDir/outputs/reports/findbugs"

            findbugs.ignoreFailures = false
            findbugs.effort = project.check.findbugs.effort
            findbugs.reportLevel = project.check.findbugs.reportLevel
            findbugs.classes = project.files("$project.buildDir/intermediates/classes")

            findbugs.source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
            findbugs.include '**/*.java'
            findbugs.exclude '**/gen/**'

            findbugs.reports {
                html {
                    enabled true
                    destination "$outputDir/findbugs.html"
                }
                xml {
                    enabled false
                    destination "$outputDir/findbugs.xml"
                    xml.withMessages true
                }
            }

            findbugs.classpath = project.files()

            findbugs.excludeFilter = project.configurations.rulesFindbugs.singleFile

            project.tasks.check.dependsOn findbugs
            findbugs.dependsOn "compileDebugJava"

            if (project.check.checkstyle.uploadReports && project.check.amazon.enabled) {
                Task upload = addUploadTask(project, "uploadFindbugs",
                        "Upload findbugs reports to amazon s3", project.file(outputDir),
                        "reports/", true);
                findbugs.finalizedBy upload
            }

        }

    }

    /**
     * Adds PMD task to gradle.
     */
    private void addPMDTask(Project project) {

        if (project.check.pmd.enabled) {

            project.apply plugin: 'pmd'

            Task pmd = project.tasks.create("pmd", org.gradle.api.plugins.quality.Pmd)
            pmd.setDescription("PMD for debug source")
            pmd.setGroup("Check")

            pmd.ignoreFailures = false
            pmd.ruleSets = ["java-basic", "java-braces", "java-strings", "java-android"]

            pmd.source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
            pmd.include '**/*.java'
            pmd.exclude '**/gen/**'

            String outputDir = "$project.buildDir/outputs/reports/pmd"

            pmd.reports {
                html {
                    enabled = true
                    destination "$outputDir/pmd.html"
                }
                xml {
                    enabled = true
                    destination "$outputDir/pmd.xml"
                }
            }

            pmd.ruleSetFiles = project.files(project.configurations.rulesPMD.files)

            project.tasks.check.dependsOn pmd

            if (project.check.checkstyle.uploadReports && project.check.amazon.enabled) {
                Task upload = addUploadTask(project, "uploadPmd", "Upload pmd reports to amazon s3",
                        project.file(outputDir), "reports/", true)
                pmd.finalizedBy upload

            }
        }

    }

    /**
     * Adds task that uploads logs
     */
    private void addLogsTask(Project project) {

        if (project.check.logs.uploadReports && project.check.amazon.enabled) {

            String outputDir = "$project.buildDir/outputs/reports/logs"

            Exec logs = project.tasks.create("logs", Exec);
            logs.outputs.file project.file(outputDir + "/emulator.log")
            logs.commandLine = ["adb", "logcat", "-d"]
            logs.doFirst {
                File file = new File(outputDir);
                file.mkdirs();

                standardOutput = new FileOutputStream(new File(file, "emulator.log"))
            }

            Task upload = addUploadTask(project, "uploadLogs",
                    "Upload logs to amazon s3", project.file(outputDir), "reports/", true)
            logs.finalizedBy upload
            project.tasks.connectedAndroidTest.finalizedBy logs

        }

    }

    /**
     * Upload connected check reports.
     */
    private void addTestsTasks(Project project) {

        if (project.check.tests.uploadReports && project.check.amazon.enabled) {

            Task upload = addUploadTask(project, "uploadTestReports",
                    "Upload test reports to amazon s3",
                    project.file("$project.buildDir/outputs/reports/androidTests"),
                    "reports/", true)
            project.tasks.connectedAndroidTest.finalizedBy upload

        }

    }

    /**
     * Adds task for publishing apk and all its dependencies.
     */
    private void addPublishTasks(Project project) {

        if (project.check.publish.enabled) {

            // add upload task
            Task upload = project.tasks.create("uploadApk")
            upload.setDescription("Upload apk to amazon s3")
            upload.setGroup("Upload")
            success.dependsOn upload

            addApkTask(project, upload)
            addCrashlyticsTask(project, upload)

        }

    }

    /**
     * Adds task that uploads apk to Amazon S3 bucket.
     */
    private void addApkTask(Project project, Task upload) {

        if (project.check.publish.amazon.upload && project.check.amazon.enabled) {

            String uploadTaskName = "uploadApk" + project.check.publish.amazon.variant.capitalize();

            UploadTask uploadVariant = addUploadTask(project, uploadTaskName,
                    "Upload apk variant to amazon s3", null, "binary/", false);

            project.android.applicationVariants.all { ApplicationVariant variant ->
                if (variant.getName().equals(project.check.publish.amazon.variant)) {

                    def files = new File[variant.getOutputs().size()];

                    variant.getOutputs().eachWithIndex { output, index ->
                        files[index] = output.getOutputFile()
                    }

                    uploadVariant.files = files;
                    uploadVariant.dependsOn variant.getAssemble()
                    upload.dependsOn uploadVariant
                }
            }

        }

    }

    /**
     * Adds task that uploads apk to Crashlytics Beta.
     */
    private void addCrashlyticsTask(Project project, Task upload) {

        if (project.check.publish.crashlytics.upload) {

            String cName = project.check.publish.crashlytics.variant.capitalize();
            def crashlyticsTaskName = "crashlyticsUploadDistribution" + cName

            def tasks = project.getTasksByName(crashlyticsTaskName, true);

            for (def t : tasks) {

                upload.dependsOn tasks

                project.android.applicationVariants.all { ApplicationVariant variant ->
                    if (variant.getName().equals(project.check.publish.crashlytics.variant)) {
                        t.dependsOn variant.getAssemble()
                    }
                }

            }

        }

    }

    /**
     * Adds all notification tasks.
     */
    private void addNotificationsTasks(Project project) {

        if (project.check.notifications.enabled) {

            addHipChatTask(project)

        }

    }

    /**
     * Adds hipchat notification tasks.
     */
    private void addHipChatTask(Project project) {

        if (project.check.notifications.hipchat.enabled) {

            project.hipchat.token = project.check.notifications.hipchat.token

            String userName = "Travis CI"
            String textPrefix = System.getenv("TRAVIS_REPO_SLUG") + "#" + System.getenv("TRAVIS_BUILD_ID") + " (" + System.getenv("TRAVIS_BRANCH") + " - " + System.getenv("TRAVIS_COMMIT").substring(0, 6) + "): "

            SendMessageTask passed = project.tasks.create("notifyHipChatPassed", SendMessageTask)
            passed.roomId = project.check.notifications.hipchat.roomId
            passed.userId = project.check.notifications.hipchat.userId
            passed.userName = userName
            passed.color = Message.Color.GREEN
            passed.message = textPrefix + "the build has passed"
            success.dependsOn passed

            SendMessageTask failedTask = project.tasks.create("notifyHipChatFailed", SendMessageTask)
            failedTask.roomId = project.check.notifications.hipchat.roomId
            failedTask.userId = project.check.notifications.hipchat.userId
            failedTask.userName = userName
            failedTask.color = Message.Color.RED
            failedTask.message = textPrefix + "the build has failed"
            failed.dependsOn failedTask

        }

    }

    /**
     * Add task that pushes code to another GIT repo.
     */
    private void addRemotePushTask(Project project) {

        if (project.check.remote.pushToRemote) {

            PushRemoteTask remote = project.tasks.create("pushRemote", PushRemoteTask)
            remote.remote = project.check.remote.remote
            remote.branch = project.check.remote.branch
            remote.username = project.check.remote.username
            remote.password = project.check.remote.password
            success.dependsOn remote

        }

    }

    /**
     * Adds upload task for specific file
     */
    private UploadTask addUploadTask(Project project, String name, String description, File file, String folder, boolean isPublic) {

        UploadTask upload = project.tasks.create(name, UploadTask);
        upload.setDescription(description)
        upload.setGroup("Upload")

        upload.accessKey = project.check.amazon.accessKey
        upload.secretKey = project.check.amazon.secretKey

        upload.file = file;
        upload.bucket project.check.amazon.bucket;
        upload.keyPrefix = project.check.amazon.path + folder
        upload.isPublic = isPublic

        return upload;

    }

}
