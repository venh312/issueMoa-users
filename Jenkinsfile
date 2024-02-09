pipeline {
    agent any
    
    environment {
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Git Clone
                    git branch: "master", credentialsId: "conf-git", url: "https://github.com/conf312/issueMoa-users.git"
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    // 프로젝트 빌드 & jar 생성
                    sh "chmod 777 gradlew && ./gradlew bootJar"
                }
            }
        }
        stage('Dokcer Image Build') {
            steps {
                script {
                    // 도커 빌드 & 이미지 생성
                    sh "docker build -t issuemoa/users ."
                }
            }
        }
        stage('Dokcer Run Conatiner') {
            steps {
                script {
                    def containerRunning = sh(script: 'docker inspect -f \'{{.State.Running}}\' issuemoa-users 2>/dev/null', returnStatus: true) == 0
                    // 컨테이너가 이미 실행중이면 종료 및 삭제
                    if (containerRunning) {
                        echo 'Container is already running'
                        sh "docker stop issuemoa-users"
                        sh "docker rm issuemoa-users"
                    }
                    
                    sh "docker run -d --name issuemoa-users -p 17070:17070 --network issuemoa issuemoa/users"
                }
            }
        }
    }

    post {
        success {
            script {
                // 성공적인 빌드일 때 실행
                def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()

                slackSend (
                    channel: '#이슈모아', 
                    color: '#0100FF', 
                    message: "😄 이슈모아 Build & Release 성공! \n 💻 ${commitMessage} \n ${env.JOB_NAME},  buildNumber: [${env.BUILD_NUMBER}]"
                )
            }
        }
        
        failure {
            script {
                // 빌드 실패 시 실행
                def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()

                slackSend (
                    channel: '#이슈모아', 
                    color: '#FF0000', 
                    message: "😱 이슈모아 Build & Release 실패! \n 💻 ${commitMessage} \n ${env.JOB_NAME},  buildNumber: [${env.BUILD_NUMBER}]"
                )
            }
        }
    }
}
