jobName = "Trainer-create-repository"

pipeline {
    agent { node { label 'master' } }
    parameters {
        string(defaultValue: '',
                description: 'e.g module_<n>',
                name: 'Project')
        string(defaultValue: '',
                description: 'e.g. https://github.com/epmd-edp/java-maven-springboot.git',
                name: 'Git_Repository')
        choice(choices: 'Java\nJavaScript', name: 'Language')
    }
    stages{
        stage('ADD APPLICATION'){
            steps{
                script {
                    wrap([$class: 'BuildUser']) {
                        jobUserName = "${BUILD_USER}"
                    }
                    def userName = jobUserName
                    build job: "$jobName",
                            parameters: [string(name: 'Student', value: "$userName"),
                                         string(name: 'Project', value: "${params.Project}"),
                                         string(name: 'Git_Repository', value: "${params.Git_Repository}"),
                                         string(name: 'Language', value: "${params.Language}")]
                }
            }
        }
    }
}
