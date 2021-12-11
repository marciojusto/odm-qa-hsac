node {

    stage("Main Build") {

        docker.image('maven:3.8.4-jdk-8').run('--version').inside {

            stage("build") {
                echo 'building the application...'
                sh 'mvn clean compile'
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