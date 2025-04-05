pipeline {
    agent any

    tools {
        maven 'Maven_3.8.6'
    }

    environment {
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/lailsonsantos/car-users-backend.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
                }
            }
        }
    }
}
