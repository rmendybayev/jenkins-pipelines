// Library should be defined in Global Pipeline Libraries
library 'jenkins-pipelines@master'

String JENKINS_NODE = 'master'
String PROJECT, POM_FILE, VERSION
GString NOTIFY_META
Boolean SKIP_DEPLOY

pipeline {
    // To avoid running in different executors
    agent { label JENKINS_NODE }

    parameters {
        string(defaultValue: "pom.xml", description: '', name: 'pom_file_path')
        string(defaultValue: "", description: '', name: 'repo_url')
        string(defaultValue: "develop", description: '', name: 'branch_name')
        booleanParam(defaultValue: true, description: '', name: 'run_tests')
        booleanParam(defaultValue: false, description: '', name: 'nexus_deploy')
    }

    options {
        // Timeout for all pipeline. Sometimes can be unrecognized,
        // See: https://issues.jenkins-ci.org/browse/JENKINS-48556
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5000'))
    }

    environment {
        MAVEN_TOOL = 'mvn' // Or /usr/local/maven/bin/mvn
        JOB_TYPE = 'BuildAndDeploy'
    }


    stages() {
        stage("Init") {
            steps {
                script {
                    PROJECT = params.repo_url.split('/').last().split('\\.')[0].toString()
                    variables.init(PROJECT, false, '', 'analytic')
                    NOTIFY_META = """<p>Project: ${PROJECT}</p> \
                                    <p>Branch: ${params.branch_name}</p>
                                    <p>POM: ${params.pom_file_path}</p>
                                    <p>Tests enabled: ${params.run_tests}</p>
                                    <p>Upload to Nexus: ${params.nexus_deploy}</p>
                                    <p>Runned by: ${OWNER}"""
                    POM_FILE = "-f ${params.pom_file_path}".toString()
                }
            }
            post {
                failure {
                    script {
                        NOTIFY_META += "<p>Stage: init</p>"
                        notify.send(NOTIFY_META, PROJECT)
                    }
                }
            }
        }
        stage("Pull") {
            steps{
                script {
                    git.pullProject(params.repo_url, params.branch_name)
                    // Release versions (like: 1.0) allowed to deploy only from 'master' branch
                    // In others should be snapshot version, like: 1.0-SNAPSHOT
                    (SKIP_DEPLOY, VERSION) = maven.checkSkipDeploy(params.pom_file_path, params.branch_name)
                    def (GIT_COMMIT, GIT_AUTHOR, GIT_DATE, GIT_COMMENT) = ['', '', '', '']
                    dir("${env.GIT_SUBDIR}") {
                        GIT_COMMIT = sh (script: "git --no-pager show -s --format='%H'", returnStdout: true).trim()
                        GIT_AUTHOR = sh (script: "git --no-pager show -s --format='%an <%ae>'", returnStdout: true).trim()
                        GIT_DATE = sh (script: "git --no-pager show -s --format='%aD'", returnStdout: true).trim()
                        GIT_COMMENT = sh (script: "git --no-pager show -s --format='%s'", returnStdout: true).trim()
                    }
                    NOTIFY_META += """<p>Commit: ${GIT_COMMIT}</p>
                                      <p>Author: ${GIT_AUTHOR}</p>
                                      <p>Date: ${GIT_DATE}</p>
                                      <p>Comment: ${GIT_COMMENT}"""
                    if (SKIP_DEPLOY) {
                        NOTIFY_META += """<br /><p><i><b>NOTE:</b> \
                                        Deploy to Nexus skipped. Version: ${VERSION}<br /> \
                                        Release version will deploy only from master branch</i></p>"""
                    }
                }
            }
            post {
                failure {
                    script {
                        NOTIFY_META += "<p>Stage: pull</p>"
                        notify.send(NOTIFY_META, PROJECT)
                    }
                }
            }
        }

    }

    post {
        failure {
            script {
                echo "Global action if job failure"
            }
        }
        success {
            script {
                echo "Global action if job success"
                notify.send(NOTIFY_META, PROJECT)
            }
        }
        always {
            script {
                echo "Global action for each jobs"
                cleanWs()
            }
        }
    }
}
