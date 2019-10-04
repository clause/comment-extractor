import com.github.javaparser.ast.Node
import com.github.javaparser.ast.comments.*
import com.github.javaparser.ast.visitor.GenericVisitor
import com.github.javaparser.ast.visitor.VoidVisitor

val Comment.beginLine: Int
    get() = range.map { it.begin.line }.orElse(-1)

val CommentsCollection.contiguousLineComments: Sequence<ContiguousLineComment>
    get() = lineComments
            .sortedWith(Node.NODE_BY_BEGIN_POSITION)
            .contiguous { prev, current -> prev.beginLine + 1 == current.beginLine }

fun JavadocComment.toText(): String {
    return parse().description.toText()
            .replace(Regex("\\s*\\R\\s*"), " ")
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

typealias ContiguousLineComment = List<LineComment>

val ContiguousLineComment.beginLine: Int
    get() = first().beginLine

fun ContiguousLineComment.toText(): String = joinToString(" ") { it.toText() }
