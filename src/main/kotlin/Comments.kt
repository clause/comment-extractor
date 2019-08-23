import com.github.javaparser.ast.comments.BlockComment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.comments.LineComment

fun JavadocComment.toText(): String {
    return parse().description.toText().replace(Regex("\\R"), " ")
}

fun BlockComment.toText(): String {
    return this.toString()
            .replace(Regex("^/"), " ")
            .replace(Regex("/$"), " ")
            .trimMargin("*")
            .trimIndent()
            .lines()
            .joinToString(" ")
}

fun LineComment.toText(): String {
    return toString()
            .replace(Regex("^\\s*//\\s*"), "")
            .replace(Regex("\\R"), "")
}