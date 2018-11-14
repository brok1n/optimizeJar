package cmcc;

import java.io.*;

public class Main {


    public static void main(String args[]) throws IOException {

        new Main().start();

    }

    public void start() throws IOException {

        SourceManager sourceManager = new SourceManager();

        if (!configure(sourceManager)) {
            return;
        }

        println("开始处理:" + System.currentTimeMillis());

        println("开始Copy源码包...");
        boolean status = sourceManager.copyToOutDir();
        if ( status ) {
            println("Copy完毕!\n");
        } else {
            println("Copy异常!");
            return;
        }

        println("开始扫描关联的所有类文件...");
        sourceManager.scanAssociatedClass();
        println("扫描完毕!\n");
        println("关联类文件个数:" + sourceManager.getClassPathList().size());
        println("");

        println("开始处理关联结果...");
        status = sourceManager.processOutput();
        if ( status ) {
            println("关联结果处理完毕!\n");
        } else {
            println("关联结果处理异常!");
        }

        println("关联类文件处理完毕！");
        println("关联类文件目录:" + sourceManager.getOutputDir());

        println("开始编译处理后的java文件");
        println("编译输出目录:" + sourceManager.getOutputDir() + File.separator + "out");
        status = sourceManager.makeOnThread();
        if ( status ) {
            println("编译完毕!\n");
        } else {
            println("编译异常!");
        }

        println("开始打包");
        status = sourceManager.packageJar();
        if ( status ) {
            println("打包完毕!\n");
        } else {
            println("打包异常!");
        }

        println("处理完毕!" +  + System.currentTimeMillis());


    }

    private boolean configure( SourceManager sourceManager ) throws IOException {
        println("开始配置运行环境...\n");

        while (true) {
            println("请输入需要使用到的类的import语句 例如:");
            println("import org.bouncycastle.util.io.pem.PemReader;");
            println("输入 q 结束添加");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine().trim();
            if ( line.startsWith("import ") && line.endsWith(";") ){
                if (line.contains(".*")) {
                    println("请输入要用到类的\"具体类名\"的import语句 不能使用 import xx.*;格式");
                } else {
                    if ( !sourceManager.getClassPathList().contains(line) ) {
                        sourceManager.getClassPathList().add(line);
                        println("添加成功!");
                    } else {
                        println("已存在该记录");
                    }
                }
            } else {
                if ( line.equalsIgnoreCase("q") ) {
                    break;
                } {
                    println("输入错误！");
                }
            }
        }

        if ( sourceManager.getClassPathList().size() <= 0 ) {
            println("没有使用的类 程序退出！");
            return false;
        }
        println("输入完毕\n");

        println("开始识别顶级包名");
        String topPkg = sourceManager.getTopPackageName();
        if ( topPkg == null ) {
            println("退出程序!");
            return false;
        } else {
            println("顶级包名为:" + topPkg);
        }
        println("");

        while ( true ) {
            println("请输入源码包路径 包含 " + topPkg + " 的文件夹路径");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine().trim();
            File file = new File(line);
            if ( file.exists() ) {
                file = new File(file, topPkg);
                if ( file.exists() ) {
                    sourceManager.setSourceDir(line);
                    println("源码包路径添加成功!");
                    break;
                } else if (file.getParentFile().getName().equals(topPkg)){
                    sourceManager.setSourceDir(file.getParentFile().getParent());
                    println("自动识别源码包路径添加成功!");
                    break;
                }
            }
            println("源码包路径输入错误！ 该路径不存在 或该目录不是源码文件夹");
        }

        println("正在创建输出文件夹...");
        println("输出到：");
        File file = new File(sourceManager.getSourceDir());
        File outFile = new File(file.getParentFile(), "out_" + System.currentTimeMillis());
        outFile.mkdir();
        sourceManager.setOutputDir(outFile.getAbsolutePath());
        println(sourceManager.getOutputDir());
        println("输出文件夹创建成功！\n");


        println("配置完成!");
        println("源码路径:" + sourceManager.getSourceDir());
        println("输出路径:" + sourceManager.getOutputDir());
        println("需要使用的类:");
        for ( String path: sourceManager.getClassPathList() ) {
            println( path );
        }
        println("");

        return true;
    }

    public static void println(String msg) {
        System.out.println(msg);
    }
    public static void print(String msg) { System.out.print(msg); }

}
