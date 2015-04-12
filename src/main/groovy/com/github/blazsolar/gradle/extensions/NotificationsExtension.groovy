package com.github.blazsolar.gradle.extensions

import org.gradle.api.Action
import org.gradle.api.tasks.Input
import org.gradle.internal.reflect.Instantiator

/**
 * Created by blazsolar on 02/10/14.
 */
class NotificationsExtension {

    @Input boolean enabled = true;
    final HipChatExtension hipchat

    NotificationsExtension(Instantiator instantiator) {
        hipchat = instantiator.newInstance(HipChatExtension)
    }

    void hipchat(Action<HipChatExtension> action) {
        action.execute(hipchat)
    }
}
