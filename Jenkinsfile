pipeline {

    agent any

    stages {

        stage("build") {

            steps {
                echo 'building the application...'
                sh './mvnw clean compile'
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
}