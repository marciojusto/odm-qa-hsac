pipeline {

    agent {
        docker {
            image 'maven:3.8.4-jdk-8'
            args '-v $HOME/.m2:/root/.m2'
        }
    }

    stage("build") {
        steps {
            echo 'building the application...'
            sh 'mvn clean compile'
            script {
                docker.image('selenium/standalone-chrome:4.1.0').inside {
                    sh 'run hello-world'
                }
            }
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