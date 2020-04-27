package TCP;

import domain.Message;
import domain.ServerException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPClient {
    public Message sendAndReceive(Message request){
        try(var socket = new Socket(Message.HOST, Message.PORT);
            var is = socket.getInputStream();
            var os = socket.getOutputStream()){

            //send request
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(request);
            //request.writeTo(os);
            //receive response
            ObjectInputStream ois = new ObjectInputStream(is);
            Message response = (Message)ois.readObject();
            //response.readFrom(is);
            System.out.println("TCPClient response received: "+response);
            socket.close();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            throw new ServerException("error connecting to server "+e.getMessage(),e);
        }
    }
}
