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
        IMAGE_NAME = "${DOCKER_USER}/${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
        JENKINS_API_TOKEN = credentials("JENKINS_API_TOKEN")
    }

    stages {

        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout from SCM") {
            steps {
                git branch: 'main',
                credentialsId: 'github',
                url: 'https://github.com/amit8788/Devops-Mega-project.git'
            }
        }

        stage("Build the application") {
            steps {
                sh "mvn clean package"
            }
        }

        stage("Test the application") {
            steps {
                sh "mvn test"
            }
        }

        stage("Build Docker Image") {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                }
            }
        }

stage("Trivy Scan") {
    steps {
        script {
            sh """
            docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            aquasec/trivy:0.49.1 \
            image ${IMAGE_NAME}:${IMAGE_TAG} \
            --no-progress \
            --scanners vuln \
            --exit-code 0 \
            --severity HIGH,CRITICAL \
            --format table
            """
        }
    }
}
        stage("Push Docker Image") {
            steps {
                script {
                    docker.withRegistry('', DOCKER_PASS) {
                        def dockerImage = docker.image("${IMAGE_NAME}:${IMAGE_TAG}")
                        dockerImage.push()
                        dockerImage.push("latest")
                    }
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
                    'http://jenkins.sadhix.com:8080/job/Gitops-devops-mega-project/buildWithParameters?token=gitops-token'
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
                body: "Build Successful. Check: ${env.BUILD_URL}",
                mimeType: 'text/html'
            )
        }

        failure {
            emailext (
                to: 'amitsangale444@gmail.com',
                subject: "FAILURE: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "Build Failed. Check: ${env.BUILD_URL}",
                mimeType: 'text/html'
            )
        }
    }
}
