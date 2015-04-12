package si.dlabs.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class AfterAllExtension {

    private static final def TRAVIS_JOB_NUMBER = "TRAVIS_JOB_NUMBER"
    private static final def TRAVIS_BUILD_ID = "TRAVIS_BUILD_ID"

    @Input String buildID = System.getenv(TRAVIS_BUILD_ID)
    @Input String jobNumber = System.getenv(TRAVIS_JOB_NUMBER)
    @Input String ghToken
    @Input long pollingInterval = 5000

}
