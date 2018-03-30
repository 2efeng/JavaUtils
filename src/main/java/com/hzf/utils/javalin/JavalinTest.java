package com.hzf.utils.javalin;

import io.javalin.Javalin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class JavalinTest {

    public static void main(String[] args) {

        save("", "");

//        Javalin app = Javalin.create().port(7000).start();
//
//
//        app.before(ctx -> {
//            String str = "java lin";
//            ctx.json(str);
//        });
//
////        app.get("/", ctx -> {
////            String str = "java lin";
////            ctx.json(str);
////        });
//
//        app.get("/hello/:name/:value", ctx -> {
//            String str = ctx.param("name");
//            String value = ctx.param("value");
//            ctx.result("Hello: " + str + value);
//        });
//
//        app.get("/hello/*/and/*", ctx -> {
//            String str = ctx.splat(0);
//            String value = ctx.splat(1);
//            ctx.result("Hello: " + str + " and " + value);
//        });
//
//        app.post("/upload", ctx -> ctx.uploadedFiles("files").forEach(file -> {
//            try {
//                FileUtils.copyInputStreamToFile(file.getContent(), new File("upload/" + file.getName()));
//                ctx.html("Upload successful");
//            } catch (IOException e) {
//                ctx.html("Upload failed");
//            }
//        }));
//
//
//        app.post("/save", context -> {
//            String json = context.body();
//            System.out.println(json);
//        });
    }


    private static void save(String... arr) {
        System.out.println(arr.getClass().getSimpleName());
    }

}
