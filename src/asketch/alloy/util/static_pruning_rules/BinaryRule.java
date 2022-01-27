package asketch.alloy.util.static_pruning_rules;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import java.util.List;
import java.util.Map;

public abstract class BinaryRule extends PruningRule {

  protected List<Relation> basicCands;
  protected Candidate op;
  protected Relation leftRel;
  protected int leftDepth;
  protected Relation rightRel;
  protected int rightDepth;
  protected Map<String, String> inheritanceMap;

  public BinaryRule(BinaryInfo binaryInfo) {
    this.basicCands = binaryInfo.getBasicCands();
    this.op = binaryInfo.getOp();
    this.leftRel = binaryInfo.getLeftRel();
    this.leftDepth = binaryInfo.getLeftDepth();
    this.rightRel = binaryInfo.getRightRel();
    this.rightDepth = binaryInfo.getRightDepth();
    this.inheritanceMap = binaryInfo.getInheritanceMap();
  }
}
