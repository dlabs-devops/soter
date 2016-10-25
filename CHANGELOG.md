# Change Log

## Version 1.0.9 *(2016-10-25)*

 * Deprecating plugin. Moved to [https://github.com/kaminomobile/soter](https://github.com/kaminomobile/soter)
 * Removing notification part of plugin.

## Version 1.0.8 *(2016-05-26)*

 * Support for test project fix (by [sschuberth](https://github.com/sschuberth))

## Version 1.0.7 *(2016-05-24)*

 * Adding support for test project (by [sschuberth](https://github.com/sschuberth))

## Version 1.0.6 *(2016-03-01)*

 * Do not explicitly depend on gradle android plugin

## Version 1.0.5 *(2016-02-20)*

 * Ignore failures for PMG and FindBugs (by [sschuberth](https://github.com/sschuberth))

## Version 1.0.4 *(2015-12-21)*

 * XML reports for FindBugs (by [sschuberth](https://github.com/sschuberth))

## Version 1.0.3 *(2015-09-10)*

 * Library support, for real this time :)

## Version 1.0.2 *(2015-09-01)*

 * Support for 1.4.0 android plugin
 * Library support (by [sschuberth](https://github.com/sschuberth))

## Version 1.0.1 *(2015-06-05)*

 * Fixed bug where findbugs and checkstyle reports were not uploaded if task failed.
 * Findbugs now runs on all variants of the app.
 * Fixed bugs where findbugs would not work if app had flavors.
 * Default checkstyle version was set to 6.7 and can now be run against java 8 projects. You can also define which version of checkstyle to use via DSL `soter.checkstyle.toolVersion`
 * Compatible with 1.3.0-beta1 android plugin

## Version 1.0.0 *(2015-05-18)*

 * Changing package name
 * Changing dependencies to reduce plugin size

## Version 0.6.1 *(2015-05-05)*

 * Fixing and adding javadoc task
 * Updating push to git remote task
 * Adding support for uploading mapping file
 * Change binary folder structure for uploaded files

## Version 0.6.0 *(2015-05-03)*

 * Adding support for unit report upload

## Version 0.5.2 *(2015-04-27)*

 * Fixing report path for 1.2.0+ Android gradle plugin

## Version 0.5.1 *(2015-04-14)*

 * Fixing afterAll task

## Version 0.5.0 *(2015-04-12)*

 * Plugin is renamed to Soter
 * Refactor of whole plugin
 * Check tasks can now be executed as standalone tasks
 * Checkstyle task now performs checks on all non test source sets

## Version 0.4.3 *(2015-03-06)*

 * Multiple variants fix

## Version 0.4.2 *(2015-03-06)*

 * Support for deploying multiple variants

## Version 0.4.1 *(2015-02-22)*

 * Master job is not the last job started.

## Version 0.4.0 *(2015-01-12)*

 * Adding support for uploading code coverage
 * Adding support for generating and uploading docs
 * Refactoring after all feature

## Version 0.3.7 *(2014-12-18)*

 * Updating crashlytics deploy

## Version 0.3.6 *(2014-12-12)*

 * Adding support for crashlytics deploy

## Version 0.3.5 *(2014-11-30)*

 * Adding after all task

## Version 0.3.4 *(2014-11-10)*

 * Bug fixes

## Version 0.3.3 *(2014-11-10)*

 * Added option to push to another git remote
 * Added task onDone that handles successful CI build
 * Added task onFailed that handles unsuccessful CI build

## Version 0.3.2 *(2014-10-04)*

 * Abstracting deploy tasks

## Version 0.3.1 *(2014-10-03)*

 * Adding reading rights to uploaded reports

## Version 0.3.0 *(2014-10-02)*

 * Adding HipChat notifications support

## Version 0.2.1 *(2014-10-02)*

 * Extending DSL for findbugs plugin

## Version 0.2.0 *(2014-09-29)*

 * Adding support for uploading apks.
 * Adding support for uploading test reports.
 * Adding support for uploading device logs.

## Version 0.1.1 *(2015-09-23)*

 * Adding amazon s3 support

## Version 0.1.0 *(2014-09-09)*

 * Initial release  
