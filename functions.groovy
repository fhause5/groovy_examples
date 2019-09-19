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

####
pipeline {
    agent {
        node {
            label "maven"
        }
    }
    parameters {
        string(
                "defaultValue": "",
                "description": "Put the user account. \nE.g.: fhause, snap032",
                "name": "user"
        )
        string(
                "defaultValue": "",
                "description": "Put the token. \nE.g.: 4e0fc06a00d7e87366bf50a9a9002979aa32aecf87 , qwerty",
                "name": "token"
        )
        choice(
                choices: 'token\npassword',
                description: 'Put the token',
                name: 'cred'
        )
    }
    stages {
        stage("GET") {
            steps {
                script {
                    //users = params.user
                    //tokens = params.token
                    avatar("github", params.user, params.token)
                }
            }
        }
    }
}
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
