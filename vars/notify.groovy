#!/usr/bin/env groovy

// Module, which uses 'email-ext' plugin to send emails, which
// configured in: Manage Jenkins -> Configure System

def send(GString notify_meta = "Job type: ${env.JOB_TYPE}", String resource = "", String recipients = "") {
    env.JOB_INFO = "<p>Check console output at <a href='${env.BUILD_URL}console'>${env.JOB_NAME} #${env.BUILD_NUMBER}</a> to view the results.</p>"
    env.SIGNATURE = '<p><i><small>You are receiving this message because the pipeline is configured \
        to send email to this address for user-defined pipeline state changes.</small></i></p>'

    GString subject = "${currentBuild.currentResult}: ${resource} - ${env.JOB_TYPE}#${env.BUILD_NUMBER}"
    String color = (currentBuild.currentResult == 'SUCCESS') ? 'green' : 'red'
    GString message = """
		<p><b style="color:${color}">${currentBuild.currentResult}</b></p>
        ${notify_meta} <br /> ${env.JOB_INFO} <br /> ${env.SIGNATURE}"""

    if (JOB_TYPE == 'BuildAndDeploy') {
        emailSend(subject, message, BUILD_EMAIL_RECIPIENTS)
    } else if (JOB_TYPE == 'BuildAndPush') {
        emailSend(subject, message, BUILD_EMAIL_RECIPIENTS)
    } else if (JOB_TYPE == 'ReleaseApp') {
        emailSend(subject, message, BUILD_EMAIL_RECIPIENTS)
    } else if (JOB_TYPE == 'RunApplication') {
        emailSend(subject, message, recipients)
    } else if (JOB_TYPE == 'SnapshotAndRestore') {
        emailSend(subject, message, recipients)
    } else if (JOB_TYPE == 'DeployLocalApp') {
        emailSend(subject, message, recipients)
    } else if (JOB_TYPE == 'DeployKibanaPlugin') {
        emailSend(subject, message, recipients)
    } else if (JOB_TYPE.contains('-test')) {
        echo "Disabled notifications"
        //emailSend(subject, message, "Oleh_Moskovych@epam.com")
    }
}

def emailSend(GString subject, GString message, String recipients, sender = EMAIL_SENDER) {
    echo "${subject} : ${message} : ${recipients}"
}