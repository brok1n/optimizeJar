package cmcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testDealJar {
	
	static ArrayList<String> classPathList = new ArrayList<String>();
	static ArrayList<String> classPathList2 = new ArrayList<String>();
	//扫描源码目录, 放org的文件夹
	static String startPath = "C:\\Users\\brok1n\\Desktop\\src\\";
	//从源码目录拷贝得到, 放org的文件夹,删除无关类
	static String startPath2 = "C:\\Users\\brok1n\\Desktop\\out\\";
	
    static String line = "return new BCMcElieceCCA2PublicKey(new McElieceCCA2PublicKeyParameters(key.getN(), key.getT(), key.getG(), Utils.getDigest(key.getDigest()).getAlgorithmName()));";

    static String line2 = "        return 1 + StreamUtil.calculateBodyLength(bytes.length) + bytes.length;";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//输入外面调用可能的类
		classPathList.add("import org.bouncycastle.util.io.pem.PemReader;");
		classPathList.add("import org.bouncycastle.jce.provider.BouncyCastleProvider;");
		classPathList.add("import org.bouncycastle.util.io.pem.PemObject;");
		//readFromFile(dealWith(startPath, "import org.bouncycastle.jce.provider.BouncyCastleProvider;"));
		//readFromFile(dealWith(startPath, "import org.bouncycastle.util.io.pem.PemObject;"));
		//readFromFile(dealWith(startPath, "import org.bouncycastle.util.io.pem.PemReader;"));
		
		for(int i = 0; i < classPathList.size(); i++){
			readFromFile(dealWith(startPath, classPathList.get(i)));
			System.out.println("size:" + classPathList.size());
		}
		
		System.out.println("End!");
		
//		LoadFileName(startPath + "\\org\\bouncycastle\\i18n");
//		
		for(int i = 0; i < classPathList.size(); i++){
			classPathList2.add(dealWith(startPath2, classPathList.get(i)));
			System.out.println(classPathList.get(i));
		}
		
		//getKeyWord(line);
		//getSameDirectoryClass(line);
		
		//getSameDirectoryClass("static final ASN1ObjectIdentifier FRP256v1 = new ASN1ObjectIdentifier(\"1.2.250.1.223.101.256.1\");");
		
		//for(int i = 0; i < classPathList2.size(); i++){
		//	System.out.println(classPathList2.get(i));
		//}
		
		traverseFolder2(startPath2);
	}
	
	public static List<String> getKeyWord(String str){
		List<String> results = new ArrayList<String>();
		Pattern p = Pattern.compile("[A-Z][a-zA-Z0-9]+\\."); 
		Matcher m = p.matcher(str); 
		while(m.find()) { 
		     results.add(m.group().substring(0, m.group().length() - 1));
		     System.out.print(results.get(results.size() - 1) + " ");
		} 
		
		p = Pattern.compile("new [A-Z][a-zA-Z0-9]+"); 
	    m = p.matcher(str); 
		while(m.find()) { 
		     results.add(m.group().substring(4, m.group().length()));
		     System.out.print(results.get(results.size() - 1) + " ");
		}
		
		p = Pattern.compile("extends [A-Z][a-zA-Z0-9]+"); 
	    m = p.matcher(str); 
		while(m.find()) { 
		     results.add(m.group().substring(8, m.group().length()));
		     System.out.print(results.get(results.size() - 1) + " ");
		}
		
		p = Pattern.compile("implements [A-Z][a-zA-Z0-9]+"); 
	    m = p.matcher(str); 
		while(m.find()) { 
		     results.add(m.group().substring(11, m.group().length()));
		     System.out.print(results.get(results.size() - 1) + " ");
		}
		
		//throws PemGenerationException
		p = Pattern.compile("throws [A-Z][a-zA-Z0-9]+"); 
	    m = p.matcher(str); 
		while(m.find()) { 
		     results.add(m.group().substring(7, m.group().length()));
		     System.out.print(results.get(results.size() - 1) + " ");
		}
		
		//instanceof InMemoryRepresentable
		p = Pattern.compile("instanceof [A-Z][a-zA-Z0-9]+"); 
	    m = p.matcher(str); 
		while(m.find()) { 
		     results.add(m.group().substring(11, m.group().length()));
		     System.out.print(results.get(results.size() - 1) + " ");
		}
		
		if(results.size() > 0){
			System.out.println("");
			System.out.println(str);
		}
		
		return results;
	}
	
	public static String getSameDirectoryClass(String str){		
		String keyword = null;	
		if(str.contains("new ")){
			int index = str.indexOf("new ");
			int index2 =  str.indexOf("(", index);
			int index3 =  str.indexOf("[", index);
			if(index2 > index)
				keyword = str.substring(index + 4, index2);
			else if(index3 > index)
				keyword = str.substring(index + 4, index3);
			else
				System.out.println(index + "," + index2 + ", " + str);	
			
			//System.out.println(index + "," + index2 + ", " + keyword);
		}
			
		return keyword;
	}
	
	public static String dealWith(String path, String str){
		String str2 = null;
		if(str.substring(str.length() - 1).equals(";"))
			str2 = str.substring(7, str.length() - 1);
		else
			str2 = str.substring(7, str.length());
			
		//System.out.println("" + str2);
		String str3 = path + str2.replace('.', '\\') + ".java"; //"C:\\Users\\cmri\\Desktop\\data\\protect\\"
		//System.out.println(str3);	
		return str3;
	}
	
	

public static void readFromFile(String name) {
	System.out.println("readFromFile: " + name);
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
				
				if(str.startsWith("import org.bouncycastle")){
					if(!classPathList.contains(str)){
						classPathList.add(str);
						System.out.println("add chen2: " + str);
					}				
				}else {
					
					List<String> results = getKeyWord(str);
					if(results != null && results.size() > 0){
						if(names == null){
							names = LoadFileName(file.getParent());
							System.out.println("path chen2: " + file.getParent() + ", " + names.size());
						}
						
						if(names.size() > 1){
							for(int i = 0; i < results.size(); i++){
								String keyword = results.get(i);
								for(int j = 0; j < names.size();j++){
									if(keyword.equals(names.get(j))){
										int index = file.getParent().indexOf("org\\bouncycastle");
										String str2 = file.getParent().substring(index, file.getParent().length()) + File.separator + keyword;
										String class2 = "import " + str2.replace("\\", ".");
										if(!classPathList.contains(class2)){
											classPathList.add(class2);
											System.out.println("add chen: " + class2);
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

public static List<String> LoadFileName(String path){
	
	List<String> results = new ArrayList<String>();
	
    File file = new File(path);
    if (file.exists()) {
        File[] files = file.listFiles();
        if (null == files || files.length == 0) {
            System.out.println("文件夹是空的!");
            return results;
        } else {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                } else {
                	results.add( file2.getName().substring(0, file2.getName().length() - 5));
                }
            }
        }
    } else {
        System.out.println("文件不存在!");
    }
    
    return results;
}

public static void traverseFolder2(String path) {

    File file = new File(path);
    if (file.exists()) {
        File[] files = file.listFiles();
        if (null == files || files.length == 0) {
            System.out.println("文件夹是空的!");
            return;
        } else {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    System.out.println("文件夹:" + file2.getAbsolutePath());
                    traverseFolder2(file2.getAbsolutePath());
                } else {
                	boolean isDeleted = true;
                	for(int i = 0; i < classPathList2.size(); i++){
                		if(file2.getAbsolutePath().equals(classPathList2.get(i)))
                		{
                			isDeleted = false;
                			break;
                		}
                	}
                	if(isDeleted){
                		file2.delete();
                	}
                    //System.out.println("文件:" + file2.getAbsolutePath());
                }
            }
        }
    } else {
        System.out.println("文件不存在!");
    }
}
}
