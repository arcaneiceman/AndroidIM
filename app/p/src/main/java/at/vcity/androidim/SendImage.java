package at.vcity.androidim;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by wali on 14/03/15.
 */
public class SendImage extends Thread {
    ArrayList<byte[]> here;

    public SendImage(ArrayList<byte[]> l){
        this.here = l;
    }

    public void run(){
        try{
            Socket socket = null;
            String host = "198.162.52.90";

            socket = new Socket(host, 5566);

            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(here);
            oos.close();
            os.close();
            socket.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

}
