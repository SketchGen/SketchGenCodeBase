package asketch.alloy.util;

import static asketch.alloy.util.AlloyUtil.isSuperType;

import asketch.alloy.cand.Relation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is the String representation of the Alloy equivalent checking formula with left and
 * right holes to fill in expressions.
 */
public class EquivalenceCheckFormula {

  private List<Node> formula;
  private Node leftHole;
  private Node rightHole;

  public EquivalenceCheckFormula(List<Relation> basicRelations, Map<String,String> inheritanceMap) {
    StringBuilder commandTemplate = new StringBuilder();
    for (Relation relation : basicRelations) {
      if (relation.getArity() != 1 || isSuperType(relation, inheritanceMap)) {
        continue;
      }
      commandTemplate.append(
          "all " + relation.getValue() + ": " + relation.getCards().get(0) + " " + relation
              .getTypes().get(0) + " | ");
    }
    
    this.formula = new ArrayList<>();
    formula.add(new Node( commandTemplate.toString()));
    this.leftHole = new Node("\\L\\");
    formula.add(leftHole);
    formula.add(new Node(" = "));
    this.rightHole = new Node("\\R\\");
    formula.add(rightHole);

  }

  /**
   * Build negation of the formula.
   */
  public String buildNegatedFormula(Relation leftRel, Relation rightRel) {
    leftHole.setText(leftRel.getValue());
    rightHole.setText(rightRel.getValue());
    return "!(" + toString() + ")";
  }

  @Override
  public String toString() {
    StringBuilder res = new StringBuilder();
    formula.stream().forEach(node -> res.append(node.toString()));
    return res.toString();
  }

  private static class Node {

    private String text;

    public Node(String text) {
      this.text = text;
    }

    public void setText(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }
}
