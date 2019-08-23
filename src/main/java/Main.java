import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.CommentsCollection;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final JavaParser parser = new JavaParser();

    public static void main(String[] args) throws IOException {

        CommentsCollection commentsCollection = parser.parse(new File("data/BatchPredictor.java")).getCommentsCollection().orElseThrow(RuntimeException::new);

        for (Comment comment : commentsCollection.getComments()) {
            int startLine = comment.getRange().map(r -> r.begin.line).orElse(-1);
            System.out.printf("%d%n", startLine);
        }
    }
}
