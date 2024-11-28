### 자바 설치
`$ brew install adoptopenjdk/openjdk/adoptopenjdk8`

### pubkey 기반의 ssh 접속 허용
- ssh keygen and add my pubkey into authorized_keys
- Turn on Mac's setting `General -> Sharing -> Remote Login`

### 하둡 설치
- Download hadoop binary and untar
- Include hadoop path to env var

### 기본 설정
- Modify config files (core-site|hdfs-site|mapred-site|yarn-site).xml
- `mkdir dfs/name`
- `mkdir dfs/data`
- `hdfs namenode -format`

### 실행
- `sbin/start-dfs.sh`
- `jps` 로 상태 확인
- `hadoop fs -ls /` -> 비어 있음
- `hadoop fs -mkdir /user`
- `hadoop fs -ls /` -> 디렉토리 생김
- `sbin/start-yarn.sh`
- `jps` 로 상태 확인 

### Hello world
- `hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.4.0.jar` 샘플 프로그램 리스트 확인
- `hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.4.0.jar pi 16 10000` PI 값을 구하는 샘플 프로그램 실행

### WEB UI
- http://localhost:9870


