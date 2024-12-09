package com.fastcampus.hadoop;

import static org.mockito.Mockito.*;

import com.fastcampus.hadoop.WordCount.TokenizerMapper;
import com.fastcampus.hadoop.WordCount.IntSumReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class WordCountTestWithMockito{
    @Test
    public void wordCountMapTest() throws IOException, InterruptedException {
        TokenizerMapper mapper = new TokenizerMapper();

        Mapper.Context context = mock(Mapper.Context.class);
        mapper.word = mock(Text.class);

        mapper.map(new LongWritable(0), new Text("dog dog cat"), context);

        InOrder inOrder = inOrder(mapper.word, context);
        inOrder.verify(mapper.word).set(eq("dog"));
        inOrder.verify(context).write(eq(mapper.word), eq(new IntWritable(1)));
        inOrder.verify(mapper.word).set(eq("dog"));
        inOrder.verify(context).write(eq(mapper.word), eq(new IntWritable(1)));
        inOrder.verify(mapper.word).set(eq("cat"));
        inOrder.verify(context).write(eq(mapper.word), eq(new IntWritable(1)));
    }

    @Test
    public void wordCountReduceTest() throws IOException, InterruptedException {
        IntSumReducer reducer = new IntSumReducer();
        List<IntWritable> values = Arrays.asList(new IntWritable(1), new IntWritable(1));

        Reducer.Context context = mock(Reducer.Context.class);

        reducer.reduce(new Text("dog"), values, context);
        verify(context).write(new Text("dog"), new IntWritable(2));
    }

    @Test
    public void counterTest() throws IOException, InterruptedException {
        WordCountWithCounter.TokenizerMapper mapper = new WordCountWithCounter.TokenizerMapper();

        Mapper.Context context = mock(Mapper.Context.class);
        Counter counter = mock(Counter.class);

        when(context.getCounter(WordCountWithCounter.Word.WITHOUT_SPECIAL_CHARACTER)).thenReturn(counter);

        mapper.map(new LongWritable(0), new Text("dog dog cat"), context);

        verify(counter, times(3)).increment(1);
    }
}
