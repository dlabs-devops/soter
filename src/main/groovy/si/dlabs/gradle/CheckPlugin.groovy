package si.dlabs.gradle
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import si.dlabs.gradle.extensions.AmazonExtension
import si.dlabs.gradle.extensions.CheckExtension
import si.dlabs.gradle.extensions.CheckstyleExtension
import si.dlabs.gradle.extensions.FindbugsExtension
import si.dlabs.gradle.extensions.PMDExtension
import si.dlabs.gradle.task.UploadTask

/**
 * Created by blazsolar on 02/09/14.
 */
class CheckPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (!project.plugins.hasPlugin(com.android.build.gradle.AppPlugin)) {
            throw new RuntimeException("CheckPlugin requires android plugin")
        }

        CheckExtension baseExtension = project.extensions.create("check", CheckExtension)
        project.check.extensions.create("checkstyle", CheckstyleExtension)
        project.check.extensions.create("findbugs", FindbugsExtension)
        project.check.extensions.create("pmd", PMDExtension)
        project.check.extensions.create("amazon", AmazonExtension)

        project.configurations {
            rulesCheckstyle
            rulesFindbugs
            rulesPMD
        }

        project.afterEvaluate {
            addCheckstyleTask(project)
            addFindbugsTask(project)
            addPMDTask(project)
        }

    }

    /**
     * Generates Checkstyle report for debug build.
     */
    private static void addCheckstyleTask(Project project) {

        if (project.check.checkstyle.enabled) {

            project.apply plugin: 'checkstyle'

            Task checkstyle = project.tasks.create("checkstyle", org.gradle.api.plugins.quality.Checkstyle);
            checkstyle.setDescription("Checkstyle for debug source")
            checkstyle.setGroup("Check")


            checkstyle.source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
            checkstyle.include '**/*.java'
            checkstyle.exclude '**/gen/**'

            checkstyle.classpath = project.files()

            checkstyle.reports {
                xml {
                    destination "$project.buildDir/outputs/reports/checkstyle/checkstyle.xml"
                }
            }

            checkstyle.configFile project.configurations.rulesCheckstyle.singleFile

            project.tasks.check.dependsOn checkstyle

            if (project.check.checkstyle.uploadReports) {

                Task upload = project.tasks.create("uploadCheckstyle", UploadTask);
                upload.setDescription("Upload checkstyle reports to amazon s3")
                upload.setGroup("Upload")

                upload.accessKey = project.check.amazon.accessKey
                upload.secretKey = project.check.amazon.secretKey

                upload.file = project.file("$project.buildDir/outputs/reports/checkstyle");
                upload.bucket project.check.amazon.bucket;

                checkstyle.finalizedBy upload

            }

        }

    }

    /**
     * Findbugs task fro debug source set
     */
    private static void addFindbugsTask(Project project) {

        if (project.check.findbugs.enabled) {

            project.apply plugin: 'findbugs'

            Task findbugs = project.tasks.create("findbugs", org.gradle.api.plugins.quality.FindBugs)
            findbugs.setDescription("Findbugs for debug source")
            findbugs.setGroup("Check")

            findbugs.ignoreFailures = false
            findbugs.effort = "max"
            findbugs.reportLevel = "low"
            findbugs.classes = project.files("$project.buildDir/intermediates/classes")

            findbugs.source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
            findbugs.include '**/*.java'
            findbugs.exclude '**/gen/**'

            findbugs.reports {
                html {
                    enabled true
                    destination "$project.buildDir/outputs/reports/findbugs/findbugs.html"
                }
                xml {
                    enabled false
                    destination "$project.buildDir/outputs/reports/findbugs/findbugs.xml"
                    xml.withMessages true
                }
            }

            findbugs.classpath = project.files()

            findbugs.excludeFilter = project.configurations.rulesFindbugs.singleFile

            project.tasks.check.dependsOn findbugs
            findbugs.dependsOn "compileDebugJava"

            if (project.check.checkstyle.uploadReports) {

                Task upload = project.tasks.create("uploadFindbugs", UploadTask);
                upload.setDescription("Upload findbugs reports to amazon s3")
                upload.setGroup("Upload")

                upload.accessKey = project.check.amazon.accessKey
                upload.secretKey = project.check.amazon.secretKey

                upload.file = project.file("$project.buildDir/outputs/reports/findbugs");
                upload.bucket project.check.amazon.bucket;

                findbugs.finalizedBy upload

            }

        }

    }

    private static void addPMDTask(Project project) {

        if (project.check.pmd.enabled) {

            project.apply plugin: 'pmd'

            Task pmd = project.tasks.create("pmd", org.gradle.api.plugins.quality.Pmd)
            pmd.setDescription("PMD for debug source")
            pmd.setGroup("Check")

            pmd.ignoreFailures = false
            pmd.ruleSets = ["basic", "braces", "strings", "android"]

            pmd.source project.android.sourceSets.main.java.getSrcDirs(), project.android.sourceSets.debug.java.getSrcDirs()
            pmd.include '**/*.java'
            pmd.exclude '**/gen/**'

            pmd.reports {
                html {
                    enabled = true
                    destination "$project.buildDir/outputs/reports/pmd/pmd.html"
                }
                xml {
                    enabled = true
                    destination "$project.buildDir/outputs/reports/pmd/pmd.xml"
                }
            }

            pmd.ruleSetFiles = project.files(project.configurations.rulesPMD.files)

            project.tasks.check.dependsOn pmd

            if (project.check.checkstyle.uploadReports) {

                Task upload = project.tasks.create("uploadPmd", UploadTask);
                upload.setDescription("Upload pmd reports to amazon s3")
                upload.setGroup("Upload")

                upload.accessKey = project.check.amazon.accessKey
                upload.secretKey = project.check.amazon.secretKey

                upload.file = project.file("$project.buildDir/outputs/reports/pmd/");
                upload.bucket project.check.amazon.bucket;

                pmd.finalizedBy upload

            }
        }

    }

}
