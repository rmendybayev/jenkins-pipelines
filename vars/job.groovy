import groovy.json.JsonSlurper
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

def getUserAndUpstream() {
    def userCause = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)
    def upstreamCause = currentBuild.rawBuild.getCause(hudson.model.Cause$UpstreamCause)
    def user = userCause?.userId
    def upstream = upstreamCause?.upstreamBuild
    return [user, upstream]
}