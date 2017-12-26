package com.hzf.utils.FTPUtil;

public class FTPConfig {

    private final String host;
    private final int port;
    private final String name;
    private final String password;

    private final int fileType;
    private final int bufferSize;
    private final String encoding;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getFileType() {
        return fileType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getEncoding() {
        return encoding;
    }

    public static class Builder {

        private String host;
        private int port;
        private String name;
        private String password;

        private int fileType;
        private int bufferSize;
        private String encoding;

        public Builder(String host, int port, String name, String password) {
            this.host = host;
            this.port = port;
            this.name = name;
            this.password = password;
        }

        public Builder setFileType(int val) {
            this.fileType = val;
            return this;
        }

        public Builder setBufferSize(int val) {
            this.bufferSize = val;
            return this;
        }

        public Builder setEncoding(String val) {
            this.encoding = val;
            return this;
        }

        public FTPConfig build() {
            return new FTPConfig(this);
        }

    }

    private FTPConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.name = builder.name;
        this.password = builder.password;
        this.fileType = builder.fileType == 0 ? 2 : builder.fileType;
        this.bufferSize = builder.bufferSize < 1024 ? 1024 : builder.bufferSize;
        this.encoding = builder.encoding.equals("") ? "GBK" : builder.encoding;
    }
}
