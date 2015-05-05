package com.github.blazsolar.gradle.manager

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.github.blazsolar.gradle.extensions.*
import com.github.blazsolar.gradle.hipchat.tasks.SendMessageTask
import com.github.blazsolar.gradle.task.*
import com.github.hipchat.api.messages.Message
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.javadoc.Javadoc

import static com.android.builder.core.BuilderConstants.FD_REPORTS
/**
 * Created by blazsolar on 12/04/15.
 */
class TaskManager {

    private static final String FOLDER_UPLOAD_REPORTS = "reports/";

    private static final String FOLDER_REPORT_CHECKSTYLE = "checkstyle";
    private static final String FOLDER_REPORT_FINDBUGS = "findbugs";
    private static final String FOLDER_REPORT_PMD = "pmd";

    private static final String FILE_REPORT_CHECKSTYLE = "checkstyle.xml";


    private final Project project
    private final SoterExtension extension;

    private Task afterAll
    private Task success
    private Task failed

    TaskManager(Project project, SoterExtension extension) {
        this.project = project
        this.extension = extension
    }

    public void createTasks() {

        afterAll = addAfterAll(project)
        success = addSuccessTask(project)
        failed = addFailedTask(project)
        addComplete(project)

        addConfigurations();

    }

    public void createCheckTasks() {

        addCheckstyleTask(project)
        addFindbugsTask(project)
        addPMDTask(project)
        addLogsTask(project)
        addTestsTasks(project)
        addDocsTask(project)
        addPublishTasks(project)

        addNotificationsTasks(project)
        addRemotePushTask(project)

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

        CheckstyleExtension checkstyleExtension = extension.checkstyle;

        project.apply plugin: 'checkstyle'

        Checkstyle checkstyle = project.tasks.create("checkstyle", Checkstyle);
        checkstyle.setup checkstyleExtension

        String outputDir = "$project.buildDir/$FD_REPORTS/$FOLDER_REPORT_CHECKSTYLE"

        checkstyle.reports {
            xml {
                destination "$outputDir/$FILE_REPORT_CHECKSTYLE"
            }
        }

        Task uploadTask = addUploadTask(project, "uploadCheckstyle",
                "Upload checkstyle reports to amazon s3", project.file(outputDir),
                FOLDER_UPLOAD_REPORTS, false)
        uploadTask.dependsOn checkstyle

        if (checkstyleExtension.enabled) {
            project.tasks.check.dependsOn checkstyle

            // upload checkstyle
            if (checkstyleExtension.uploadReports && extension.amazon.enabled) {
                checkstyle.finalizedBy uploadTask
            }

        }

    }

    /**
     * Findbugs task fro debug source set
     */
    private void addFindbugsTask(Project project) {

        project.apply plugin: 'findbugs'

        FindbugsExtension extension = this.extension.findbugs

        FindBugs findbugs = project.tasks.create("findbugs", FindBugs)
        findbugs.setup extension

        String outputDir = "$project.buildDir/$FD_REPORTS/$FOLDER_REPORT_FINDBUGS"

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

        Task upload = addUploadTask(project, "uploadFindbugs",
                "Upload findbugs reports to amazon s3", project.file(outputDir),
                "reports/", true);
        upload.dependsOn findbugs

        if (extension.enabled) {

            project.tasks.check.dependsOn findbugs

            if (extension.uploadReports && this.extension.amazon.enabled) {
                findbugs.finalizedBy upload
            }

        }

    }

    /**
     * Adds PMD task to gradle.
     */
    private void addPMDTask(Project project) {

        project.apply plugin: 'pmd'

        PMDExtension extension = this.extension.pmd

        Pmd pmd = project.tasks.create("pmd", Pmd)
        pmd.setup extension

        String outputDir = "$project.buildDir/$FD_REPORTS/$FOLDER_REPORT_PMD"

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

        Task upload = addUploadTask(project, "uploadPmd", "Upload pmd reports to amazon s3",
                project.file(outputDir), "reports/", true)

        if (project.soter.pmd.enabled) {
            project.tasks.check.dependsOn pmd

            if (project.soter.checkstyle.uploadReports && project.soter.amazon.enabled) {
                pmd.finalizedBy upload
            }
        }

    }

    /**
     * Adds task that uploads logs
     */
    private void addLogsTask(Project project) {

        String outputDir = "$project.buildDir/reports/logs"

        LogsExtension extension = this.extension.logs

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

        if (project.soter.logs.uploadReports && project.soter.amazon.enabled) {
            project.tasks.connectedAndroidTest.finalizedBy logs
            logs.finalizedBy upload
        }

    }

    /**
     * Upload connected check reports.
     */
    private void addTestsTasks(Project project) {

        Task uploadAndroidTests = addUploadTask(project, "uploadAndroidTestReports",
                "Upload test reports to amazon s3",
                project.file("$project.buildDir/$FD_REPORTS/androidTests"),
                "reports/", true)

        Task uploadUnitTests = addUploadTask(project, "uploadUnitTestReports",
                "Upload test reports to amazon s3",
                project.file("$project.buildDir/$FD_REPORTS/tests"),
                "reports/", true)

        Task uploadcoverage = addUploadTask(project, "uploadCodeCoverage",
                "Upload code coverage reports to amazon s3",
                project.file("$project.buildDir/$FD_REPORTS/coverage"),
                "reports/", true)

        if (extension.amazon.enabled) {

            if (extension.tests.uploadAndroidTestReports) {
                project.tasks.connectedAndroidTest.finalizedBy uploadAndroidTests
            }

            if (extension.tests.uploadUnitTestReports) {
                project.tasks.test.finalizedBy uploadUnitTests
            }

            if (extension.codeCoverage.uploadReports) {
                project.tasks.createDebugCoverageReport.finalizedBy uploadcoverage

            }

        }

    }

    private void addDocsTask(Project project) {

        project.android.applicationVariants.all { variant ->

            if (variant.name.equals("release")) {

                String dir = "$project.buildDir/reports/docs"

                Javadoc docs = project.tasks.create("androidJavadoc", Javadoc) {
                    title = "Documentation for Android"
                    description "Generates Javadoc for $variant.name."
                    source = variant.javaCompile.source

                    def claspathFiles = project.files(project.android.getBootClasspath().join(File.pathSeparator))
                    classpath = project.files(variant.javaCompile.classpath.files) + claspathFiles

                    options.locale = 'en_US'
                    options.encoding = 'UTF-8'
                    options.charSet = 'UTF-8'
                    options.links("http://docs.oracle.com/javase/7/docs/api/")
                    options.linksOffline("http://d.android.com/reference", "${project.android.sdkDirectory}/docs/reference")
                    options.memberLevel = org.gradle.external.javadoc.JavadocMemberLevel.PRIVATE
                    exclude '**/BuildConfig.java', '**/R.java', '**/internal/**'
                    failOnError false

                    destinationDir = new File(dir, variant.baseName)
                }

                Task upload = addUploadTask(project, "uploadDocs",
                        "Upload docs to amazon s3", project.file(dir),
                        "reports/", true);
                upload.dependsOn docs

                if (project.soter.docs.uploadReports && project.soter.amazon.enabled) {
                    success.dependsOn upload
                }

            }

        }

    }

    /**
     * Adds task for publishing apk and all its dependencies.
     */
    private void addPublishTasks(Project project) {

        // add upload task
        Task upload = project.tasks.create("uploadApk")
        upload.setDescription("Upload apk to amazon s3")
        upload.setGroup("Upload")
        success.dependsOn upload

        if (project.soter.publish.enabled) {
            addApkTask(project, upload)
            addFabricTask(project, upload)
        }

    }

    /**
     * Adds task that uploads apk to Amazon S3 bucket.
     */
    private void addApkTask(Project project, Task upload) {

        String[] variants = project.soter.publish.amazon.variants

        project.android.applicationVariants.all { ApplicationVariant variant ->

            def variantName = variant.name;

            if (variants.contains(variantName)) {

                String uploadTaskName = "uploadApk" + variantName.capitalize();

                UploadTask uploadVariant = addUploadTask(project, uploadTaskName,
                        "Upload apk variant to amazon s3", null, "binary/", false);

                def files = new File[variant.getOutputs().size()];

                variant.getOutputs().eachWithIndex { output, index ->
                    files[index] = output.getOutputFile()
                }

                uploadVariant.files = files;
                uploadVariant.dependsOn variant.getAssemble()

                if (project.soter.publish.amazon.upload && project.soter.amazon.enabled) {
                    upload.dependsOn uploadVariant
                }
            }
        }

    }

    /**
     * Adds task that uploads apk to Fabric Beta.
     */
    private void addFabricTask(Project project, Task upload) {

        String[] variants = project.soter.publish.fabric.variants

        project.android.applicationVariants.all { ApplicationVariant variant ->

            def variantName = variant.name;

            if (variants.contains(variantName)) {

                String cName = variantName.capitalize();
                def crashlyticsTaskName = "crashlyticsUploadDistribution" + cName

                def tasks = project.getTasksByName(crashlyticsTaskName, true);

                for (def t : tasks) {

                    if (project.soter.publish.fabric.upload) {
                        t.dependsOn variant.getAssemble()
                        upload.dependsOn tasks
                    }

                }

            }
        }

    }

    /**
     * Adds all notification tasks.
     */
    private void addNotificationsTasks(Project project) {

        if (project.soter.notifications.enabled) {

            addHipChatTask(project)

        }

    }

    /**
     * Adds hipchat notification tasks.
     */
    private void addHipChatTask(Project project) {

        if (project.soter.notifications.hipchat.enabled) {

            project.apply plugin: "com.github.blazsolar.hipchat"

            project.hipchat.token = project.soter.notifications.hipchat.token

            String userName = "Travis CI"
            String textPrefix = System.getenv("TRAVIS_REPO_SLUG") + "#" + System.getenv("TRAVIS_BUILD_ID") + " (" + System.getenv("TRAVIS_BRANCH") + " - " + System.getenv("TRAVIS_COMMIT").substring(0, 6) + "): "

            SendMessageTask passed = project.tasks.create("notifyHipChatPassed", SendMessageTask)
            passed.roomId = project.soter.notifications.hipchat.roomId
            passed.userId = project.soter.notifications.hipchat.userId
            passed.userName = userName
            passed.color = Message.Color.GREEN
            passed.message = textPrefix + "the build has passed"
            success.dependsOn passed

            SendMessageTask failedTask = project.tasks.create("notifyHipChatFailed", SendMessageTask)
            failedTask.roomId = project.soter.notifications.hipchat.roomId
            failedTask.userId = project.soter.notifications.hipchat.userId
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

        PushRemoteTask remote = project.tasks.create("pushRemote", PushRemoteTask)
        remote.remote = project.soter.remote.remote
        remote.branch = project.soter.remote.branch
        remote.username = project.soter.remote.username
        remote.password = project.soter.remote.password

        if (project.soter.remote.pushToRemote) {
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

        upload.accessKey = project.soter.amazon.accessKey
        upload.secretKey = project.soter.amazon.secretKey

        upload.file = file;
        upload.bucket project.soter.amazon.bucket;
        upload.keyPrefix = project.soter.amazon.path + folder
        upload.isPublic = isPublic

        return upload;

    }

    private void addConfigurations() {

        addConfiguration("checkstyleRules", "Checkstyle configuration file.")
        addConfiguration("findbugsRules", "FindBugs configuration file.")
        addConfiguration("pmdRules", "PMD configuration file.")

    }

    private void addConfiguration(String configurationName, String configurationDescription) {

        ConfigurationContainer configurations = project.configurations

        Configuration configuration = configurations.findByName(configurationName)
        if (configuration == null) {
            configuration = configurations.create(configurationName)
        }
        configuration.setVisible(false);
        configuration.setDescription(configurationDescription)

    }

}
