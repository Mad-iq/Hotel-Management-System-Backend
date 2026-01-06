pipeline {
    agent any

    stages {

        stage('Checkout Repo') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Mad-iq/Hotel-Management-System-Backend.git'
            }
        }

        stage('Verify Docker') {
            steps {
                bat '''
                docker --version
                docker compose version
                '''
            }
        }

        stage('Docker Compose Deploy') {
            steps {
                bat '''
                docker compose -p hms ps
                docker compose -p hms up -d --no-build
                '''
            }
        }
    }

    post {
        success {
            echo 'Docker deployment successful'
        }
        failure {
            echo 'Docker deployment failed'
        }
    }
}
