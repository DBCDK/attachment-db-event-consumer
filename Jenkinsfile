#!groovy

def workerNode = "devel10"

pipeline {
	agent {label workerNode}
	tools {
		// refers to the name set in manage jenkins -> global tool configuration
		maven "Maven 3"
	}
	environment {
		GITLAB_PRIVATE_TOKEN = credentials("metascrum-gitlab-api-token")
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
		stage("docker deploy") {
			agent {
				docker {
					label workerNode
					image "docker.dbc.dk/build-env:latest"
					alwaysPull true
				}
			}
			when {
				expression {
					(currentBuild.result == null || currentBuild.result == 'SUCCESS') && env.BRANCH_NAME == 'master'
				}
			}
			steps {
				script {
					dir("deploy") {
						sh """
                            set-new-version attachment-db-event-consumer.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/attachment-db-event-consumer-deploy DIT-${env.BUILD_NUMBER} -b fbstest
                            
                            set-new-version services/attachment-db-event-consumer-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/dit-gitops-secrets DIT-${env.BUILD_NUMBER} -b master
						"""
					}
				}
			}
		}
	}
}
