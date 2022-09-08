pipeline {
    agent any 
    
    tools {
        maven "maven3.8"
    }
    
    // parameters { choice(name: 'BRANCH', choices: ['main', 'dev'], description: 'Environment') }
    
     
    environment {
        GIT_REPOSITORY = "https://github.com/tranvannhan1911/Jenkins-K8s-CICD-Hiitfigure.git"
        DOCKER_REPOSITORY = "tranvannhan1911/hiitfigure"
        DOCKERHUB_CREDENTIAL = credentials('dockerhub')
    }
    
    // triggers {
    //     GenericTrigger(
    //         causeString: 'Hiitfigure Trigger',
    //         genericVariables: [
    //         [key: 'BRANCH', value: '$.ref'],
    //         ],
    //         printContributedVariables: false,
    //         printPostContent: false,
    //         regexpFilterText: '$BRANCH',
    //         regexpFilterExpression: 'refs/heads/[main|dev]',
    //         token: 'hiitfigure',
    //     )
    // }
     
    
    stages {
        stage("Checkout"){
            steps {
                script{
                    if(BRANCH.split("/").size() == 1)
                        BRANCH = "refs/heads/"+BRANCH
                    def map = ["refs/heads/main": "prod", "refs/heads/dev": "dev"]
                    ENV = map[BRANCH]
                    
                    checkout([$class: 'GitSCM', 
                        branches: [[name: BRANCH]],
                        extensions: [[$class: 'CleanCheckout']], // clean workspace after checkout
                        userRemoteConfigs: [[url: GIT_REPOSITORY]]])
                }
            }
        }
        
        stage("Maven build"){
            steps {
                script{
                    sh 'mvn clean install -DskipTests'
                    
                    pom = readMavenPom file: "pom.xml"
                    filesByGlob = findFiles(glob: "target/*.jar")
                    artifactPath = filesByGlob[0].path
                    
                    sh "mv ${artifactPath} target/store.jar"
                }
            }
        }
        
        stage("Code analysis with SonarQube"){
            steps{
                script{
                    def scannerHome = tool 'sonarqubescanner4.7';
                    withSonarQubeEnv('sonarqube') {
                      
                        sh """${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=hiitfigure -Dsonar.sources=. -Dsonar.java.binaries=target/classes"""
                    }
                    
                    timeout(time: 10, unit: 'MINUTES'){
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
        
        stage("Build docker image"){
            steps {
                script{
                    env.DOCKER_TAG = ENV+"-"+BUILD_NUMBER
                    env.DOCKER_REPOSITORY_TAG = DOCKER_REPOSITORY+":"+DOCKER_TAG
                    // appImage = docker.build DOCKER_REPOSITORY_TAG
                    sh 'docker build -t $DOCKER_REPOSITORY_TAG .'
                    
                }
                
            }
        }
        
        stage("Push image to docker hub"){
            steps {
                script{
                    // docker.withRegistry('', DOCKERHUB_CREDENTIAL){
                    //     appImage.push(DOCKER_TAG)
                    // }
                    sh 'echo $DOCKERHUB_CREDENTIAL_PSW | docker login -u $DOCKERHUB_CREDENTIAL_USR --password-stdin'
                    sh 'docker push $DOCKER_REPOSITORY_TAG'
                }
            }
        }
        
        stage("Deploy to eks cluster"){
            steps {
                script{
                    withCredentials([file(credentialsId: "kubeconfig-$ENV", variable: 'KUBECONFIG')]) {
                        sh """helm upgrade hiitfigure-stack helm/hiitfigure-chart \
                            --set hiitfigure.image=${DOCKER_REPOSITORY_TAG} \
                            --set hiitfigure.env=${ENV} \
                            --namespace hiitfigure-${ENV}"""
                    }
                }
            }
        }
    }
    post {
		always {
            cleanWs()
			sh "docker rmi $DOCKER_REPOSITORY_TAG"
            echo "cleaned"
		}
	}
}
