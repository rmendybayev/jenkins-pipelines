#!/usr/bin/env groovy

List<Object> checkSkipDeploy(String pom, String branch) {
    def pom_file = readMavenPom file: pom
    String version = pom_file.version
    Boolean IS_SNAPSHOT = version.contains('SNAPSHOT')
    if (!IS_SNAPSHOT && (branch == 'master' || branch.matches('hotfix/(.*)'))) {
        return [false, version]
    } else if (IS_SNAPSHOT && branch != 'master') {
        return [false, version]
    } else {
        return [true, version]
    }
}

def compile(String maven, String pom) {
    sh "${maven} -U ${pom} compile"
}

def test(String maven, String pom) {
    sh "${maven} ${pom} test"
}

def packageArtifact(String maven, String pom) {
    sh "${maven} ${pom} -DskipTests package"
}

def deployToNexus(String maven, String pom, String branch) {
    // Deploy to master/develop branch use maven-releases/maven-snapshots repositories
    // For others: custom repository with retention period (~15 days) to reduce disk space usage
    if (branch in ['develop', 'master'] || branch.matches('hotfix/(.*)')) {
        sh "${maven} ${pom} -DskipTests -B deploy"
    } else {
        String group = 'gf-prod-maven-group' // Should be defined in .m2/settings.xml
        GString repository = "${NEXUS_HTTPS_URL}/repository/${NEXUS_CUSTOM_REPOSITORY}/"
        GString alt_repo = """-DaltDeploymentRepository='${group}::default::${repository}'"""
        sh "${maven} ${pom} ${alt_repo} -DskipTests -B deploy"
    }
}

def releasePrepare(String maven, String pom, String tag, String release_version, String development_version,
                   Boolean dry_run, git_creds = GIT_BITBUCKET_CREDS_ID) {
    // NOTICE: This method creates
    // ssh key: /var/lib/jenkins/.ssh/id_rsa
    // for push committed changes into git
    auxiliary.create_ssh_key(git_creds)

    String maven_cmd = """${maven} ${pom} --batch-mode -DupdateBranchVersions=true""".toString()
    maven_cmd += " -DpreparationGoals=package -DpushChanges=true -DautoVersionSubmodules=true"

    if (tag != '') {
        def tag_rev = sh(script: "git rev-parse ${tag} 2> /dev/null | echo \$(< /dev/stdin)",
                returnStdout: true).trim().split(' ')
        if (!tag_rev.contains(tag)) {
            error("Such tag ${tag} already exist in repository. Try another one...")
        } else {
            maven_cmd += " -Dtag=${tag}"
        }
    } else {
        echo "Tag wasn't specified. Use default: v${release_version}..."
        maven_cmd += " -Dtag=v${release_version}"
    }
    if (release_version != '') {
        maven_cmd += " -DreleaseVersion=${release_version}"
    }
    if (development_version != '') {
        maven_cmd += " -DdevelopmentVersion=${development_version}"
    }
    maven_cmd += " release:prepare release:clean"

    if (!dry_run) {
        sh "${maven_cmd}"
    } else {
        echo "DRY RUN ENABLED. JUST PRINT COMMAND."
        echo "Maven command: ${maven_cmd}"
    }
}