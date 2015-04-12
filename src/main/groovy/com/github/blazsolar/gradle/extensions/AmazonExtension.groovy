package com.github.blazsolar.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 02/09/14.
 */
class AmazonExtension {

    @Input boolean enabled = false
    @Input String accessKey
    @Input String secretKey
    @Input String bucket
    @Input String path = ""

}
