package domain;

import java.io.*;

public class Message<T> implements Serializable{
    public static final int PORT = 1234;
    public static final String HOST = "localhost";

    private String header;
    private T body;

    public Message() {
    }

    public Message(String header, T body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
    /*
    public void writeTo(OutputStream os) throws IOException {
        os.write((header + System.lineSeparator() + body + System.lineSeparator()).getBytes());
    }

    public void readFrom(InputStream is) throws IOException {
        var br = new BufferedReader(new InputStreamReader(is));
        header = br.readLine();
        body =br.readLine();
    }
*/
    @Override
    public String toString() {
        return "Message{" +
                "header='" + header + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
