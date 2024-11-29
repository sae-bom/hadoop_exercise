### Install IntelliJ (IDE)

### Install maven
- Download binary
- untar
- set PATH

### Create maven project via IDE

### TroubleShooting
`pom.xml` 에서 `Dependency 'org.apache.hadoop:hadoop-client:3.4.0' not found` 라는 에러와 함께 dependency resolve를 할 수 없는 경우, 프로젝트 루트 디렉토리 (즉, pom.xml 파일이 존재하는 곳)에서 다음을 실행
```
$ mvn dependency:purge-local-repository
$ mvn clean install
```

### 빌드 명령어
`$ mvn package`

### 실행 명령어
`$ hadoop jar target/hdfs-example-1.0.0.jar com.fastcampus.hadoop.FileSystemPrint ${args[0]}`
