package com.ultreon.test.bubbles;

import com.ultreon.libs.commons.v0.Identifier;

import java.io.*;

public class DataFileTest {
    public static void main(String[] args) {
        var file = new File("test.qdat");

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        var out = new DataOutputStream(fout);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            var key = new Identifier("qbubbles", "hello");
            System.out.println(key);

            oos.writeObject(key);

            try {
                oos.close();
                out.close();
                fout.close();
            } catch (IOException ignored) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileInputStream fin;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        var in = new DataInputStream(fin);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            var key = (Identifier) ois.readObject();
            System.out.println(key);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
