pipeline {
    agent {
        node {
            label 'master'
            //customWorkspace "/var/lib/jenkins/jobs/"
        }
    }
    triggers {
        cron('35 10 * * 5')
        // On friday at 10:35
    }

    stages {
        stage('Clean') {
            steps {
                script {
                    sh '''
                    find /var/lib/jenkins/jobs/*-cd-pipeline  -name workspace -type d
                    find /var/lib/jenkins/jobs/*-cd-pipeline/jobs  -name workspace -type d -prune -exec rm -rf '{}' '+'
                    '''
                }
            }
        }
    }
}
