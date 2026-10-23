pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['dev', 'staging'],
               description: 'Target environment for the optional Tomcat deployment (context path /expense-<ENV>)')
        booleanParam(name: 'DEPLOY_TOMCAT', defaultValue: false,
                     description: 'Also deploy the WAR file to Tomcat (Task 8). Tomcat must be running')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        JAVA_HOME       = 'C:\\Program Files\\Java\\jdk-21'
        MVN             = 'C:\\DevTools\\apache-maven-3.9.16\\bin\\mvn.cmd'
        TOMCAT_URL      = 'http://localhost:8081'
        UI_TEST_PORT    = '8083'
        APP_PORT        = '8082'
        DOCKER_BUILDKIT = '1'
        TARGET_ENV      = "${params.ENV ?: 'dev'}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    def scmVars = checkout scm
                    env.COMMIT_ID = scmVars.GIT_COMMIT
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

        stage('UI Tests') {
            steps {
                bat 'powershell -NoProfile -ExecutionPolicy Bypass -File scripts\\run-ui-tests.ps1 -Port %UI_TEST_PORT% -Mvn "%MVN%"'
            }
        }

        stage('Docker Build') {
            steps {
                bat '''
                    @echo off
                    docker version --format "Docker server version: {{.Server.Version}}"
                    if errorlevel 1 exit /b 1
                    call "%MVN%" -q -B -DforceStdout help:evaluate -Dexpression=project.version > target\\version.txt
                    if errorlevel 1 exit /b 1
                '''
                script {
                    def version = readFile('target/version.txt').trim()
                    if (!version || version.contains(' ') || version.contains('\n')) {
                        error("Could not read the project version from Maven: '${version}'")
                    }
                    env.IMAGE_TAG = "${version}-${env.BUILD_NUMBER}"
                    echo "Image tag: ${env.IMAGE_TAG}"
                }
                bat 'docker build --build-arg APP_ENV=docker -t expense-app:%IMAGE_TAG% -t expense-app:latest --label org.opencontainers.image.revision=%COMMIT_ID% --label ci.build.number=%BUILD_NUMBER% .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DOCKERHUB_USER',
                                                  passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    bat '''
                        @echo off
                        echo %DOCKERHUB_TOKEN%| docker login -u %DOCKERHUB_USER% --password-stdin
                        if errorlevel 1 exit /b 1
                        docker tag expense-app:%IMAGE_TAG% %DOCKERHUB_USER%/expense-app:%IMAGE_TAG%
                        docker tag expense-app:%IMAGE_TAG% %DOCKERHUB_USER%/expense-app:latest
                        docker push %DOCKERHUB_USER%/expense-app:%IMAGE_TAG%
                        set "RESULT=%errorlevel%"
                        if "%RESULT%"=="0" docker push %DOCKERHUB_USER%/expense-app:latest
                        if "%RESULT%"=="0" set "RESULT=%errorlevel%"
                        docker logout
                        exit /b %RESULT%
                    '''
                }
            }
        }

        stage('Deploy Container') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DOCKERHUB_USER',
                                                  passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    bat '''
                        @echo off
                        set "IMAGE=%DOCKERHUB_USER%/expense-app:%IMAGE_TAG%"
                        docker pull %IMAGE%
                        if errorlevel 1 exit /b 1
                        docker stop expense >nul 2>&1
                        docker rm expense >nul 2>&1
                        docker run -d --name expense --restart unless-stopped -p %APP_PORT%:8082 -v expense-data:/app/data -v expense-uploads:/app/uploads %IMAGE%
                        if errorlevel 1 exit /b 1
                        docker ps --filter name=expense
                    '''
                }
            }
        }

        stage('Verify Container') {
            steps {
                bat '''
                    @echo off
                    curl --fail --silent --show-error --retry 20 --retry-delay 5 --retry-connrefused --retry-all-errors --max-time 5 "http://localhost:%APP_PORT%/actuator/health"
                    if errorlevel 1 (
                        docker logs --tail 40 expense
                        exit /b 1
                    )
                    echo.
                    docker ps --filter name=expense
                    echo Application URL: http://localhost:%APP_PORT%/
                '''
            }
        }

        stage('Deploy to Tomcat') {
            when { expression { params.DEPLOY_TOMCAT } }
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

        stage('Verify Tomcat') {
            when { expression { params.DEPLOY_TOMCAT } }
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
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/screenshots/**,target/ui-test-app.log*', allowEmptyArchive: true
        }
        success {
            echo "Image ${env.IMAGE_TAG} is published and runs in the container expense on port ${env.APP_PORT}"
        }
        failure {
            echo 'Pipeline failed. See the console output of the failing stage. Nothing is deployed when tests or the image build fail.'
        }
    }
}