pipeline {
	agent any
	stages {
		stage('Build') {
			steps {
				sh 'rm -f private.gradle'
				sh './gradlew clean build'
			}
		}
		stage('Deploy') {
			steps {
				archive 'build/libs/*jar'
			}
		}
	}
}