import com.github.javaparser.JavaParser
import java.io.File

fun main(args: Array<String>) {

    val parser = JavaParser()

    val commentsCollection = parser.parse(File("data/BatchPredictor.java")).commentsCollection.orElseThrow { RuntimeException() }

    for (comment in commentsCollection.comments) {
        val startLine = comment.range
                .map { r -> r.begin.line }
                .orElse(-1)

        System.out.printf("%d%n", startLine)
    }
}
