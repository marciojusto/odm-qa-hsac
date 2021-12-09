pipeline {

    agent any

    stages {

        stage("build") {

            steps {
                echo 'building the application...'
                withMaven(jdk: 'OpenJDK-8', maven: 'maven') {
                    sh 'mvn clean compile'
                }
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