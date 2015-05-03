package com.github.blazsolar.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class TestsExtension {

    @Input boolean uploadAndroidTestReports = false;
    @Input boolean uploadUnitTestReports = false;

}
