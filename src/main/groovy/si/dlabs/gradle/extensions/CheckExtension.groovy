package si.dlabs.gradle.extensions
/**
 * Created by blazsolar on 02/09/14.
 */
class CheckExtension {

    String configRoot;
    String reportRoot;
    CheckstyleExtension checkstyle;
    FindbugsExtension findbugs;
    PMDExtension pmd;

    CheckExtension() {
//        configRoot = "$project.rootDir/config/quality/"
//        reportRoot = "$project.buildDir/outputs/reports/";
        checkstyle = new CheckstyleExtension();
        findbugs = new FindbugsExtension();
        pmd = new PMDExtension();
    }
}
