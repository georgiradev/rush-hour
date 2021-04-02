pipeline {
    agent any
    triggers {
        pollSCM 'H/15 * * * *'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}