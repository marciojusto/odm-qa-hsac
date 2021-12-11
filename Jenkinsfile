node {

    stage("Main Build") {

        docker.image('maven:3.8.4-jdk-8').inside {

            stage("build") {
                echo 'building the application...'
                sh 'mvn clean compile'
            }
        }

        docker.image('selenium/standalone-chrome:4.1.0').run('--version') {
            stage("test") {
                echo 'testing the application...'
            }
        }
    }
}