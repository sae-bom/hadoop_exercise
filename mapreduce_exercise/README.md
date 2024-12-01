### Create maven project via IDE

---

### Write Java Class

---

### TroubleShooting
#### 1. class import 안 되는 문제
프로젝트를 생성하고 `pom.xml`을 적절히 수정한 후 `.java` 파일을 작성하기 시작했다.  
`public class WordCount extends Configured implements Tool {`
 를 작성하는데, IDE에서 `Configured` 를 포함하는 `org.apache.hadoop.conf.Configured` 를 못 찾고 있었다.

지난 실습때 비슷한 문제가 있어 시도한 mvn purge + mvn clean install을 해 봐도 해결되지 않았다

이리저리 찾아보다 IDE(IntelliJ)에서 `Maven > Sync Project`를 클릭해 보라는 이야기를 보고 시도해봤고, 해결되었다


#### 2. parameter type 불일치 문제
```java
FileInputFormat.addInputPath(job, new Path(args[0]));
FileOutputFormat.setOutputPath(job, new Path(args[1]));
```
위 두 라인에서 첫 번째 param의 타입이 jobconf여야 하는데 job이라는 오류가 나면서 컴파일이 되지 않았다

IDE의 자동 import 기능을 쓰다가

```java
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
```
를 임포트해야 하는데 잘못해서
```java
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
```
를 임포트해서 생긴 문제였다.  
그렇다면 비슷한 이름을 가진 mapreduce 라이브러리와 mapred 라이브러리는 무엇이 다를까?

---

### Learned
- `mapreduce` vs `mapred`    
`org.apache.hadoop.mapred` 는 구형 API, `org.apache.hadoop.mapreduce` 는 신형 API 이다

| API     | org.apache.hadoop.mapred | org.apache.hadoop.mapreduce   |
|---------|--------------------------|-------------------------------|
| 버전      | Hadoop 1.x               | Hadoop 2.x                    |
| 유연성     | 낮음                       | 높음                            |
| YARN 지원 | 제한적                      | 완벽 지원                         |
| 설계 방식   | 클래스 기반                   | 인터페이스 기반                      |
| 중심 클래스  | JobConf, Mapper, Reducer | Job, Mapper, Reducer, Context |
| 유지보수 상태 | 제한적                      | 적극적 유지보수                      |

- `IntWritable` 타입이란?  
Mapper와 Reducer 간에는 네트워크 통신이 필요한데, 네트워크 전송을 위한 데이터 직렬화/역직렬화를 제공하는 타입이다.  
BlahblahWritable 클래스들이 여럿 있고, 커스텀 Writable class를 직접 작성할 수도 있다. 