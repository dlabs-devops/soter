package si.dlabs.gradle.model

import org.gradle.api.tasks.Input
import org.gradle.model.Managed

/**
 * Created by blazsolar on 01/09/15.
 */
@Managed
interface Checkstyle {

    String getReportFile();
    void setReportFile(String reportFile);

    boolean getIgnoreFailures();
    void setIgnoreFailures(boolean ignoreFailures);

    boolean getShowViolations();
    void setShovViolations(boolean showViolations);s

    String getToolVersion();
    void setToolVersion(String version);

}
