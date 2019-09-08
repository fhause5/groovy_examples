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
