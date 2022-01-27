module binarySearchTree

one sig BinaryTree {
  root: lone Node
}

sig Node {
  left, right: lone Node
}

pred IsTree() {
  all n: Node {
    n in BinaryTree.root.*(left + right)  => {
      n !in n.^(left + right)
      no n.left & n.right
      lone n.~(left + right)
    }
  }
}


pred Test0 {
  some disj BinaryTree0 : BinaryTree |  some disj Node0, Node1, Node2 : Node {
    BinaryTree = BinaryTree0
    Node = Node0 + Node1 + Node2
    root = BinaryTree0->Node2
    left = Node1->Node0
    right = Node2->Node1
    IsTree[]
  }
}
run Test0 for 3
