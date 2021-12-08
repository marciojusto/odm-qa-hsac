#!/usr/bin/env bash

SELENIUM_VERSION=${2:-4.1.0}

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
IMAGE=hsac/fitnesse-fixtures-test-jre8-chrome:latest

docker pull selenium/standalone-chrome:${SELENIUM_VERSION}

docker build --build-arg SELENIUM_VERSION=${SELENIUM_VERSION} -t ${IMAGE} chrome

docker run --rm --user root \
    -v ${BASEDIR}/target/failsafe-reports:/fitnesse/target/failsafe-reports \
    -v ${BASEDIR}/target/fitnesse-results/chrome:/fitnesse/target/fitnesse-results \
    -v ${BASEDIR}/target/fitnesse-results/chrome-rerun:/fitnesse/target/fitnesse-rerun-results \
    -v ${BASEDIR}/target/selenium-log:/fitnesse/target/selenium-log \
    -v ${BASEDIR}/wiki/FitNesseRoot:/fitnesse/wiki/FitNesseRoot \
    -e RE_RUN_FAILED=false \
    ${IMAGE} \
    -DfitnesseSuiteToRun=AcceptanceTests.FrontEndTests.GoogleTest
