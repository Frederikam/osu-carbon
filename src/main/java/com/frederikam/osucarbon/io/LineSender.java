package com.frederikam.osucarbon.io;

import com.frederikam.osucarbon.OsuCarbon;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LineSender {
    
    public static void submitData(Line line) {
        try {
            String output = line.getData();
            try (Socket socket = new Socket(OsuCarbon.carbonHost, OsuCarbon.carbonPort)) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeBytes(output + "\n");
                dos.flush();
            }

            System.out.println("Submitted data: " + output);
        } catch (IOException ex) {
            Logger.getLogger(LineSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void submitData(ArrayList<Line> lines) {
        try {
            String output = "";
            for(Line line : lines){
                output = output + line.getDataAsLine();
            }
            try (Socket socket = new Socket(OsuCarbon.carbonHost, OsuCarbon.carbonPort)) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeBytes(output);
                dos.flush();
                socket.close();
            }

            System.out.println("Submitted data: " + output);
        } catch (IOException ex) {
            Logger.getLogger(LineSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
