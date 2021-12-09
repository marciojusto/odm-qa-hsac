pipeline {

    agent any

    stages {

        stage("build") {

            steps {
                echo 'building the application...'
                git 'https://github.com/marciojusto/odm-qa-hsac.git'
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