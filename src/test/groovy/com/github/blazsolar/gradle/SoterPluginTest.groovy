package com.github.blazsolar.gradle
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail
/**
 * Created by blazsolar on 18/04/15.
 */
class SoterPluginTest {

    @Test
    public void testNoAndroidPulgin() throws Exception {

        try {
            Project project = ProjectBuilder.builder().build()
            project.apply plugin: 'com.github.blazsolar.soter'
            fail "Should require android plugin"
        } catch (PluginApplicationException e) {
            assertTrue e.cause instanceof IllegalStateException
        }


    }

}
