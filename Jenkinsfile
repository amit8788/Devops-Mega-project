pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment {
        APP_NAME = "devops-mega-project"
        RELEASE = "1.0.0"
        DOCKER_USER = "amit687"
        DOCKER_PASS = 'dockerhub'
        IMAGE_NAME = "${DOCKER_USER}" + "/" + "${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
        JENKINS_API_TOKEN = credentials("JENKINS_API_TOKEN")

    }
    

    stages {

        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout Code") {
            steps {
                git branch: 'main',
                credentialsId: 'github',
                url: 'https://github.com/amit8788/Devops-Mega-project.git'
            }
        }

        stage("Build Application") {
            steps {
                sh "mvn clean package"
            }
        }

        stage("Run Tests") {
            steps {
                sh "mvn test"
            }
        }

       stage("Sonarqube Analysis") {
            steps {
                script {
                    withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-token'){
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }

        stage("Quality Gate") {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-token'
                }
            }
        }

        stage("Build Docker Image") {
            steps {
                script {
                   docker.withRegistry('',DOCKER_PASS) {
                         docker_image = docker.build "${IMAGE_NAME}"
                }
            }
        }
     }
        stage("Trivy Scan") {
            steps {
                script sh ('docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image amit687/devops-mega-project:latest  --no-progress --scanners vuln  --exit-code 0 --severity HIGH,CRITICAL --format table')
            }
        }

        stage("Push Docker Image") {
            steps {
                script {
                      docker.withRegistry('',DOCKER_PASS) {
                      docker_image.push("${IMAGE_TAG}")
                      docker_image.push('latest')
                    }
                }
            }
        }

        stage("Cleanup Images") {
            steps {
                script {
                    sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker rmi ${IMAGE_NAME}:latest"
                }
            }
        }

        stage("Trigger CD Pipeline") {
            steps {
                script {
                    sh """
                    curl -v -k --user admin:${JENKINS_API_TOKEN} \
                    -X POST \
                    -H 'cache-control: no-cache' \
                    -H 'content-type: application/x-www-form-urlencoded' \
                    --data 'IMAGE_TAG=${IMAGE_TAG}' \
                    'http://16.170.240.185:8080/job/gitops-devops-mega-project/buildWithParameters?token=gitops-token'
                    """
                }
            }
        }
    }

    post {
        success {
            emailext (
                to: 'amitsangale444@gmail.com',
                subject: "SUCCESS: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "Build Successful: ${env.BUILD_URL}",
                mimeType: 'text/html'
            )
        }

        failure {
            emailext (
                to: 'amitsangale444@gmail.com',
                subject: "FAILURE: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "Build Failed: ${env.BUILD_URL}",
                mimeType: 'text/html'
            )
        }
    }
}
