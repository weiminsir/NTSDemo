package com.ulang.nts.model;

import java.util.List;

public class NLP {


    /**
     * domain : menu
     * dialog : {"userid":"1","keypad":false,"replys":[{"interrupt":true,"property":"synthesis","content":"打电话"}],"action":"打电话","input":"我想打个电话","finalSluResult":["打电话"],"query":{"params":{},"function":""}}
     * func : {"debug":null,"gazetteer":null}
     */

    private String domain;
    private DialogBean dialog;
    private FuncBean func;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public DialogBean getDialog() {
        return dialog;
    }

    public void setDialog(DialogBean dialog) {
        this.dialog = dialog;
    }

    public FuncBean getFunc() {
        return func;
    }

    public void setFunc(FuncBean func) {
        this.func = func;
    }

    public static class DialogBean {
        /**
         * userid : 1
         * keypad : false
         * replys : [{"interrupt":true,"property":"synthesis","content":"打电话"}]
         * action : 打电话
         * input : 我想打个电话
         * finalSluResult : ["打电话"]
         * query : {"params":{},"function":""}
         */

        private String userid;
        private boolean keypad;
        private String action;
        private String input;
        private QueryBean query;
        private List<ReplysBean> replys;
        private List<String> finalSluResult;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public boolean isKeypad() {
            return keypad;
        }

        public void setKeypad(boolean keypad) {
            this.keypad = keypad;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public QueryBean getQuery() {
            return query;
        }

        public void setQuery(QueryBean query) {
            this.query = query;
        }

        public List<ReplysBean> getReplys() {
            return replys;
        }

        public void setReplys(List<ReplysBean> replys) {
            this.replys = replys;
        }

        public List<String> getFinalSluResult() {
            return finalSluResult;
        }

        public void setFinalSluResult(List<String> finalSluResult) {
            this.finalSluResult = finalSluResult;
        }

        public static class QueryBean {
            /**
             * params : {}
             * function :
             */

            private ParamsBean params;
            private String function;

            public ParamsBean getParams() {
                return params;
            }

            public void setParams(ParamsBean params) {
                this.params = params;
            }

            public String getFunction() {
                return function;
            }

            public void setFunction(String function) {
                this.function = function;
            }

            public static class ParamsBean {
            }
        }

        public static class ReplysBean {
            /**
             * interrupt : true
             * property : synthesis
             * content : 打电话
             */

            private boolean interrupt;
            private String property;
            private String content;

            public boolean isInterrupt() {
                return interrupt;
            }

            public void setInterrupt(boolean interrupt) {
                this.interrupt = interrupt;
            }

            public String getProperty() {
                return property;
            }

            public void setProperty(String property) {
                this.property = property;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }

    public static class FuncBean {
        /**
         * debug : null
         * gazetteer : null
         */

        private Object debug;
        private Object gazetteer;

        public Object getDebug() {
            return debug;
        }

        public void setDebug(Object debug) {
            this.debug = debug;
        }

        public Object getGazetteer() {
            return gazetteer;
        }

        public void setGazetteer(Object gazetteer) {
            this.gazetteer = gazetteer;
        }
    }
}
