package si.dlabs.gradle

import com.android.SdkConstants
import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.model.AndroidComponentModelSourceSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.base.LanguageSourceSet
import org.gradle.language.base.ProjectSourceSet
import org.gradle.model.Model
import org.gradle.model.ModelMap
import org.gradle.model.Mutate
import org.gradle.model.Path
import org.gradle.model.RuleSource
import si.dlabs.gradle.model.Checkstyle

import static com.android.builder.core.BuilderConstants.FD_REPORTS

/**
 * Created by blazsolar on 01/09/15.
 */
class SoterRules extends RuleSource {

    @Model void checkstyle(Checkstyle checkstyle) {
        checkstyle.setReportFile("reports/checkstyle/checkstyle.xml")
        checkstyle.setToolVersion("6.7")
    }

    @Mutate void createCheckstyleTask(ModelMap<Task> tasks, Checkstyle checkstyle, ProjectSourceSet projectSourceSet,
                                      Project project) {

        org.gradle.api.plugins.quality.Checkstyle task = tasks.create("checkstyle", org.gradle.api.plugins.quality.Checkstyle);

        task.description "Checkstyle for debug source"
        task.group "Check"

        task.ignoreFailures checkstyle.ignoreFailures
        task.showViolations checkstyle.showViolations

        projectSourceSet.all { LanguageSourceSet sourceSet ->
            if (!sourceSet.name.startsWith("test") && !sourceSet.name.startsWith(SdkConstants.FD_TEST)) {
                task.source sourceSet.getSource().srcDirs
            }
        }

        task.include '**/*.java'
        task.exclude '**/gen/**'

        task.classpath = project.files()

        String outputDir = "$project.buildDir/$FD_REPORTS/$FOLDER_REPORT_CHECKSTYLE"

        task.reports {
            xml {
                destination "$outputDir/$FILE_REPORT_CHECKSTYLE"
            }
        }

//        Task uploadTask = addUploadTask(project, "uploadCheckstyle",
//                "Upload checkstyle reports to amazon s3", project.file(outputDir),
//                FOLDER_UPLOAD_REPORTS, false)
//
//        if (checkstyleExtension.enabled) {
//            project.tasks.check.dependsOn checkstyle
//
//            // upload checkstyle
//            if (checkstyleExtension.uploadReports && extension.amazon.enabled) {
//                checkstyle.finalizedBy uploadTask
//            }
//
//        }
//
//        if (checkstyleExtension.toolVersion) {
//            project.checkstyle {
//                toolVersion = checkstyleExtension.toolVersion
//            }
//        }

    }

}
