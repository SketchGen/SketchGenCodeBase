package asketch.alloy.util.static_pruning_rules.join;

import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.util.AlloyUtil.createRelation;

import asketch.alloy.util.static_pruning_rules.BinaryInfo;
import asketch.alloy.util.static_pruning_rules.BinaryRule;

public class JRule8 extends BinaryRule {

  private JRule8(BinaryInfo binaryInfo) {
    super(binaryInfo);
  }

  public static JRule8 given(BinaryInfo binaryInfo) {
    return new JRule8(binaryInfo);
  }

  @Override
  public boolean isPruned() {
    return consecutiveNodesUnderOps(createRelation(-1, op, leftRel, rightRel, inheritanceMap), op,
        (prev, cur) -> opIsOr(prev.getOp(), STAR, CARET) && opIsOr(cur.getOp(), STAR, CARET)
            && sameRelations(getChild(prev, 0), getChild(cur, 0)));
  }
}
