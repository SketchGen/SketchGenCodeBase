package asketch.alloy.util.static_pruning_rules;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import java.util.List;
import java.util.Map;

public class BinaryInfo {

  private List<Relation> basicCands;
  private Candidate op;
  private Relation leftRel;
  private int leftDepth;
  private Relation rightRel;
  private int rightDepth;
  private Map<String, String> inheritanceMap;

  public BinaryInfo(List<Relation> basicCands, Candidate op, Relation leftRel,
      int leftDepth, Relation rightRel, int rightDepth, Map<String, String> inheritanceMap) {
    this.basicCands = basicCands;
    this.op = op;
    this.leftRel = leftRel;
    this.leftDepth = leftDepth;
    this.rightRel = rightRel;
    this.rightDepth = rightDepth;
    this.inheritanceMap = inheritanceMap;
  }

  public List<Relation> getBasicCands() {
    return basicCands;
  }

  public Candidate getOp() {
    return op;
  }

  public Relation getLeftRel() {
    return leftRel;
  }

  public int getLeftDepth() {
    return leftDepth;
  }

  public Relation getRightRel() {
    return rightRel;
  }

  public int getRightDepth() {
    return rightDepth;
  }

  public Map<String, String> getInheritanceMap() {
    return inheritanceMap;
  }
}
