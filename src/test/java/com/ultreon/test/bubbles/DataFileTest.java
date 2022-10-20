package com.ultreon.test.bubbles;

import com.ultreon.bubbles.common.Identifier;

import java.io.*;

public class DataFileTest {
    public static void main(String[] args) {
        File file = new File("test.qdat");

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        DataOutputStream out = new DataOutputStream(fout);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            Identifier key = new Identifier("hello", "qbubbles");
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
        DataInputStream in = new DataInputStream(fin);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            Identifier key = (Identifier) ois.readObject();
            System.out.println(key);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
