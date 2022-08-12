pipeline {
    agent any 
    
    tools {
        maven "Maven"
    }
    
    environment {
        GIT_REPOSITORY = "https://github.com/tranvannhan1911/Jenkins-K8s-CICD-Hiitfigure.git"
        DOCKER_REPOSITORY = "tranvannhan1911/hiitfigure"
        DOCKER_TAG = "${DOCKER_REPOSITORY}:${ENV}-${BUILD_NUMBER}"
        DOCKERHUB_CREDENTIAL = credentials('dockerhub')
    }
    
    stages {
        stage("Checkout"){
            steps {
                // sh "echo ${GIT_BRANCH}"
                // sh "echo ${env}" 
                
                checkout([$class: 'GitSCM', 
                    branches: [[name: '*/main']],
                    extensions: [[$class: 'CleanCheckout']], // clean workspace after checkout
                    userRemoteConfigs: [[url: GIT_REPOSITORY]]])
                    
            }
        }
        
        stage("Build"){
            steps {
                sh 'mvn install'
            }
        }
        
        stage("Containerize"){
            steps {
                sh 'docker build -t $DOCKER_TAG .'
            }
        }
        
        stage("Push image to docker hub"){
            steps {
                sh 'echo $DOCKERHUB_CREDENTIAL_PSW | docker login -u $DOCKERHUB_CREDENTIAL_USR --password-stdin'
                sh 'docker push $DOCKER_TAG'
            }
        }
    }
}