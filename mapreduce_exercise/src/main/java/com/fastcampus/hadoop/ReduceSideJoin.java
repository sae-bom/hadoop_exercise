package com.fastcampus.hadoop;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReduceSideJoin extends Configured implements Tool {
    public static class EmployeeMapper extends Mapper<LongWritable, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(",");
            outKey.set(split[6]);
            outValue.set("1\t" + split[0] + "\t" + split[2] + "\t" + split[4]);

            context.write(outKey, outValue);
        }
    }

    public static class DepartmentMapper extends Mapper<LongWritable, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(",");
            outKey.set(split[0]);
            outValue.set("0\t" + split[1]);

            context.write(outKey, outValue);
        }
    }

    public static class ReduceJoinReducer extends Reducer<Text, Text, Text, Text> {
        Text outKey = new Text();
        Text outValue = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String departmentName = "";
            HashMap<String, String> employeeMap = new HashMap<>();

            for (Text t: values) {
                String[] split = t.toString().split("\t");
                if (split[0].equals("0")) {
                    departmentName = split[1];
                } else {
                    employeeMap.put(split[1], split[2] + "\t" + split[3]);
                }
            }

            for (Map.Entry<String, String> e: employeeMap.entrySet()) {
                outKey.set(e.getKey());
                outValue.set(e.getValue() + "\t" + departmentName);
                context.write(outKey, outValue);
            }
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf(), "ReduceSideJoin");

        job.setJarByClass(ReduceSideJoin.class);
        job.setReducerClass(ReduceJoinReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, EmployeeMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, DepartmentMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new ReduceSideJoin(), args);
        System.exit(exitCode);
    }
}
