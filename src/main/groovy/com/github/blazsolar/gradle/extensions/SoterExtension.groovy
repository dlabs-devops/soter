package com.github.blazsolar.gradle.extensions
import org.gradle.api.Action
import org.gradle.internal.reflect.Instantiator
/**
 * Created by blazsolar on 02/09/14.
 */
class SoterExtension {

    final CheckstyleExtension checkstyle
    final FindbugsExtension findbugs
    final PMDExtension pmd
    final LogsExtension logs
    final TestsExtension tests
    final DocsExtension docs
    final CodeCoverageExtension codeCoverage
    final PublishExtension publish
    final NotificationsExtension notifications
    final AmazonExtension amazon
    final RemoteExtension remote
    final AfterAllExtension afterAll

    SoterExtension(Instantiator instantiator) {
        checkstyle = instantiator.newInstance(CheckstyleExtension)
        findbugs = instantiator.newInstance(FindbugsExtension)
        pmd = instantiator.newInstance(PMDExtension)
        logs = instantiator.newInstance(LogsExtension)
        tests = instantiator.newInstance(TestsExtension)
        docs = instantiator.newInstance(DocsExtension)
        codeCoverage = instantiator.newInstance(CodeCoverageExtension)
        publish = instantiator.newInstance(PublishExtension, instantiator)
        notifications = instantiator.newInstance(NotificationsExtension, instantiator)
        amazon = instantiator.newInstance(AmazonExtension)
        remote = instantiator.newInstance(RemoteExtension)
        afterAll = instantiator.newInstance(AfterAllExtension)
    }

    void checkstyle(Action<CheckstyleExtension> action) {
        action.execute(checkstyle)
    }

    void findbugs(Action<FindbugsExtension> action) {
        action.execute(findbugs)
    }

    void pmd(Action<PMDExtension> action) {
        action.execute(pmd)
    }

    void logs(Action<LogsExtension> action) {
        action.execute(logs)
    }

    void tests(Action<TestsExtension> action) {
        action.execute(tests)
    }

    void docs(Action<DocsExtension> action) {
        action.execute(docs)
    }

    void codeCoverage(Action<CodeCoverageExtension> action) {
        action.execute(codeCoverage)
    }

    void publish(Action<PublishExtension> action) {
        action.execute(publish)
    }

    void notifications(Action<NotificationsExtension> action) {
        action.execute(notifications)
    }

    void amazon(Action<AmazonExtension> action) {
        action.execute(amazon)
    }

    void remote(Action<RemoteExtension> action) {
        action.execute(remote)
    }

    void afterAll(Action<AfterAllExtension> action) {
        action.execute(afterAll)
    }

}
