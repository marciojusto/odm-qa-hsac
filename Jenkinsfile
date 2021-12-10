pipeline {

    agent {
        docker { image 'maven:3.8.4-jdk-8' }
    }

    stages {

        stage("Git Checkout") {

            steps {
                echo 'checkout the application...'
                //git branch: 'dev', url: 'https://github.com/marciojusto/odm-qa-hsac.git'
            }
        }

        stage("build") {

            steps {
                echo 'building the application...'
            }
        }

        stage("test") {

            steps {
                echo 'testing the application...'
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