package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.TreeMap;

public class MovieAverageRateTopK extends Configured implements Tool {
    private final static int K = 30;

    public static class MovieMapper extends Mapper<LongWritable, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(",");
            if (split[0].equals("movieId")) {
                return;
            }
            outKey.set(split[0]);
            outValue.set("M\t" + split[1]);

            context.write(outKey, outValue);
        }
    }

    public static class RatingMapper extends Mapper<LongWritable, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(",");
            if (split[0].equals("userId")) {
                return;
            }
            outKey.set(split[1]);
            outValue.set("R\t" + split[2]);

            context.write(outKey, outValue);
        }
    }

    public static class MovieRatingJoinReducer extends Reducer<Text, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            double sum = 0.0;
            int numMovies = 0;
            String movieName = "";

            for (Text t: values) {
                String[] split = t.toString().split("\t");
                if (split[0].equals("M")) {
                    movieName = split[1];
                } else {
                    sum += Double.parseDouble(split[1]);
                    numMovies += 1;
                }
            }

            double averageRate = (numMovies > 0) ? sum / numMovies : 0.0;
            outKey.set(movieName);
            outValue.set(String.valueOf(averageRate));
            context.write(outKey, outValue);
        }
    }

    public static class TopKMapper extends Mapper<LongWritable, Text, Text, Text> {
        private TreeMap<Double, String> topKMap = new TreeMap<>();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");

            topKMap.put(Double.parseDouble(split[1]), split[0]);

            if (topKMap.size() > K) {
                topKMap.remove(topKMap.firstKey());
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            for (Double k : topKMap.keySet()) {
                context.write(new Text(k.toString()), new Text(topKMap.get(k)));
            }
        }
    }

    public static class TopKReducer extends Reducer<Text, Text, Text, Text> {
        private TreeMap<Double, Text> topKMap = new TreeMap<>();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text t: values) {
                topKMap.put(Double.parseDouble(key.toString()), new Text(t));

                if (topKMap.size() > K) {
                    topKMap.remove(topKMap.firstKey());
                }
            }
        }

        @Override
        protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {
            for (Double k : topKMap.descendingKeySet()) {
                context.write(new Text(k.toString()), new Text(topKMap.get(k)));
            }
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf(), "MovieAverageRateTopK First");
        job.setJarByClass(MovieAverageRateTopK.class);

        job.setReducerClass(MovieRatingJoinReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, MovieMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, RatingMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        int returnCode = job.waitForCompletion(true) ? 0 : 1;
        if (returnCode == 0) {
            Job job2 = Job.getInstance(getConf(), "MovieAverageRateTopK Second");

            job2.setJarByClass(MovieAverageRateTopK.class);
            job2.setMapperClass(TopKMapper.class);
            job2.setReducerClass(TopKReducer.class);
            job2.setNumReduceTasks(1);
            job2.setOutputKeyClass(Text.class);
            job2.setOutputValueClass(Text.class);

            FileInputFormat.addInputPath(job2, new Path(args[2]));
            FileOutputFormat.setOutputPath(job2, new Path(args[3]));

            return job2.waitForCompletion(true) ? 0 : 1;
        }

        return 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MovieAverageRateTopK(), args);
        System.exit(exitCode);
    }
}
