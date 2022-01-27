package asketch.alloy.util;

import static asketch.alloy.etc.Constants.CROSS_PRODUCT;
import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.etc.Constants.UNIV;
import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.ARROW;
import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.DOT;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.PLUS;
import static asketch.alloy.etc.Operators.SET;
import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.etc.Operators.TILDE;
import static asketch.etc.Names.BASIC_PRUNING;
import static asketch.etc.Names.DOT_PKL;
import static asketch.etc.Names.EXPR_DIR_PATH;
import static asketch.etc.Names.NO_PRUNING;
import static asketch.opts.DefaultOptions.maxDynamicPruningScope;
import static asketch.opts.DefaultOptions.logger;
import static asketch.util.StringUtil.afterSubstring;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.cand.Type;
import asketch.alloy.etc.ColSpan;
import asketch.alloy.etc.Constants;
import asketch.alloy.etc.RowSpan;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.PruningRule;
import asketch.alloy.util.static_pruning_rules.UnaryInfo;
import asketch.alloy.util.static_pruning_rules.closure.VRule1;
import asketch.alloy.util.static_pruning_rules.closure.VRule2;
import asketch.alloy.util.static_pruning_rules.crossproduct.CRule1;
import asketch.alloy.util.static_pruning_rules.difference.DRule1;
import asketch.alloy.util.static_pruning_rules.difference.DRule9;
import asketch.alloy.util.static_pruning_rules.difference.DRule10;
import asketch.alloy.util.static_pruning_rules.difference.DRule11;
import asketch.alloy.util.static_pruning_rules.difference.DRule2;
import asketch.alloy.util.static_pruning_rules.difference.DRule3;
import asketch.alloy.util.static_pruning_rules.difference.DRule4;
import asketch.alloy.util.static_pruning_rules.difference.DRule5;
import asketch.alloy.util.static_pruning_rules.difference.DRule6;
import asketch.alloy.util.static_pruning_rules.difference.DRule7;
import asketch.alloy.util.static_pruning_rules.difference.DRule8;
import asketch.alloy.util.static_pruning_rules.intersect.IRule1;
import asketch.alloy.util.static_pruning_rules.intersect.IRule2;
import asketch.alloy.util.static_pruning_rules.intersect.IRule3;
import asketch.alloy.util.static_pruning_rules.intersect.IRule4;
import asketch.alloy.util.static_pruning_rules.intersect.IRule5;
import asketch.alloy.util.static_pruning_rules.intersect.IRule6;
import asketch.alloy.util.static_pruning_rules.intersect.IRule7;
import asketch.alloy.util.static_pruning_rules.join.JRule1;
import asketch.alloy.util.static_pruning_rules.join.JRule10;
import asketch.alloy.util.static_pruning_rules.join.JRule11;
import asketch.alloy.util.static_pruning_rules.join.JRule12;
import asketch.alloy.util.static_pruning_rules.join.JRule13;
import asketch.alloy.util.static_pruning_rules.join.JRule14;
import asketch.alloy.util.static_pruning_rules.join.JRule2;
import asketch.alloy.util.static_pruning_rules.join.JRule3;
import asketch.alloy.util.static_pruning_rules.join.JRule4;
import asketch.alloy.util.static_pruning_rules.join.JRule5;
import asketch.alloy.util.static_pruning_rules.join.JRule6;
import asketch.alloy.util.static_pruning_rules.join.JRule7;
import asketch.alloy.util.static_pruning_rules.join.JRule8;
import asketch.alloy.util.static_pruning_rules.join.JRule9;
import asketch.alloy.util.static_pruning_rules.rclosure.RRule1;
import asketch.alloy.util.static_pruning_rules.transpose.TRule1;
import asketch.alloy.util.static_pruning_rules.transpose.TRule2;
import asketch.alloy.util.static_pruning_rules.transpose.TRule3;
import asketch.alloy.util.static_pruning_rules.transpose.TRule4;
import asketch.alloy.util.static_pruning_rules.union.URule1;
import asketch.alloy.util.static_pruning_rules.union.URule2;
import asketch.alloy.util.static_pruning_rules.union.URule3;
import asketch.alloy.util.static_pruning_rules.union.URule4;
import asketch.alloy.util.static_pruning_rules.union.URule5;
import asketch.alloy.util.static_pruning_rules.union.URule6;
import asketch.alloy.util.static_pruning_rules.union.URule7;
import asketch.alloy.util.static_pruning_rules.union.URule8;
import asketch.etc.Names;
import asketch.opts.ASketchGenOpt;
import asketch.opts.Opt;
import asketch.util.Timer;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.alloy4compiler.ast.Browsable;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlloyUtil {

  public static List<Relation> findSigsAndFields(CompModule module) {
    return findSigsAndFields(module, null);
  }

  public static List<Relation> findSigsAndFields(CompModule module,
      Map<String, String> inheritanceMap) {
    List<Relation> sigsAndRelations = new ArrayList<>();
    Browsable sigParent = findSubnode(module, "sig");
    List<? extends Browsable> sigs = sigParent.getSubnodes();
    for (int i = 0; i < sigs.size(); i++) {
      Sig sig = (Sig) sigs.get(i);
      String sigName = afterSubstring(sig.label, Names.SLASH, true);
      // Construct inheritance map.  The type hierarchy goes up to univ.
      if (inheritanceMap != null) {
        boolean isSubsig = sig.isSubsig != null;
        if (isSubsig) { // S extends P
          inheritanceMap
              .put(sigName, afterSubstring(((Sig.PrimSig) sig).parent.label, Names.SLASH, true));
        } else { // S in P
          inheritanceMap.put(sigName,
              afterSubstring(((Sig.SubsetSig) sig).parents.get(0).label, Names.SLASH, true));
        }
      }
      if (sig instanceof PrimSig) {
        // Sig name is also it's type.  We add sigs as relations
        String sigCard = cardOfSig(sig);
        sigsAndRelations
            .add(new Relation(sigName, 1, Collections.singletonList(createType(sigName)),
                Collections.singletonList(sigCard)));
        // Search for relations declared in sigs
        for (int j = (hasExtension(sig) ? 1 : 0);
            j < sig.getSubnodes().size() - (hasFact(sig) ? 1 : 0); j++) {
          Browsable field = sig.getSubnodes().get(j);
          String fieldName = extractHTML(field.getHTML(), Pattern.compile("</b> (.*?) <i>"));
          String relationType = extractHTML(field.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
          String[] relationSubtypes = relationType.split(CROSS_PRODUCT);
          for (int k = 0; k < relationSubtypes.length; k++) {
            relationSubtypes[k] = afterSubstring(relationSubtypes[k], SLASH, true);
          }
          // The arity of relations should be >= 2, we do not
          // support card1 mult1->mult2 card2 except the default ->.
          // But we do support card for binary relations.
          if (relationSubtypes.length == 2) {
            Browsable card = field.getSubnodes().get(0);
            String rightCard = cardOfRel(extractHTML(card.getHTML(), Pattern.compile("(.*?) <i>")));
            Type leftType = createType(relationSubtypes[0]);
            Type rightType = createType(relationSubtypes[1]);
            sigsAndRelations.add(new Relation(fieldName, 2, Arrays.asList(leftType, rightType),
                Arrays.asList(sigCard, rightCard)));
          } else { // relationSubtypes.length >= 3
            List<String> cards = new ArrayList<>();
            for (int k = 0; k < relationSubtypes.length; k++) {
              if (k == 0) {
                cards.add(sigCard);
              } else {
                cards.add(Constants.SET);
              }
            }
            sigsAndRelations.add(
                new Relation(fieldName, relationSubtypes.length, createTypes(relationSubtypes),
                    cards));
          }
        }
      }
    }
    return sigsAndRelations;
  }

  public static void findParametersAndVariables(AlloyProgram alloyProgram, CompModule module,
      String browsableName) {
    Browsable browsable = findSubnode(module, browsableName);
    if (browsable != null) {
      for (int i = 0; i < browsable.getSubnodes().size(); i++) {
        Browsable construct = browsable.getSubnodes().get(i);
        RowSpan rowSpan = new RowSpan(construct.pos().y - 1, construct.pos().y2 - 1);
        List<E> holesInRowSpan = findRelevantExprHoles(rowSpan, alloyProgram.getHoles());
        for (Browsable para : findSubnodes(construct, "parameter")) {
          for (E exprHole : holesInRowSpan) {
            exprHole.addPrimaryRelations(createRelationFromParameter(para));
          }
        }
        visitVariables(construct, holesInRowSpan);
      }
    }
  }

  public static Relation createRelationFromParameter(Browsable para) {
    String paraName = extractHTML(para.getHTML(), Pattern.compile("</b> (.*?) <i>"));
    String paraType = extractHTML(para.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
    String[] paraSubtypes = paraType.split(CROSS_PRODUCT);
    for (int k = 0; k < paraSubtypes.length; k++) {
      paraSubtypes[k] = afterSubstring(paraSubtypes[k], SLASH, true);
    }
    String[] cards = new String[paraSubtypes.length];
    if (paraSubtypes.length == 1) { // Find parameter cardinality if arity == 1
      Browsable child = para.getSubnodes().get(0);
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold == null) { // Explicit cardinality.
        cards[0] = cardOfRel(extractHTML(child.getHTML(), Pattern.compile("(.*?) <i>")));
      } else { // Implicit cardinality is one.
        cards[0] = ONE.getValue();
      }
    } else { // arity >= 2
      for (int k = 0; k < cards.length; k++) {
        cards[k] = SET.getValue();
      }
    }
    return new Relation(paraName, paraSubtypes.length, createTypes(paraSubtypes),
        Arrays.asList(cards));
  }

  private static void visitVariables(Browsable browsable, List<E> exprHoles) {
    LinkedList<Browsable> astQueue = new LinkedList<>();
    Browsable body = findSubnode(browsable, "body");
    if (body != null) {
      astQueue.add(body);
    } else {
      astQueue.add(browsable);
    }
    while (!astQueue.isEmpty()) {
      Browsable astNode = astQueue.remove();
      List<? extends Browsable> children = astNode.getSubnodes();
      if (children.size() >= 2) {
        // First var.
        Browsable quantifiedVar = children.get(0);
        String varBold = extractHTML(quantifiedVar.getHTML(), Pattern.compile("<b>(.*?)</b>"));
        // Last body.
        Browsable quantifiedBody = children.get(children.size() - 1);
        String bodyBold = extractHTML(quantifiedBody.getHTML(), Pattern.compile("<b>(.*?)</b>"));
        // Find quantified formula
        if (varBold != null && varBold.equals("var") && bodyBold != null && bodyBold
            .equals("body")) {
          // Find expression holes in the quantified formula body
          RowSpan bodyRowSpan = new RowSpan(quantifiedBody.pos().y - 1,
              quantifiedBody.pos().y2 - 1);
          List<E> holesInRowSpan = findRelevantExprHoles(bodyRowSpan, exprHoles);
          // Add all variable declarations.
          for (int i = 0; i < children.size() - 1; i++) {
            quantifiedVar = children.get(i);
            for (E exprHole : holesInRowSpan) {
              exprHole.addPrimaryRelations(createRelationFromVariable(quantifiedVar));
            }
          }
        }
      }
      for (Browsable n : astNode.getSubnodes()) {
        String bold = extractHTML(n.getHTML(), Pattern.compile("<b>(.*?)</b>"));
        if (bold == null) {
          astQueue.add(n);
        } else if (bold.equals("field") || bold.equals("sig") || bold.equals("call")) {
          ;
        } else {
          astQueue.add(n);
        }
      }
    }
  }

  public static Relation createRelationFromVariable(Browsable var) {
    // Collect quantified variable name, arity, type and cardinality
    String varName = extractHTML(var.getHTML(), Pattern.compile("</b> (.*?) <i>"));
    String varType = extractHTML(var.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
    String[] varSubtypes = varType.split(CROSS_PRODUCT);
    for (int k = 0; k < varSubtypes.length; k++) {
      varSubtypes[k] = afterSubstring(varSubtypes[k], SLASH, true);
    }
    String[] cards = new String[varSubtypes.length];
    if (varSubtypes.length == 1) { // Find var cardinality if arity == 1
      Browsable child = var.getSubnodes().get(0);
      cards[0] = cardOfRel(extractHTML(child.getHTML(), Pattern.compile("(.*?) <i>")));
    } else { // arity >= 2
      for (int k = 0; k < cards.length; k++) {
        cards[k] = "set";
      }
    }
    return new Relation(varName, varSubtypes.length, createTypes(varSubtypes),
        Arrays.asList(cards));
  }

  private static List<E> findRelevantExprHoles(RowSpan rowSpan, List<? extends Hole> holes) {
    List<E> holesInRowSpan = new ArrayList<>();
    for (Hole hole : holes) {
      if (rowSpan.containsLine(hole.getLineNumber()) && hole instanceof E) {
        holesInRowSpan.add((E) hole);
      }
    }
    return holesInRowSpan;
  }

  public static Browsable findSubnode(Browsable parent, String childName) {
    for (Browsable child : parent.getSubnodes()) {
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold != null && bold.contains(childName)) {
        return child;
      }
    }
    return null;
  }

  public static List<Browsable> findSubnodes(Browsable parent, String childName) {
    List<Browsable> result = new ArrayList<>();
    for (Browsable child : parent.getSubnodes()) {
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold != null && bold.contains(childName)) {
        result.add(child);
      }
    }
    return result;
  }

  public static String extractHTML(String html, Pattern pattern) {
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }

  public static String extractBold(Browsable browsable) {
    String res = extractHTML(browsable.getHTML(), Pattern.compile("<b>(.*?)</b>"));
    return res == null ? null : res.trim();
  }

  public static String extractNormal(Browsable browsable) {
    String res = extractHTML(browsable.getHTML(), Pattern.compile("</b>:?(.*?) <i>"));
    if (res != null) {
      return res.trim();
    }
    res = extractHTML(browsable.getHTML(), Pattern.compile("(.*?) <i>"));
    return res == null ? null : res.trim();
  }

  public static String extractItalic(Browsable browsable) {
    String res = extractHTML(browsable.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
    return res == null ? null : res;
  }

  private static boolean hasExtension(Sig s) {
    if (s.getSubnodes().isEmpty()) {
      return false;
    } else {
      Browsable ext = s.getSubnodes().get(0);
      String sigExt = extractHTML(ext.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      return sigExt != null && (sigExt.equals("extends sig") || sigExt.equals("in sig"));
    }
  }

  private static boolean hasFact(Sig s) {
    if (s.getSubnodes().isEmpty()) {
      return false;
    } else {
      int size = s.getSubnodes().size();
      Browsable fact = s.getSubnodes().get(size - 1);
      String sigFact = extractHTML(fact.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      return sigFact != null && sigFact.equals("fact");
    }
  }

  public static String cardOfSig(Sig s) {
    if (s.isLone != null) {
      return Constants.LONE;
    } else if (s.isOne != null) {
      return Constants.ONE;
    } else if (s.isSome != null) {
      return Constants.SOME;
    } else {
      return Constants.SET;
    }
  }

  public static String cardOfRel(String card) {
    switch (card) {
      case "lone of":
        return Constants.LONE;
      case "one of":
        return Constants.ONE;
      case "some of":
        return Constants.SOME;
      case "set of":
        return Constants.SET;
      default:
        return null;
    }
  }

  public static List<Type> createTypes(String... typeNames) {
    List<Type> types = new ArrayList<>();
    for (String typeName : typeNames) {
      types.add(createType(typeName));
    }
    return types;
  }

  public static Type createType(String type) {
    return createType(type, type);
  }

  public static Type createType(String genType, String pruningType) {
    return new Type(genType, pruningType);
  }

  public static List<String> createCards(String card, int repeats) {
    List<String> cards = new ArrayList<>();
    for (int i = 0; i < repeats; i++) {
      cards.add(card);
    }
    return cards;
  }

  public static boolean isCommutative(Candidate op) {
    return opIsOr(op, PLUS, AMP);
  }

  public static int opNum(Relation rel) {
    char[] arr = rel.getValue().toCharArray();
    int count = 0;
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == '.' || arr[i] == '+' || arr[i] == '&' || arr[i] == '*' || arr[i] == '^'
          || arr[i] == '~') {
        count += 1;
        continue;
      }
      if (arr[i] == '-') {
        if (arr[i + 1] == '>') {
          i = i + 1;
        }
        count += 1;
      }
    }
    return count;
  }

  /**
   * Find the lowest common ancestor type for both leftType and rightType.  If no LCA is found,
   * return null.
   */
  public static String getLCAType(String leftType, String rightType,
      Map<String, String> inheritanceMap) {
    Set<String> leftChain = new HashSet<>();
    while (inheritanceMap.containsKey(leftType)) {
      leftChain.add(leftType);
      leftType = inheritanceMap.get(leftType);
    }
    while (inheritanceMap.containsKey(rightType)) {
      if (leftChain.contains(rightType)) {
        return rightType;
      }
      rightType = inheritanceMap.get(rightType);
    }
    return null;
  }

  public static String getLCATypeWithUniv(String leftType, String rightType,
      Map<String, String> inheritanceMap) {
    String lcaType = getLCAType(leftType, rightType, inheritanceMap);
    if (lcaType != null) {
      return lcaType;
    }
    return UNIV;
  }

  /**
   * Find the greatest common descendant type for both leftType and rightType.  This returns either
   * leftType or rightType.  If leftType and rightType are not in the same inheritance hierarchy,
   * return null.
   */
  public static String getMinimumType(String leftType, String rightType,
      Map<String, String> inheritanceMap) {
    String lcaType = getLCATypeWithUniv(leftType, rightType, inheritanceMap);
    if (leftType.equals(lcaType)) {
      return rightType;
    }
    if (rightType.equals(lcaType)) {
      return leftType;
    }
    return lcaType;
  }

  public static boolean isValidExpression(Candidate op, Relation leftRel, Relation rightRel,
      Map<String, String> inheritanceMap) {
    // Left and right operands must have the same type.
    if (opIsOr(op, PLUS, MINUS, AMP)) {
      if (leftRel.getArity() != rightRel.getArity()) {
        return false;
      }
      for (int i = 0; i < leftRel.getArity(); i++) {
        if (getLCAType(leftRel.getTypes().get(i).getGenType(),
            rightRel.getTypes().get(i).getGenType(), inheritanceMap) == null) {
          return false;
        }
      }
      return true;
    }
    // The last type of the left operand must equal to
    // the first type of the right operand.
    if (opIsOr(op, DOT)) {
      List<Type> leftTypes = leftRel.getTypes();
      List<Type> rightTypes = rightRel.getTypes();
      return getLCAType(leftTypes.get(leftTypes.size() - 1).getGenType(),
          rightTypes.get(0).getGenType(), inheritanceMap) != null
          // TODO(kaiyuanw): Should remove this by implementing more advanced type inference.
          // The current implementation cannot remove n.header if n is List.header.*link with univ type.
          // Dynamic checking gives check { all n: univ | n.header = none } which yields counter-example.
          || leftTypes.get(leftTypes.size() - 1).getGenType().equals(UNIV)
          || rightTypes.get(0).getGenType().equals(UNIV);
    }
    // Default to true.
    return true;
  }

  public static boolean isValidExpression(Candidate op, Relation rel) {
    // Operand must be binary.
    if (opIsOr(op, TILDE)) {
      return rel.getTypes().size() == 2;
    }
    // Operand must be binary and homogeneous.
    if (opIsOr(op, STAR, CARET)) {
      return rel.getTypes().size() == 2 && rel.getTypes().get(0).equals(rel.getTypes().get(1));
    }
    // Default to true.
    return true;
  }

  public static boolean isStaticPruned(ASketchGenOpt opt, List<Relation> preStateRels,
      List<Relation> basicCands, Candidate op, Relation leftRel, int leftDepth, Relation rightRel,
      int rightDepth, Map<String, String> inheritanceMap) {
    // If no pruning rule is specified, return false.
    if (opt.getPruningRule() == NO_PRUNING) {
      return false;
    }
    // If leftRel and rightRel have different state relations, return false.
//    String combineLeftRight = leftRel.getValue() + " " + rightRel.getValue();
    // Prune if (1) combining leftRel and rightRel mix pre and post state relations.
//    if (containsPreAndPostStates(combineLeftRight, preStateRels)) {
//      return true;
//    }
    // TODO(kaiyuanw): Initialize all pruning rules to control whether enable them or not.
    BinaryInfo binaryInfo = new BinaryInfo(basicCands, op, leftRel, leftDepth, rightRel,
        rightDepth, inheritanceMap);
    if (opIsOr(op, PLUS)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        // (1) a + b if a is c + d
        return URule1.given(binaryInfo).isPruned();
      }
      // Prune if
      List<PruningRule> pruningRulesForSetUnion = Arrays.asList(
          // (1) a + b if a is c + d
          URule1.given(binaryInfo),
          // (2) one of the operands is a super set
          URule2.given(binaryInfo),
          // (3) a + b where a is (c +|&|- b) or (b +|&|- c) (not possible as left depth is bigger),
          // or b is (a +|&|- c) or (c +|&|- a)
          URule3.given(binaryInfo),
          // (4) a.|->|&b + a.|->|&c; or a.|->|&b + c.|->|&b
          URule4.given(binaryInfo),
          // (5) *|^a + *|^a
          URule5.given(binaryInfo),
          // (6) ~a + ~b
          URule6.given(binaryInfo),
          // (7) a + *|^a, a.a + *|^a, a.a.a + *|^a, ...
          URule7.given(binaryInfo),
          // (8) a + b + ... where some pair of elements are same
          URule8.given(binaryInfo)
      );
      return pruningRulesForSetUnion.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }
    if (opIsOr(op, AMP)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        // (1) a & b if a is c & d
        return IRule1.given(binaryInfo).isPruned();
      }
      // Prune if
      List<PruningRule> pruningRulesForSetIntersect = Arrays.asList(
          // (1) a & b if a is c & d
          IRule1.given(binaryInfo),
          // (2) a + b if a or b is a super set and type(a) == type(b)
          IRule2.given(binaryInfo),
          // (3) a & b & ... where some pair of elements are same
          IRule3.given(binaryInfo),
          // (4) a->b & a->c; or a->b & c->b.
          // Wrong as a.b & a.c != a.(b&c)
          IRule4.given(binaryInfo),
          // (5) *|^a & *|^a
          IRule5.given(binaryInfo),
          // (6) ~a & ~b
          IRule6.given(binaryInfo),
          // (7) a & *|^a, a.a & *|^a, a.a.a & *|^a, ...
          IRule7.given(binaryInfo)
      );
      return pruningRulesForSetIntersect.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }
    if (opIsOr(op, MINUS)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        return false;
      }
      // Prune if
      List<PruningRule> pruningRulesForSetDifference = Arrays.asList(
          // (1) rightRel is super type
          // Wrong as *a - A->A is not empty
          DRule1.given(binaryInfo),
          // (2) both operands are same
          DRule2.given(binaryInfo),
          // (3) a-(b+c) = (a-b)-c
          DRule3.given(binaryInfo),
          // (4) a - (a +|&|- b) or a - (b +|&|- a)
          DRule4.given(binaryInfo),
          // (5) (a +|&|- b) - b or (b +|&|- a) - b
          DRule5.given(binaryInfo),
          // (6) a->|&b - a->|&c; or a->|&b - c->|&b
          // Wrong as a.b - a.c != a.(b-c)
          DRule6.given(binaryInfo),
          // (7) a - (*|^a), a.a - (*|^a), ...
          DRule7.given(binaryInfo),
          // (8) (*|^a) - (*|^a)
          // Wrong as *a - ^a in iden
          DRule8.given(binaryInfo),
          // (9) ~a - ~b
          DRule9.given(binaryInfo),
          // (10) (a->a) - *b if a has cardinality 1
          DRule10.given(binaryInfo),
          // (11) (a & b) - c = a & (b - c)
          DRule11.given(binaryInfo)
      );
      return pruningRulesForSetDifference.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }
    // Prune if (1) a -> b, where b is c -> d.
    if (opIsOr(op, ARROW)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        // (1) a -> b if a is c -> d
        return CRule1.given(binaryInfo).isPruned();
      }
      // Prune if
      List<PruningRule> pruningRulesForCrossProduct = Arrays.asList(
          // (1) a -> b if a is c -> d
          CRule1.given(binaryInfo)
          // (2) (a.b)->c = a.(b->c), a->(b.c) = (a->b).c
          // Should not use because arity(b->c) may be bigger than the max arity.
//          CRule2.given(binaryInfo)
      );
      return pruningRulesForCrossProduct.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }
    if (opIsOr(op, DOT)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        return false;
      }
      // Prune if
      List<PruningRule> pruningRulesForRelationJoin = Arrays.asList(
          // (1) (a.b).c where arity(b) >= 2
          JRule1.given(binaryInfo),
          // (2) a.(a->b) or (b->a).a if card(a) >= 1
          // Wrong without card as (Node.(Node->BinaryTree)) can be either BinaryTree or empty.
          JRule2.given(binaryInfo),
          // (3) a.(~b) = b.a if arity(a) = 1
          JRule3.given(binaryInfo),
          // (4) a.(*b) = a and (*b).a = a if a is super type
          JRule4.given(binaryInfo),
          // (5) a.(^b) = a.b and (^b).a = b.a if a is super type
          JRule5.given(binaryInfo),
          // (6) a1.a2.a3.a4 where a(i) is b and a(i+1) is *b, or a(i) is *b and a(i+1) is b
          JRule6.given(binaryInfo),
          // (7) a1.a2.a3.a4 where a(i) is ^b and a(i+1) is b (^b.b = b.^b)
          JRule7.given(binaryInfo),
          // (8) a1.a2.a3.a4 where a(i).a(i+1) is *a.*a = *a, *a.^a = ^a.*a = ^a, ^a.^a = a.^a
          JRule8.given(binaryInfo),
          // (9) a1.a2.a3.a4 where a(i).a(i+1) is (~a).(~b) = ~(a.b)
          JRule9.given(binaryInfo),
          // (10) (a-...-b-...).(b->c) = (a->b).(c-...-b-...) = none
          JRule10.given(binaryInfo),
          // (11) a.((b-...-a-...)->c) = (c->(b-...-a-...)).a = none
          JRule11.given(binaryInfo),
          // (12) ~b.a = a.b if arity(a) = 1
          JRule12.given(binaryInfo),
          // (13) A.(A->b) or (b->A).A if arity(A) = 1 and type(b) contains A
          // and b does not contain identity.
          JRule13.given(binaryInfo),
          // (14) a.(b->c) = (a.b)->c if arity(a) + arity(b) > 2, similarly (a->b).c = a->(b.c)
          JRule14.given(binaryInfo)
      );
      return pruningRulesForRelationJoin.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }
    return false;
  }

  public static boolean isStaticPruned(ASketchGenOpt opt, List<Relation> preStateRels, Candidate op,
      Relation rel) {
    // If no pruning rule is specified, return false.
    if (opt.getPruningRule() == NO_PRUNING) {
      return false;
    }
    // Prune if rel mixes pre and post state relations.
//    if (containsPreAndPostStates(rel.getValue(), preStateRels)) {
//      return true;
//    }
    // TODO(kaiyuanw): Initialize all pruning rules to control whether enable them or not.
    UnaryInfo unaryInfo = new UnaryInfo(op, rel);
    if (opIsOr(op, STAR)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        return false;
      }
      // Prune if
      List<PruningRule> pruningRulesForReflexiveClosure = Arrays.asList(
          // (1) * and ^ appear consecutively
          RRule1.given(unaryInfo)
      );
      return pruningRulesForReflexiveClosure.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }

    if (opIsOr(op, CARET)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        return false;
      }
      // Prune if
      List<PruningRule> pruningRulesForClosure = Arrays.asList(
          // (1) ^ and * appear consecutively
          VRule1.given(unaryInfo),
          // (2) ^(a->b) = a->b
          VRule2.given(unaryInfo)
      );
      return pruningRulesForClosure.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }

    if (opIsOr(op, TILDE)) {
      if (opt.getPruningRule() == BASIC_PRUNING) {
        return false;
      }
      // Prune if
      List<PruningRule> pruningRulesForTranspose = Arrays.asList(
          // (1) ~ appear before *, ^ or ~
          TRule1.given(unaryInfo),
          // (2) ~a where a is b -> c
          TRule2.given(unaryInfo),
          // (3) ~(a.b) where a is c->d or b is c->d
          TRule3.given(unaryInfo),
          // (4) ~(a+|&|-|.b) if a or b is ~c
          TRule4.given(unaryInfo)
      );
      return pruningRulesForTranspose.stream().anyMatch(PruningRule::isEnabledAndPruned);
    }
    return false;
  }

  public static boolean opIsOr(Candidate op, Candidate... cands) {
    for (Candidate cand : cands) {
      if (op.getValue().equals(cand.getValue())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return true if str contains both x and x', where x could be any relation.
   */
  private static boolean containsPreAndPostStates(String str, List<Relation> preStateRels) {
    List<Integer> preStateIndexes = new ArrayList<>();
    List<Integer> postStateIndexes = new ArrayList<>();
    for (Relation preStateRel : preStateRels) {
      String preStateString = preStateRel.getValue();
      String postStateString = preStateString + "'";
      preStateIndexes.addAll(indexesOf(str, preStateString));
      postStateIndexes.addAll(indexesOf(str, postStateString));
    }
    // Does not contain post state relations
    if (postStateIndexes.size() == 0) {
      return false;
    }
    // Only contains post state relations
    if (preStateIndexes.equals(postStateIndexes)) {
      return false;
    }
    // Contains both pre and post state relations
    return true;
  }

  private static List<Integer> indexesOf(String str, String substring) {
    List<Integer> indexes = new ArrayList<>();
    int lastIndex = 0;
    while (lastIndex != -1) {
      lastIndex = str.indexOf(substring, lastIndex);
      if (lastIndex != -1) {
        indexes.add(lastIndex);
        lastIndex += substring.length();
      }
    }
    return indexes;
  }

  /**
   * Return true if all leaves are super types.  Super type means value == type and type is top
   * level.  For example, Farmer extends Object so Farmer is not super type.
   */
  public static boolean isSuperType(Relation root, Map<String, String> inheritanceMap) {
    if (root.getSubRelations() == null) {
      String value = root.getValue();
      return root.getArity() == 1
          && value.equals(root.getTypes().get(0).getPruneType())
          && UNIV.equals(inheritanceMap.get(value));
    }
    // Super type must be a single type or connected with cross product S, S->T, ...
    if (!opIsOr(root.getOp(), ARROW)) {
      return false;
    }
    for (Relation subRelation : root.getSubRelations()) {
      if (!isSuperType(subRelation, inheritanceMap)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Create a new relation with the binary op, left operand and right operand.
   */
  public static Relation createRelation(int cost, Candidate op, Relation leftRel, Relation rightRel,
      Map<String, String> inheritanceMap) {
    String newValue = "(" + leftRel.getValue() + op + rightRel.getValue() + ")";
    int newArity = 0;
    int leftRelArity = leftRel.getArity();
    int rightRelArity = rightRel.getArity();
    List<Type> newTypes = new ArrayList<>();
    List<Type> leftTypes = leftRel.getTypes();
    List<Type> rightTypes = rightRel.getTypes();
    List<String> newCards = new ArrayList<>();
    List<String> leftCards = leftRel.getCards();
    List<String> rightCards = rightRel.getCards();
    boolean hasIden = false;
    // +, - and & does not alternate the arity
    if (opIsOr(op, PLUS)) {
      newArity = leftRelArity;
      for (int i = 0; i < leftRelArity; i++) {
        String lcaType = getLCAType(leftTypes.get(i).getGenType(), rightTypes.get(i).getGenType(),
            inheritanceMap);
        String lcaTypeWithUniv = getLCATypeWithUniv(leftTypes.get(i).getPruneType(),
            rightTypes.get(i).getPruneType(), inheritanceMap);
        newTypes.add(createType(lcaType, lcaTypeWithUniv));
      }
      newCards.addAll(createCards(Constants.SET, leftRelArity));
      if (leftRel.hasIden() || rightRel.hasIden()) {
        hasIden = true;
      }
    }
    if (opIsOr(op, AMP)) {
      newArity = leftRelArity;
      for (int i = 0; i < leftRelArity; i++) {
        String lcaType = getLCAType(leftTypes.get(i).getGenType(), rightTypes.get(i).getGenType(),
            inheritanceMap);
        String minimumType = getMinimumType(leftTypes.get(i).getPruneType(),
            rightTypes.get(i).getPruneType(), inheritanceMap);
        newTypes.add(createType(lcaType, minimumType));
      }
      newCards.addAll(leftCards);
      if (leftRel.hasIden() && rightRel.hasIden()) {
        hasIden = true;
      }
    }
    if (opIsOr(op, MINUS)) {
      newArity = leftRelArity;
      for (int i = 0; i < leftRelArity; i++) {
        String lcaType = getLCAType(leftTypes.get(i).getGenType(), rightTypes.get(i).getGenType(),
            inheritanceMap);
        newTypes.add(createType(lcaType, leftTypes.get(i).getPruneType()));
      }
      newCards.addAll(leftCards);
      if (leftRel.hasIden()) {
        hasIden = true;
      }
    }
    // -> increases the arity to leftRelArity + rightRelArity
    if (opIsOr(op, ARROW)) {
      newArity = leftRelArity + rightRelArity;
      newTypes.addAll(leftTypes);
      newTypes.addAll(rightTypes);
      newCards.addAll(leftCards);
      newCards.addAll(rightCards);
    }
    // . decreases the arity by 2
    if (opIsOr(op, DOT)) {
      newArity = leftRelArity + rightRelArity - 2;
      // If leftRel has identity, it must be binary relation
      for (int i = 0; i < leftTypes.size() - 1; i++) {
        newTypes.add(leftRel.hasIden() ? rightTypes.get(0) : leftTypes.get(i));
//        newTypes.add(leftTypes.get(i));
        newCards.add(leftCards.get(i));
      }
      // If rightRel has identity, it must be binary relation
      for (int i = 1; i < rightTypes.size(); i++) {
        newTypes.add(rightRel.hasIden() ? leftTypes.get(leftTypes.size() - 1) : rightTypes.get(i));
//        newTypes.add(rightTypes.get(i));
        newCards.add(rightCards.get(i));
      }
    }
    return new Relation(newValue, cost, newArity, newTypes, hasIden, newCards, op,
        Arrays.asList(leftRel, rightRel));
  }

  public static Relation createRelation(int cost, Candidate op, Relation rel) {
    String newValue = "(" + op + rel.getValue() + ")";
    List<Type> newTypes = new ArrayList<>();
    List<Type> relTypes = rel.getTypes();
    List<String> newCards = new ArrayList<>();
    List<String> relCards = rel.getCards();
    boolean hasIden = rel.hasIden();
    // If op is ~, then types and cardinality should be reversed
    if (opIsOr(op, TILDE)) {
      for (int i = relTypes.size() - 1; i >= 0; i--) {
        newTypes.add(relTypes.get(i));
        newCards.add(relCards.get(i));
      }
    }
    // If op is *, then the pruning type should be univ
    if (opIsOr(op, STAR)) {
      for (int i = 0; i < relTypes.size(); i++) {
        newTypes.add(createType(relTypes.get(i).getGenType(), UNIV));
        newCards.add(relCards.get(i));
      }
      hasIden = true;
    }
    // If op is ^, then the type should be same as the operand
    if (opIsOr(op, CARET)) {
      for (int i = 0; i < relTypes.size(); i++) {
        newTypes.add(relTypes.get(i));
        newCards.add(relCards.get(i));
      }
    }
    return new Relation(newValue, cost, rel.getArity(), newTypes, hasIden, newCards, op,
        Collections.singletonList(rel));
  }


  /**
   * This method converts a list of Candidate to a list of Relation. Use with caution as the type
   * may not be convertible to Relation.
   */
  public static List<Relation> convertToRelations(List<Candidate> cands) {
    List<Relation> relations = new ArrayList<>();
    for (Candidate cand : cands) {
      relations.add((Relation) cand);
    }
    return relations;
  }

  public static CompModule compileAlloyModule(String modelPath) {
    try {
      return CompUtil.parseEverything_fromFile(A4Reporter.NOP, null, modelPath);
    } catch (Err e) {
      return null;
    }
  }

  public static boolean isDynamicPruned(
      CompModule checkModule, EquivalenceCheckFormula checkFormula, Relation relation,
      Map<Integer, Map<Integer, List<Relation>>> cands, int startLevel, int currentDepthOrCost,
      List<Relation> currentDepthRelations) {
    A4Options options = new A4Options();
    // We check if the relation is equivalent to some relation
    // with the same arity and type in previous depths
    for (int depthOrCost = startLevel; depthOrCost < currentDepthOrCost; depthOrCost++) {
      // Find existing relations with the same arity and type
      if (cands.get(depthOrCost).get(relation.getArity())
          .stream()
          .filter(rel -> relation.getTypes().equals(rel.getTypes()))
              // Check equivalence on the fly.
          .anyMatch(existingRelation ->
              // We build the negation of the formula simply because
              // the check command does not automatically translate
              // formula to it's negation.
              areEquivalent(checkModule, checkFormula, relation, existingRelation, options))) {
        return true;
      }
    }
    // Check equivalent of the current depth
    if (currentDepthRelations.stream()
        .filter(rel -> relation.getTypes().equals(rel.getTypes()))
            // Check equivalence on the fly.
        .anyMatch(existingRelation ->
            areEquivalent(checkModule, checkFormula, relation, existingRelation, options))) {
      return true;
    }
    // Check equivalence between relation and special relations
    Relation noneRelation = createSpecialRelation(Constants.NONE, relation.getArity());
    if (areEquivalent(checkModule, checkFormula, relation, noneRelation, options)) {
      return true;
    }
    Relation univRelation = createSpecialRelation(Constants.UNIV, relation.getArity());
    if (areEquivalent(checkModule, checkFormula, relation, univRelation, options)) {
      return true;
    }
    if (relation.getArity() == 2) {
      Relation identityRelation = new Relation(Constants.IDEN, 1, 2);
      if (areEquivalent(checkModule, checkFormula, relation, identityRelation, options)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean areEquivalent(CompModule checkModule, EquivalenceCheckFormula checkFormula,
      Relation source, Relation target, A4Options options) {
    String negatedFormulaString = checkFormula.buildNegatedFormula(source, target);
    try {
      Expr negatedFormula = CompUtil
          .parseOneExpression_fromString(checkModule, negatedFormulaString);
      Timer timer = new Timer();
      timer.start();
      for (int scope = 1; scope <= maxDynamicPruningScope; scope++) {
        Command checkCommand = new Command(true, scope, -1, -1, negatedFormula);
        A4Solution sol = TranslateAlloyToKodkod
            .execute_command(A4Reporter.NOP, checkModule.getAllReachableSigs(), checkCommand,
                options);
        if (sol != null && sol.satisfiable()) {
          return false;
        }
      }
      timer.stop();
      logger.debug(
          source.getValue() + " = " + target.getValue() + " (" + timer.getMilliSeconds() + ")");
//      Command checkCommand = new Command(true, maxDynamicPruningScope, -1, -1, negatedFormula);
//      Timer timer = new Timer();
//      timer.start();
//      A4Solution sol = TranslateAlloyToKodkod
//          .execute_command(A4Reporter.NOP, checkModule.getAllReachableSigs(), checkCommand,
//              options);
//      timer.stop();
      // No counter example found.  The relation is equivalent to some existing relation.
//      if (sol != null) {
//        if (!sol.satisfiable()) {
//          logger.debug(
//              source.getValue() + " = " + target.getValue() + " (" + timer.getMilliSeconds() + ")");
//          return true;
//        }
//        logger.debug(source.getValue() + " != " + target.getValue() + " (" + timer.getMilliSeconds() + ")");
//      }
    } catch (Err err) {
      err.printStackTrace();
    }
    return true;
  }

  /**
   * Create special relations like none, none->none,... and univ, univ->univ, ...
   *
   * @param repeats is the number of times to use ->, normally this is equal to the arity.  But for
   * iden, this should be 1.
   */
  public static Relation createSpecialRelation(String noneOrUniv, int repeats) {
    StringBuilder noneOrUnivString = new StringBuilder();
    for (int repeat = 0; repeat < repeats; repeat++) {
      noneOrUnivString.append(noneOrUniv);
      if (repeat != repeats - 1) {
        noneOrUnivString.append("->");
      }
    }
    return new Relation(noneOrUnivString.toString(), repeats - 1, repeats);
  }

  public static int[] connectExprHolesWithSameExpressions(List<Hole> holes) {
    // Quick union connected graph
    int[] connectedHoles = new int[holes.size()];
    // Initialize connected graph with -1
    for (int i = 0; i < connectedHoles.length; i++) {
      connectedHoles[i] = -1;
    }
    // uniqueRelations keeps track of the holes that contains unique set of
    // basic relations.
    Map<String, Integer> uniqueRelations = new HashMap<>();
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      if (!(hole instanceof E)) {
        continue;
      }
      E exprHole = (E) hole;
      List<Relation> basicRelationsForHole = new ArrayList<>(exprHole.getPrimaryRelations());
      // Sort basic relations of that hole for easy comparison.
      basicRelationsForHole.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
      StringBuilder holeId = new StringBuilder();
      // Construct the key for the hole.  Note that holes with the same key
      // share the same set of basic relations
      basicRelationsForHole.stream().forEach(relation -> holeId.append(relation.getValue()));
      // Find the index of the first hole with the key
      int rootHoleIndex = uniqueRelations.getOrDefault(holeId.toString(), i);
      uniqueRelations.putIfAbsent(holeId.toString(), rootHoleIndex);
      // Connect the first hole with the same basic candidates to the rest holes.
      // Quick union
      connectedHoles[i] = rootHoleIndex;
    }
    return connectedHoles;
  }

  /**
   * This method computes a unique key for holes that can share the same set of expressions.  We
   * sort basicCands and concatenate values in basicCands as our key.
   */
  public static String computeSerialPath(Opt opt, List<Relation> basicRelations) {
    StringBuilder key = new StringBuilder();
    basicRelations.stream().forEach(cand -> key.append(cand.getValue()));
    // TODO(kaiyuanw): This is a hacking way to reuse expression generated by previous runs.
    String modelName = opt.getModelName();
    modelName =
        modelName.contains("_") ? modelName.substring(0, modelName.indexOf("_")) : modelName;
    return Paths
        .get(EXPR_DIR_PATH, modelName + "-" + opt.getPruningRuleName() + "-" + key.toString() + DOT_PKL)
        .toString();
  }

  public static List<Relation> getAllRelationsByArity(
      Map<Integer, Map<Integer, List<Relation>>> relations, int arity) {
    List<Relation> relationsByArity = new ArrayList<>();
    relations.entrySet().stream()
        .forEach(e1 -> e1.getValue().entrySet().stream()
            .filter(e2 -> e2.getKey() == arity)
            .forEach(e3 -> relationsByArity.addAll(e3.getValue())));
    return relationsByArity;
  }

  /**
   * Create a unique name for the original sigs and replace all occurrence of the original name in
   * the relationName with the new name.
   */
  public static String generateRelationNameInMetaModel(String relationName,
      List<Relation> sigsAndFields) {
    for (Relation relation : sigsAndFields) {
      // Ignore relations other than sigs, this is because
      // we don't want to handle x and x'.
      if (relation.getTypes().size() != 1) {
        continue;
      }
      String sigName = relation.getValue();
      String newSigName = sigName + "s";
      relationName = relationName.replaceAll(sigName, newSigName);
    }
    return relationName;
  }

  public static void putNodeToHoleMapIfAbsent(Browsable node, String nodeValue, List<Hole> holes,
      Set<Hole> assignedHoles, List<Relation> relations, Map<Browsable, Integer> nodeToHoleMap) {
    // All nodes are associated with some nodes.
    if (assignedHoles.size() == holes.size()) {
      return;
    }
    // Iterate over each hole and see if the pos matches its line number and column span.
    Pos pos = node.pos();
    for (int holeId = 0; holeId < holes.size(); holeId++) {
      Hole hole = holes.get(holeId);
      // Skip if the content does not match or the hole is assigned before
      if (!hole.getContent().equals(nodeValue) || assignedHoles.contains(hole)) {
        continue;
      }
      // Skip if the hole fall outside of pos
      if (hole.getLineNumber() < pos.y - 1 || hole.getLineNumber() > pos.y2 - 1) {
        continue;
      }
      // If the node is in a single line and it intersects with the hole,
      // then add it.
      if (pos.y == pos.y2) {
        ColSpan colSpan = hole.getColSpan();
        if (pos.x - 1 >= colSpan.getEnd() || pos.x2 <= colSpan.getBegin()) {
          continue;
        }
        if (!(hole instanceof E)) {
          hole.addPrimaryRelations(relations);
        }
        nodeToHoleMap.put(node, holeId);
        assignedHoles.add(hole);
        return;
      }
      // Pick the first hole and assign it to the node.  This is not correct but may
      // be sufficient for our example.
      if (!(hole instanceof E)) {
        hole.addPrimaryRelations(relations);
      }
      nodeToHoleMap.put(node, holeId);
      assignedHoles.add(hole);
      break;
    }
  }
}
