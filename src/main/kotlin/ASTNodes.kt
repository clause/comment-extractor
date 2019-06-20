import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTVisitor

inline fun <reified T : ASTNode> ASTNode.find(): List<T> {

    val nodes = mutableListOf<T>()

    accept(object : ASTVisitor(true) {
        override fun preVisit(node: ASTNode) {
            nodes += node as? T ?: return
        }
    })

    return nodes
}
