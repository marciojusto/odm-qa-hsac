node {

    stage("Main Build") {

        docker.image('maven:3.8.4-jdk-8').inside {

            stage("build") {
                echo 'building the application...'
                sh 'mvn clean compile'
                sh 'docker --version'
            }

            stage("test") {
                echo 'testing the application...'
            }

            stage("deploy") {
                echo 'deploying the application...'
            }
        }
    }
}