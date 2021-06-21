#!/bin/sh

## This is an example script that runs wpi-many.sh with appropriate
## arguments and environment variables, using a custom typechecker.
## If you want to use this script, you'll have to change
## environment variables and paths below.  Note that this script is
## intended for use with a custom typechecker (i.e., a typechecker
## that is not in the main Checker Framework distribution).  If your
## typechecker is in the main Checker Framework distribution, you
## should use wpi-many.sh or wpi.sh directly rather than making a copy
## of this script.

## Change these if necessary.

export JAVA11_HOME=$(/usr/libexec/java_home -v 11.0.9)
export JAVA8_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_144.jdk/Contents/Home

## Everyone must change these.

export PARENTDIR=${HOME}/nonempty-checker
export CHECKERFRAMEWORK=${PARENTDIR}/checker-framework
checker=org.checkerframework.checker.iteration.IterationChecker
checkername=iteration
repolist=iteration-verification.list
workingdir=$(pwd)

# the stub files for the checker being used

custom_stubs=''

# The checker classpath.  Paste in the result of running ./gradlew -q
# printClasspath in the subproject of your custom checker with the
# checker implementation.  If your custom checker does not define such
# a task, you can define it:
#
# task printClasspath {
#     doLast {
#         println sourceSets.main.runtimeClasspath.asPath
#     }
# }
#
checker_classpath=''

# The qualifier classpath. Usually, this is a subset of
# checker_classpath that contains just two elements:
#  * the qual jar for your checker, and
#  * the version of checker-qual.jar that your qualifiers depend on.
#
# Like checker_classpath, this is usually generated using the printClasspath
# task in the qualifier subproject of your custom checker, if it has one.
#
qual_classpath=''

## Optionally change these.

export ANDROID_HOME=${PARENTDIR}/android_home
timeout=3600 # 60 minutes

## There is no need to make changes below this point.

export JAVA_HOME=${JAVA11_HOME}
repolistbase=$(basename "$repolist")

## Code starts here.

rm -rf "${checkername}-${repolistbase}-results"

bash "${CHECKERFRAMEWORK}"/checker/bin/wpi-many.sh -o "${workingdir}/${checkername}-${repolistbase}" \
     -i "${PARENTDIR}/${repolist}" \
     -t ${timeout} \
     -- \
     --checker "${checker}"
#     --quals "${qual_classpath}" \
#     --lib "${checker_classpath}" \
#     --stubs "${custom_stubs}"
