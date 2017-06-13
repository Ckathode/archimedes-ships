node {
	checkout scm
	sh 'git submodule update --init --force' 
	sh './gradlew setupDecompWorkspace clean build -x test'
	archive 'build/libs/*jar'
}
