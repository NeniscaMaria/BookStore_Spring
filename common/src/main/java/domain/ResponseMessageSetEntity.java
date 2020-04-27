package domain;

import java.io.*;
import java.util.Set;

public class ResponseMessageSetEntity<T extends BaseEntity<Long>> {
    public static final int PORT = 1234;
    public static final String HOST = "localhost";

    private String header;
    private Set<T> body;

    public ResponseMessageSetEntity() {
    }

    public ResponseMessageSetEntity(String header, Set<T> body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Set<T> getBody() {
        return body;
    }

    public void setBody(Set<T> body) {
        this.body = body;
    }

    public void writeTo(OutputStream os) throws IOException {
        os.write((header + System.lineSeparator() + body + System.lineSeparator()).getBytes());
    }

    public void readFrom(InputStream is) throws IOException {
        var br = new BufferedReader(new InputStreamReader(is));
        header = br.readLine();
        //body = br.readLine();
    }

    @Override
    public String toString() {
        return "Message{" +
                "header='" + header + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
