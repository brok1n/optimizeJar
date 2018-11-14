import cmcc.Main;

import java.io.*;

public class Test {

    public static void main(String args[]) throws IOException, InterruptedException {

        Main.println("开始处理...");
        for (int i = 0; i < 5; i ++ ) {
            Main.printSingleLine("index:" + i);
            Thread.sleep(800);
        }
        Main.println("处理完毕...");

//        //Process process = Runtime.getRuntime().exec("/bin/sh && cd /home/brok1n/workspace/src && javac org\\bouncycastle\\util\\io\\pem\\PemReader.java -d out");
//        Process process = Runtime.getRuntime().exec("cmd.exe", null, null);
//        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
//        out.println("C:");
//        out.println("cd C:\\Users\\brok1n\\Desktop\\out_1541740434903");
//        out.println("javac org\\bouncycastle\\util\\io\\pem\\PemReader.java -d out");
//        out.println("exit");
//        String line = null;
//        while((line = in.readLine()) != null) {
//            System.out.println(line);
//        }
//        process.waitFor();


//        Process process = Runtime.getRuntime().exec("cmd.exe /c e: & cd E:\\Workspace\\out_1541744316082 & javac org\\bouncycastle\\util\\io\\pem\\PemReader.java -d out");
//        InputStream inputStream = process.getInputStream();
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"gb2312"));
//        String line = null;
//        while((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
//        process.waitFor();

    }

}
