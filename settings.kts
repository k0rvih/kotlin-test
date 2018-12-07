import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.msBuild

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2018.1"

project {

    buildType(BuildAndPackage)
}

object BuildAndPackage : BuildType({
    name = "Build and Package"

    buildNumberPattern = "1.0.%build.counter%"

    vcs {
        root(AbsoluteId("Kotlin"))
    }

    steps {
        step {
            name = "Restore Nuget Packages"
            type = "jb.nuget.installer"
            param("nuget.path", "%teamcity.tool.NuGet.CommandLine.DEFAULT%")
            param("nuget.updatePackages.mode", "sln")
            param("sln.path", "%SolutionPath%")
        }
        msBuild {
            name = "Build and Package"
            path = "%SolutionPath%"
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V15_0
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
            param("octopus_octopack_publish_api_key", "API-R9ULKGFIHD0PZWRKWQDMMBKYIW0")
            param("octopus_octopack_package_version", "%build.number%")
            param("octopus_run_octopack", "true")
            param("octopus_octopack_publish_package_to_http", "http://172.22.71.250/nuget/packages")
        }
    }
})
