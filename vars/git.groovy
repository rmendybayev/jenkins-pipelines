#!/usr/bin/env groovy


def pullProject(repo_url, branch_name, git_creds = GIT_BITBUCKET_CREDS_ID) {
    def GIT_VARS = checkout([$class: 'GitSCM',
                             branches: [[name: branch_name]],
                             doGenerateSubmoduleConfigurations: false,
                             extensions: [[$class: 'LocalBranch', localBranch: '**']],
                             ubmoduleCfg: [],
                             userRemoteConfigs: [[credentialsId: git_creds, url: repo_url]]])
    info.append('GIT_URL', "${GIT_VARS.GIT_URL}")
    info.append('GIT_BRANCH', "${GIT_VARS.GIT_BRANCH}")
    info.append('GIT_COMMIT', "${GIT_VARS.GIT_COMMIT}")
}