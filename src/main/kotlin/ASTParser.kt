import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.CompilationUnit
import java.io.File

class ASTParser(private val level: Int = AST.JLS12,
                private val kind: Int = ASTParser.K_COMPILATION_UNIT,
                private val resolveBindings: Boolean = true,
                classpathEntries: Iterable<File> = emptyList(),
                sourcepathEntries: Iterable<File> = emptyList()) {

    private val classpathEntries = classpathEntries.map(File::getAbsolutePath).toTypedArray()
    private val sourcepathEntries = sourcepathEntries.map(File::getAbsolutePath).toTypedArray()

    fun parse(unitName: String, source: String) = with(ASTParser.newParser(level)) {
        setKind(kind)
        setSource(source.toCharArray())
        setUnitName(unitName)
        setResolveBindings(resolveBindings)
        setEnvironment(classpathEntries, sourcepathEntries, null, true)
        createAST(NullProgressMonitor()) as CompilationUnit
    }

    fun parse(file: File) = parse(file.path, file.readText())
}