#!/usr/bin/env groovy

def init(String app_name, Boolean jenkins_job_dryrun, String dest_host, String ssh_key_name = 'NULL') {
    currentBuild.displayName = app_name
    def user = job.getUserAndUpstream()[0]
    env.OWNER = (user != null) ? user.split("@")[0].replaceAll(' ', '_') : 'jenkins'
    currentBuild.description = "Runned by: ${OWNER}"

    env.WORK_DIR = "/opt/${app_name}"
    env.LOG_FILE = "${WORK_DIR}/debug.log"
    env.PIDFILE = "${WORK_DIR}/proc.pid"

    env.CMD = ''

    env.GIT_SUBDIR = 'git'
    env.INFO_FILE = 'run.info'
    env.RUN_SCRIPT = 'run.sh'
    env.JAR_URL = ''
    info.create()

    env.JOB_DRY_RUN = jenkins_job_dryrun.toString()
    info.append('JOB_DRY_RUN', "${JOB_DRY_RUN}")

    env.SSH_CREDS = SSH_CREDS_ANALYTIC_ID
    env.SSH_USERNAME = "hadoop"
    env.DESTINATION_HOST = dest_host.toString()
    env.SSH_CMD_PREFIX = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -l ${SSH_USERNAME} ${DESTINATION_HOST} "
    env.SCP_CMD_PREFIX = "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ".toString()
}
