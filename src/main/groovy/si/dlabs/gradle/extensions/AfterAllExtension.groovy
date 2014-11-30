package si.dlabs.gradle.extensions

/**
 * Created by blazsolar on 02/09/14.
 */
class AfterAllExtension {

    private static final def TRAVIS_JOB_NUMBER = "TRAVIS_JOB_NUMBER"
    private static final def TRAVIS_BUILD_ID = "TRAVIS_BUILD_ID"

    def buildID = System.getenv(TRAVIS_BUILD_ID)
    def jobNumber = System.getenv(TRAVIS_JOB_NUMBER)
    def ghToken
    def pollingInterval = 5000

}
