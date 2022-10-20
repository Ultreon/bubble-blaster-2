package com.ultreon.test.bubbles;

import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.types.Decimal128;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class BsonTestData {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void main(String[] args) {
        BsonDocument document = new BsonDocument();
        document.put("String", new BsonString("Test String"));
        document.put("Integer", new BsonInt32(123456789));
        document.put("Long", new BsonInt64(123456789));
        document.put("double", new BsonDouble(01234.56789));
        document.put("BigDecimal", new BsonDecimal128(new Decimal128(new BigDecimal("01234.56789"))));
        document.put("Boolean", new BsonBoolean(true));

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(75763242));
        array.add(new BsonInt64(756298648239654823L));
        array.add(new BsonBoolean(false));
        array.add(new BsonString("TEST STRING IN A ARRAY"));
        array.add(new BsonString("Test String In a Array"));

        BsonArray array1 = new BsonArray();
        array1.add(new BsonString("Array in a Array in a Document."));
        array.add(array1);

        BsonDocument document1 = new BsonDocument();
        document1.put("Lol", new BsonString("Is a Lol Document"));
        document1.put("Int32", new BsonInt32(4224));

        array.add(document1);
        document.put("Boolean", array);

        BsonDocument document2 = new BsonDocument();
        document2.put("Docx", new BsonString("Document in a Document, may be need MS Office Word?"));

        System.out.println(document.toJson());

        RawBsonDocument doc = new RawBsonDocument(document, new BsonDocumentCodec());
        try {
            FileOutputStream fos = new FileOutputStream("test.bson");
            fos.write(doc.getByteBuffer().array());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream("test.bson");
            RawBsonDocument document3 = new RawBsonDocument(fis.readAllBytes());
            System.out.println(document3.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
