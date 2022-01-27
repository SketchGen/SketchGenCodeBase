package asketch.opts;

public class ASketchGenOpt extends Opt {

  public enum BoundType {
    DEPTH,
    COST
  }

  // Bound type.
  private BoundType boundType = BoundType.COST;
  private int maxDepthOrCost;
  private int maxArity;
  private int maxOpNum;

  public ASketchGenOpt(String modelName, int maxDepthOrCost, int maxArity, int maxOpNum,
      String pruningRule) {
    super(modelName, pruningRule);
    this.maxDepthOrCost = maxDepthOrCost;
    this.maxArity = maxArity;
    this.maxOpNum = maxOpNum;
  }

  public boolean boundOnDepth() {
    return boundType == BoundType.DEPTH;
  }

  public boolean boundOnCost() {
    return boundType == BoundType.COST;
  }

  public BoundType getBoundType() {
    return boundType;
  }

  public int getMaxDepthOrCost() {
    return maxDepthOrCost;
  }

  public int getMaxArity() {
    return maxArity;
  }

  public int getMaxOpNum() {
    return maxOpNum;
  }
}
