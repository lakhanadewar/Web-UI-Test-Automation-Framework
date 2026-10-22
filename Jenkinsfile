// Jenkins pipeline: pick a suite and browser, run on Selenium Grid, publish the Extent report.
// Needs the TestKart app running and reachable from the Grid at APP_URL.

pipeline {
    agent any

    parameters {
        choice(name: 'SUITE', choices: ['testng-smoke.xml', 'testng-regression.xml', 'testng-full.xml'], description: 'Which suite to run')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser for smoke and regression (full runs all three)')
        string(name: 'APP_URL', defaultValue: 'http://host.docker.internal:8080', description: 'TestKart URL as seen from the Grid browsers')
    }

    stages {
        stage('Start Selenium Grid') {
            steps {
                bat 'docker compose up -d'
            }
        }

        stage('Run Tests') {
            steps {
                bat "mvn clean test -Dsuite=${params.SUITE} -Dbrowser=${params.BROWSER} -DrunMode=grid -DbaseUrl=${params.APP_URL} -Dmaven.test.failure.ignore=true"
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
            bat 'docker compose down'
        }
    }
}
