#!/usr/bin/env groovy

// Just for generate info file with basic information about build

import java.util.Date

def create() {
    sh """echo -e "# \$(date)" > ${WORKSPACE}/${INFO_FILE}"""
    String timestamp = new Date().getTime().toString()
    sh """echo -e "BUILD_TIMESTAMP: ${timestamp}" >> ${WORKSPACE}/${INFO_FILE}"""
    sh """echo 'BUILD_URL: ${env.BUILD_URL}' >> ${WORKSPACE}/${INFO_FILE}"""
    sh """echo 'BUILD_NUMBER: ${env.BUILD_NUMBER}' >> ${WORKSPACE}/${INFO_FILE}"""
    sh """echo 'JOB_TYPE: ${env.JOB_TYPE}' >> ${WORKSPACE}/${INFO_FILE}"""
}

def append(String key, GString value) {
    sh """echo '${key}: ${value}' >> ${WORKSPACE}/${INFO_FILE}"""
}
