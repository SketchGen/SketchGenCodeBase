sig Node {
  neighbors: set Node,
} 	

fact undirected {
  neighbors = ~neighbors
  no iden & neighbors	
}

pred graphIsConnected {
  all n1: Node | all n2 : Node | n1 != n2 => n1 in n2.^neighbors  }



pred test0 {
some disj Node0, Node1: Node {
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1
!graphIsConnected[]
}
}

run test0

pred test1 {
some disj Node0, Node1: Node {
Node = Node0 + Node1
neighbors = Node0->Node1 + Node1->Node0
graphIsConnected[]
}
}

run test1

pred test2 {
some disj Node0, Node1: Node {
Node = Node0 + Node1
neighbors = Node0->Node1
!graphIsConnected[]
}
}

run test2

pred test3 {
some disj Node0, Node1: Node {
Node = Node0 + Node1
no neighbors
!graphIsConnected[]
}
}

run test3

pred test4 {
some disj Node0: Node {
Node = Node0
no neighbors
graphIsConnected[]
}
}
run test4