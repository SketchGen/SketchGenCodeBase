package asketch;

import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.findSubnode;

import edu.mit.csail.sdg.alloy4compiler.ast.Browsable;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

  public static void main(String[] args) {
    System.out.println(Paths.get(System.getProperty("user.dir")));
    System.out.println("a\nb".split("->")[0]);
    System.out.println((int) Math.pow(2, 3) - 1);
    List<String> l = Arrays.asList("header", "header'", "elem'", "elem");
    l.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    });
    System.out.println(l);
    CompModule module = compileAlloyModule("sketch/.hidden/equiv/sll.als");
    Browsable sigs = findSubnode(module, "sig");
    for (int i = 0; i < sigs.getSubnodes().size(); i++) {
      System.out.println("----------");
      Sig sig = (Sig) sigs.getSubnodes().get(i);
      System.out.println(sig.getClass());
      System.out.println(sig.toString());
      System.out.println(sig.attributes);
      System.out.println(sig.isSubsig);
      System.out.println(sig.isSubset);
      if (!(sig instanceof Sig.PrimSig)) {
        continue;
      }
      Sig.PrimSig primSig = (Sig.PrimSig) sig;
      System.out.println(primSig.parent);
      for (Sig.Field field : sig.getFields()) {
        System.out.println(field.getHTML());
        System.out.println(field.type());
      }
//            String sigName = afterSubstring(extractHTML(primSig.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>")), "/", true);
//            System.out.println(primSig.toString());
//            System.out.println(primSig.isSubsig);
//            System.out.println(primSig.isSubset);
    }
    System.out.println(isOf(Integer.class, new Integer(1)));
    Set<Class> classes = new HashSet<>();
    System.out.println(classes.add(Integer.class));
    System.out.println(classes.add(Integer.class));
  }

  public static boolean isOf(Class clazz, Object obj) {
    return clazz.isInstance(obj);
  }
}
