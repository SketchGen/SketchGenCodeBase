package asketch.opts;

public class ASketchSolveOpt extends Opt {

  private int testNum;

  public ASketchSolveOpt(String modelPath, String pruningRule, int testNum) {
    super(modelPath, pruningRule);
    this.testNum = testNum;
  }

  public int getTestNum() {
    return testNum;
  }
}
