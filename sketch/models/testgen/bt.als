module binarySearchTree

one sig BinaryTree {
  root: lone Node
}

sig Node {
  left, right: lone Node
}

pred IsTree() {
  all n: Node {
    // Generating n.left & n.right requires cost >= 7
    n in \E\ => {
      n !in n.^(left + right)
      no n.left & n.right
      lone n.~(left + right)
    }
  }
}
