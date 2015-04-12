package com.github.blazsolar.gradle.extensions

import org.gradle.api.Action
import org.gradle.api.tasks.Input
import org.gradle.internal.reflect.Instantiator

/**
 * Created by blazsolar on 03/10/14.
 */
class PublishExtension {

    @Input boolean enabled = true
    final AmazonApkExtension amazon;
    final FabricExtension fabric;

    PublishExtension(Instantiator instantiator) {
        amazon = instantiator.newInstance(AmazonApkExtension)
        fabric = instantiator.newInstance(FabricExtension)
    }

    void amazon(Action<AmazonApkExtension> action) {
        action.execute(amazon)
    }

    void fabric(Action<FabricExtension> action) {
        action.execute(fabric)
    }

}
