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

pred Test1 {
  some disj BinaryTree0 : BinaryTree |  some disj Node0, Node1 : Node {
    BinaryTree = BinaryTree0
    Node = Node0 + Node1
    root = BinaryTree0->Node0
    left = Node0->Node1
    right= Node0->Node1
    !IsTree[]
  }
}
run Test1 for 3

pred Test2 {
  some disj BinaryTree0 : BinaryTree |  some disj Node0, Node1 : Node {
    BinaryTree = BinaryTree0
    Node = Node0 + Node1
    root = BinaryTree0->Node1
    left = Node0 ->Node1
    right = Node1->Node0
    !IsTree[]
  }
}
run Test2 for 3

pred Test3 {
  some disj BinaryTree0 : BinaryTree |  some disj Node0, Node1 : Node {
    BinaryTree = BinaryTree0
    Node = Node0 + Node1
    root = BinaryTree0->Node0
    left = Node0->Node1
    right = Node0->Node0
    !IsTree[]
  }
}
run Test3 for 3

pred Test4 {
  some disj BinaryTree0 : BinaryTree |  some disj Node0, Node1 : Node {
    BinaryTree = BinaryTree0
    Node = Node0 + Node1
    root = BinaryTree0->Node0
    left = Node0->Node1 + Node1->Node1
    no right
    !IsTree[]
  }
}

run Test4 for 3

