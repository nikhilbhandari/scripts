/*************************************************
Description: This script needs to put in Groovy 
Postbuild plugin of Jenkins as a Post Build task.

Author			: Nikhil Bhandari
Last Revision	: 1/27/2015
*************************************************/
//PASS
import hudson.model.*
 
void log(msg) {
  manager.listener.logger.println(msg)
}
 
def failRecursivelyUsingCauses(cause) {
     if (cause.class.toString().contains("UpstreamCause")) {
        def projectName = cause.upstreamProject
        def number = cause.upstreamBuild
        upstreamJob = hudson.model.Hudson.instance.getItem(projectName)
        if(upstreamJob) {
             upbuild = upstreamJob.getBuildByNumber(number)
             if(upbuild) {
				 log("Setting to '" + manager.build.result + "' for Project: " + projectName + " | Build # " + number)
                 //upbuild.setResult(hudson.model.Result.UNSTABLE)
                 upbuild.setResult(manager.build.result);
                 upbuild.save()
              
                 // fail other builds
                 for (upCause in cause.upstreamCauses) {
                     failRecursivelyUsingCauses(upCause)
                 }
             }
        } else {
            log("No Upstream job found for " + projectName);
        }
    }
}

 
if(manager.build.result.isWorseOrEqualTo(hudson.model.Result.UNSTABLE)) {
    log("****************************************");
	log("Must mark upstream builds fail/unstable");
    def thr = Thread.currentThread()
    def build = thr.executable
    def c = build.getAction(CauseAction.class).getCauses()
	
	log("Current Build Status: " + manager.build.result);
    for (cause in c) {
        failRecursivelyUsingCauses(cause)
    }
	log("****************************************");
}
else {
    log("Current build status is: Success - Not changing Upstream build status");
}


///////////////////////////////////////

/* 
if(manager.build.result.isWorseOrEqualTo(hudson.model.Result.UNSTABLE)) {
    log("Must fail upstream builds");
    for (cause in manager.build.causes) {
        //failRecursivelyUsingCauses(cause)
       log("cause" + cause.toString());
    }
}
else {
    log("Success - not changing");
}

if(manager.build.result.isWorseOrEqualTo(hudson.model.Result.UNSTABLE)) {
    log("Must fail upstream builds");
def cause = [];
cause = manager.build.getCauses();
  log("cause: " + cause[1]); 
  log("build# " + manager.build.getUpstreamRelationship());
  log("map# " + manager.build.getUpstreamBuilds());

//  log("ID# " + hudson.model.Cause.UpstreamCause.getUpstreamBuild());
}
else {
    log("Success - not changing");
}
*/
