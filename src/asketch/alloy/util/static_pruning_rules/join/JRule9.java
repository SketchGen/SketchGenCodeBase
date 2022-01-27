package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.TILDE;
import static asketch.alloy.util.AlloyUtil.createRelation;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule9 extends BinaryRule {

  private JRule9(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule9 given(BinaryInfo binaryInfo) {
    return new JRule9(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return consecutiveNodesUnderOps(createRelation(-1, op, leftRel, rightRel, inheritanceMap), op,
        (prev, cur) -> opIsOr(prev.getOp(), TILDE) && opIsOr(cur.getOp(), TILDE));
  }
}
