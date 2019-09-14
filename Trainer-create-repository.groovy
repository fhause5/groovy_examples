import groovy.json.*
import groovy.json.JsonBuilder
pipeline {
    agent { node { label 'master' } }
    parameters {
        string(defaultValue: '',
                description: 'e.g. name_surname@epam.com, name_surname@epam.com',
                name: 'Student')
        string(defaultValue: '',
                description: 'e.g module_1,module_<n> ',
                name: 'Project')
        string(defaultValue: '',
                description: 'In case public repository e.g. https://github.com/epmd-edp/java-maven-springboot.git ' +
                        '\n In case internal repository add ssh link e.g. git@git.epam.com:epmd-edp/examples/basic/edp-springboot-helloworld.git',
                name: 'Git_Repository')
        choice(choices: 'Java\nJavaScript',description: '', name: 'Language')
    }
    stages {
        stage('CREATE GERRIT PROJECT') {
            steps {
                script {
                    def listStudent = params.Student.replaceAll(' ','').split(',')
                    def listProject = params.Project.replaceAll(' ','').split(',')
                    for (int i = 0; i < listProject.size(); ++i) {
                        for (int n = 0; n < listStudent.size(); ++n) {
                            def userName = listStudent[n] -"@epam.com"
                            def splitedUserName = userName.replaceAll('_',' ').split(' ')

                            def userNameUperCase = splitedUserName[0].capitalize()
                            def userSurnameUperCase = splitedUserName[1].capitalize()
                            echo "Student name: ${userNameUperCase}, Surname ${userSurnameUperCase}"
                            def projectName = userNameUperCase.toLowerCase() + '_' + userSurnameUperCase.toLowerCase() + '_' + listProject[i]
                            projectGerrit += projectName + ' '
                            echo "[JENKINS][INFO] Project ${projectName} will be created for student ${listStudent[n]}"
                            project(userNameUperCase, userSurnameUperCase, projectName,params.Git_Repository)
                        }
                    }
                }
            }
        }
    }
}

def createListView(codebaseName, branchName) {
    listView("${codebaseName}/${branchName}") {
        if (branchName.toLowerCase() == "releases") {
            jobFilters {
                regex {
                    matchType(MatchType.INCLUDE_MATCHED)
                    matchValue(RegexMatchValue.NAME)
                    regex("^Create-release.*")
                }
            }
        } else {
            jobFilters {
                regex {
                    matchType(MatchType.INCLUDE_MATCHED)
                    matchValue(RegexMatchValue.NAME)
                    regex("^${branchName}-(Code-review|Build).*")
                }
            }
        }
        columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }
    }
}


/*
gerritHost = "gerrit.epm-rdru-edp-cicd.svc"
gerritPort = "30804"
openShiftCLuster = "https://master.demo.edp-epam.com"
openShiftNameSapce = "epm-rdru-edp"
projectGerrit = ""
jobProvision = "Job-provisioning"
textCodeReviewGroovyFile = "@Library(['edp-library-stages', 'edp-library-pipelines']) _\n\nCodeReview()"

pipeline {
    agent { node { label 'master' } }
    parameters {
        string(defaultValue: '',
                description: 'e.g. name_surname@epam.com, name_surname@epam.com',
                name: 'Student')
        string(defaultValue: '',
                description: 'e.g module_1,module_<n> ',
                name: 'Project')
        string(defaultValue: '',
                description: 'In case public repository e.g. https://github.com/epmd-edp/java-maven-springboot.git ' +
                        '\n In case internal repository add ssh link e.g. git@git.epam.com:epmd-edp/examples/basic/edp-springboot-helloworld.git',
                name: 'Git_Repository')
        choice(choices: 'Java\nJavaScript',description: '', name: 'Language')
    }
    stages {
        stage('CREATE GERRIT PROJECT') {
            steps {
                script {
                    def listStudent = params.Student.replaceAll(' ','').split(',')
                    def listProject = params.Project.replaceAll(' ','').split(',')
                    for (int i = 0; i < listProject.size(); ++i) {
                        for (int n = 0; n < listStudent.size(); ++n) {
                            def userName = listStudent[n] -"@epam.com"
                            def splitedUserName = userName.replaceAll('_',' ').split(' ')

                            def userNameUperCase = splitedUserName[0].capitalize()
                            def userSurnameUperCase = splitedUserName[1].capitalize()
                            echo "Student name: ${userNameUperCase}, Surname ${userSurnameUperCase}"
                            def projectName = userNameUperCase.toLowerCase() + '_' + userSurnameUperCase.toLowerCase() + '_' + listProject[i]
                            projectGerrit += projectName + ' '
                            echo "[JENKINS][INFO] Project ${projectName} will be created for student ${listStudent[n]}"
                            project(userNameUperCase, userSurnameUperCase, projectName,params.Git_Repository)
                        }
                    }
                }
            }
        }
        stage('COPY TO GERRIT') {
            steps {
                script {
                    def projectsList = projectGerrit.split(' ')
                    if (params.Git_Repository) {
                        for (int z = 0; z < projectsList.size(); ++z) {
                            git_clone(params.Git_Repository, projectsList[z])
                        }
                    }else {
                        echo """[JENKINS][INFO] Step "COPY TO GERRIT" skipped"""
                    }
                }
            }
        }
        stage('RECORD IN PROJECT CM') {
            steps {
                script {
                    def projectsList = projectGerrit.split(' ')
                    for (int z = 0; z < projectsList.size(); ++z) {
                        openShiftCM(openShiftCLuster, openShiftNameSapce, projectsList[z],params.Language)
                    }
                }
            }
        }
        stage ('ADD PIPELINES') {
            steps {
                script {
                    trigerJobProvision(jobProvision)
                }
            }
        }
    }
}

def project(studentName,studentSurname,projectName,gitRepo) {

    def parentProject = "All-Projects"
    def studentMail = studentName.toLowerCase() + '_' + studentSurname.toLowerCase() + '@epam.com'
    def gerritGroup = 'user/' + studentName + ' ' + studentSurname + ' ' + '(' + studentMail + ')'
    def sshUser = "jenkins"
    //file parameters
    def textGroups = """# UUID\tGroup Name\n#\nuser:$studentMail\t$gerritGroup"""
    def textProjectConfig = """[access]\n\tinheritFrom = All-Proj
ects\n[access "refs/*"]\n\tread = group $gerritGroup\n[access "refs/heads/*"]\n\tsubmit = group $gerritGroup"""
    def groupsFile = "groups"
    def projectConfigFile = "project.config"

    if (gitRepo == "") {
        sh """ssh -p $gerritPort $gerritHost gerrit create-project  $projectName """
    }else{
        sh """ssh -p $gerritPort $gerritHost gerrit create-project $projectName """
    }//     add remote repo which was created before
    sh """
        rm -rf $projectName
        mkdir $projectName
        cd $projectName 
        git init 
        git config user.name  $sshUser
        git config user.email $sshUser@example.com
        git remote add origin ssh://$sshUser@$gerritHost:$gerritPort/$projectName
        
    """
    dir("./$projectName") {
        addCodeReviewFile(textCodeReviewGroovyFile, "Add code-review to empty project")
    }
    sh """
        cd $projectName    
        git push origin refs/heads/*:refs/heads/* refs/tags/*:refs/tags/*
        git fetch origin refs/meta/config:refs/remotes/origin/meta/config 
        git checkout meta/config
    """
    //create and push groupsfile
    fillFileWithText(textGroups,groupsFile,projectName)
    def groupsCommit = "Add groups file to project"
    gerritPushconfig(groupsFile,groupsCommit,projectName)

    // creat and push project.config
    fillFileWithText(textProjectConfig,projectConfigFile,projectName)
    def configCommit = "Add project.config to project"
    gerritPushconfig(projectConfigFile,configCommit,projectName)

}

def fillFileWithText(text,file,folder){
    sh """
        cat > ./$folder/$file << EOF
$text
EOF
"""
}

def gerritPushconfig(file,commit,project){
    sh """
        cd $project
        git add $file
        git commit -a -m "$commit"
        git push origin meta/config:meta/config
    """
}

def git_clone(gitRepo,gerritProject){
    echo "[JENKINS][INFO] Git repo:$gitRepo Will be added in project: $gerritProject"
    git credentialsId: 'auto_epmc', url: "$gitRepo"

        sh """git remote set-url origin \
        ssh://jenkins@$gerritHost:$gerritPort/$gerritProject
        git config user.name  jenkins 
        git config user.email jenkins@example.com
        git pull origin master
       git push origin refs/heads/*:refs/heads/* refs/tags/*:refs/tags/*
       rm -rf *
   """
    }


def openShiftCM(clusterName,namespaceName,name,projectLang) {
    openshift.withCluster(clusterName) {
        openshift.withProject(namespaceName) {

            def json = sh( script:"oc get configmaps project-settings -o json", returnStdout: true)
            def jsonSlurper = new groovy.json.JsonSlurperClassic().parseText(json)

            def newApplicationMap = [:]
            newApplicationMap['name'] = name
            if (projectLang == "Java") {
                newApplicationMap['build_tool'] = "Maven"
                newApplicationMap['framework'] = "SpringBoot"
                newApplicationMap['language'] = "Java"
            }else{
                newApplicationMap['build_tool'] = "NPM"
                newApplicationMap['framework'] = "React"
                newApplicationMap['language'] = "JavaScript"
            }

            applicationsJson = new groovy.json.JsonSlurperClassic().parseText(jsonSlurper.data.'app.settings.json')
            println applicationsJson.getClass().getName()
            def addNewvValuToAppJson = applicationsJson << newApplicationMap
            def newJsonApp = JsonOutput.toJson(addNewvValuToAppJson)

            def appNew = ['app.settings.json':"$newJsonApp"] // out is value with new string

            def appJson = jsonSlurper.data << appNew
            def dataJson = ['data':appJson]
            def newJson = jsonSlurper << dataJson

            def jsonFile = new File('/tmp/new_json.json').write(new JsonBuilder(newJson).toPrettyString())
            sh "oc replace cm project-settings  -f /tmp/new_json.json"
            sh "rm -rf /tmp/new_json.json"

        }
    }
}

def addCodeReviewFile(CodeReviewGroovyFile, commitMessage){
    sh """
        cat > code-review.groovy << EOF
$CodeReviewG
roovyFile
EOF
        git add code-review.groovy
        git commit -a -m "$commitMessage"
       """
}

def trigerJobProvision(job){
    build "$job"
}
