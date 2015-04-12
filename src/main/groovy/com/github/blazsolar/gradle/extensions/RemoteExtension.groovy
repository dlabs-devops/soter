package com.github.blazsolar.gradle.extensions

import org.gradle.api.tasks.Input

/**
 * Created by blazsolar on 10/11/14.
 */
class RemoteExtension {

    @Input boolean pushToRemote = false;
    @Input String gitRoot;
    @Input String remote
    @Input String branch
    @Input String username;
    @Input String password;

}
