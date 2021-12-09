pipeline {

    agent any

    stages {

        stage("build") {

            steps {
                echo 'building the application...'
                withMaven {
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