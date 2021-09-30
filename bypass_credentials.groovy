pipeline {
    agent any
    environment {
        def USERNAME_PASSWORD = "f803ce8a-1c5a-4bf5-a9ab-69c8fa1051a1"
        def SSH_KEY = "5090b57a-18e4-469e-a8fc-b69c8943ffcb"
        def SSH_KEY_ENCRYPTED = "26a28a65-7d9e-4608-9657-a7d168af302c"
        def SECRET_TEXT = "4e83936a-e06b-4214-a52c-f55c49214cf6"
        def SECRET_FILE = "df385149-6ddb-4d8c-be48-5bfd7b4d371b"
    }
    stages {
        stage("1: Username with password") {
            steps {
                withCredentials([
                usernamePassword(credentialsId: USERNAME_PASSWORD, usernameVariable: "username", passwordVariable: "password")
                ]){
                    sh 'echo "${username};${password}" > bypass_secret'
                }
                sh "cat bypass_secret"
            }
        }
        stage("2.1: SSH private key 1st") {
            steps {
                withCredentials([
                sshUserPrivateKey(credentialsId: SSH_KEY, keyFileVariable: "private_key")
                ]){
                    sh 'cat ${private_key}'
                }
            }
        }
        stage("2.2: SSH private key 2nd") {
            steps {
                withCredentials([
                sshUserPrivateKey(credentialsId: SSH_KEY, keyFileVariable: "private_key")
                ]){
                    sh 'ln -s ${private_key} link_private_key'
                    sh 'cp link_private_key bypass_private_key'
                }
                sh "cat bypass_private_key"
            }
        }
        stage("2.3: SSH Username with private key and passphrase") {
            steps {
                withCredentials([
                sshUserPrivateKey(credentialsId: SSH_KEY_ENCRYPTED, keyFileVariable: "encrypted_private_key", usernameVariable: "username", passphraseVariable: "passphrase")
                ]){
                    sh 'cat ${encrypted_private_key}'
                    sh 'echo "${username};${passphrase}" > bypass_username_and_passphrase'
                }
                sh "cat bypass_username_and_passphrase"
            }
        }
        stage("3: Secret text") {
            steps {
                withCredentials([
                string(credentialsId: SECRET_TEXT, variable: "text")
                ]){
                    sh 'echo $text > bypass_text'
                }
                sh "cat bypass_text"
            }
        }
        stage("4.1: Secret file 1st") {
            steps {
                withCredentials([
                file(credentialsId: SECRET_FILE, variable: "file")
                ]){
                    sh 'cat ${file}'
                }
            }
        }
        stage("4.2: Secret file 2nd") {
            steps {
                withCredentials([
                file(credentialsId: SECRET_FILE, variable: "file")
                ]){
                    sh 'ln -s ${file} link_file'
                    sh 'cp link_file bypass_file'
                }
                sh "cat bypass_file"
            }
        }
        stage("4.3: Getting name of Secret file") {
            steps {
                withCredentials([
                file(credentialsId: SECRET_FILE, variable: "file")
                ]){
                    sh 'find $file > find_stdout'
                }
                sh "cat find_stdout"
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}