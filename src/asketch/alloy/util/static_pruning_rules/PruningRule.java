package asketch.alloy.util.static_pruning_rules;

import static asketch.alloy.etc.Operators.EMPTY;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.cand.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class PruningRule {

  protected boolean enabled;

  public PruningRule() {
    this.enabled = true;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public abstract boolean isPruned();

  public boolean isEnabledAndPruned() {
    return isEnabled() && isPruned();
  }

  protected boolean sameRelations(Relation rel1, Relation rel2) {
    if (rel1 == null || rel2 == null) {
      return rel1 == rel2;
    }
    return rel1.getValue().equals(rel2.getValue());
  }

  protected Relation getChild(Relation parent, int childIndex) {
    return parent.getSubRelations().get(childIndex);
  }

  protected boolean opIsOr(Candidate op, Candidate... cands) {
    for (Candidate cand : cands) {
      if (op.equals(cand)) {
        return true;
      }
    }
    return false;
  }

  protected boolean pruningTypeMatches(List<Type> leftTypes, List<Type> rightTypes) {
    return leftTypes.size() == rightTypes.size() && IntStream.range(0, leftTypes.size())
        .allMatch(i -> leftTypes.get(i).getPruneType().equals(rightTypes.get(i).getPruneType()));
  }

  protected boolean opIsAll(Candidate op, Relation relation) {
    return relation.getOp().equals(EMPTY) || relation.getOp().equals(op) && relation
        .getSubRelations().stream().allMatch(subRelation -> opIsAll(op, subRelation));
  }

  protected boolean leafIsAll(Relation atomicRelation, Relation relation) {
    if (relation.getOp().equals(EMPTY)) {
      return relation.equals(atomicRelation);
    }
    return relation.getSubRelations().stream()
        .allMatch(subRelation -> leafIsAll(atomicRelation, subRelation));
  }

  /**
   * Find a barrier of operators in an AST and return true if all direct children are unique.
   */
  protected boolean uniqueNodesUnderOps(Relation root, Candidate op) {
    Set<Relation> visited = new HashSet<>();
    return uniqueNodesUnderOps(root, op, visited);
  }

  private boolean uniqueNodesUnderOps(Relation root, Candidate op, Set<Relation> visited) {
    if (!root.getOp().equals(op)) {
      return visited.add(root);
    }
    return root.getSubRelations().stream()
        .allMatch(subRelation -> uniqueNodesUnderOps(subRelation, op, visited));
  }

  /**
   * Find a barrier of operators in an AST and return true if all direct children are same.
   */
  protected Relation duplicateNodesUnderOps(Relation root, Candidate op) {
    Set<Relation> visited = new HashSet<>();
    if (duplicateNodesUnderOps(root, op, visited)) {
      return visited.iterator().next();
    }
    return null;
  }

  private boolean duplicateNodesUnderOps(Relation root, Candidate op, Set<Relation> visited) {
    if (!root.getOp().equals(op)) {
      if (visited.isEmpty()) {
        return visited.add(root);
      }
      return !visited.add(root);
    }
    return root.getSubRelations().stream()
        .allMatch(subRelation -> duplicateNodesUnderOps(subRelation, op, visited));
  }

  static class Wrapper {

    Relation prev;

    public void setPrev(Relation prev) {
      this.prev = prev;
    }

    public Relation getPrev() {
      return prev;
    }
  }

  /**
   * Find a barrier of operators in an AST and return true if any of the two consecutive children
   * meet certain constraints.
   */
  protected boolean consecutiveNodesUnderOps(Relation root, Candidate op,
      BiFunction<Relation, Relation, Boolean> matcher) {
    Wrapper wrapper = new Wrapper();
    return consecutiveNodesUnderOps(root, op, matcher, wrapper);
  }

  private boolean consecutiveNodesUnderOps(Relation root, Candidate op,
      BiFunction<Relation, Relation, Boolean> matcher, Wrapper wrapper) {
    if (!root.getOp().equals(op)) {
      if (wrapper.getPrev() != null && matcher.apply(wrapper.getPrev(), root)) {
        return true;
      }
      wrapper.setPrev(root);
      return false;
    }
    return root.getSubRelations().stream()
        .anyMatch(subRelation -> consecutiveNodesUnderOps(subRelation, op, matcher, wrapper));
  }

  /**
   * Go to left subtree from root as long as op is as specified.  If a constraint hold for any
   * iteration, return true.  Otherwise, return false. For example, process (((a-b)-c)-d) with the
   * following order: (1) (((a-b)-c)-d) (2) ((a-b)-c) (3) (a-b) (4) a
   */
  protected boolean rightChildUnderLeftImbalancedOps(Relation root, Candidate op,
      Function<Relation, Boolean> matcher) {
    return root.getOp().equals(op) && (matcher.apply(root) || rightChildUnderLeftImbalancedOps(
        getChild(root, 0), op, matcher));
  }
}
