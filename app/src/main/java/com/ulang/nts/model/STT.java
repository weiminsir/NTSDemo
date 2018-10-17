package com.ulang.nts.model;

import java.util.List;

public class STT {


    /**
     * count : 1
     * segments : [{"no":1,"content":"今天星期五","command":""}]
     */

    private int count;
    private List<SegmentsBean> segments;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<SegmentsBean> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentsBean> segments) {
        this.segments = segments;
    }

    public static class SegmentsBean {
        /**
         * no : 1
         * content : 今天星期五
         * command :
         */

        private int no;
        private String content;
        private String command;

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }
}
