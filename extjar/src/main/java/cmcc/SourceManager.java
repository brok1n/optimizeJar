package cmcc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceManager {

    //源码文件夹
    private String sourceDir = "";
    //输出文件夹
    private String outputDir = "";
    //包名文件夹数组
    private String[] pkgDirArr = new String[]{};

    private ArrayList<String> classPathList = new ArrayList<String>();
    private ArrayList<String> classPathList2 = new ArrayList<String>();
    private ArrayList<String> packageList = new ArrayList<>();

    public boolean copyToOutDir() {
        try {
            copyDir(sourceDir, outputDir);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void copyDir(String sourcePath, String newPath) throws IOException {
        File file = new File(sourcePath);
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
                copyDir(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

            if (new File(sourcePath + file.separator + filePath[i]).isFile()) {
                copyFile(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

        }
    }

    private void copyFile(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = new FileInputStream(oldFile);
        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[2097152];
        int readByte = 0;
        while ((readByte = in.read(buffer)) != -1) {
            out.write(buffer, 0, readByte);
        }
        in.close();
        out.close();
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public ArrayList<String> getClassPathList() {
        return classPathList;
    }

    public ArrayList<String> getPackageList() {
        return packageList;
    }

    public String getTopPackageName() {
        String pkg =  classPathList.get(0).replace("import ", "");
        pkgDirArr = pkg.split("\\.");

        if ( pkgDirArr.length <= 0 ) {
            Main.println("包名异常！ 未找到包名");
            return null;
        } else {
            return pkgDirArr[0];
        }
    }

    public void scanAssociatedClass() {
        for (int i = 0; i < classPathList.size(); i ++ ) {
            readFromFile(dealWith(sourceDir, classPathList.get(i)));
        }
    }

    public boolean processOutput(){
        try {
            for (String path:classPathList ) {
                classPathList2.add(dealWith(outputDir, path));
            }
            traverseFolder2(outputDir);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean makeOnThread() {
        try {
            if ( packageList.size() < 3 ) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                Main.println("待编译文件数量:" + packageList.size());
                Main.println("开始编译...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        makeOutput(packageList);
                        countDownLatch.countDown();
                    }
                }).start();
                countDownLatch.await();
                Main.println("编译完毕...");
                return true;
            }
            int pageSize = packageList.size() / 2;
            List<String> list1 = packageList.subList(0, pageSize);
            List<String> list2 = packageList.subList(pageSize, packageList.size());
//            List<String> list2 = packageList.subList(pageSize, pageSize * 2);
//            List<String> list3 = packageList.subList(pageSize * 2, packageList.size());
//            List<String> list3 = packageList.subList(pageSize * 2, pageSize * 3);
//            List<String> list4 = packageList.subList(pageSize * 3, packageList.size());

            CountDownLatch countDownLatch = new CountDownLatch(2);
            Main.println("待编译文件数量:" + packageList.size());
            Main.println("编译时间较长 请耐心等待 预计需要:" + ( pageSize * 8 + 15 ) + " 秒");
            Main.println("开始编译..." + System.currentTimeMillis());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    makeOutput(list1);
                    countDownLatch.countDown();
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    makeOutput(list2);
                    countDownLatch.countDown();
                }
            }).start();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    makeOutput(list3);
//                    countDownLatch.countDown();
//                }
//            }).start();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    makeOutput(list4);
//                    countDownLatch.countDown();
//                }
//            }).start();

            countDownLatch.await();
            Main.println("编译完毕..." + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean makeOutput(List<String> pkgList) {
        try {

//            //优化需要编译的文件
//            List<String> optList = optimize();
//            cmcc.Main.println("原需要编译文件个数:" + classPathList2.size());
//            cmcc.Main.println("优化后需要编译文件个数:" + classPathList2.size());

            Process ps = null;
            Process process = null;
            BufferedReader in = null;
            PrintWriter out = null;
            if ( outputDir.startsWith("/") ) {
                process = Runtime.getRuntime().exec("/bin/sh", null, null);
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
                out.println("cd " + outputDir);
            } else {
                process = Runtime.getRuntime().exec("cmd.exe", null, null);
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
                out.println(outputDir.substring(0, outputDir.indexOf("\\")));
                out.println("cd " + outputDir);
            }
            Thread.sleep(1500);
//            in.readLine();
//            in.readLine();
//            in.readLine();
//            in.readLine();
//            in.readLine();

            File outFile = new File(outputDir, "out");
            outFile.mkdirs();

//            cmcc.Main.println("待编译数量:" + pkgList.size());
//            cmcc.Main.println("开始编译...");
            int realCount = 0;
            for (String path:pkgList) {
                String tmpClassPath = path.replace(outputDir + File.separator, "");
                String buildCmd = "javac -encoding utf-8 " + path + File.separator + "*.java -d out";
                out.println(buildCmd);
                realCount ++;
                Thread.sleep(7000);
            }
//            cmcc.Main.println("实际编译文件个数:" + realCount);
            try {
                String line = null;
                out.println("exit");
//                while((line = in.readLine()) != null) {
//                    System.out.println(line);
//                }
                out.println("");
                out.println("exit");
                out.println("");
                out.println("exit");
//                cmcc.Main.println("等待编译处理完毕...");
                process.waitFor(20, TimeUnit.SECONDS);
//                cmcc.Main.println("编译处理完毕...");
            }catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void traverseFolder2(String path) {
//        cmcc.Main.println("开始优化源码文件:" + path + "  files:" + classPathList2.size() );
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
//                cmcc.Main.println("文件夹是空的!");
                packageList.remove(path);
                return;
            } else {
                String tmpPath = "";
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder2(file2.getAbsolutePath());
                    } else {
                        tmpPath = path.replace(outputDir + File.separator, "");
                        if ( !packageList.contains(tmpPath)) {
                            packageList.add(tmpPath);
                        }
                        if ( !classPathList2.contains(file2.getAbsolutePath() ) ) {
                            file2.delete();
                        }
                    }
                }
                files = file.listFiles(fileFilter);
                if ( files == null || files.length == 0 ) {
                    packageList.remove(tmpPath);
                }

            }
        } else {
            Main.println("文件不存在!");
        }
    }

    FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if ( pathname.isDirectory() ) {
                File[] files = pathname.listFiles();
                if ( files == null || files.length == 0 ) {
                    pathname.delete();
                }
            }
            return pathname.isFile();
        }
    };

    private String dealWith(String path, String str) {
        String realPath = path + File.separator + str.replace("import ", "").replace(";", "").replace(".", File.separator).trim() + ".java";
//        cmcc.Main.println(realPath);
        return realPath;
    }

    private void readFromFile(String name) {
        List<String> names = null;
        try {
            File file = new File(name);
            if(!file.exists()){
                file = new File(file.getParent() + ".java");
                if(!file.exists())
                {
                    //return;
                }
            }
            InputStreamReader input = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(input);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                str = str.trim();

                if(str.startsWith("/*") || str.startsWith("*") || str.startsWith("//")){
                    //System.out.println("add chen3: " + str);
                    continue;
                }

                if(str.startsWith(String.format("import %s.%s", pkgDirArr[0], pkgDirArr[1]))){
                    if(!classPathList.contains(str)){
                        classPathList.add(str);
//                        cmcc.Main.println("add chen2: " + str);
                    }
                }else {

                    List<String> results = getKeyWord(str);
                    if(results != null && results.size() > 0){
                        if(names == null){
                            names = LoadFileName(file.getParent());
//                            cmcc.Main.println("path chen2: " + file.getParent() + ", " + names.size());
                        }

                        if(names.size() > 1){
                            for(int i = 0; i < results.size(); i++){
                                String keyword = results.get(i);
                                for(int j = 0; j < names.size();j++){
                                    if(keyword.equals(names.get(j))){
                                        int index = file.getParent().indexOf(String.format("%s%s%s", pkgDirArr[0],File.separator, pkgDirArr[1]));
                                        String str2 = file.getParent().substring(index, file.getParent().length()) + File.separator + keyword;
                                        String class2 = "import " + str2.replace(File.separator, ".");
                                        if(!classPathList.contains(class2)){
                                            classPathList.add(class2);
//                                            cmcc.Main.println("add chen: " + class2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            bf.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> LoadFileName(String path) {

        List<String> results = new ArrayList<String>();

        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                Main.println("文件夹是空的!");
                return results;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                    } else {
                        results.add(file2.getName().substring(0, file2.getName().length() - 5));
                    }
                }
            }
        } else {
            Main.println("文件不存在!");
        }

        return results;
    }

    private List<String> getKeyWord(String str) {
        List<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile("[A-Z][a-zA-Z0-9]+\\.");
        Matcher m = p.matcher(str);
        while (m.find()) {
            results.add(m.group().substring(0, m.group().length() - 1));
//            cmcc.Main.print(results.get(results.size() - 1) + " ");
        }

        p = Pattern.compile("new [A-Z][a-zA-Z0-9]+");
        m = p.matcher(str);
        while (m.find()) {
            results.add(m.group().substring(4, m.group().length()));
//            cmcc.Main.print(results.get(results.size() - 1) + " ");
        }

        p = Pattern.compile("extends [A-Z][a-zA-Z0-9]+");
        m = p.matcher(str);
        while (m.find()) {
            results.add(m.group().substring(8, m.group().length()));
//            cmcc.Main.print(results.get(results.size() - 1) + " ");
        }

        p = Pattern.compile("implements [A-Z][a-zA-Z0-9]+");
        m = p.matcher(str);
        while (m.find()) {
            results.add(m.group().substring(11, m.group().length()));
//            cmcc.Main.print(results.get(results.size() - 1) + " ");
        }

        //throws PemGenerationException
        p = Pattern.compile("throws [A-Z][a-zA-Z0-9]+");
        m = p.matcher(str);
        while (m.find()) {
            results.add(m.group().substring(7, m.group().length()));
//            cmcc.Main.print(results.get(results.size() - 1) + " ");
        }

        //instanceof InMemoryRepresentable
        p = Pattern.compile("instanceof [A-Z][a-zA-Z0-9]+");
        m = p.matcher(str);
        while (m.find()) {
            results.add(m.group().substring(11, m.group().length()));
//            cmcc.Main.print(results.get(results.size() - 1) + " ");
        }

//        if (results.size() > 0) {
//            cmcc.Main.println("");
//            cmcc.Main.println(str);
//        }

        return results;
    }


    public boolean packageJar() {
        try {

            Process ps = null;
            Process process = null;
            BufferedReader in = null;
            PrintWriter out = null;
            if ( outputDir.startsWith("/") ) {
                process = Runtime.getRuntime().exec("/bin/sh", null, null);
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
                out.println("cd " + outputDir);
            } else {
                process = Runtime.getRuntime().exec("cmd.exe", null, null);
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
                out.println(outputDir.substring(0, outputDir.indexOf("\\")));
                out.println("cd " + outputDir);
            }
            out.println("cd out");
            Thread.sleep(1500);

            String buildCmd = "jar cvf out.jar " + getTopPackageName();
            out.println(buildCmd);
            try {
                String line = null;
                out.println("exit");
                while((line = in.readLine()) != null) {
//                    System.out.println(line);
                }
                out.println("");
                out.println("exit");
                out.println("");
                out.println("exit");
                process.waitFor(20, TimeUnit.SECONDS);
            }catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
