import com.github.javaparser.ast.Node
import com.github.javaparser.ast.comments.*

fun Comment.startLine(): Int = range.map { it.begin.line }.orElse(-1)

val CommentsCollection.contiguousLineComments: Sequence<ContiguousLineComments>
    get() = lineComments
            .sortedWith(Node.NODE_BY_BEGIN_POSITION)
            .contiguous { prev, current -> prev.startLine() + 1 == current.startLine() }

fun JavadocComment.toText(): String {
    return parse().description.toText().replace(Regex("\\s*\\R\\s*"), " ")
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

typealias ContiguousLineComments = List<LineComment>

fun ContiguousLineComments.startLine(): Int = first().startLine()
fun ContiguousLineComments.toText(): String = joinToString(" ") { it.toText() }