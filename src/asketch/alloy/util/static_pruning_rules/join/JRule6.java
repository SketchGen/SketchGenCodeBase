package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.util.AlloyUtil.createRelation;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule6 extends BinaryRule {

  private JRule6(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule6 given(BinaryInfo binaryInfo) {
    return new JRule6(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return consecutiveNodesUnderOps(createRelation(-1, op, leftRel, rightRel, inheritanceMap), op,
        (prev, cur) -> (opIsOr(prev.getOp(), STAR) && sameRelations(getChild(prev, 0), cur))
            || (opIsOr(cur.getOp(), STAR) && sameRelations(prev, getChild(cur, 0))));
  }
}
