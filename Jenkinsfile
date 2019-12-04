#!groovy

def workerNode = "devel9"

pipeline {
	agent {label workerNode}
	tools {
		// refers to the name set in manage jenkins -> global tool configuration
		maven "Maven 3"
	}
	environment {
		MARATHON_TOKEN = credentials("METASCRUM_MARATHON_TOKEN")
	}
	triggers {
		pollSCM("H/03 * * * *")
        upstream(upstreamProjects: "Docker-payara5-bump-trigger",
            threshold: hudson.model.Result.SUCCESS)
	}
	options {
		timestamps()
	}
	stages {
		stage("clear workspace") {
			steps {
				deleteDir()
				checkout scm
			}
		}
		stage("verify") {
			steps {
				sh "mvn verify pmd:pmd javadoc:aggregate"
				junit "target/**/TEST-*.xml"
			}
		}
		stage("warnings") {
			agent {label workerNode}
			steps {
				warnings consoleParsers: [
					[parserName: "Java Compiler (javac)"],
					[parserName: "JavaDoc Tool"]
				],
					unstableTotalAll: "0",
					failedTotalAll: "0"
			}
		}
		stage("pmd") {
			agent {label workerNode}
			steps {
				step([$class: 'hudson.plugins.pmd.PmdPublisher',
					  pattern: '**/target/pmd.xml',
					  unstableTotalAll: "1",
					  failedTotalAll: "1"])
			}
		}
		stage("docker build") {
			steps {
				script {
					if (env.BRANCH_NAME == 'master') {
						imageTag = "DIT-${env.BUILD_NUMBER}"
					} else {
						imageTag = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
					}
					def image = docker.build("docker-io.dbc.dk/attachment-db-event-consumer:${imageTag}",
						"--pull --no-cache .")
					image.push()
				}
			}
		}
	}
}
