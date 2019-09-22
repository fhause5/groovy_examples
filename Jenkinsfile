def EXECUTOR_AGENT=null

pipeline {
    agent {
        node {
            label '' // Execute the Pipeline on an agent available in the Jenkins environment with the provided label
        }
    }
    parameters {
        choice(choices: "prod\nany", description: '', name: 'ENVIRONMENT')
    }

    stages {

        stage('Determine Agent') {
            steps {
                script {
                    if(params.ENVIRONMENT == 'prod') {
                        EXECUTOR_AGENT="master"
                    } else {
                        EXECUTOR_AGENT="any"
                    }
                }
            }
        }

        stage('STAGE 1') {
            steps {
                node(EXECUTOR_AGENT) {
                    script {
                        sh 'echo 111 > Dockerfile'
                        sh 'echo 222 > Dockerfile'
                    }
                }
            }
        }
    }
}
