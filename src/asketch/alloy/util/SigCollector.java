package asketch.alloy.util;

import static parser.etc.Names.COMMA;
import static parser.etc.Names.NEW_LINE;

import java.util.Arrays;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.SigDecl;
import parser.ast.visitor.PrettyStringVisitor;

public class SigCollector extends PrettyStringVisitor {

  @Override
  public String visit(ModelUnit n, Object arg) {
    String moduleDecl = n.getModuleDecl().accept(this, arg);
    String openDecls = String.join(NEW_LINE,
        n.getOpenDeclList().stream().map(openDecl -> openDecl.accept(this, arg))
            .collect(Collectors.toList()));
    String sigDecls = String.join(NEW_LINE,
        n.getSigDeclList().stream().map(signature -> signature.accept(this, arg))
            .collect(Collectors.toList()));
    return String.join(NEW_LINE, Arrays.<CharSequence>asList(moduleDecl, openDecls, sigDecls));
  }

  @Override
  public String visit(SigDecl n, Object arg) {
    inSigDecl = true;
    // We do not collect signature fact.
    String sigDeclAsString =
        (n.isAbstract() ? "abstract " : "") + n.getMult() + "sig " + n.getName() + " " + (
        n.isTopLevel() ? "" : (n.isSubsig() ? "extends" : "in") + " " + n.getParentName() + " ")
        + "{" +
        (n.getFieldList().size() > 0 ? NEW_LINE + String.join(COMMA + NEW_LINE,
            n.getFieldList().stream().map(field -> field.accept(this, arg))
                .collect(Collectors.toList())) + NEW_LINE : "") +
        "}";
    inSigDecl = false;
    return sigDeclAsString;
  }
}
