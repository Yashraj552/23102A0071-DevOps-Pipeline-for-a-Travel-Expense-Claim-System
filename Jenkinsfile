pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['dev', 'staging'],
               description: 'Target environment. Sets the Tomcat context path /expense-<ENV> and the app.environment build setting')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    environment {
        JAVA_HOME  = 'C:\\Program Files\\Java\\jdk-21'
        MVN        = 'C:\\DevTools\\apache-maven-3.9.16\\bin\\mvn.cmd'
        TOMCAT_URL = 'http://localhost:8081'
        TARGET_ENV = "${params.ENV ?: 'dev'}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    def scmVars = checkout scm
                    echo "Building commit ${scmVars.GIT_COMMIT} for environment ${env.TARGET_ENV}"
                }
            }
        }

        stage('Build') {
            steps {
                bat 'call "%MVN%" -B -Dapp.environment=%TARGET_ENV% clean compile'
            }
        }

        stage('Package') {
            steps {
                bat 'call "%MVN%" -B -Dapp.environment=%TARGET_ENV% package'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'tomcat-creds',
                                                  usernameVariable: 'TOMCAT_USER',
                                                  passwordVariable: 'TOMCAT_PASS')]) {
                    bat '''
                        @echo off
                        set "WAR="
                        for %%f in (target\\*.war) do set "WAR=%%f"
                        if not defined WAR (
                            echo No WAR file found in target
                            exit /b 1
                        )
                        echo Deploying %WAR% to %TOMCAT_URL% at context path /expense-%TARGET_ENV%
                        curl --silent --show-error -u "%TOMCAT_USER%:%TOMCAT_PASS%" -T "%WAR%" -o deploy-result.txt "%TOMCAT_URL%/manager/text/deploy?path=/expense-%TARGET_ENV%&update=true"
                        if errorlevel 1 exit /b 1
                        type deploy-result.txt
                        findstr /B /C:"OK - " deploy-result.txt
                        if errorlevel 1 exit /b 1
                    '''
                }
            }
        }

        stage('Verify') {
            steps {
                bat '''
                    @echo off
                    curl --fail --silent --show-error --retry 5 --retry-delay 3 --retry-connrefused "%TOMCAT_URL%/expense-%TARGET_ENV%/actuator/health"
                    if errorlevel 1 exit /b 1
                    echo.
                    echo Application URL: %TOMCAT_URL%/expense-%TARGET_ENV%/
                '''
            }
        }
    }

    post {
        success {
            echo "Deployed to ${env.TOMCAT_URL}/expense-${env.TARGET_ENV}/"
        }
        failure {
            echo 'Pipeline failed. See the console output of the failing stage.'
        }
    }
}