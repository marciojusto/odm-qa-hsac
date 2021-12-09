pipeline {

    agent {
        docker {
            image 'maven:3.8.4-jdk-8'
            label 'docker'
        }
    }

    stages {

        stage("build") {

            steps {
                echo 'building the application...'
                sh 'mvn -version'
            }
        }

        stage("test") {

            steps {
                echo 'testing the application...'
                //sh   './buildChrome.sh'
            }
        }

        stage("deploy") {

            steps {
                echo 'deploying the application...'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}