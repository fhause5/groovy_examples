// List repositories
import groovy.json.JsonSlurper

def cred = "admin:admin"
String auth = cred.bytes.encodeBase64().toString()

def repos =  httpRequest url: "http://192.168.0.106:8081/service/rest/v1/repositories",
        httpMode: 'GET',
        customHeaders: [[name: 'Authorization', value: "Basic ${auth}"]],
        consoleLogResponseBody: true,
        validResponseCodes: '100:499'
return repos.content

// List of scrips
import groovy.json.JsonSlurper

def cred = "admin:admin"
String auth = cred.bytes.encodeBase64().toString()

def repos =  httpRequest url: "http://192.168.0.106:8081/service/rest/v1/script",
        httpMode: 'GET',
        customHeaders: [[name: 'Authorization', value: "Basic ${auth}"]],
        consoleLogResponseBody: true,
        validResponseCodes: '100:499'
return repos.content

// Upload new script 
import groovy.json.JsonSlurper

def cred = "admin:admin"
String auth = cred.bytes.encodeBase64().toString()
def user = "name=11111&content=log.info('1111, 1111!')&type=groovy"

String formBody = "{ \"name\": \"jen1111\", \"content\": \"string\", \"type\": \"groovy\"}"

def repos =  httpRequest url: "http://192.168.0.106:8081/service/rest/v1/script",
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        customHeaders: [[name: 'Authorization', value: "Basic ${auth}"]],
        requestBody: formBody,
        consoleLogResponseBody: true
        //validResponseCodes: '100:499'
return repos.content

// git ava
import groovy.json.JsonSlurper
def avatar(url, user, cred) {
    def response = httpRequest url: "https://api.${url}.com/users/${user}/repos/ceph/branches",
            httpMode: 'GET',
            customHeaders: [[name: 'Authorization', value: "Bearer ${cred}"]],
            consoleLogResponseBody: false,
            validResponseCodes: '100:499'
    def ava = new groovy.json.JsonSlurper().parseText(response.content)
    //def photo = ava.each { println it.owner.avatar_url }
}

// Create folder
def CreateFolder(name) {
    def ckeckfile = name
    if (fileExists(ckeckfile)) {
         echo "file exist"
    } else {
        //File Operations plugin
        fileOperations([folderCreateOperation('${ckeckfile}')])
        println("${ckeckfile} has been created")
    }
}

// loop
def loop2(Dockerfiles) {
  for (int i = 0; i < 2; i++) {
    sh "echo Here is ${Dockerfiles}"
    try {
        sh "mv ${Dockerfiles} ${Dockerfiles}.txt"
    } catch(Exception e) {
        sh "ls -la"
    }
  }
}
// zip
def gitZip(url) {
    try {
        sh "git clone ${url} archive "
        zip zipFile: 'archive.zip', archive: false, dir: 'archive'
        archiveArtifacts artifacts: 'archive.zip', fingerprint: true

    } catch(Exception e) {
        echo "The build is failed"
        currentBuild.result = 'FAILURE'
    }

}

// triger
def trigerEcho(job, ulrs) {
  build job: "$job"
      //parameters: [string(name: 'URL', value: "$urls")]
}

// Send_massega_telegram
def chatId = '_________'
def botId = '_____________'
def text1 = "Hello from Jenkins"
String formBody4 = "{ \"chat_id\": \"${chatId}\", \"text\": \"${text1}\"}"

def send_telegram_message =  httpRequest url: "https://api.telegram.org/bot${botId}/sendMessage",
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        requestBody: formBody4,
        consoleLogResponseBody: true
        validResponseCodes: '100:499'
return repos.content

// aws_s3 upload files  (AWS plugins)
//aws_s3('jenkins098765', 'aws')

def aws_s3(bucket, cred) {
    withAWS(credentials:cred) {
        s3Upload(bucket: bucket, workingDir:'build', includePathPattern:'**/*');
    }
}
def aws_s3_download(bucket, cred) {
    withAWS(credentials:cred) {
        s3Download(file: 'Hello3.txt', bucket: bucket, path: '')
    }
}
