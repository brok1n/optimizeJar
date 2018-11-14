import java.io.File

fun scanDir(dir:File ) {
    dir.listFiles().forEach {
        val file = it;
        if ( file.isDirectory ) {
            scanDir(file)
        } else {
            file.forEachLine {
                if ( it.contains("MaMaPe") ) {
                    println(file.absolutePath)
                    println(it)
                }
            }
        }

    }
}


fun main(args:Array<String>) {

    scanDir(File("C:\\Users\\brok1n\\Desktop\\src\\org"))

}