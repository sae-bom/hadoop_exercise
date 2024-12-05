package com.fastcampus.hadoop;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

// 복합 키 클래스. 첫 번째 키의 타입도 Text, 두 번째 키의 타입도 Text
public class TextText implements WritableComparable<TextText> {
    private Text first;
    private Text second;

    public TextText() {
        set(new Text(), new Text());
    }

    public TextText(String first, String second) {
        set(new Text(first), new Text(second));
    }

    public TextText(Text first, Text second) {
        set(first, second);
    }

    public void set(Text first, Text second) {
        this.first = first;
        this.second = second;
    }

    public Text getFirst() {
        return first;
    }

    public Text getSecond() {
        return second;
    }

    @Override
    public int compareTo(TextText o) {
        int cmp = first.compareTo(o.first);
        if (cmp != 0) {
            return cmp;
        }
        return second.compareTo(o.second);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        first.write(out);
        second.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        first.readFields(in);
        second.readFields(in);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 163 + second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TextText) {
            TextText tt = (TextText) obj;
            return first.equals(tt.first) && second.equals(tt.second);
        }
        return false;
    }

    @Override
    public String toString() {
        return first.toString() + ", " + second.toString();
    }
}
