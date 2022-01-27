package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.util.AlloyUtil.createRelation;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule7 extends BinaryRule {

  private JRule7(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule7 given(BinaryInfo binaryInfo) {
    return new JRule7(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return consecutiveNodesUnderOps(createRelation(-1, op, leftRel, rightRel, inheritanceMap), op,
        (prev, cur) -> opIsOr(prev.getOp(), CARET) && sameRelations(getChild(prev, 0), cur));
  }
}
